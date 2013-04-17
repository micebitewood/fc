package com.micebitewood.fc;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MyRandomVectorGeneratorTest {

	MyRandomVectorGenerator mrvg;
	@Before
	public void setUp() throws Exception {
		mrvg = new MyRandomVectorGenerator();
		mrvg.setDimension(252);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetVector() {
		double[] v = mrvg.getVector();
		System.out.println(v.length);
		for(double d : v)
		{
			System.out.println(d + "");
		}
	}

}
