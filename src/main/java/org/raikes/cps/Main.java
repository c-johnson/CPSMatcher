package org.raikes.cps;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.swing.JFileChooser;

public class Main
{
    public static Connection conn;

    // TODO: This is hard coded
    public static int startYear = 1980;
    public static int endYear = 2010;

    private static final boolean preserveExistingData = false;
    private static final boolean debug = true;
    
    // TODO: I have no idea what number is optimal here, but it spends 99% of the time during population doing the actual SQL commit and very little time parsing the files
    static final long rowBatchCount = 10000;

    // TODO: Enforce age, etc -- right now it will crash when it tries to join... most likely.
    // TODO: Should any of the ints be longs in the database?

    private static void databaseInit() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        Class.forName("com.mysql.jdbc.Driver").newInstance();

        conn = DriverManager.getConnection("jdbc:mysql:///cps", "cps", "cps");	

        Statement s = conn.createStatement();
        if (!preserveExistingData)
        {
            s.execute("drop table if exists variables");
            s.execute("drop table if exists AGE_codes");
            s.execute("drop table if exists MARST_codes");
            s.execute("drop table if exists RACE_codes");
            s.execute("drop table if exists SEX_codes");
            s.execute("drop table if exists disabwrk_codes");
            s.execute("drop table if exists lineno_codes");
            s.execute("drop table if exists educ_codes");
            s.execute("drop table if exists empstat_codes");
            s.execute("drop table if exists incbus_codes");
            s.execute("drop table if exists incfarm_codes");
            s.execute("drop table if exists inctot_codes");
            s.execute("drop table if exists incwage_codes");
            s.execute("drop table if exists occ_codes");
            s.execute("drop table if exists hwtsupp_codes");
            s.execute("drop table if exists month_codes");
            s.execute("drop table if exists statefip_codes");
            for (int i = startYear; i <= endYear; i++){
            	s.execute("drop table if exists data"+i);
            }            
        }
        s.close();
    }

    private static void databaseDeinit()
    {
        if (conn != null)
        {
            try
            {
                conn.close();
                conn = null;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static File chooseFile(String s, int mode)
    {
        File file = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(s);
        chooser.setFileSelectionMode(mode);
        int retVal = chooser.showOpenDialog(null);
        if (retVal == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile().exists() && chooser.getSelectedFile().canRead())
        {
            file = chooser.getSelectedFile();
        }
        return file;
    }

    public static void main(String args[]) throws Exception
    {
        databaseInit();
        File ipumsDataFile = null;
        File ipumsDefFile = null;
        File rawCpsDir = null;
        File outputDir = null;
        File h_idnum94 = null;

        // Set these file paths manually if you're debugging.  Save yourself some trouble
        if (debug)
        {
        	String currentDir = new File(".").getAbsolutePath();
            String thisDir = "C:\\Users\\Chris Johnson\\Documents\\RAIK\\Sophomore Semester 2\\CSE383 Software Engineering\\workspace\\cpsmatcher\\input\\";
            ipumsDefFile = new File(thisDir + "cps_00011.sps");
            ipumsDataFile = new File(thisDir + "cps_00011.dat.gz");
            rawCpsDir = new File(thisDir + "raw cps");
            outputDir = new File(thisDir + "..\\output");
            h_idnum94 = new File(thisDir + "h_idnum_mar94.dat");
        }
        


        // CHOOSE FILES
        while (ipumsDefFile == null)
        {
            ipumsDefFile = chooseFile("Choose IPUMS Definition File (cps_0000x.sps))", JFileChooser.FILES_ONLY);
        }

        while (ipumsDataFile == null)
        {
            ipumsDataFile = chooseFile("Choose IPUMS Data File (cps_0000x.dat.gz))", JFileChooser.FILES_ONLY);
        }

        while (rawCpsDir == null)
        {
            rawCpsDir = chooseFile("Choose CPS Data Directory", JFileChooser.DIRECTORIES_ONLY);
        }
        while (outputDir == null)
        {
            outputDir = chooseFile("Choose Output Directory", JFileChooser.DIRECTORIES_ONLY);
            // ensure we can write to it. not pretty, but this will work
            if (outputDir != null && !outputDir.canWrite())
            {
                outputDir = null;
            }
        }

    	if (!preserveExistingData){
            DatabaseCreator.createDatabase(ipumsDefFile);
            IpumsPopulator.populateDatabase(ipumsDataFile);
      	  	CpsPopulator.populateDatabase(rawCpsDir);    
            if (h_idnum94.exists()){
            	if (h_idnum94.canRead()){
            		correct1994(h_idnum94);
            	}
            }
    	} 
        Merger.doMerge(outputDir);


        databaseDeinit();
    }
    
    
    public static void correct1994(File correctionFile) throws FileNotFoundException, SQLException{
    	System.out.println("Correcting 1994 HHID numbers...");
    	Scanner sc = new Scanner(correctionFile);
    	String line = "";
    	String[] vals = new String[2];
    	long hhid = 0;
    	int serial = 0;
		PreparedStatement ps = conn.prepareStatement("UPDATE data1994 SET hhid=? WHERE serial=?");
    	while (sc.hasNextLine()){
    		serial++; // starts at 1
    		line = sc.nextLine();
    		vals = line.split(" ");
    		hhid = Long.parseLong(vals[1]);
    		ps.setLong(1, hhid);
    		ps.setInt(2, serial);
    		ps.addBatch();
    		if (serial % 1000 == 0){
    			System.out.println(serial);
    	    	ps.executeBatch();
    		}
    	}
    	System.out.println("Done correcting 1994 HHID numbers.");
    }
}
