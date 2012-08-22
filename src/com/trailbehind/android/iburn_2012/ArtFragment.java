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

import com.trailbehind.android.iburn_2012.data.ArtTable;
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

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.Contacts.People;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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


    public static class CursorLoaderListFragment extends ListFragment
            implements LoaderManager.LoaderCallbacks<Cursor> {

        // This is the Adapter being used to display the list's data.
        SimpleCursorAdapter mAdapter;
        
        // TextView to display when no ListView items are present
        private TextView emptyText;

        // If non-null, this is the current filter the user has provided.
        String mCurFilter;
        
        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        	View v = inflater.inflate(R.layout.listview, null);
        	emptyText = (TextView) v.findViewById(android.R.id.empty);
        	return v;
        }

        @Override public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Text to display before ListView is populated
            emptyText.setText("Loading Art");

            // We have a menu item to show in action bar.
            setHasOptionsMenu(true);

            // Create an empty adapter we will use to display the loaded data.
            mAdapter = new SimpleCursorAdapter(getActivity(),
                    android.R.layout.simple_list_item_1, null,
                    new String[] { ArtTable.COLUMN_NAME },
                    new int[] { android.R.id.text1}, 0);
            setListAdapter(mAdapter);

            // Start out with a progress indicator.
            //setListShown(false);

            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one.
            getLoaderManager().initLoader(0, null, this);
        }

        @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Place an action bar item for searching.
            MenuItem item = menu.add("Search");
            item.setIcon(android.R.drawable.ic_menu_search);
            MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS
                    | MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
            View searchView = SearchViewCompat.newSearchView(getActivity());
            if (searchView != null) {
                SearchViewCompat.setOnQueryTextListener(searchView,
                        new OnQueryTextListenerCompat() {
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        // Called when the action bar search text has changed.  Update
                        // the search filter, and restart the loader to do a new query
                        // with this filter.
                        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
                        // Don't do anything if the filter hasn't actually changed.
                        // Prevents restarting the loader when restoring state.
                        if (mCurFilter == null && newFilter == null) {
                            return true;
                        }
                        if (mCurFilter != null && mCurFilter.equals(newFilter)) {
                            return true;
                        }
                        mCurFilter = newFilter;
                        getLoaderManager().restartLoader(0, null, CursorLoaderListFragment.this);
                        return true;
                    }
                });
                MenuItemCompat.setActionView(item, searchView);
            }
        }

        @Override public void onListItemClick(ListView l, View v, int position, long id) {
            // Insert desired behavior here.
            Log.i("FragmentComplexList", "Item clicked: " + id);
        }

        // These are the Camp rows that we will retrieve.
        static final String[] CAMP_PROJECTION = new String[] {
            ArtTable.COLUMN_ID,
            ArtTable.COLUMN_NAME,
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

        public void onLoaderReset(Loader<Cursor> loader) {
            // This is called when the last Cursor provided to onLoadFinished()
            // above is about to be closed.  We need to make sure we are no
            // longer using it.
            mAdapter.swapCursor(null);
        }
    }

}
