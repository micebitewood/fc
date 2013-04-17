package com.micebitewood.fc;

import org.apache.commons.math3.distribution.NormalDistribution;

/*
 * Each request has a unique request number, and all the information for Monte-Carlo simulation
 * In addition, each request evaluates its stop criteria itself
 * The evaluation and calculation are synchronized
 */
public class Request {
	int requestNum;
	String payout;
	double price;
	double r;
	double sigma;
	int days;
	double strikePrice;
	double mean = 0;
	double sqrMean = 0;
	int N = 0;
	double standardDeviation = 0;
	double probability;
	double criticalValue;
	
	public double getStandardDeviation(){return standardDeviation;}
	
	public double getCriticalValue(){return criticalValue;}
	
	public int getN(){return N;}
	
	public double getMean(){return mean;}
	
	public synchronized boolean evaluation()
	{
		if(standardDeviation == 0 || criticalValue * standardDeviation / Math.sqrt(1.0 * N) > 0.01)
			return true;
		return false;
	}
	
	public synchronized void calc(double value){
		N++;
		mean = (N - 1) * mean / N + value / N;
		sqrMean = (N - 1) * sqrMean / N + value * value / N;
		standardDeviation = Math.sqrt(sqrMean - mean * mean);
	}
	
	public String getRequest(){
		String str = new String(requestNum + " " + payout + " " + price + " " + r + " " + sigma + " " + days + " " + strikePrice);
		return str;
	}

	public int getRequestNum(){return requestNum;}
	
	public void setRequestNum(int requestNum){this.requestNum = requestNum;}
	
	public void setPayout(String payout){this.payout = payout;}
	
	/*
	 * set original price
	 */
	public void setPrice(double price){this.price = price;}
	
	public void setR(double r){this.r = r;}
	
	public void setSigma(double sigma){this.sigma = sigma;}
	
	/*
	 * set total days, which is also the dimension of RandomVector
	 */
	public void setDays(int days){this.days = days;}
	
	public void setStrikePrice(double strikePrice){this.strikePrice = strikePrice;}
	
	/*
	 * set probability for stopping criteria, 0 < probability < 1
	 */
	public void setProbability(double probability){
		this.probability = probability;
		criticalValue = new NormalDistribution().inverseCumulativeProbability((1 + probability) / 2);
	}
}
