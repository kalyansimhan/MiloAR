package com.apv.accelerate.miloAR.activities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import com.apv.accelerate.miloAR.data.ARData;
import com.apv.accelerate.miloAR.data.MiloDataSource;
import com.apv.accelerate.miloAR.data.NetworkDataSource;
import com.apv.accelerate.miloAR.views.Marker;


/**
 *  Augmented Reality Screen
 *  This is a custom implementation for Milo
 */
public class MiloAugmentedReality extends AugmentedReality {
    private static final String locale = Locale.getDefault().getLanguage();
	
	private static Collection<NetworkDataSource> sources = null;    
    private static Thread thread = null;
    
    private String searchQuery = "";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (sources==null) {
        	sources = new ArrayList<NetworkDataSource>();
                
            NetworkDataSource milo = new MiloDataSource(this.getResources());
            sources.add(milo);
        }
        searchQuery = getIntent().getExtras().getString("SEARCH_QUERY");
        searchQuery = searchQuery.trim();
        if(MiloDataSource.merchantDetails.size()!=0)
                MiloDataSource.merchantDetails.clear();
    }

	@Override
    public void onStart() {
        super.onStart();
        Location last = ARData.getCurrentLocation();
        updateData(last.getLatitude(),last.getLongitude(),last.getAltitude());
    }

	@Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        
        updateData(location.getLatitude(),location.getLongitude(),location.getAltitude());
    }

	@Override
	protected void markerTouched(Marker marker) {
        System.out.println(marker.getName());
        MerchantDialog dialog = new MerchantDialog(this, marker.getName());
        dialog.setCancelable(true);
        dialog.show();
	}

    private void updateData(final double lat, final double lon, final double alt) {
    	if (thread!=null && thread.isAlive()) return;
    	
    	thread = new Thread(
    		new Runnable(){
				@Override
				public void run() {
					for (NetworkDataSource source : sources) {
						download(source, lat, lon, alt,searchQuery);
					}
				}
			}
    	);
    	thread.start();
    }
    
    private static boolean download(NetworkDataSource source, double lat, double lon, double alt, String searchQuery) {
		if (source==null) return false;
		
		String url = null;
		try {
			url = source.createRequestURL(lat, lon, alt, ARData.getRadius(), searchQuery);    	
		} catch (NullPointerException e) {
			return false;
		}
    	
		List<Marker> markers = null;
		try {
			if(source instanceof MiloDataSource) {
				String response = source.parse_1(url);
				System.out.println(response);
				markers = source.parse(response);
			}
			else{
				markers = source.parse(url);
			}
		} catch (NullPointerException e) {
			return false;
		}

		
    	ARData.addMarkers(markers);
    	return true;
    }
}
