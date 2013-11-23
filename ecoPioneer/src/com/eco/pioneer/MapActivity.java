package com.eco.pioneer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity {

	  static final LatLng NORTH_MELBOURNE = new LatLng(-37.796, 144.937);
	  static final LatLng VICMARKET = new LatLng(-37.805, 144.956);
	  
	  private GoogleMap map;

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_map);
	    
	    if (map == null) {
		    map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
			        .getMap();	    	
	    }
	    
	    Marker north_melbourne = map.addMarker(new MarkerOptions().position(NORTH_MELBOURNE)
	        .title("North Melbourne"));

//        .icon(BitmapDescriptorFactory
//	            .fromResource(R.drawable.ic_launcher)) 
	            
	    Marker vic_market = map.addMarker(new MarkerOptions()
	        .position(VICMARKET)
	        .title("Collingwood")
	        .snippet("You are master"));
	    vic_market.showInfoWindow();
	    

	    // Move the camera instantly to hamburg with a zoom of 15.
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(NORTH_MELBOURNE, 15));

	    // Zoom in, animating the camera.
	    map.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
	  }

	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
//	    getMenuInflater().inflate(R.menu.activity_main, menu);
	    return true;
	  }

    
}
