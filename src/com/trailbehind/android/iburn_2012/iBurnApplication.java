package com.trailbehind.android.iburn_2012;

import java.util.ArrayList;

import com.trailbehind.android.iburn_2012.DeviceLocation.LocationResult;


import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class iBurnApplication extends Application {
	
	// If database ready signal received AND data embargo lifted
	public static boolean dbReady = false;
	public static boolean embargoClear = false;
	
	public static String unlockPassword = "venus";

	
	
	@Override
	public void onCreate(){
		LocalBroadcastManager.getInstance(this).registerReceiver(dbReadyReceiver, new IntentFilter("dbReady"));
		LocalBroadcastManager.getInstance(this).registerReceiver(embargoClearReceiver, new IntentFilter("embargoClear"));
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
	  	    	dbReady = true;
	  	    }
	  	  }
	 };
	 
	 private BroadcastReceiver embargoClearReceiver = new BroadcastReceiver() {
	  	  @Override
	  	  public void onReceive(Context context, Intent intent) {
	  	    // 1 -- success, 0 -- error, -1 no data
	  	    int status = intent.getIntExtra("status", -1);
	  	    if(status == 1){
	  	    	embargoClear = true;
	  	    }
	  	  }
	 };
	 

}
