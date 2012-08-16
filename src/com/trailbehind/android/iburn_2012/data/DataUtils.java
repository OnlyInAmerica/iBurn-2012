package com.trailbehind.android.iburn_2012.data;

import java.util.ArrayList;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.trailbehind.android.iburn_2012.FragmentTabsPager;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class DataUtils {
	
	private static final String CAMP_DATA_PATH = "/playa_data/camp_data.json";
	// Relative to getFilesDir() (/data/data/app.namespace/)
	
	public static class ImportJsonToCampTable extends AsyncTask<Void, Void, Void>{
		// This method is executed in a separate thread
		@Override
		protected Void doInBackground(Void... input) {
			if(FragmentTabsPager.app == null){
				Log.d("ImportJsonToCampTable","FragmentTabsPager Context not set");
				return null;
			}
			
			File camp_data = new File(FragmentTabsPager.app.getFilesDir().getAbsoluteFile() + CAMP_DATA_PATH);
			if (!camp_data.exists()){
		            Log.d("ImportJsonToCampTable", "Camp data not found!");
		            return null;
		     }
			
			
			Gson gson = new GsonBuilder().registerTypeAdapter(ArrayList.class, new JSONDeserializers.CampsDeserializer()).create();
			try {
				// Parse JSON
				ArrayList<ContentValues> result = gson.fromJson(FileUtils.fileToString(camp_data), ArrayList.class);
				// Insert JSON into database
				FragmentTabsPager.app.contentValuesToTable(result, PlayaContentProvider.CAMP_URI);
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
	    protected void onPostExecute(Void result) {

			super.onPostExecute(result);

	    }
		
	}
	


}
