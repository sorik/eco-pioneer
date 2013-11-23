package com.eco.pioneer.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class EcoDataAdapter {
	
	private JSONObject data;
	
	public EcoDataAdapter() {

		try {			
			JSONParser parser = new JSONParser();
			Object object = parser.parse(mockData);
			data = (JSONObject)object;
		} catch (ParseException e) {
			System.out.println("Fail to create EcoDataAdapter." + e.getMessage());
		} 
	}
	
	public String Start() {
		String name = (String)data.get("name");
		String start = (String)data.get("start");
		return "Hello" + " " + name + " " + "You are on" + " " + start;
		
	}
	
	public String Stop() {
		String summary = new String();
		return "Well done";
	}
	
	public String get(int count) {
		// XXX
		JSONArray records = (JSONArray)data.get("records");
		if (count >= records.size()) {
			return "no records";
		}
		return (String)records.get(count);
	}
	
	
	private String mockData = new String( "{" +
  "\"name\": \"sori\"," +
  "\"start\": \"Sturt Street SouthBank\"," +
  "\"stop\": \"Univesity of Melbourne\", " +
  "\"score\": \"100\", " +
  "\"rank\": \"1\", " +
  "\"pionner\": \"yes\", " +
  "\"records\": [\"You are not efficient\", \"You are super awesome\"]}");
	

}
