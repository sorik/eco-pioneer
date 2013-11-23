package com.eco.pioneer.model;

public class EcoScore {

	public double bestVelocityMin=0, bestVelocityMax=0;
	
	public enum Grade {
		GOOD,
		TOO_FAST, 
		TOO_SLOW
	}
	
	EcoScore(double bestVelocityMin, double bestVelocityMax)
	{
		this.bestVelocityMin= bestVelocityMin;
		this.bestVelocityMax= bestVelocityMax;
	}
	
	private int penalty = 0;
	private int measureCount = 0;
	
	public Grade insertVelocity(double velocity)
	{
		int diff = 0;
		measureCount++;
		
		diff = (int)Math.round(velocity - bestVelocityMax);
		
		if(diff >= 0)
		{
			penalty += diff*diff;
			return Grade.TOO_FAST;
		}
		else
		{
			diff = (int)Math.round(bestVelocityMin - velocity);
			if(diff >= 0)
			{
				penalty += diff*diff;
				return Grade.TOO_SLOW;
			}
		}
		
		return Grade.GOOD;
	}
	
	public int getScore()
	{
		double avgPenalty = (double)penalty / measureCount;
		int score = (int) (100 - (avgPenalty / 16));
		
		if(score > 100)
			score = 100;
		else if(score <0)
			score = 0;
		return score;
	}
}
