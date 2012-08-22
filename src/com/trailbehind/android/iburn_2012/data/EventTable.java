package com.trailbehind.android.iburn_2012.data;

public class EventTable {
	
	//CAMP TABLE
    public static final String TABLE_NAME = "events";
    
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_ALL_DAY = "all_day";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_CHECK_LOCATION = "check_location";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_HOST_CAMP_ID = "camp_id";
    public static final String COLUMN_HOST_CAMP_NAME = "camp_name";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_END_TIME = "end_time";
    //public static final String COLUMN_CONTACT = "contact";
    
    public static final String[] COLUMNS = {COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_ALL_DAY, COLUMN_URL,
    	COLUMN_YEAR, COLUMN_CHECK_LOCATION, COLUMN_HOST_CAMP_ID, COLUMN_HOST_CAMP_NAME, COLUMN_LATITUDE, COLUMN_LONGITUDE,
    	COLUMN_START_TIME, COLUMN_END_TIME};
    
    public static final String CREATE_TABLE_STATEMENT = "create table " + TABLE_NAME + " ("+ COLUMN_ID +" integer primary key autoincrement, " 
	        +  COLUMN_NAME + " text, "+ COLUMN_DESCRIPTION + " text, " + COLUMN_CHECK_LOCATION + " integer, " 
	        +  COLUMN_ALL_DAY + " integer, "+ COLUMN_URL +" text, " 
	        +  COLUMN_YEAR + " integer, " + COLUMN_HOST_CAMP_ID +" integer, " 
	        +  COLUMN_HOST_CAMP_NAME + " text, " + COLUMN_START_TIME + " text, "
	        +  COLUMN_END_TIME + " text, " + COLUMN_LATITUDE +" real," 
	        +  COLUMN_LONGITUDE + " real, " + COLUMN_LOCATION + " text;";

}
