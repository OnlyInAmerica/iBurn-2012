package com.trailbehind.android.iburn_2012;

import com.trailbehind.android.iburn_2012.CampFragment.CursorLoaderListFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SearchViewCompat;
import android.support.v4.widget.SearchViewCompat.OnQueryTextListenerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.EditText;

/**
 * This class handles actionbar / menuItem (pre SDK 11) creation and click listening
 * for generic name searching
 * @author davidbrodsky
 *
 */
public abstract class PlayaListFragmentBase extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{
	// Search by name string
	String mCurFilter;
	
	 @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
         // Place an action bar item for searching.
         MenuItem item = menu.add("Search");
         item.setIcon(android.R.drawable.ic_menu_search);
         
         // Pre-Honeycomb, actionviews do not show!
         if(Build.VERSION.SDK_INT > 11){
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
	                        restartLoader();
	                        //getLoaderManager().restartLoader(0, null, (LoaderCallbacks) PlayaListFragmentBase.this);
	                        return true;
	                    }
	                });
	                MenuItemCompat.setActionView(item, searchView);
	            }
         }
         // PRE-HONEYCOMB Behavior
         else{
         	item.setOnMenuItemClickListener(new OnMenuItemClickListener(){

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

						alert.setTitle("Search");

						// Set an EditText view to get user input 
						final EditText input = new EditText(getActivity());
						alert.setView(input);
						alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								String value = input.getText().toString();
							  	mCurFilter = value;
							  	//Log.d("ArtFragment","Abstract class: Restart Loader");
							  	restartLoader();
							  	//getLoaderManager().restartLoader(0, null, (LoaderCallbacks) PlayaListFragmentBase.this);
							  }
							});

							alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							  public void onClick(DialogInterface dialog, int whichButton) {
							    // Canceled.
							  }
							});
							
							alert.show();
						
						return true;
					}
         		
         	});
         }
     } // end OnCreateOptionsMenu
     
	 @Override
     public void onPrepareOptionsMenu(Menu menu) {
		 /*
		 if(mCurFilter == null)
			 Log.d("onPrepareOptionsMenu"," " + mCurFilter);
		 else
			 Log.d("onPrepareOptionsMenu"," null");
		 */
     	// If a search filter is applied, allow clearing 
     	// search filter for pre-honeycomb devices
     	
     	if((mCurFilter != "" && mCurFilter != null) && menu.findItem(R.id.menu_show_all) == null){
     		MenuItem item = menu.add(Menu.NONE, R.id.menu_show_all, Menu.NONE, "Show All");
             item.setIcon(android.R.drawable.ic_menu_more);
             item.setOnMenuItemClickListener(new OnMenuItemClickListener(){

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						mCurFilter = null;
						restartLoader();
						//getLoaderManager().restartLoader(0, null, (LoaderCallbacks) PlayaListFragmentBase.this);
						return false;
					}
             	
             });
     	}
     	// If no search filter applied, remove show_all menu
     	else if(!(mCurFilter != "" && mCurFilter != null) && menu.findItem(R.id.menu_show_all) != null){
     		menu.removeItem(R.id.menu_show_all);
     	}
     
     }
	 
	 // Child classes override this to hookup
	 public void restartLoader(){
		 
	 }

}
