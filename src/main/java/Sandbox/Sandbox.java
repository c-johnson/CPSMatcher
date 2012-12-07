package Sandbox;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Sandbox {
	
	public static void main(String[] args){
		
		File rawCpsDir = new File("C:\\Users\\Chris Johnson\\Documents\\RAIK\\Economics\\colemickens-cpsMatcher-a4e955e\\cps\\raw_cps");
		
        System.out.print("Inserting CPS data... ");
        String zipPath = rawCpsDir.getAbsolutePath() + "/" + "cpsmar96.zip";
        ZipFile zf;
        Scanner cpsZipScanner = null;
		try {
			zf = new ZipFile(zipPath);
	        ZipEntry ze = zf.entries().nextElement();
	        InputStream ifs = zf.getInputStream(ze);
	        cpsZipScanner = new Scanner(ifs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		boolean found = false;
		
		 while (cpsZipScanner.hasNextLine())
         {
			 if (found) System.out.println("Fuck");
             String line = cpsZipScanner.nextLine();

             String check = line.substring(0, 1);
             int checkInt;
             
             try{
             	checkInt = Integer.parseInt(check);
             }catch (NumberFormatException nfe){
             	System.out.println("found it");
             	System.out.println(check);
             	found = true;
             }
         }
		 System.out.println("Program finished");
		 
	}
}
