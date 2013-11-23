package com.eco.pioneer.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class EcoDataAdapter {
	
	private JSONObject data;
	private EcoScore score = null;	
	public EcoDataAdapter() {

		try {			
			JSONParser parser = new JSONParser();
			Object object = parser.parse(mockData);
			data = (JSONObject)object;
		} catch (ParseException e) {
			System.out.println("Fail to create EcoDataAdapter." + e.getMessage());
		} 
	}
	
	public String start() {
		score = new EcoScore(60, 80);
		
		String name = (String)data.get("name");
		String start = (String)data.get("start");
		return "Hello" + " " + name + " " + "You are on" + " " + start;
		
	}
	
	public String stop() {
		String summary = new String();
		return "Your socre is "+ score.getScore();
	}
	
	public int getScore() {
		return score.getScore();
	}
	
	public String get(int count) {
		// XXX
		JSONArray records = (JSONArray)data.get("records");
		if (count >= records.size()) {
			return "no records";
		}
		return (String)records.get(count);
	}
	
	public String updateScore(double velocityKPH)
	{
		String ret="";
		switch(score.insertVelocity(velocityKPH))
		{
		case TOO_FAST:
			ret = "You are too fast. slow down";
			break;
		case TOO_SLOW:
			ret = "Come on, speed up";
			break;
		case GOOD:
		default:
			ret = "Well done, keep going on the same speed";
			break;
		}
		
		return ret;
		
	}
	private String mockData = new String( "{" +
  "\"name\": \"sori\"," +
  "\"start\": \"Sturt Street SouthBank\"," +
  "\"stop\": \"Univesity of Melbourne\", " +
  "\"score\": \"100\", " +
  "\"rank\": \"1\", " +
  "\"pionner\": \"yes\", " +
  "\"records\": [\"You are not efficient\", \"You are super awesome\"]}");
	
	public Double [] mocDistancesGood = new Double[] {83.0, 83.3, 83.3, 83.3, 83.3, 83.3, 83.3, 83.3, 83.3, 83.3, 83.3, 83.3};
	public Double [] mocDistancesBad =  new Double[] {150.0, 150.0, 130.0, 100.0, 83.0, 83.0, 83.0, 60.0, 60.0, 60.0, 150.0, 150.0};

}
