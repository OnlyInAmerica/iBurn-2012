package pro.dbro.iburn_2012;

import java.util.ArrayList;

import pro.dbro.iburn_2012.DeviceLocation.LocationResult;
import pro.dbro.iburn_2012.data.DataUtils;



import android.app.AlertDialog;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class iBurnApplication extends Application {
	
	// If database ready signal received AND data embargo lifted
	public static boolean dbReady = false;
	public static boolean embargoClear = false;
	
	public static String unlockPassword = "venus";
	
	private static SharedPreferences prefs;
	private static SharedPreferences.Editor editor;
	static Resources res;

	
	@Override
	public void onCreate(){
		
		LocalBroadcastManager.getInstance(this).registerReceiver(dbReadyReceiver, new IntentFilter("dbReady"));
		LocalBroadcastManager.getInstance(this).registerReceiver(embargoClearReceiver, new IntentFilter("embargoClear"));
		
		res = getResources();
        prefs = getSharedPreferences("PREFS", 0);
        
    	embargoClear = prefs.getBoolean("embargoClear", false);
    	dbReady = prefs.getBoolean("dbReady", false);
    	Log.d("SetGlobalsFromPrefs","dbReady: " + String.valueOf(dbReady) + " embargoClear: " + String.valueOf(embargoClear));

	}

	public static void setEmbargoClear(boolean value){
        editor = prefs.edit();
        editor.putBoolean("embargoClear", value);
        editor.commit();
        embargoClear = value;
	}
	
	public static void setDbReady(boolean value){
        editor = prefs.edit();
        editor.putBoolean("dbReady", value);
        editor.commit();
        dbReady = value;
	}
	
	public Uri contentValuesToTable(ArrayList<ContentValues> cv, Uri uri){
		int size = cv.size();	
		Uri result = null;
		for(int x = 0; x<size;x++){
			result = getContentResolver().insert(uri, cv.get(x));
		}
			return result;
	}
	
	public int update(ContentValues values, Uri uri){
		return getContentResolver().update(uri, values, null, null);
	}
	
	private BroadcastReceiver dbReadyReceiver = new BroadcastReceiver() {
	  	  @Override
	  	  public void onReceive(Context context, Intent intent) {
	  	    // 1 -- success, 0 -- error, -1 no data
	  	    int status = intent.getIntExtra("status", -1);
	  	    if(status == 1){
	  	    	setDbReady(true);
	  	    }
	  	  }
	 };
	 
	 private BroadcastReceiver embargoClearReceiver = new BroadcastReceiver() {
	  	  @Override
	  	  public void onReceive(Context context, Intent intent) {
	  	    // 1 -- success, 0 -- error, -1 no data
	  	    int status = intent.getIntExtra("status", -1);
	  	    if(status == 1){
	  	    	setEmbargoClear(true);
	  	    }
	  	  }
	 };
	 

}
