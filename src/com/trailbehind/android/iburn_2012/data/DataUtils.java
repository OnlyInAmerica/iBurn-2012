package com.trailbehind.android.iburn_2012.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.trailbehind.android.iburn_2012.FragmentTabsPager;

import pro.dbro.timelapse.BrowserActivity;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

public class DataUtils {
	
	private static final String CAMP_DATA_PATH = "/playa_data/camp_data.json";
	// Relative to getFilesDir() (/data/data/app.namespace/)
	
			public static class ImportJsonToCampTable extends AsyncTask<Void, Void, Void>{
					

				// This method is executed in a separate thread
				@Override
				protected Void doInBackground(Void... input) {
					if(FragmentTabsPager.context == null){
						Log.d("ImportJsonToCampTable","FragmentTabsPager Context not set");
						return null;
					}
					
					File camp_data = new File(FragmentTabsPager.context.getFilesDir().getAbsoluteFile() + CAMP_DATA_PATH);
					// JUST PARSE THE JSON, PUT IT IN THE DB
					// HAVE A SMOKE, AND SAVE BURNING MAN!
					return null;
				}
				
				@Override
			    protected void onPostExecute(Void result) {

					super.onPostExecute(result);

			    }
				
			}


}
