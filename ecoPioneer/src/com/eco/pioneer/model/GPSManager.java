package com.eco.pioneer.model;

import com.eco.pioneer.LockScreenActivity;
import com.eco.pioneer.MainActivity;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GPSManager {

	Location prev = null;
	LocationListener locationListener=null;
	LocationManager locationManager = null;
	
	GPSManager()
	{
		locationManager = (LocationManager) MainActivity.getInstance().getSystemService(Context.LOCATION_SERVICE);
	}
	
	public void start()
	{		
		prev = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		// Define a listener that responds to location updates
		locationListener = new LocationListener() {

			@Override
		    public void onLocationChanged(Location location) {
				System.out.println("New Location:" + location);
				
				if(prev != null)
					System.out.println("Distance:" + prev.distanceTo(location));
				
				prev=location;
		    }

			@Override
		    public void onProviderEnabled(String provider) {}

			@Override
		    public void onProviderDisabled(String provider) {}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}
		};

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
		System.out.println("GPSManager started");
	}
	
	public void stop()
	{
		locationManager.removeUpdates(locationListener);
	}
}
