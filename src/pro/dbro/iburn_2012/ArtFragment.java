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

import pro.dbro.iburn_2012.CampFragment.CursorLoaderListFragment;
import pro.dbro.iburn_2012.data.ArtTable;
import pro.dbro.iburn_2012.data.CampTable;
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

import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * Demonstration of the use of a CursorLoader to load and display contacts
 * data in a fragment.
 */
@SuppressWarnings("all")
public class ArtFragment extends FragmentActivity {

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


    public static class CursorLoaderListFragment extends PlayaListFragmentBase implements LoaderManager.LoaderCallbacks<Cursor> {

        // This is the Adapter being used to display the list's data.
        ArtCursorAdapter mAdapter;
        LoaderManager lm;
        
        @Override
        public void restartLoader(){
        	getLoaderManager().restartLoader(0, null, CursorLoaderListFragment.this);
    	 }
        
        public void initLoader(){
        	getLoaderManager().initLoader(0, null, this);
        }
        

        @Override public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Log.d("ArtFragment","OnActivityCreated");
            // Text to display before ListView is populated
            emptyText.setText("Loading Art");
            lm = this.getLoaderManager();
            // We have a menu item to show in action bar.
            setHasOptionsMenu(true);
            // Start out with a progress indicator.
            //setListShown(false);
            /*
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(dbReadyReceiver,
            	      new IntentFilter("dbReady"));
            */
            
            // if the db is ready AND the adapter hasn't all ready been set
            if(FragmentTabsPager.app.dbReady && mAdapter == null){
            	mAdapter = new ArtCursorAdapter(getActivity(), null);
            	initLoader();
            	//getLoaderManager().initLoader(0, null, this);
                setListAdapter(mAdapter);
        	}
	    	
            
            ListView lv = getListView();
            lv.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
            lv.setFastScrollEnabled(true);
        }
        
        private BroadcastReceiver dbReadyReceiver = new BroadcastReceiver() {
        	  @Override
        	  public void onReceive(Context context, Intent intent) {
        	    // 1 -- success, 0 -- error, -1 no data
        	    int status = intent.getIntExtra("status", -1);
        	    if(status == 1){
        	    	mAdapter = new ArtCursorAdapter(getActivity(), null);
                    initLoader();
        	    	//getLoaderManager().initLoader(0, null, this);
                    setListAdapter(mAdapter);
        	    	
        	    }
        	  }
        	};

        // These are the Camp rows that we will retrieve.
        static final String[] ART_PROJECTION = new String[] {
            ArtTable.COLUMN_ID,
            ArtTable.COLUMN_NAME,
            ArtTable.COLUMN_ARTIST,
            ArtTable.COLUMN_FAVORITE
        };

        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // This is called when a new Loader needs to be created.  This
            // sample only has one Loader, so we don't care about the ID.
            // First, pick the base URI to use depending on whether we are
            // currently filtering.
            
