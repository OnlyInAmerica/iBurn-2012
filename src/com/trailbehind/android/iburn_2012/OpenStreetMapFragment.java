package com.trailbehind.android.iburn_2012;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.modules.MapTileAssetProvider;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.tilesource.BitmapAssetTileSource;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.BoundedMapView;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.TilesOverlay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.trailbehind.android.iburn_2012.data.CampTable;
import com.trailbehind.android.iburn_2012.data.JSONDeserializers;
import com.trailbehind.android.iburn_2012.data.PlayaContentProvider;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

/**
* Based on osmdroid default map view activity.
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
* 
* Default map view activity.
* @author Manuel Stahl
*
*/

public class OpenStreetMapFragment extends Fragment {
    /** Called when the activity is first created. */
    private MapController mapController;
    private BoundedMapView mapView;
    
    public static View container;
    //private MapView mapView;
    
    private MyLocationOverlay mLocationOverlay;
	private ResourceProxy mResourceProxy;
    
    // Map bounds
    private final GeoPoint northEast = new GeoPoint(40802822, -119172673);
	private final GeoPoint southWest = new GeoPoint(40759210, -119234540);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        setContentView(R.layout.map);
        mapView = (MapView) findViewById(R.id.mapview);
    	mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        mapController.setZoom(15);
        GeoPoint point2 = new GeoPoint(51496994, -134733);
        mapController.setCenter(point2);
		*/
        
    }
   
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
    	
    	// http://iburn.s3.amazonaws.com/2012
    	final MapTileProviderBasic tileProvider = new MapTileProviderBasic(FragmentTabsPager.app);
		//final XYTileSource tileSource = new XYTileSource("burn", null, 5, 18, 256, ".png",
		//                    "http://iburn.s3.amazonaws.com/2012/");
		final BitmapAssetTileSource tileSource = new BitmapAssetTileSource("burn", null, 5, 18, 256, "");
		tileSource.isSourceTMS = false;
		AssetManager assets = FragmentTabsPager.app.getAssets();
		//MapTileModuleProviderBase[] tileProviderArray = {new MapTileAssetProvider(assets, tileSource, 0)};
		//MapAssetTileProviderArray providerArray = new MapAssetTileProviderArray(tileSource, null, tileProviderArray);
		
		MapTileModuleProviderBase[] myProviders = new MapTileModuleProviderBase[1];

        //myProviders[0] = new MapTileFileArchiveProvider(new SimpleRegisterReceiver(getApplicationContext()), myTiles, myArchives);

        myProviders[0] =  new MapTileAssetProvider(assets, tileSource, 0);

        //myProviders[0] =  new MapTileDownloader(TileSourceFactory.MAPNIK);

        MapTileProviderArray MyTileProvider = new MapTileProviderArray(tileSource, null, myProviders);

        TilesOverlay MyTilesOverlay = new TilesOverlay(MyTileProvider, FragmentTabsPager.app);
		// MapView Bounds:
        // .northeast = {.latitude = 40.802822, .longitude = -119.172673}, 
        //.southwest = {.latitude = 40.759210, .longitude = -119.23454}})
        // Bounds offset Lat .006720
        
        

		//MapTileAssetProvider mtap = new MapTileAssetProvider(assets, tileSource, 0);
    	View view = inflater.inflate(R.layout.map, null);
    	this.container = (View) view.findViewById(R.id.mapview);
    	mapView = (BoundedMapView) view.findViewById(R.id.mapview);
    	
    	mResourceProxy = new ResourceProxyImpl(FragmentTabsPager.app);
        mLocationOverlay = new MyLocationOverlay(FragmentTabsPager.app, mapView,
				mResourceProxy);
		mapView.getOverlays().add(this.mLocationOverlay);
		//mapView.setMultiTouchControls(true);
		
		//ItemizedIconOverlay<OverlayItem> itemOverlay = new ItemizedIconOverlay<OverlayItem>(generateOverlayItems(),
        ItemizedOverlayWithFocus<OverlayItem> itemOverlay = new ItemizedOverlayWithFocus<OverlayItem>(generateOverlayItems(),   
		new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                        @Override
                        public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        	LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE); 
               			 	View popup = layoutInflater.inflate(R.layout.map_item_popup, null); 
                        	PopupWindow pw = new PopupWindow(popup,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT, true);
            	        	pw.setBackgroundDrawable(new BitmapDrawable());
            	        	pw.showAtLocation(OpenStreetMapFragment.container, Gravity.CENTER, 0, 0);
            	        	
                                return false; // We 'handled' this event.
                        }

                        @Override
                        public boolean onItemLongPress(final int index, final OverlayItem item) {
                                return false;
                        }
                }, mResourceProxy);
		
		
    	
    	//mapView = (MapView) view.findViewById(R.id.mapview);
    	BoundingBoxE6 bounds = new BoundingBoxE6(northEast, southWest);
    	mapView.setScrollableAreaLimit(bounds);
    	mapView.getOverlays().add(MyTilesOverlay);
    	mapView.getOverlays().add(itemOverlay);
    	//mapView.setTileSource(tileSource);
        mapView.setBuiltInZoomControls(true);
        mapView.setUseDataConnection(false);
        mapController = mapView.getController();
        mapController.setZoom(15);
        GeoPoint point2 = new GeoPoint(40.782818, -119.209042);
        mapController.setCenter(point2);
    	
    	return view;
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	mLocationOverlay.enableMyLocation();
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	mLocationOverlay.disableMyLocation();
    }
    
 
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }
    
    public static ArrayList<OverlayItem> generateOverlayItems(){
    	
		final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		String[] projection = new String[]{CampTable.COLUMN_NAME, CampTable.COLUMN_LATITUDE, CampTable.COLUMN_LONGITUDE, CampTable.COLUMN_HOMETOWN};
		Cursor camps = FragmentTabsPager.app.getContentResolver().query(PlayaContentProvider.CAMP_URI, projection, null, null, null);
        if(camps.moveToFirst()){
        	do{
        		if(!camps.isNull(camps.getColumnIndex(CampTable.COLUMN_LATITUDE))){
	        		items.add(new OverlayItem(camps.getString(camps.getColumnIndex(CampTable.COLUMN_NAME)),
	        				 "test", new GeoPoint(camps.getDouble(camps.getColumnIndex(CampTable.COLUMN_LATITUDE)),
	        						 camps.getDouble(camps.getColumnIndex(CampTable.COLUMN_LONGITUDE))))); 
	        		//Log.d("Camp Location added", String.valueOf(camps.getDouble(camps.getColumnIndex(CampTable.COLUMN_LATITUDE))) + " : " + camps.getDouble(camps.getColumnIndex(CampTable.COLUMN_LONGITUDE)));
        		}
        	}while(camps.moveToNext());
        }

        return items;
    }
    
    public static class loadOverlayItems extends AsyncTask<Void, Void, ArrayList<OverlayItem>>{
		// This method is executed in a separate thread
		@Override
		protected ArrayList<OverlayItem> doInBackground(Void... input) {
			return generateOverlayItems();
		}
		
		@Override
	    protected void onPostExecute(ArrayList<OverlayItem> result) {
			
			super.onPostExecute(result);

	    }
		
	}
}   