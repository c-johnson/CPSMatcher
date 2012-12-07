package org.raikes.cps;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.sql.rowset.serial.SerialBlob;

public class CpsPopulator
{

    public static String getYear(int year)
    {
        year = year % 100;
        if (year < 10)
        {
            return "0" + year;
        }
        else
        {
            return "" + year;
        }
    }
    /*
     * Here's the premise:
     * Query: UPDATE data SET hhid=val, hhnum=val2, personno=val3 WHERE hhid IS NULL AND year=year_val LIMIT 1
     * This should allow me to sequentially update each row in the table from the source CPS file.
     */

    public static void populateDatabase(File rawCpsDir) throws SQLException, IOException
    {

        Connection conn = Main.conn;
        
        int currentYear = 0;
        int identifier = 0;

        PreparedStatement ps = null;       

        for (int year = Main.startYear; year <= Main.endYear; year++)
        {
        	if (year <= 2004){
        		ps = conn.prepareStatement("UPDATE data"+year+" SET hhid=?, hhnum=?, personno=? WHERE year=? AND hhid IS NULL LIMIT 1");
        	}
        	else if (year >= 2005 && year <= 2010){
        		ps = conn.prepareStatement("UPDATE data"+year+" SET hhid=?, hhid2=?, hhnum=?, personno=? WHERE year=? AND hhid IS NULL LIMIT 1");
        	}

        	currentYear = year;
            System.out.print("Inserting " + year + " CPS data... ");
            String zipPath = rawCpsDir.getAbsolutePath() + "/" + "cpsmar" + getYear(year) + ".zip";
            ZipFile zf = new ZipFile(zipPath);
            ZipEntry ze = zf.entries().nextElement();
            InputStream ifs = zf.getInputStream(ze);
            Scanner cpsZipScanner = new Scanner(ifs);

            int rowCount = 0;
            int commitCount = 0;
            String templine = "";
            
//          if (line.charAt(0) != '3')
//          {
//              continue; // skip non-person records
//          }
          // TODO: Actually extract these based on their widths as defined per year.
          // TODO: Are these the identifier vals we need?
//          String hhidString = extractFieldForYear("hhid", year);
//          hhid = Integer.parseInt(hhidString);
            
//            BigInteger hhid = null;
//            SerialBlob blob = null;
//        	byte[] blob = null;
            
            
            long hhid = 1;
            long hhid2 = 0;
//            String hhid = "";
            int hhnum = 1;
            int personno = 1;
            
            while (cpsZipScanner.hasNextLine())
            {
                String line = cpsZipScanner.nextLine();

                String check = line.substring(0, 1);
                int checkInt;
                
                try{
                	checkInt = Integer.parseInt(check);
                }catch (NumberFormatException nfe){
                	continue;
                }
                
                if (currentYear >=1980 && currentYear <= 1987){
                	identifier = Integer.parseInt(line.substring(6, 8));
                	
                	if (identifier==0){ // household record -- grab hhid and hhnum
                        hhid = Long.parseLong(line.substring(17, 29));
//                      hhid = new BigInteger(line.substring(17, 29));
//                      blob = new SerialBlob(hhid.toByteArray());
//                		hhid = line.substring(17, 30);
                        hhnum = Integer.parseInt(line.substring(15, 16));
                	}
                	else if (identifier > 40 && identifier < 80){ // family record -- do nothing
                		
                	}
                	else if (identifier >=1 && identifier < 40){ // person record -- grab line number
                		personno = identifier;
                		
                		ps.clearParameters();
	                    ps.setLong(1, hhid);
//	                    ps.setBlob(1, blob);
//                		ps.setString(1, hhid);
	                    ps.setInt(2, hhnum);
	                    ps.setInt(3, personno);
	                    ps.setInt(4, currentYear);
	
	                    ps.addBatch();
	                    rowCount++;
	                    if (rowCount >= Main.rowBatchCount)
	                    {
	                        ps.executeBatch();
	                        rowCount = 0;
	                    }
                	}
                	
                	
                	
                }
                
                else if (currentYear >=1988 && currentYear <=2010){
                	identifier = Integer.parseInt(line.substring(0, 1));
                
	                switch(identifier){
	                case 1:
//	                	year = Integer.parseInt(line.substring(13, 17));
	                	if (currentYear >=1988 && currentYear <= 1995){
	                		hhid = Long.parseLong(line.substring(319, 331));
//	                		hhid = new BigInteger(line.substring(319, 331));
//	                        blob = new SerialBlob(hhid.toByteArray());
//	                		hhid = line.substring(319, 332);
	                	}
	                	else if (currentYear >= 1996 && currentYear <= 2004){
	                		hhid = Long.parseLong(line.substring(343, 358));
//	                		hhid = new BigInteger(line.substring(343, 358));
//	                        blob = new SerialBlob(hhid.toByteArray());
//	                		hhid = line.substring(343, 358);
	                	}
	                	else if(currentYear >= 2005 && currentYear <= 2010){
//	                		templine = line.substring(343, 358) + line.substring(319, 324);
	                		hhid = Long.parseLong(line.substring(343, 358));
	                		hhid2 = Long.parseLong(line.substring(319, 324));
//	                		hhid = new BigInteger(templine);
//	                        blob = new SerialBlob(hhid.toByteArray());
//	                		hhid = templine;
	                	}
	                    hhnum = Integer.parseInt(line.substring(29, 30));
	                	break;
	                case 2:
	                	break;
	                case 3:
	                	personno = Integer.parseInt(line.substring(8, 10));
	                	
	                	if (currentYear >=1988 && currentYear <= 2004){
	                		ps.clearParameters();
		                	ps.setLong(1, hhid);
//		                	ps.setLong(2, hhid2);
//		                	ps.setBlob(1, blob);
//		                	ps.setString(1, hhid);
		                    ps.setInt(2, hhnum);
		                    ps.setInt(3, personno);
		                    ps.setInt(4, currentYear);
	                	}
	                	else if (currentYear >= 2005 && currentYear <= 2010){
	                		
	                		ps.clearParameters();
		                	ps.setLong(1, hhid);
		                	ps.setLong(2, hhid2);
//		                	ps.setBlob(1, blob);
//		                	ps.setString(1, hhid);
		                    ps.setInt(3, hhnum);
		                    ps.setInt(4, personno);
		                    ps.setInt(5, currentYear);
	                	}
	
	                    ps.addBatch();
	                    rowCount++;
	                    if (rowCount >= Main.rowBatchCount)
	                    {
	                    	System.out.println("Finished CPS batch for year " + currentYear);
	                        ps.executeBatch();
	                        rowCount = 0;
	                    }
	                	break;
	                }     
                }
            }
            ps.executeBatch();
            System.out.println("done");
        }
        ps.close();
        System.out.println("Finished inserting all CPS Data");
    }

}
