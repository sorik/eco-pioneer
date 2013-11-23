package com.eco.pioneer;

import java.util.ArrayList;

import org.w3c.dom.Document;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.eco.pioneer.service.EcoRoute;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


public class MapActivity extends Activity {

	  static final LatLng NORTH_MELBOURNE = new LatLng(-37.796, 144.937);
	  static final LatLng VICMARKET = new LatLng(-37.805, 144.956);
	  static final LatLng CENTER = new LatLng(-37.800392,144.944852);
	  
	  private GoogleMap map;

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Log.e("Map", "onCreated");
	    setContentView(R.layout.activity_map);
		  
	    showMap();

	  }

	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
//	    getMenuInflater().inflate(R.menu.activity_main, menu);
	    return true;
	  }
	  
	  private void showMap() {
		  map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				  .getMap();	    	
		  Marker north_melbourne = map.addMarker(new MarkerOptions().position(NORTH_MELBOURNE)
				  .title("North Melbourne"));
		  north_melbourne.showInfoWindow();

		  Marker vic_market = map.addMarker(new MarkerOptions()
		  .position(VICMARKET)
		  .title("Vic Market")
		  .snippet("You become a pioneer"));
		  vic_market.showInfoWindow();


		  // Move the camera instantly to hamburg with a zoom of 15.
		  map.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTER, 14));

		  // Zoom in, animating the camera.
		  map.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);	
		  
		  new RouteTask().execute(NORTH_MELBOURNE, VICMARKET);
		  
		  TextView result = (TextView)findViewById(R.id.map_result);
		  
		  result.setText("Your eco score is " + MainActivity.ecoService.getScore());
		  
	  }	
	  
		private class RouteTask extends AsyncTask<LatLng, Void, Document> {

			EcoRoute route;
	        @Override
	        protected void onPreExecute()
	        {
	            super.onPreExecute();
	            Log.e("Rest", "onPreExecute");
	        }
	        
	        
			@Override
		    protected Document doInBackground(LatLng... params) {
				Log.e("Rest", "doInBackground");
				
				LatLng source = params[0];
				LatLng destination = params[1];
				
				route = new EcoRoute();
				Document routeDocument = route.getDocument(source, destination);
		          
				return routeDocument;
			}
			
			@Override
			protected void onPostExecute(Document doc) {
				Log.e("Rest", "onPostExecute");
				super.onPostExecute(doc);
				
				ArrayList<LatLng> routePoints = route.getDirection(doc);
				PolylineOptions routeLines = new PolylineOptions().width(10).color(Color.RED);
				routeLines.addAll(routePoints);
				map.addPolyline(routeLines);
			}
		}

    
}
