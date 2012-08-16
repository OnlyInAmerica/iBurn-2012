package com.trailbehind.android.iburn_2012.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * 
 * @author davidbrodsky
 * @description SQLiteWrapper is written to be application agnostic.
 * requires Strings: DATABASE_NAME, DATABASE_VERSION,
 * CREATE_TABLE_STATEMENT, TABLE_NAME
 */
class DBWrapper extends SQLiteOpenHelper {
	
	//DATABASE INFO
    public static final String DATABASE_NAME = "iburn.db";
    public static final int DATABASE_VERSION = 1;
    
    //TABLE INFO
    
    public static final String CREATE_TABLE_STATEMENT = CampTable.CREATE_TABLE_STATEMENT;
    public static final String TABLE_NAME = CampTable.TABLE_NAME;

    //Schema: Number, Name, Datetime [YYYYMMDDKKMMSS]
    /**
     * Constructor
     * @param context the application context
     */
    public DBWrapper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    /**
     * Called at the time to create the DB.
     * The create DB statement
     * @param the SQLite DB
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_STATEMENT);
    }
    
    /**
     * Invoked if a DB upgrade (version change) has been detected
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, 
       int oldVersion, int newVersion) {
        // Drop old table and re-create
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
    }
    
    public static ContentValues cursorRowToContentValues(Cursor cursor){
    	ContentValues values = new ContentValues();
    	
    	if(cursor == null || !cursor.moveToFirst())
    		return values;
    	
    	AbstractWindowedCursor awc =
                (cursor instanceof AbstractWindowedCursor) ? (AbstractWindowedCursor) cursor : null;

        String[] columns = cursor.getColumnNames();
        int length = columns.length;
        for (int i = 0; i < length; i++) {
        	//Log.d("cursorRowToContentValues",columns[i] + " null: " + String.valueOf(cursor.isNull(i)));
        	if(cursor.isNull(i)){
        		// Don't insert null table records into ContentValues
        		continue;
        	}
        	else{
	            if (awc != null && awc.isBlob(i)) {
	                values.put(columns[i], cursor.getBlob(i));
	            } else {
	                values.put(columns[i], cursor.getString(i));
	            }
        	}
        }
        
    	return values;
    }

}