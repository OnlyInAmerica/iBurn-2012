package pro.dbro.iburn_2012;

import pro.dbro.iburn_2012.CampFragment.CursorLoaderListFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v4.widget.SearchViewCompat;
import android.support.v4.widget.SearchViewCompat.OnQueryTextListenerCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * This class handles actionbar / menuItem (pre SDK 11) creation and click listening
 * for generic name searching
 * @author davidbrodsky
 *
 */
public abstract class PlayaListFragmentBase extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{
	// Search by name string
	String mCurFilter;
	// Limit display to favorites
	boolean limitListToFavorites = false;

	LinearLayout listContainer;
	
	protected TextView emptyText;
    
    protected ListView listView;
    
    protected View popupView;
    
	@Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
    	View v = inflater.inflate(R.layout.listview, null);
    	listContainer = (LinearLayout) v.findViewById(R.id.list_container);
    	emptyText = (TextView) v.findViewById(android.R.id.empty);
    	listView = (ListView) v.findViewById(android.R.id.list);
    	return v;
    }
	
	 @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
         // Place an action bar item for searching.
         MenuItem searchItem = menu.add(Menu.NONE, R.id.menu_search, Menu.NONE, "Search");
         searchItem.setIcon(android.R.drawable.ic_menu_search);
         if(!FragmentTabsPager.app.embargoClear){
        	 MenuItem unlockItem = menu.add(Menu.NONE, R.id.menu_unlock, Menu.NONE, "Unlock");
        	 unlockItem.setIcon(R.drawable.unlock);
        	 unlockItem.setOnMenuItemClickListener(new OnMenuItemClickListener(){

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					if(!FragmentTabsPager.app.embargoClear){
						AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

						alert.setTitle("Enter Unlock Password");

						// Set an EditText view to get user input 
						final EditText input = new EditText(getActivity());
						input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
						alert.setView(input);
						alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								String value = input.getText().toString();
							  	if(value.compareTo(FragmentTabsPager.app.unlockPassword)==0){
							  		FragmentTabsPager.app.setEmbargoClear(true);
							  		//FragmentTabsPager.app.embargoClear = true;
							  		restartLoader();
							  	}
							  	else{
								  	dialog.cancel();
								  	AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
									alert.setTitle("Invalid Password");
									alert.show();
							  	}
							  }
							});

							alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							  public void onClick(DialogInterface dialog, int whichButton) {
							  }
							});
							
							alert.show();
						//FragmentTabsPager.app.embargoClear = true;
					}
					return false;
				}
        		 
        	 });
         }
         MenuItem favItem = menu.add(Menu.NONE, R.id.menu_favorite, Menu.NONE, "Favorites");
         MenuItemCompat.setShowAsAction(favItem, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
         favItem.setIcon(android.R.drawable.star_big_off);
         favItem.setOnMenuItemClickListener(new OnMenuItemClickListener(){

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if(!limitListToFavorites){
					item.setIcon(android.R.drawable.star_big_on);
					limitListToFavorites = true;
					restartLoader();
				} else {
					item.setIcon(android.R.drawable.star_big_off);
					limitListToFavorites = false;
					restartLoader();
				}
				return false;
			}
        	 
         });
         // Pre-Honeycomb, actionviews do not show!
         if(Build.VERSION.SDK_INT > 11){
         	MenuItemCompat.setShowAsAction(searchItem, MenuItemCompat.SHOW_AS_ACTION_ALWAYS
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
	                MenuItemCompat.setActionView(searchItem, searchView);
	            }
         }
         // PRE-HONEYCOMB Behavior
         else{
        	 searchItem.setOnMenuItemClickListener(new OnMenuItemClickListener(){

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
     	// Set favorite menu state on load
     	if(limitListToFavorites){
			MenuItem item = menu.findItem(R.id.menu_favorite);
     		item.setIcon(android.R.drawable.star_big_on);
		}
     	if(FragmentTabsPager.app.embargoClear && menu.findItem(R.id.menu_unlock) != null){
     		menu.removeItem(R.id.menu_unlock);
     		
     	}
     
     }
	 
	 // Child classes override this to hookup
	 public void restartLoader(){
		 
	 }

	
	 // Get popUpWindow instance
	protected View getPopupView(){
		if(popupView == null){
			LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE); 
			 popupView = layoutInflater.inflate(R.layout.popup, null); 
		}

		return popupView;

	}


}
