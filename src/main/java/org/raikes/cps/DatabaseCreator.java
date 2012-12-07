package org.raikes.cps;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseCreator
{
    @SuppressWarnings("unused")
    public static Pattern getColumnPattern()
    {
        String txt = "     HHWT    1-4 (2)";

        String re1 = ".*?"; // Non-greedy match on filler
        String re2 = "((?:[a-z][a-z]+))"; // Word 1
        String re3 = "(\\s+)"; // White Space 1
        String re4 = "(\\d+)"; // Integer Number 1
        String re5 = "(-)"; // Any Single Character 1
        String re6 = "(\\d+)"; // Integer Number 2
        String re7 = "(\\s+)"; // White Space 2
        String re8 = "(\\()"; // Any Single Character 2
        String re9 = "(\\d+)"; // Integer Number 3
        String re10 = "(\\))"; // Any Single Character 3

        Pattern p = Pattern.compile(re1 + re2 + re3 + re4 + re5 + re6 + re7 + re8 + re9 + re10, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(txt);
        if (m.find())
        {
            String word1 = m.group(1); // column label
            String ws1 = m.group(2);
            String int1 = m.group(3); // startpos
            String c1 = m.group(4);
            String int2 = m.group(5); // endpos
            String ws2 = m.group(6);
            String c2 = m.group(7);
            String int3 = m.group(8); // precision
            String c3 = m.group(9);
            // System.out.print("("+word1.toString()+")"+"("+ws1.toString()+")"+"("+int1.toString()+")"+"("+c1.toString()+")"+"("+int2.toString()+")"+"("+ws2.toString()+")"+"("+c2.toString()+")"+"("+int3.toString()+")"+"("+c3.toString()+")"+"\n");
        }
        return p;
    }

    @SuppressWarnings("unused")
    private static Pattern getVariablePattern()
    {
        String txt = "    YEAR      \"Survey Year\"";

        String re1 = ".*?"; // Non-greedy match on filler
        String re2 = "((?:[a-z][a-z]+))"; // Word 1
        String re3 = "(\\s+)"; // White Space 1
        String re4 = "(\".*?\")"; // Double Quote String 1

        Pattern p = Pattern.compile(re1 + re2 + re3 + re4, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(txt);
        if (m.find())
        {
            String word1 = m.group(1); // label
            String ws1 = m.group(2);
            String string1 = m.group(3); // description
            // System.out.print("("+word1.toString()+")"+"("+ws1.toString()+")"+"("+string1.toString()+")"+"\n");
        }
        return p;
    }
    
    @SuppressWarnings("unused")
    private static Pattern getValuesPattern()
    {
        String txt = "    300    \"American Indian/Aleut/Eskimo\"";

        String re1 = ".*?"; // Non-greedy match on filler
        String re2 = "(\\d+)"; // Integer Number 1
        String re3 = "(\\s+)"; // White Space 1
        String re4 = "(\".*?\")"; // Double Quote String 1

        Pattern p = Pattern.compile(re1 + re2 + re3 + re4, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(txt);
        if (m.find())
        {
            String int1 = m.group(1); // code
            String ws1 = m.group(2);
            String string1 = m.group(3); // value
            // System.out.print("("+int1.toString()+")"+"("+ws1.toString()+")"+"("+string1.toString()+")"+"\n");
        }
        return p;
    }

    public static void createDatabase(File spsDefinition) throws FileNotFoundException, SQLException
    {
        System.out.print("Creating databases... ");
        Connection conn = Main.conn;
        Statement s = conn.createStatement();

        s.execute("create table variables (label varchar(20), startpos int, endpos int, preciseness int, description varchar(50), encoded boolean)");
        s.close();

        // Used txt2re.com for these regular expressions
        Pattern columnPattern = getColumnPattern(); // http://goo.gl/yrP0F
        Pattern variablePattern = getVariablePattern(); // http://goo.gl/s9XXE
        Pattern valuesPattern = getValuesPattern(); // http://goo.gl/wn60N

        Scanner ipumsDefScanner = new Scanner(spsDefinition);
        while (ipumsDefScanner.hasNextLine())
        {
            String line = ipumsDefScanner.nextLine();

            // VARIABLE + COLUMN WIDTH PARSING
            if (line.startsWith("data list file"))
            {
                line = ipumsDefScanner.nextLine();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO variables VALUES (?, ?, ?, ?, null, ?)");

                while (!line.equals("."))
                {
                    Matcher m = columnPattern.matcher(line);

                    if (!m.matches())
                    {
                        System.err.println("Uh oh, why didn't this match?");
                    }

                    // TODO: Bug, this breaks because the precision is not appropriately optional.
                    // I work around this by adding (0) to all of the non-precise rows manually in the "original" SPS definition file
                    // The Fix is to get the precision group to be optional. I don't know enough RegEx to do this myself
                    
                    
                    String label = m.group(1);
                    int startpos = Integer.parseInt(m.group(3));
                    int endpos = Integer.parseInt(m.group(5));
                    int precision = Integer.parseInt(m.group(8));

                    ps.clearParameters();
                    ps.setString(1, label);
                    ps.setInt(2, startpos);
                    ps.setInt(3, endpos);
                    ps.setInt(4, precision);
                    ps.setBoolean(5, false);
                    ps.addBatch();

                    line = ipumsDefScanner.nextLine();
                }

                ps.executeBatch();
                ps.close();
            }

            // VARIABLE LABELS
            if (line.startsWith("variable labels"))
            {
                line = ipumsDefScanner.nextLine();
                PreparedStatement ps = conn.prepareStatement("UPDATE variables SET description=? WHERE label=?");
                
                while (!line.equals("."))
                {
                    Matcher m = variablePattern.matcher(line);

                    if (!m.matches())
                    {
                        System.err.println("Uh oh, again, this should have matched...");
                    }

                    String label = m.group(1);
                    String description = m.group(3);
                    description = description.substring(1, description.length()-1);
                    
                    ps.setString(1, description);
                    ps.setString(2, label);
                    ps.addBatch();

                    line = ipumsDefScanner.nextLine();
                }
                ps.executeBatch();
                ps.close();
            }

            // VARIABLE CODES (aka, value labels)
            if (line.startsWith("value labels"))
            {
                String label = "";
                line = ipumsDefScanner.nextLine();
                PreparedStatement ps = null;

                while (!line.equals("."))
                {
                    if (line.trim().startsWith("/"))
                    {
                        label = line.trim().substring(1);

                        String tableName = label + "_codes";
                        Statement s1 = conn.createStatement();
                        String query = "CREATE TABLE " + tableName + " (code int, value varchar(60))";
                        String query2 = "UPDATE variables SET encoded=true WHERE label=\'" + label +"\'";
                        ps = conn.prepareStatement("INSERT INTO " + tableName + " VALUES (?, ?)");
                        s1.execute(query);
                        s1.execute(query2);
                        s1.close();
                    }
                    else
                    {
                        Matcher m = valuesPattern.matcher(line);

                        if (!m.matches())
                        {
                            System.err.println("Should have matched here.");
                        }

                        String code = m.group(1);
                        String value = m.group(3);
                        value = value.substring(1, value.length()-1);

                        String tableName = label + "_codes";

                        ps.clearParameters();
                        ps.setString(1, code);
                        ps.setString(2, value);
                        ps.execute();
                    }

                    line = ipumsDefScanner.nextLine();
                }
                ps.close();
            }
        }

        // CREATE TABLES FOR EVERY INCLUDED YEAR
        String schema = "";

        Statement s1 = conn.createStatement();
        ResultSet rs = s1.executeQuery("select label, preciseness from variables");
        while (rs.next())
        {
            String label = rs.getString("label");
            int precision = rs.getInt("preciseness");

            if (precision == 0)
            {
                schema += label + " int, ";
            }
            else
            {
                schema += label + " double, ";
            }
        }
        rs.close();
        s1.close();
        schema = schema.substring(0, schema.length() - 2);

        Statement s2 = conn.createStatement();

        for (int i = Main.startYear; i <= Main.endYear; i++){
            String query = "create table data"+i+" (HHID bigint, HHID2 bigint, HHNUM int, PERSONNO int, " + schema + ", index mainindex (hhid, hhid2, hhnum, personno))";
            s2.execute(query);
        }
        
        System.out.println("done");
    }
}