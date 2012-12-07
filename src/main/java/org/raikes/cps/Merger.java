package org.raikes.cps;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVWriter;

public class Merger
{

    // TODO: Weirdness around age=90 ? (check the variable label to see what I'm wondering about...)
    public static void doMerge(File outputDir) throws SQLException, IOException
    {
        Connection conn = Main.conn;
        String query = "";

        System.out.println("Starting merge");
        
        for (int year = Main.startYear; year < Main.endYear; year++)
        {
        	query = "SELECT * FROM data"+year+" data1 JOIN data"+(year+1)+" data2 ";
            query += " ON data1.personno = data2.personno";
            query += " AND data1.hhnum = data2.hhnum";
            query += " AND data1.hhid = data2.hhid";
            if (year >= 2005) query += " AND data1.hhid2 = data2.hhid2";
            
            PreparedStatement ps = conn.prepareCall(query);
        	
            int firstYear = year;
            int secondYear = year + 1;
            
            if (firstYear == 1985 || firstYear == 1995){
            	continue;
            }
            
            System.out.println("Merging " + firstYear + " & " + secondYear);

            String filename = "merged_" + firstYear + "_" + secondYear + ".csv";
            String path = outputDir.getAbsolutePath() + "/" + filename;
            CSVWriter writer = new CSVWriter(new FileWriter(path), ',');
            System.out.println("Query text:");
            System.out.println(query);
            System.out.println("Starting query...");
            ResultSet results = ps.executeQuery();
            System.out.println("query finished.");
            ResultSetMetaData resultsMD = results.getMetaData();
            ArrayList<String> columnHeaders = new ArrayList<String>();
            int colCount = resultsMD.getColumnCount();
            System.out.println(colCount);
            for (int i = 1; i <= colCount; i++)
            {
                String columnLabel = resultsMD.getColumnLabel(i);
                System.out.println(columnLabel);
                columnHeaders.add(columnLabel);
            }

            String[] columnHeadersArray = new String[columnHeaders.size()];
            columnHeadersArray = columnHeaders.toArray(columnHeadersArray);
            writer.writeNext(columnHeadersArray);
            writer.writeAll(results, false);
            writer.close();
            ps.close();
        }
    }
}
