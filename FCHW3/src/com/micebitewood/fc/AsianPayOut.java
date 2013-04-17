package com.micebitewood.fc;

import java.util.Iterator;
import java.util.TreeMap;

import org.joda.time.DateTime;

public class AsianPayOut implements PayOut {
	/*
	 * return the average price of all the days
	 * @see com.micebitewood.fc.PayOut#getPayout(com.micebitewood.fc.StockPath)
	 */
	@Override
	public double getPayout(StockPath path) {
		TreeMap<DateTime, Double> prices = path.getPrices();
		Iterator<DateTime> it = prices.keySet().iterator();
		double price = 0;
		int i = 0;
		while(it.hasNext())
		{
			price += ((Double) prices.get((DateTime) it.next())).doubleValue();
			i++;
		}
		return price / i;
	}

}
