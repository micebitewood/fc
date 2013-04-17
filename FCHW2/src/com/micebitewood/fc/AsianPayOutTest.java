package com.micebitewood.fc;

import static org.junit.Assert.*;

import java.util.TreeMap;

import org.joda.time.DateTime;
import org.junit.Test;

public class AsianPayOutTest {
	PayOut payOut = new AsianPayOut();
	MyStockPath myStockPath = new MyStockPath();
	TreeMap<DateTime, Double> prices;
	DateTime dateTime;
	@Test
	public void test() {
		myStockPath.setDays(252);
		myStockPath.setPrice(152.35);
		myStockPath.setR(0.0001);
		myStockPath.setSigma(0.01);
		System.out.println(payOut.getPayout(myStockPath));
	}

}
