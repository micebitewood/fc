package com.micebitewood.fc;

import org.apache.commons.math3.distribution.NormalDistribution;

public class Manager {
	
	PayOut payout;
	double price;
	double r;
	double sigma;
	int days;
	double strikePrice;
	double probability;
	
	/*
	 * @param payout can be any implementation of PayOut including american and asian
	 */
	public void setPayout(PayOut payout)
	{
		this.payout = payout;
	}
	
	/*
	 * set original price
	 */
	public void setPrice(double price)
	{
		this.price = price;
	}
	
	public void setR(double r)
	{
		this.r = r;
	}
	
	public void setSigma(double sigma)
	{
		this.sigma = sigma;
	}
	
	/*
	 * set total days, which is also the dimension of RandomVector
	 */
	public void setDays(int days)
	{
		this.days = days;
	}
	
	public void setStrikePrice(double strikePrice)
	{
		this.strikePrice = strikePrice;
	}
	
	/*
	 * set probability for stopping criteria, 0 < probability < 1
	 */
	public void setProbability(double probability)
	{
		this.probability = probability;
	}
	
	/*
	 * calculate the option value
	 */
	public double calculateOptionValue()
	{
		//initialize a stockPath with Decorator Pattern
		MyStockPath myStockPath = new MyStockPath();
		myStockPath.setDays(days);
		myStockPath.setPrice(price);
		myStockPath.setR(r);
		myStockPath.setSigma(sigma);
		
		//calculation
		double mean = 0;
		double sqrMean = 0;
		int N = 0;
		double standardDeviation = 0;
		double value;
		double criticalValue = 
				new NormalDistribution().inverseCumulativeProbability((1 + probability) / 2);	//P(X <= criticalValue) = probability
		do{
			value = payout.getPayout(myStockPath) - strikePrice;
			value = value > 0 ? value : 0;
			N++;
			mean = (N - 1) * mean / N + value / N;
			sqrMean = (N - 1) * sqrMean / N + value * value / N;
			standardDeviation = Math.sqrt(sqrMean - mean * mean);
			if(N % 100000 == 0)
				System.out.println(mean + " " + standardDeviation + " " + N);
		}while(standardDeviation == 0 || criticalValue * standardDeviation / Math.sqrt(1.0 * N) > 0.01);
		return mean;
	}
}
