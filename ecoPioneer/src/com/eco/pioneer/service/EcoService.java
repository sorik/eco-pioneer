package com.eco.pioneer.service;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

import com.eco.pioneer.model.EcoDataAdapter;

public class EcoService {

	private EcoDataAdapter ecoData;
	private EcoServiceUpdater updater;
	private Timer timer;
	
	public EcoService() {
		
		ecoData = new EcoDataAdapter();
		updater = new EcoServiceUpdater(ecoData);
		updater.setDistances(Arrays.asList(ecoData.mocDistancesBad));
	}
	
	public String where() {
		return "You are here";
	}
	
	public String start() {
		StartTracking();
		return ecoData.start();
	}
	
	public String stop() {
		timer.cancel();
		return ecoData.stop();
	}
	
	public int getScore() {
		return ecoData.getScore();
	}
	
	private void StartTracking() {
		if(timer != null)
			return;
		
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Log.v("pioneer", "timer task called.");
				updater.update();
			}
		}, UPDATE_INTERVAL, UPDATE_INTERVAL);
		
	}
	
	public static int UPDATE_INTERVAL = 5*1000;

}
