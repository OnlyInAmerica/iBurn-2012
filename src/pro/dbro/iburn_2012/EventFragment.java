/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pro.dbro.iburn_2012;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import pro.dbro.iburn_2012.ArtFragment.CursorLoaderListFragment;
import pro.dbro.iburn_2012.data.ArtTable;
import pro.dbro.iburn_2012.data.CampTable;
import pro.dbro.iburn_2012.data.EventTable;
import pro.dbro.iburn_2012.data.PlayaContentProvider;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SearchViewCompat;
import android.support.v4.widget.SearchViewCompat.OnQueryTextListenerCompat;
import android.support.v4.widget.SimpleCursorAdapter;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.Contacts.People;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SectionIndexer;
import android.widget.TextView;

/**
 * Demonstration of the use of a CursorLoader to load and display contacts
 * data in a fragment.
 */
@SuppressWarnings("all")
public class EventFragment extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            CursorLoaderListFragment list = new CursorLoaderListFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }


    public static class CursorLoaderListFragment extends PlayaListFragmentBase implements LoaderManager.LoaderCallbacks<Cursor>{

        // This is the Adapter being used to display the list's data.
        SimpleCursorAdapter mAdapter;
        
        LoaderManager lm;
        
        public static Integer[] daySections = new Integer[8];
        // Burning man start date. WHY are months zero-based?
        GregorianCalendar cal = new GregorianCalendar(2012, 7, 28, 0, 0);
        boolean sectionsSet = false;
        @Override
        public void restartLoader(){
        	getLoaderManager().restartLoader(0, null, CursorLoaderListFragment.this);
    	 }
        
        public void initLoader(){
        	getLoaderManager().initLoader(0, null, this);
        }


        @SuppressLint("NewApi")
		@Override public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Text to display before ListView is populated
            emptyText.setText("Loading Events");

            // We have a menu item to show in action bar.
            setHasOptionsMenu(true);

            
            // Start out with a progress indicator.
            //setListShown(false);
            /*
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(dbReadyReceiver,
            	      new IntentFilter("dbReady"));
            */
            lm = this.getLoaderManager();
            if(FragmentTabsPager.app.dbReady && mAdapter == null){
            	mAdapter = new EventCursorAdapter(getActivity(), null);
                initLoader();
            	//getLoaderManager().initLoader(0, null, (android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>) lm);
                setListAdapter(mAdapter);
            }
        	// Else LoaderManager will be started on dbReady signal
            
            // Set up SectionIndexer by date
            if(!sectionsSet)
                setSections();
            
            ListView lv = getListView();
            lv.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
            if(Build.VERSION.SDK_INT > 15){
            	lv.setFastScrollAlwaysVisible(true);
            	lv.setScrollBarSize(10);
            }
            lv.setFastScrollEnabled(true);
        }
        
        private BroadcastReceiver dbReadyReceiver = new BroadcastReceiver() {
      	  @Override
      	  public void onReceive(Context context, Intent intent) {
      	    // 1 -- success, 0 -- error, -1 no data
      	    int status = intent.getIntExtra("status", -1);
      	    if(status == 1){
      	    	mAdapter = new EventCursorAdapter(getActivity(), null);
                initLoader();  
      	    	//getLoaderManager().initLoader(0, null, (android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>) lm);
                setListAdapter(mAdapter);
      	    	
      	    }
      	  }
      	};

        // These are the Camp rows that we will retrieve.
        static final String[] EVENT_PROJECTION = new String[] {
            EventTable.COLUMN_ID,
            EventTable.COLUMN_NAME,
            EventTable.COLUMN_START_TIME,
            EventTable.COLUMN_START_TIME_PRINT,
            EventTable.COLUMN_ALL_DAY,
            EventTable.COLUMN_FAVORITE
        };

        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // This is called when a new Loader needs to be created.  This
            // sample only has one Loader, so we don't care about the ID.
            // First, pick the base URI to use depending on whether we are
            // currently filtering.
            
        	Uri baseUri;
            String ordering = null;
            if (mCurFilter != null) {
                baseUri = Uri.withAppendedPath(PlayaContentProvider.EVENT_SEARCH_URI, Uri.encode(mCurFilter));
            } else {
                baseUri = PlayaContentProvider.EVENT_URI;
                ordering = EventTable.COLUMN_START_TIME + " ASC";
            }
            
            String selection = null;
            String[] selectionArgs = null;
            
            if(limitListToFavorites){
            	selection = ArtTable.COLUMN_FAVORITE + " = ?";
            	selectionArgs = new String[]{"1"};
            }
			
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
        	/*
            String select = "((" + People.DISPLAY_NAME + " NOTNULL) AND ("
                    + People.DISPLAY_NAME + " != '' ))";
            
            return new CursorLoader(getActivity(), PlayaContentProvider.CAMP_URI,
                    CAMP_PROJECTION, select, null, CampTable.COLUMN_NAME + " ASC");
            */
            return new CursorLoader(getActivity(), baseUri,
                    EVENT_PROJECTION, selection, selectionArgs,
                    ordering);
        }

        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            // Swap the new cursor in.  (The framework will take care of closing the
            // old cursor once we return.)
            mAdapter.swapCursor(data);
            
            // If searching, show no camps match query
            if (data.getCount() == 0 && mCurFilter != "" && mCurFilter != null){
            	emptyText.setVisibility(View.VISIBLE);
            	emptyText.setText("These aren't the events you're looking for...");
            }
            else if(data.getCount() == 0){
            	if(limitListToFavorites)
            		emptyText.setText("Select an event, favorite it and see it here.");
            	else
            		emptyText.setText("No Events Found");
            }
            else
            	emptyText.setVisibility(View.GONE);
            // The list should now be shown.
            /*
            if (isResumed()) {
                setListShown(true);
            } else {
                setListShownNoAnimation(true);
            }*/
        }
        
        @Override
        public void onListItemClick (ListView l, View v, int position, long id){
        	String event_id = v.getTag(R.id.list_item_related_model).toString();
        	Cursor result = getActivity().getContentResolver().query((PlayaContentProvider.EVENT_URI.buildUpon().appendPath(event_id).build()), 
        			new String[] {EventTable.COLUMN_NAME, EventTable.COLUMN_DESCRIPTION, 
        						  EventTable.COLUMN_LATITUDE, EventTable.COLUMN_LONGITUDE, 
        						  EventTable.COLUMN_LOCATION, EventTable.COLUMN_HOST_CAMP_NAME,
        						  EventTable.COLUMN_FAVORITE, EventTable.COLUMN_HOST_CAMP_ID},
        			null, 
        			null, 
        			null);
        	if(result.moveToFirst()){
        		View popup = super.getPopupView();
        		
	        	((TextView) popup.findViewById(R.id.popup_title)).setText(result.getString(result.getColumnIndexOrThrow(EventTable.COLUMN_NAME)));
	        	((TextView) popup.findViewById(R.id.popup_contact)).setText(result.getString(result.getColumnIndexOrThrow(EventTable.COLUMN_HOST_CAMP_NAME)));
	        	((TextView) popup.findViewById(R.id.popup_hometown)).setText(result.getString(result.getColumnIndexOrThrow(EventTable.COLUMN_LOCATION)));
	        	((TextView) popup.findViewById(R.id.popup_description)).setText(result.getString(result.getColumnIndexOrThrow(EventTable.COLUMN_DESCRIPTION)));
	        	
	        	if(!result.isNull(result.getColumnIndex(EventTable.COLUMN_HOST_CAMP_NAME))){
	        		((TextView) popup.findViewById(R.id.popup_contact)).setText(result.getString(result.getColumnIndexOrThrow(EventTable.COLUMN_HOST_CAMP_NAME)));
	        		((TextView) popup.findViewById(R.id.popup_contact)).setVisibility(View.VISIBLE);
	        	}
	        	if(FragmentTabsPager.app.embargoClear && !result.isNull(result.getColumnIndex(EventTable.COLUMN_LOCATION))){
	        		((TextView) popup.findViewById(R.id.popup_hometown)).setText(result.getString(result.getColumnIndexOrThrow(EventTable.COLUMN_LOCATION)));
	        		((TextView) popup.findViewById(R.id.popup_hometown)).setVisibility(View.VISIBLE);
	        	}
	        	if(!result.isNull(result.getColumnIndex(EventTable.COLUMN_DESCRIPTION))){
	        		((TextView) popup.findViewById(R.id.popup_description)).setText(result.getString(result.getColumnIndexOrThrow(EventTable.COLUMN_DESCRIPTION)));
	        		((TextView) popup.findViewById(R.id.popup_description)).setVisibility(View.VISIBLE);
	        	}
	        	
	        	View favoriteBtn = popup.findViewById(R.id.favorite_button);
	        	int isFavorite = result.getInt(result.getColumnIndex(EventTable.COLUMN_FAVORITE));
	        	if(isFavorite == 1)
	        		((ImageView)favoriteBtn).setImageResource(android.R.drawable.star_big_on);
	        	else
	        		((ImageView)favoriteBtn).setImageResource(android.R.drawable.star_big_off);
	        	favoriteBtn.setTag(R.id.list_item_related_model, event_id);
	        	favoriteBtn.setTag(R.id.favorite_button_state, isFavorite);
	        	
	        	favoriteBtn.setOnClickListener(new OnClickListener(){

	    			@Override
	    			public void onClick(View v) {
	    				String event_id = v.getTag(R.id.list_item_related_model).toString();
	    				ContentValues values = new ContentValues();
	    				if((Integer)v.getTag(R.id.favorite_button_state) == 0){
	    					values.put(EventTable.COLUMN_FAVORITE, 1);
	    					v.setTag(R.id.favorite_button_state, 1);
	    					((ImageView)v).setImageResource(android.R.drawable.star_big_on);
	    				}
	    				else if((Integer)v.getTag(R.id.favorite_button_state) == 1){
	    					values.put(EventTable.COLUMN_FAVORITE, 0);
	    					v.setTag(R.id.favorite_button_state, 0);
	    					((ImageView)v).setImageResource(android.R.drawable.star_big_off);
	    				}
	    				int result = getActivity().getContentResolver().update(PlayaContentProvider.EVENT_URI.buildUpon().appendPath(event_id).build(), 
	    						values, null, null);
	    				
	    			}
	    			 
	    		 });
	        	PopupWindow pw = new PopupWindow(popup,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT, true);
	        	pw.setBackgroundDrawable(new BitmapDrawable());
	        	pw.showAtLocation(listView, Gravity.CENTER, 0, 0);
	        	result.close();
        	}
        }

        public void onLoaderReset(Loader<Cursor> loader) {
            // This is called when the last Cursor provided to onLoadFinished()
            // above is about to be closed.  We need to make sure we are no
            // longer using it.
            mAdapter.swapCursor(null);
        }
        
        public void setSections(){
        	ContentResolver cr = this.getActivity().getContentResolver();
        	//(int year, int month, int day, int hour, int minute)
        	//GregorianCalendar cal = new GregorianCalendar(2012, 8, 28, 0, 0);
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	Cursor result;
        	for(int x=0; x<8; x++){
        		result = cr.query(PlayaContentProvider.EVENT_URI, new String[]{EventTable.COLUMN_ID}, 
            			EventTable.COLUMN_START_TIME + "<?", new String[]{sdf.format(cal.getTime())}, null);
        		daySections[x] = result.getCount();
        		Log.d("DateSection",sdf.format(cal.getTime()) + " : " + String.valueOf(result.getCount()));
        		cal.add(GregorianCalendar.DAY_OF_YEAR, 1);
        	}
        	sectionsSet = true;
        }
    }

}
