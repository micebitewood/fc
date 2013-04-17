package com.micebitewood.fc;

import java.util.Iterator;
import java.util.TreeMap;

import org.joda.time.DateTime;

public class MyRandomVectorDecorator implements RandomVectorGenerator{
	double[] randomVector;
	RandomVectorGenerator rvg;
	boolean newVector = false;
	
	public MyRandomVectorDecorator(RandomVectorGenerator rvg)
	{
		this.rvg = rvg;
	}
	
	/*
	 * this function will return either a new random vector, or an inverse random vector of the existing one. 
	 * @see com.micebitewood.fc.RandomVectorGenerator#getVector()
	 */
	public double[] getVector()
	{
		if(!newVector)
		{
			randomVector = rvg.getVector();
			newVector = true;
			return randomVector;
		}
		else
		{
			for(int i = 0; i < randomVector.length; i++)
				randomVector[i] *= -1;
			newVector = false;
			return randomVector;
		}
	}
}
