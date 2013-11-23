package com.eco.pioneer.service;


import java.util.List;

import com.eco.pioneer.AppLinkService;
import com.eco.pioneer.model.EcoDataAdapter;


public class EcoServiceUpdater {
	
	private int count;
	private EcoDataAdapter data;
	
	public EcoServiceUpdater(EcoDataAdapter dataAdpater) {
		count = 0;
		data = dataAdpater;
	}
	
	private List<Double> fakeDistances = null;
	
	public void setDistances(List<Double> distances)
	{
		this.fakeDistances = distances;
	}
	
	public void update() {
		System.out.println("Updater called. [" + count + "]");
		String record = data.get(count);

		// Get fake distances instead of real GPS data
		double distance = fakeDistances.get(count % fakeDistances.size());
		
		double velocityKPH = getVelocity(distance);
		int vel = (int) Math.round(velocityKPH);
		
		String narr = data.updateScore(velocityKPH);
		
		System.out.println("Updater: distance=" +distance + ", KPH = "+vel);
		count++;
		
		//AppLinkService.speakVoice(record);
		AppLinkService.speakVoice(narr);
		//AppLinkService.speakVoice(""+ Integer.toString(vel)+" killo meters per hour" );
		// send msg to sync
	}
	
	private double getVelocity(double distance)
	{
		return (3.6*distance/(EcoService.UPDATE_INTERVAL/1000));

	}
}
