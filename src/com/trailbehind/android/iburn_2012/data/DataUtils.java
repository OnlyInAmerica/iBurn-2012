package com.trailbehind.android.iburn_2012.data;

import java.util.ArrayList;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.trailbehind.android.iburn_2012.CampFragment;
import com.trailbehind.android.iburn_2012.FragmentTabsPager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class DataUtils {
	
	private static final String CAMP_DATA_PATH = "playa-json/camp_data.json";
	private static final String EVENT_DATA_PATH = "playa-json/event_data.json";
	private static final String ART_DATA_PATH = "playa-json/art_data.json";
	// Relative to getFilesDir() (/data/data/app.namespace/)
	
	public static class ImportJsonToCampTable extends AsyncTask<Void, Void, Integer>{
		// This method is executed in a separate thread
		@Override
		protected Integer doInBackground(Void... input) {
			if(FragmentTabsPager.app == null){
				Log.d("ImportJsonToCampTable","FragmentTabsPager Context not set");
				return null;
			}
			
			AssetManager assets = FragmentTabsPager.app.getAssets();
			
			
			try {
				
				// CAMPS
				Gson gson = new GsonBuilder().registerTypeAdapter(ArrayList.class, new JSONDeserializers.CampsDeserializer()).create();
				//String[] asset_list = assets.list("playa-json");
				// Get Asset
				InputStream is = assets.open(CAMP_DATA_PATH);
				// Parse JSON
				ArrayList<ContentValues> result = gson.fromJson(inputStreamToChar(is), ArrayList.class);
				// Insert JSON into database
				//content://com.trailbehind.android.iburn.playacontentprovider/camp
				FragmentTabsPager.app.contentValuesToTable(result, PlayaContentProvider.CAMP_URI);
				/*
				Gson gson;
				InputStream is;
				ArrayList<ContentValues> result;
				*/
				// EVENTS
				gson = new GsonBuilder().registerTypeAdapter(ArrayList.class, new JSONDeserializers.EventsDeserializer()).create();
				is = assets.open(EVENT_DATA_PATH);
				result = gson.fromJson(inputStreamToChar(is), ArrayList.class);
				FragmentTabsPager.app.contentValuesToTable(result, PlayaContentProvider.EVENT_URI);
				
				// ART
				gson = new GsonBuilder().registerTypeAdapter(ArrayList.class, new JSONDeserializers.ArtDeserializer()).create();
				is = assets.open(ART_DATA_PATH);
				result = gson.fromJson(inputStreamToChar(is), ArrayList.class);
				FragmentTabsPager.app.contentValuesToTable(result, PlayaContentProvider.ART_URI);
				
				Log.d("ImportJsonToCampTable","Camps sent to database");
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
				return 0;
			} catch (IOException e) {
				e.printStackTrace();
				return 0;
			}
			return 1;
		}
		
		@Override
	    protected void onPostExecute(Integer result) {
			sendSuccessMessage(result);
			super.onPostExecute(result);

	    }
		
		private void sendSuccessMessage(int result) { // 0 = service stopped , 1 = service started, 2 = refresh view with call to bartApiRequest(), 3 = 
		  	  int status = 3; // hardcode status for calling TheActivity.parseBart
			  //Log.d("BART_Response", result);
		  	  Intent intent = new Intent("dbReady");
		  	  // You can also include some extra data.
		  	  intent.putExtra("status", result);
		  	  LocalBroadcastManager.getInstance(CampFragment.c).sendBroadcast(intent);
		  	}
		
	}

	public static String inputStreamToChar(InputStream is) throws IOException{
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		StringBuilder total = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
		    total.append(line);
		}
		return total.toString();
	}
	


}
