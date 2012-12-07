package org.raikes.cps;

import java.util.HashMap;

public class CPSVariableLocator {

	/*indexes in the hashmap ("hhids") are zero-indexed, so they are one less than the cps data dictionaries.*/
	
	private HashMap<Integer, Integer> hhids;
	private HashMap<Integer, Integer> hhnums;
	
	private HashMap<Integer, SurveyYear> years;
	
	public CPSVariableLocator(){
		//        year                 year  t  id  num no rect
		years.put(1980, new SurveyYear(1980, 6, 17, 15, 6, false));
		years.put(1982, new SurveyYear(1982, 6, 17, 15, 6, false));
		years.put(1983, new SurveyYear(1983, 6, 17, 15, 6, false));
		years.put(1984, new SurveyYear(1984, 6, 17, 15, 6, false));
		years.put(1985, new SurveyYear(1985, 6, 17, 15, 6, false));
		years.put(1986, new SurveyYear(1986, 6, 17, 15, 6, false));
		years.put(1987, new SurveyYear(1987, 6, 17, 15, 6, false)); // these pages are missing from the data dictionaries -- may be wrong
		years.put(1988, new SurveyYear(1988, 0, 319, 29, 8, true));
		years.put(1989, new SurveyYear(1989, 0, 319, 29, 8, true));
		years.put(1990, new SurveyYear(1990, 0, 319, 29, 8, true));
		years.put(1991, new SurveyYear(1991, 0, 319, 29, 8, true));
		years.put(1992, new SurveyYear(1992, 0, 343, 29, 8, true));
		years.put(1993, new SurveyYear(1993, 0, 343, 29, 8, true));
		years.put(1994, new SurveyYear(1994, 0, 343, 29, 8, true));
		
		years.put(1999, new SurveyYear(1999, 0, 343, 29, 8, true));
		
		hhids.put(1980, 17); // hhidnum
		hhids.put(1981, 11); 
		hhids.put(1982, 11);
		hhids.put(1983, 11);
		hhids.put(1984, 11);
		hhids.put(1985, 11);
		hhids.put(1986, 11);
		hhids.put(1987, 11);
		hhids.put(1988, 11);
		hhids.put(1989, 11);
		hhids.put(1990, 11);
		hhids.put(1991, 11);
		hhids.put(1992, 11);
		hhids.put(1993, 11);
		hhids.put(1994, 11);
		hhids.put(1995, 11);
		hhids.put(1996, 11);
		hhids.put(1997, 11);
		hhids.put(1998, 11);
		hhids.put(1999, 11);
		hhids.put(2000, 11);
		hhids.put(2001, 11);
		hhids.put(2002, 11);
		hhids.put(2003, 11);
		hhids.put(2004, 11);
		hhids.put(2005, 11);
		hhids.put(2006, 11);
		hhids.put(2007, 11);
		hhids.put(2008, 11);
		hhids.put(2009, 11);
		hhids.put(2010, 11);
		
		hhnums.put(1980, 15); // item9
		hhnums.put(1981, 11);
		hhnums.put(1982, 11);
		hhnums.put(1983, 11);
		hhnums.put(1984, 11);
		hhnums.put(1985, 11);
		hhnums.put(1986, 11);
		hhnums.put(1987, 11);
		hhnums.put(1988, 11);
		hhnums.put(1989, 11);
		hhnums.put(1990, 11);
		hhnums.put(1991, 11);
		hhnums.put(1992, 11);
		hhnums.put(1993, 11);
		hhnums.put(1994, 11);
		hhnums.put(1995, 11);
		hhnums.put(1996, 11);
		hhnums.put(1997, 11);
		hhnums.put(1998, 11);
		hhnums.put(1999, 11);
		hhnums.put(2000, 11);
		hhnums.put(2001, 11);
		hhnums.put(2002, 11);
		hhnums.put(2003, 11);
		hhnums.put(2004, 11);
		hhnums.put(2005, 11);
		hhnums.put(2006, 11);
		hhnums.put(2007, 11);
		hhnums.put(2008, 11);
		hhnums.put(2009, 11);
		hhnums.put(2010, 11);
	}
}
