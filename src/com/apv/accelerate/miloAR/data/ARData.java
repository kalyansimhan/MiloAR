package com.apv.accelerate.miloAR.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.location.Location;
import android.util.Log;

import com.apv.accelerate.miloAR.common.Matrix;
import com.apv.accelerate.miloAR.views.Marker;


public abstract class ARData {
    private static final String TAG = "ARData";
	private static final Map<String,Marker> markerList = new ConcurrentHashMap<String,Marker>();
    private static final List<Marker> cache = new CopyOnWriteArrayList<Marker>();
    private static final AtomicBoolean dirty = new AtomicBoolean(false);
    private static final float[] locationArray = new float[3];
    
    /*defaulting to our place*/
    public static final Location hardFix = new Location("ATL");
    static {
        hardFix.setLatitude(37.3393857);
        hardFix.setLongitude(-121.8949555);
        hardFix.setAltitude(1);
    }
    
    private static float radius = 2;
    private static String zoomLevel = null;
    private static int zoomProgress = 0;
    private static Location currentLocation = hardFix;
    private static Matrix rotationMatrix = null;

    public static void setZoomLevel(String zoomLevel) {
    	if (zoomLevel==null) throw new NullPointerException();
    	
        ARData.zoomLevel = zoomLevel;
    }
    
    public static String getZoomLevel() {
        return zoomLevel;
    }
    
    public static void setZoomProgress(int zoomProgress) {
        if (ARData.zoomProgress != zoomProgress) {
            ARData.zoomProgress = zoomProgress;
            if (dirty.compareAndSet(false, true)) {
                cache.clear();
            }
        }
    }
    
    public static int getZoomProgress() {
        return zoomProgress;
    }
    
    public static void setRadius(float radius) {
        ARData.radius = radius;
    }
    
    public static float getRadius() {
        return radius;
    }
    
    public static void setCurrentLocation(Location currentLocation) {
    	if (currentLocation==null) throw new NullPointerException();
    	
    	Log.d(TAG, "current location. location="+currentLocation.toString());
        ARData.currentLocation = currentLocation;
        onLocationChanged(currentLocation);
    }
    
    private static void onLocationChanged(Location location) {
        Log.d(TAG, "New location, updating markers. location="+location.toString());
        for(Marker ma: markerList.values()) {
            ma.calcRelativePosition(location);
        }

        if (dirty.compareAndSet(false, true)) {
            Log.v(TAG, "Setting DIRTY flag!");
            cache.clear();
        }
    }
    
    public static Location getCurrentLocation() {
        return currentLocation;
    }
    
    public static void setRotationMatrix(Matrix rotationMatrix) {
        ARData.rotationMatrix = rotationMatrix;
    }
    
    public static Matrix getRotationMatrix() {
        return rotationMatrix;
    }

    public static void addMarkers(Collection<Marker> markers) {
    	if (markers==null) throw new NullPointerException();

    	markerList.clear();
    	
    	Log.d(TAG, "New markers, updating markers. new markers="+markers.toString());
    	for(Marker marker : markers) {
    	    if (!markerList.containsKey(marker.getName())) {
    	        marker.calcRelativePosition(ARData.getCurrentLocation());
    	        markerList.put(marker.getName(),marker);
    	    }
    	}

    	if (dirty.compareAndSet(false, true)) {
    	    Log.v(TAG, "Setting DIRTY flag!");
    	    cache.clear();
    	}
    }

    public static Collection<Marker> getMarkers() {
        //If markers we added, zero out the altitude to recompute the collision detection
        if (dirty.compareAndSet(true, false)) {
            Log.v(TAG, "DIRTY flag found, resetting all marker heights to zero.");
            for(Marker ma : markerList.values()) {
                ma.getLocation().get(locationArray);
                locationArray[1]=ma.getInitialY();
                ma.getLocation().set(locationArray);
            }

            Log.v(TAG, "Populating the cache.");
            List<Marker> copy = new ArrayList<Marker>();
            copy.addAll(markerList.values());
            Collections.sort(copy,comparator);
            //The cache should be sorted from closest to farthest marker.
            cache.clear();
            cache.addAll(copy);
        }
        return Collections.unmodifiableCollection(cache);
    }
    
    private static final Comparator<Marker> comparator = new Comparator<Marker>() {
        @Override
        public int compare(Marker arg0, Marker arg1) {
            return Double.compare(arg0.getDistance(),arg1.getDistance());
        }
    };
}
