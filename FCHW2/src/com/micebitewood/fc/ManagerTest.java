package com.micebitewood.fc;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ManagerTest {
	Manager manager;
	PayOut myPayout;
	@Before
	public void setUp() throws Exception {
		manager = new Manager();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		manager.setPrice(152.35);
		manager.setR(0.0001);
		manager.setSigma(0.01);
		manager.setDays(252);
		manager.setStrikePrice(165);
		manager.setProbability(0.96);
		myPayout = new AmericanPayOut();
		manager.setPayout(myPayout);
		System.out.println(manager.calculateOptionValue());
		myPayout = new AsianPayOut();
		manager.setPayout(myPayout);
		System.out.println(manager.calculateOptionValue());
	}

}
