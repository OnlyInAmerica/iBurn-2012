package pro.dbro.iburn_2012;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class PlayaViewPager extends ViewPager {
	private float lastX, maxX = 0;
	// threshold in pixels from screen border to allow scroll in mapview
	private static final float cutoff_threshold = 20; 
	
	// Different scroll behavior on map view
	private boolean onMap = false;
	
	
    public PlayaViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(FragmentTabsPager.display_width != -1)
        	maxX =FragmentTabsPager.display_width - cutoff_threshold;
        	//maxX = InputDevice.getMotionRange(MotionEvent.AXIS_X).getMax() - cutoff_threshold ;
        //InputDevice.getDevice(id)
        Log.d("ViewPager-Maxwidth:",String.valueOf(this.getMeasuredWidth()));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       return super.onTouchEvent(event);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
    	if(onMap){
	    	switch (event.getAction()) {
	        case MotionEvent.ACTION_DOWN:
	            lastX = event.getX();
	            //Log.d("DOWN","X: " + String.valueOf(lastX) + " Y: " + String.valueOf(lastY));
	            break;
	        case MotionEvent.ACTION_MOVE:
	            if(lastX < maxX)
	                return false;
    	}
    }
    	return super.onInterceptTouchEvent(event);
    }
    
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    	if(positionOffset == 0){
    		if(position == 0)
    			onMap = true;
    		else
    			onMap = false;
    	}
    	super.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

}
