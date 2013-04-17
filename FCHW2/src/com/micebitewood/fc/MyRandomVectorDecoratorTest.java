package com.micebitewood.fc;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MyRandomVectorDecoratorTest {
	MyRandomVectorGenerator mrvg;
	MyRandomVectorDecorator mrvd;
	
	@Before
	public void setUp() throws Exception {
		mrvg = new MyRandomVectorGenerator();
		mrvg.setDimension(252);
		mrvd = new MyRandomVectorDecorator(mrvg);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetVector() {
		double[] v = mrvd.getVector();
		System.out.println(v.length);
		for(double d : v)
		{
			System.out.print(d + " ");
		}
		System.out.println();
		v = mrvd.getVector();
		System.out.println(v.length);
		for(double d : v)
		{
			System.out.print(d + " ");
		}
	}
}
