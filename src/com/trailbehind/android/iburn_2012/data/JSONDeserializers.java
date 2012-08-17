package com.trailbehind.android.iburn_2012.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.ContentValues;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class JSONDeserializers {
	/**
	 * Deserializes an array of JSON objects representing Burning Man Camps
	 * into an array of ContentValues for direct insertion into a database
	 * @author davidbrodsky
	 *
	 */
	public static class CampsDeserializer implements JsonDeserializer<ArrayList<ContentValues>>{
		
		public ArrayList<ContentValues> deserialize(JsonElement json, Type type,
		        JsonDeserializationContext context) throws JsonParseException {
			
			ArrayList<ContentValues> result = new ArrayList<ContentValues>();
	
			JsonArray array= json.getAsJsonArray();
			int len = array.size();
			JsonObject object;
			
			for(int x=0; x<len; x++){
				object = array.get(x).getAsJsonObject();
				ContentValues cv = new ContentValues();
				try{
					if(object.has(CampJSON.KEY_NAME))
						if(!object.get(CampJSON.KEY_NAME).isJsonNull())
						cv.put(CampTable.COLUMN_NAME, object.get(CampJSON.KEY_NAME).getAsString()); 
					
					if(object.has(CampJSON.KEY_DESCRIPTION))
						if(!object.get(CampJSON.KEY_DESCRIPTION).isJsonNull())
						cv.put(CampTable.COLUMN_DESCRIPTION, object.get(CampJSON.KEY_DESCRIPTION).getAsString());
					
					if(object.has(CampJSON.KEY_CAMP_ID))
						if(!object.get(CampJSON.KEY_CAMP_ID).isJsonNull())
						cv.put(CampTable.COLUMN_CAMP_ID, object.get(CampJSON.KEY_CAMP_ID).getAsInt()); 
					
					if(object.has(CampJSON.KEY_CONTACT))
						if(!object.get(CampJSON.KEY_CONTACT).isJsonNull())
							cv.put(CampTable.COLUMN_CONTACT, object.get(CampJSON.KEY_CONTACT).getAsString()); 
					
					if(object.has(CampJSON.KEY_HOMETOWN))
						if(!object.get(CampJSON.KEY_HOMETOWN).isJsonNull())
						cv.put(CampTable.COLUMN_HOMETOWN, object.get(CampJSON.KEY_HOMETOWN).getAsString()); 
					
					if(object.has(CampJSON.KEY_LATITUDE))
						if(!object.get(CampJSON.KEY_LATITUDE).isJsonNull())
						cv.put(CampTable.COLUMN_LATITUDE, object.get(CampJSON.KEY_LATITUDE).getAsDouble()); 
					
					if(object.has(CampJSON.KEY_LONGITUDE))
						if(!object.get(CampJSON.KEY_LONGITUDE).isJsonNull())
						cv.put(CampTable.COLUMN_LONGITUDE, object.get(CampJSON.KEY_LONGITUDE).getAsDouble()); 
					
					if(object.has(CampJSON.KEY_LOCATION))
						if(!object.get(CampJSON.KEY_LOCATION).isJsonNull())
						cv.put(CampTable.COLUMN_LOCATION, object.get(CampJSON.KEY_LOCATION).getAsString());
					
					if(object.has(CampJSON.KEY_YEAR))
						if(!object.get(CampJSON.KEY_YEAR).isJsonNull())
						cv.put(CampTable.COLUMN_YEAR, ((JsonObject)object.get(CampJSON.KEY_YEAR)).get(CampJSON.KEY_YEAR).getAsInt());
					
					if(object.has(CampJSON.KEY_URL))
						if(!object.get(CampJSON.KEY_URL).isJsonNull())
						cv.put(CampTable.COLUMN_URL, object.get(CampJSON.KEY_URL).getAsString());
					
				    result.add(cv);
			    } catch(Throwable t){
			    	throw new JsonParseException(t);
			    }				
			}

		    return result;
		}
	}

}
