package com.micebitewood.fc;

import java.util.TreeMap;

import org.joda.time.DateTime;

public interface StockPath {
	public TreeMap<DateTime, Double> getPrices();
}
