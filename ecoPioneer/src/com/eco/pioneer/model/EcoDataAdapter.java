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
		return "Hello " + name + ".. " + "You are on " + start + ".. "
				+ "Enjoy your journey.";
		
	}
	
	public String stop() {
		String summary = "";

		summary += "You traveled from " + (String)data.get("start");
		summary += " to " +  (String)data.get("stop") + ".. ";
		
		summary += "Your score is "+ score.getScore() +".. ";
		summary += "Congratulations.. You became a pioneer..";
		
		return summary; 
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
			ret = "You are too fast. slow down!";
			break;
		case TOO_SLOW:
			ret = "You can go faster!";
			break;
		case GOOD:
		default:
			ret = goodjob[goodjobI];
			goodjobI = (goodjobI + 1 ) % goodjob.length;
			break;
		}
		
		return ret;
		
	}
	
	private final String [] goodjob = {"Good job. Steady on.", "Beautiful. Keep on.", "You are gorgeous." };
	private int goodjobI = 0;
	
	private String mockData = new String( "{" +
  "\"name\": \"sori\"," +
  "\"start\": \"Two Ton Max\"," +
  "\"stop\": \"Vic Market\", " +
  "\"score\": \"100\", " +
  "\"rank\": \"1\", " +
  "\"pionner\": \"yes\", " +
  "\"records\": [\"You are not efficient\", \"You are super awesome\"]}");
	
	public Double [] mocDistancesGood = new Double[] {85.0, 85.3};
	public Double [] mocDistancesBad =  new Double[] {85.0, 85.0, 150.0, 150.0, 85.0, 60.0, 85.3};

}
