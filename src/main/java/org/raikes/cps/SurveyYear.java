package org.raikes.cps;

public class SurveyYear {

	private int year; // year of the survey defintiions
	private int typePos; // position of the "type" variable (household, family, or person)
	private int hhidPos; // position of the "hhid" variable (household identification number)
	private int hhnumPos; // position of the "hhnum" variable (household number)
	private int personno;
	private boolean rect; // if this year's CPS is rectangular.  false is hierarchical.  1980-1988 is hierarchical, 1988B-2010 is rectangular
	
	/*
	 * for hierarchical entries, the type of record is given by "hhpos";
	 * 
	 * 00 indicates a household record
	 * 01-39 indicates a person record
	 * 41-79 indicates a family record
	 */
	
	public SurveyYear(int yearIn, int typePosIn, int hhidPosIn, int hhnumPosIn, int personnoIn, boolean rectIn){
		year = yearIn;
		typePos = typePosIn;
		hhidPos = hhidPosIn;
		hhnumPos = hhnumPosIn;
		personno = personnoIn;
		rect = rectIn;
	}
}
