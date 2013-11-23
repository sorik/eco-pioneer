package com.eco.pioneer.service;


import com.eco.pioneer.AppLinkService;
import com.eco.pioneer.model.EcoDataAdapter;


public class EcoServiceUpdater {
	
	private int count;
	private EcoDataAdapter data;
	public EcoServiceUpdater(EcoDataAdapter dataAdpater) {
		count = 0;
		data = dataAdpater;
	}
	
	public void update() {
		System.out.println("Updater called. [" + count + "]");
		String record = data.get(count);
		count++;
		AppLinkService.speakVoice(record);
		// send msg to sync
	}

}
