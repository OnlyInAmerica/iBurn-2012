package com.trailbehind.android.iburn_2012;

import java.util.ArrayList;

import android.app.Application;
import android.content.ContentValues;
import android.net.Uri;

public class iBurnApplication extends Application {
	
	public Uri contentValuesToTable(ArrayList<ContentValues> cv, Uri uri){
		int size = cv.size();	
		Uri result = null;
		for(int x = 0; x<size;x++){
			result = getContentResolver().insert(uri, cv.get(x));
		}
			return result;
	}

}
