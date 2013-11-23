package com.eco.pioneer.service;

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
	}
	
	public String Where() {
		return "You are here";
	}
	
	public String Start() {
		StartTracking();
		return ecoData.Start();
	}
	
	public String Stop() {
		timer.cancel();
		return ecoData.Stop();
	}
	
	private void StartTracking() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Log.v("pioneer", "timer task called.");
				updater.update();
			}
		}, 8*1000, 5*1000);
		
	}
	
	
	
	
	
	
	

}
