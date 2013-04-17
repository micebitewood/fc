package com.micebitewood.fc;

import java.util.*;

import org.apache.commons.math3.analysis.function.Exp;
import org.joda.time.DateTime;

public class MyStockPath implements StockPath{
	MyRandomVectorGenerator mrvg = new MyRandomVectorGenerator();
	RandomVectorGenerator rvg;
	double price = 0;
	DateTime today = new DateTime();
	double r;
	double sigma;
	int days;
	TreeMap<DateTime, Double> prices;
	Comparator<DateTime> comparator = new DateTimeComparator<DateTime>();
	
	public void setPrice(double price)
	{
		this.price = price;
	}
	
	public void setR(double r)
	{
		this.r = r;
	}
	
	public void setSigma(double sigma)
	{
		this.sigma = sigma;
	}
	
	public void setDays(int days)
	{
		this.days = days;
		mrvg.setDimension(days);
		rvg = new MyRandomVectorDecorator(mrvg);
	}
	
	@Override
	public TreeMap<DateTime, Double> getPrices()
	{
		double[] randomVector = rvg.getVector();
		
		//create a TreeMap whose keys are sorted by date using comparator
		TreeMap<DateTime, Double> prices = new TreeMap<DateTime, Double>(comparator);
		double value = price;
		DateTime date = today;
		prices.put(date, value);
		double a = r - sigma * sigma / 2;
		for(int i = 0; i < randomVector.length; i++)
		{
			//Geometric Brownian Motion
			value = value * (new Exp().value(a + sigma * randomVector[i]));
			date = date.plusDays(1);
			prices.put(date, value);
		}
		return prices;
	}
}

/*
 * implements Comparator, compare two DateTime in ascendent order
 */
class DateTimeComparator<T> implements Comparator<T>{
	public int compare(T o1, T o2)
	{
		if(o1 instanceof DateTime && o2 instanceof DateTime)
		{
			int yy1 = ((DateTime) o1).year().get();
			int yy2 = ((DateTime) o2).year().get();
			if(yy1 != yy2)
				return yy1 < yy2 ? -1 : 1;
			int dd1 = ((DateTime) o1).dayOfYear().get();
			int dd2 = ((DateTime) o2).dayOfYear().get();
			if(dd1 == dd2)
				return 0;
			else
				return dd1 < dd2 ? -1 : 1;
		}
		throw new ClassCastException("not instanceof DateTime");
	}
}