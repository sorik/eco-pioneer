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
	
	int score = 0;
	
	public Grade insertVelocity(double velocity)
	{
		int diff = 0;
		
		diff = (int)Math.round(velocity - bestVelocityMax);
		
		if(diff >= 0)
		{
			score += diff*diff;
			return Grade.TOO_FAST;
		}
		else
		{
			diff = (int)Math.round(bestVelocityMin - velocity);
			if(diff >= 0)
			{
				score += diff*diff;
				return Grade.TOO_SLOW;
			}
		}
		
		return Grade.GOOD;
	}
	
	public int getScore()
	{
		return score;
	}
}