        	Uri baseUri;
            String ordering = null;
            if (mCurFilter != null) {
                baseUri = Uri.withAppendedPath(PlayaContentProvider.ART_SEARCH_URI, Uri.encode(mCurFilter));
            } else {
                baseUri = PlayaContentProvider.ART_URI;
                ordering = ArtTable.COLUMN_NAME + " ASC";
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
                    ART_PROJECTION, selection, selectionArgs,
                    ordering);
        }

        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            // Swap the new cursor in.  (The framework will take care of closing the
            // old cursor once we return.)
            mAdapter.swapCursor(data);
            
            // If searching, show no camps match query
            if (data.getCount() == 0 && mCurFilter != "" && mCurFilter != null){
            	emptyText.setVisibility(View.VISIBLE);
            	emptyText.setText("These aren't the arts you're looking for...");
            }
            else if(data.getCount() == 0)
            	if(limitListToFavorites)
            		emptyText.setText("Select some art, favorite it and see it here.");
            	else
            		emptyText.setText("No Art Found");
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

        public void onLoaderReset(Loader<Cursor> loader) {
            // This is called when the last Cursor provided to onLoadFinished()
            // above is about to be closed.  We need to make sure we are no
            // longer using it.
            mAdapter.swapCursor(null);
        }
        
        @Override
        public void onListItemClick (ListView l, View v, int position, long id){
        	super.onListItemClick(l, v, position, id);
        	String art_id = v.getTag(R.id.list_item_related_model).toString();
        	Cursor result = getActivity().getContentResolver().query((PlayaContentProvider.ART_URI.buildUpon().appendPath(art_id).build()), 
        			new String[] {ArtTable.COLUMN_NAME, ArtTable.COLUMN_DESCRIPTION, 
        						  ArtTable.COLUMN_LATITUDE, ArtTable.COLUMN_LONGITUDE, 
        						  ArtTable.COLUMN_TIME_ADDRESS, ArtTable.COLUMN_CONTACT,
        						  ArtTable.COLUMN_ARTIST, ArtTable.COLUMN_ARTIST_LOCATION,
        						  ArtTable.COLUMN_FAVORITE},
        			null, 
        			null, 
        			null);
        	if(result.moveToFirst()){
        		View popup = super.getPopupView();
	        	((TextView) popup.findViewById(R.id.popup_title)).setText(result.getString(result.getColumnIndexOrThrow(ArtTable.COLUMN_NAME)));
	        	
	        	if(!result.isNull(result.getColumnIndex(ArtTable.COLUMN_ARTIST))){
	        		((TextView) popup.findViewById(R.id.popup_artist)).setText(result.getString(result.getColumnIndexOrThrow(ArtTable.COLUMN_ARTIST)));
	        		((TextView) popup.findViewById(R.id.popup_artist)).setVisibility(View.VISIBLE);
	        	}
	        	if(!result.isNull(result.getColumnIndex(ArtTable.COLUMN_CONTACT))){
	        		((TextView) popup.findViewById(R.id.popup_contact)).setText(result.getString(result.getColumnIndexOrThrow(ArtTable.COLUMN_CONTACT)));
	        		((TextView) popup.findViewById(R.id.popup_contact)).setVisibility(View.VISIBLE);
	        	}
	        	if(!result.isNull(result.getColumnIndex(ArtTable.COLUMN_ARTIST_LOCATION))){
	        		((TextView) popup.findViewById(R.id.popup_hometown)).setText(result.getString(result.getColumnIndexOrThrow(ArtTable.COLUMN_ARTIST_LOCATION)));
	        		((TextView) popup.findViewById(R.id.popup_hometown)).setVisibility(View.VISIBLE);
	        	}
	        	if(!result.isNull(result.getColumnIndex(ArtTable.COLUMN_DESCRIPTION))){
	        		((TextView) popup.findViewById(R.id.popup_description)).setText(result.getString(result.getColumnIndexOrThrow(ArtTable.COLUMN_DESCRIPTION)));
	        		((TextView) popup.findViewById(R.id.popup_description)).setVisibility(View.VISIBLE);
	        	}
	        	//((TextView) popup.findViewById(R.id.popup_description)).setText(result.getString(result.getColumnIndexOrThrow(ArtTable.COLUMN_DESCRIPTION)));
	        	
	        	View favoriteBtn = popup.findViewById(R.id.favorite_button);
	        	int isFavorite = result.getInt(result.getColumnIndex(ArtTable.COLUMN_FAVORITE));
	        	if(isFavorite == 1)
	        		((ImageView)favoriteBtn).setImageResource(android.R.drawable.star_big_on);
	        	else
	        		((ImageView)favoriteBtn).setImageResource(android.R.drawable.star_big_off);
	        	favoriteBtn.setTag(R.id.list_item_related_model, art_id);
	        	favoriteBtn.setTag(R.id.favorite_button_state, isFavorite);
	        	
	        	favoriteBtn.setOnClickListener(new OnClickListener(){

	    			@Override
	    			public void onClick(View v) {
	    				String art_id = v.getTag(R.id.list_item_related_model).toString();
	    				ContentValues values = new ContentValues();
	    				if((Integer)v.getTag(R.id.favorite_button_state) == 0){
	    					values.put(ArtTable.COLUMN_FAVORITE, 1);
	    					v.setTag(R.id.favorite_button_state, 1);
	    					((ImageView)v).setImageResource(android.R.drawable.star_big_on);
	    				}
	    				else if((Integer)v.getTag(R.id.favorite_button_state) == 1){
	    					values.put(ArtTable.COLUMN_FAVORITE, 0);
	    					v.setTag(R.id.favorite_button_state, 0);
	    					((ImageView)v).setImageResource(android.R.drawable.star_big_off);
	    				}
	    				int result = getActivity().getContentResolver().update(PlayaContentProvider.ART_URI.buildUpon().appendPath(art_id).build(), 
	    						values, null, null);
	    				
	    			}
	    			 
	    		 });
	        	
	        	PopupWindow pw = new PopupWindow(popup,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT, true);
	        	pw.setBackgroundDrawable(new BitmapDrawable());
	        	pw.showAtLocation(listView, Gravity.CENTER, 0, 0);
	        	result.close();
        	}
        }
    }
   

}
