package org.raikes.cps;

import java.util.HashMap;

public class CpsConstants
{
    static HashMap<String, Integer> constants = new HashMap<String, Integer>();
    
    static
    {
        // This is a fucking disaster.
        // Their own published documents for matching and definitions files don't agree...
        
        // These are constants as defined in the original NBER CPS definition files.
        // For example, year might be 1-4, starting at the second character and counting 4 characters over...
        
        constants.put("1999_hhid_start", 344);   // 1999: h_idnum 344-358
        constants.put("1999_hhid_end", 358);
        constants.put("2000_hhid_start", 344);   // 2000: h_idnum 344-358
        constants.put("2000_hhid_end", 358);
    }
    
    public int getStart(String year, String field) 
    {
        return constants.get(year+"_"+field+"_start");
    }
    
    public int getEnd(String year, String field)
    {
        return constants.get(year+"_"+field+"_end");
    }
}