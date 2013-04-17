package com.micebitewood.fc;

import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.UncorrelatedRandomVectorGenerator;

public class MyRandomVectorGenerator implements RandomVectorGenerator{
	RandomGenerator rg;
	GaussianRandomGenerator grg;
	UncorrelatedRandomVectorGenerator urv;
	
	/*
	 *	initialization of random vector generator 
	 */
	public void setDimension(int dimension)
	{
		rg = new JDKRandomGenerator();

		grg = new GaussianRandomGenerator(rg);

		urv = new UncorrelatedRandomVectorGenerator(dimension, grg);
	}
	/*
	 * return a new random vector
	 * @see com.micebitewood.fc.RandomVectorGenerator#getVector()
	 */
	@Override
	public double[] getVector()
	{
		double[] randomVector = urv.nextVector();
		
		return randomVector;
	}
}
