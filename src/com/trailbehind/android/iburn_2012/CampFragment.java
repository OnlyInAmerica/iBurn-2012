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

package com.trailbehind.android.iburn_2012;

import com.trailbehind.android.iburn_2012.ArtFragment.CursorLoaderListFragment;
import com.trailbehind.android.iburn_2012.data.CampTable;
import com.trailbehind.android.iburn_2012.data.EventTable;
import com.trailbehind.android.iburn_2012.data.PlayaContentProvider;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SearchViewCompat;
import android.support.v4.widget.SearchViewCompat.OnQueryTextListenerCompat;
import android.support.v4.widget.SimpleCursorAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * Demonstration of the use of a CursorLoader to load and display contacts
 * data in a fragment.
 */
@SuppressWarnings("all")
public class CampFragment extends FragmentActivity {
	private static Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = this.getBaseContext();
        FragmentManager fm = getSupportFragmentManager();

        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            CursorLoaderListFragment list = new CursorLoaderListFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }


    public static class CursorLoaderListFragment extends PlayaListFragmentBase implements LoaderManager.LoaderCallbacks<Cursor> {

        // This is the Adapter being used to display the list's data.
        SimpleCursorAdapter mAdapter;
        
        // TextView to display when no ListView items are present
        

        @Override
        public void restartLoader(){
        	getLoaderManager().restartLoader(0, null, CursorLoaderListFragment.this);
    	 }

        @Override public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Text to display before ListView is populated
            emptyText.setText("Loading Camps");

            // We have a menu item to show in action bar.
            setHasOptionsMenu(true);

            // Create an empty adapter we will use to display the loaded data.
            /*
            mAdapter = new SimpleCursorAdapter(getActivity(),
                    android.R.layout.simple_list_item_1, null,
                    new String[] { CampTable.COLUMN_NAME },
                    new int[] { android.R.id.text1}, 0);
            */
            mAdapter = new CampCursorAdapter(getActivity(), null);
            setListAdapter(mAdapter);

            // Start out with a progress indicator.
            //setListShown(false);

            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one.
            getLoaderManager().initLoader(0, null, this);
        }

        // These are the Camp rows that we will retrieve.
        static final String[] CAMP_PROJECTION = new String[] {
            CampTable.COLUMN_ID,
            CampTable.COLUMN_NAME,
            CampTable.COLUMN_LOCATION,
        };

        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // This is called when a new Loader needs to be created.  This
            // sample only has one Loader, so we don't care about the ID.
            // First, pick the base URI to use depending on whether we are
            // currently filtering.
            
        	Uri baseUri;
            String ordering = null;
            if (mCurFilter != null) {
                baseUri = Uri.withAppendedPath(PlayaContentProvider.CAMP_SEARCH_URI, Uri.encode(mCurFilter));
            } else {
                baseUri = PlayaContentProvider.CAMP_URI;
                ordering = CampTable.COLUMN_NAME + " ASC";
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
                    CAMP_PROJECTION, null, null,
                    ordering);
        }

        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            // Swap the new cursor in.  (The framework will take care of closing the
            // old cursor once we return.)
            mAdapter.swapCursor(data);
            
            // If searching, show no camps match query
            if (data.getCount() == 0 && mCurFilter != "" && mCurFilter != null){
            	emptyText.setVisibility(View.VISIBLE);
            	emptyText.setText("These aren't the camps you're looking for...");
            }
            else if(data.getCount() == 0)
            	emptyText.setText("No Camps Found");
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
        	
        	String camp_id = v.getTag(R.id.list_item_related_model).toString();
        	Cursor result = getActivity().getContentResolver().query((PlayaContentProvider.CAMP_URI.buildUpon().appendPath(camp_id).build()), 
        			new String[] {CampTable.COLUMN_NAME, CampTable.COLUMN_DESCRIPTION, 
        						  CampTable.COLUMN_LATITUDE, CampTable.COLUMN_LONGITUDE, 
        						  CampTable.COLUMN_LOCATION, CampTable.COLUMN_CONTACT,
        						  CampTable.COLUMN_HOMETOWN},
        			null, 
        			null, 
        			null);
        	if(result.moveToFirst()){
        		View popup = super.getPopupView();
        		
	        	((TextView) popup.findViewById(R.id.popup_title)).setText(result.getString(result.getColumnIndexOrThrow(CampTable.COLUMN_NAME)));
	        	((TextView) popup.findViewById(R.id.popup_contact)).setText(result.getString(result.getColumnIndexOrThrow(CampTable.COLUMN_CONTACT)));
	        	((TextView) popup.findViewById(R.id.popup_hometown)).setText(result.getString(result.getColumnIndexOrThrow(CampTable.COLUMN_HOMETOWN)));
	        	((TextView) popup.findViewById(R.id.popup_description)).setText(result.getString(result.getColumnIndexOrThrow(CampTable.COLUMN_DESCRIPTION)));
	        	
	        	PopupWindow pw = new PopupWindow(popup,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT, true);
	        	pw.setBackgroundDrawable(new BitmapDrawable());
	        	pw.showAtLocation(listView, Gravity.CENTER, 0, 0);
        	}
        }

        public void onLoaderReset(Loader<Cursor> loader) {
            // This is called when the last Cursor provided to onLoadFinished()
            // above is about to be closed.  We need to make sure we are no
            // longer using it.
            mAdapter.swapCursor(null);
        }
    }

}
