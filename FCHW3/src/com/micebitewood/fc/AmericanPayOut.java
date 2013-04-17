package com.micebitewood.fc;

import java.util.TreeMap;
import org.joda.time.DateTime;

public class AmericanPayOut implements PayOut{
	/*
	 * return the price of the last day
	 * @see com.micebitewood.fc.PayOut#getPayout(com.micebitewood.fc.StockPath)
	 */
	@Override
	public double getPayout(StockPath path)
	{
		TreeMap<DateTime, Double> prices = path.getPrices();
		return prices.get(prices.lastKey());
	}
}
