package org.raikes.cps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

public class IpumsPopulator
{

    // TODO: It seems that querying the ResultSet every time for column defs could be a smidge inefficient
    public static void populateDatabase(File ipumsDataFile) throws SQLException, IOException
    {
    	try{
    		System.out.print("Inserting IPUMS data... ");
            Connection conn = Main.conn;
            String gzipPath = ipumsDataFile.getAbsolutePath();
            FileInputStream fis = new FileInputStream(gzipPath);
            GZIPInputStream gzifs = new GZIPInputStream(fis);
            Scanner ipumsGZIPDataScanner = new Scanner(gzifs);

            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM variables");
            rs.first();
            int numberOfParams = rs.getInt(1);
            rs.close();
            
            int year = 0;
            
            int numYears = (Main.endYear - Main.startYear)+1;  
            
            HashMap<Integer, PreparedStatement> pss = new HashMap<Integer, PreparedStatement>();
            int[] rows = new int[Main.endYear+1];
            
            String query;
            PreparedStatement ps;
            
            for (int z = Main.startYear; z <= Main.endYear; z++){
            	rows[z] = 0;
                query = "INSERT INTO data"+z+" VALUES (null, null, null, null, ";
                for (int i = 0; i < numberOfParams; i++)
                {
                    query += "?, ";
                }

                query = query.substring(0, query.length() - 2);
                query += ")";
                ps = conn.prepareStatement(query);
                pss.put(z, ps);
            }

            ResultSet rs2 = s.executeQuery("SELECT * FROM variables");
            int rowCount = 0;
            int commitCount = 0;
            while (ipumsGZIPDataScanner.hasNextLine())
            {
                String line = ipumsGZIPDataScanner.nextLine();
                int paramCount = 0;

                rs2.beforeFirst();
                while (rs2.next())
                {
                    paramCount++; // not 0-based indexing so this is OK.

                    String label = rs2.getString("label");
                    if (label.compareTo("DISABWRK") == 0 && year <= 1987 && year >= 1980){
                    	pss.get(year).setObject(paramCount, "55555");
                    	continue;
                    }
                    int startpos = rs2.getInt("startpos");
                    int endpos = rs2.getInt("endpos");
                    int precision = rs2.getInt("preciseness");

                    String value = line.substring(startpos - 1, endpos);
                    if (precision != 0)
                    {
                        double val = Double.parseDouble(value);
                        val = val / (Math.pow(10, precision));
                        value = "" + val;
                    }
                    
                    if (label.compareTo("YEAR") == 0){
                    	year = Integer.parseInt(value);                	
                    }
                    
                    pss.get(year).setObject(paramCount, value);
                }
                
                pss.get(year).addBatch();
                
                rows[year]++;       
                
                if (rows[year] >= Main.rowBatchCount){
                	pss.get(year).executeBatch();
                	System.out.println("Done" + rows[year] + " rows from year" + year);
                	rows[year] = 0;
                }
            }
            System.out.println("done");
            
            for (int zz = Main.startYear; zz <= Main.endYear; zz++){
            	System.out.println("About to insert for year "+zz);
            	pss.get(zz).executeBatch();
            	pss.get(zz).close();
            	System.out.println("Finished inserting for year "+zz);
            }

            rs2.close();
            s.close();
    	} catch (BatchUpdateException bue){
    		System.out.println("hey");
    	}
        
    }
}
