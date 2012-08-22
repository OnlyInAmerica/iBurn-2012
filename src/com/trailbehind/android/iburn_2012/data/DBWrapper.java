package com.trailbehind.android.iburn_2012.data;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.trailbehind.android.iburn_2012.FragmentTabsPager;

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
    
    private static String DB_DESTINATION_PATH = "/data/data/com.trailbehind.android.iburn_2012/databases/";
    
    //TABLE INFO
    // public static final String CREATE_TABLE_STATEMENT = CampTable.CREATE_TABLE_STATEMENT;
    // public static final String TABLE_NAME = CampTable.TABLE_NAME;

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
        db.execSQL(CampTable.CREATE_TABLE_STATEMENT);
        db.execSQL(EventTable.CREATE_TABLE_STATEMENT);
        db.execSQL(ArtTable.CREATE_TABLE_STATEMENT);
        Log.d("DBWrapper","Creating DB");
    }
    
    /**
     * Invoked if a DB upgrade (version change) has been detected
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, 
       int oldVersion, int newVersion) {
        // Drop old table and re-create
    	db.execSQL("DROP TABLE IF EXISTS " + CampTable.TABLE_NAME);
    	db.execSQL("DROP TABLE IF EXISTS " + EventTable.TABLE_NAME);
    	db.execSQL("DROP TABLE IF EXISTS " + ArtTable.TABLE_NAME);
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
    
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = FragmentTabsPager.app.getAssets().open(DBWrapper.DATABASE_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_DESTINATION_PATH + DATABASE_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }

}