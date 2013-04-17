package com.micebitewood.fc;

import static org.junit.Assert.*;

import java.util.TreeMap;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AmericanPayOutTest {
	AmericanPayOut payout;
	MyStockPath myStockPath;
	TreeMap<DateTime, Double> prices;
	DateTime dateTime;
	@Before
	public void setUp() throws Exception {
		payout = new AmericanPayOut();
		myStockPath = new MyStockPath();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		myStockPath.setDays(252);
		myStockPath.setPrice(152.35);
		myStockPath.setR(0.0001);
		myStockPath.setSigma(0.01);
		System.out.println(payout.getPayout(myStockPath));
	}

}
