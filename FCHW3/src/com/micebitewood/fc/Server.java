package com.micebitewood.fc;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Server{
    public static void main(String[] args)
    {
    	/*
    	 * 1. Create requests(which can be implemented as an ArrayList)
    	 * 2. Create Producers according to different requests, #Producers = #requests
    	 * 3. Run Producers, which send the requests 100 times for each to Clients, the requests include: request number, payout type, original price, r, sigma, days, and strike price
    	 * 4. Create Consumers according to different requests, #Consumers = #requests
    	 * 5. Run Consumers, which listen to feedbacks of Clients based on different requests, each request has a unique topic
    	 * 6. evaluate stop criteria, once completed, print the output 
    	 * 7. Done!
    	 */
    	Request[] request = new Request[2];
    	request[0] = new Request();
    	request[0].setRequestNum(1);
    	request[0].setDays(252);
    	request[0].setPayout("American");
    	request[0].setPrice(152.35);
    	request[0].setProbability(0.96);
    	request[0].setR(0.0001);
    	request[0].setSigma(0.01);
    	request[0].setStrikePrice(165);
    	
    	request[1] = new Request();
    	request[1].setRequestNum(2);
    	request[1].setDays(252);
    	request[1].setPayout("Asian");
    	request[1].setPrice(152.35);
    	request[1].setProbability(0.96);
    	request[1].setR(0.0001);
    	request[1].setSigma(0.01);
    	request[1].setStrikePrice(165);

    	
    	
      	//for(int k = 0; k < 1; k++)
    	boolean flag = false;
    	boolean[] isFinished = {false, false};
    	do{
			Producer[] producer = new Producer[2];
			Thread[] producerThread = new Thread[2];
			Consumer[] consumer = new Consumer[2];
	    	Thread[] consumerThread = new Thread[2];
    		for(int k = 0; k < 2; k++)
    		{
    			if(isFinished[k])
    				continue;
    			producer[k] = new Producer();
    			producer[k].setRequest(request[k]);
    			producerThread[k] = new Thread(producer[k]);
    			producerThread[k].setDaemon(false);
    			producerThread[k].start();
    		}
	    	for(int k = 0; k < 2; k++)
	    	{
	    		if(isFinished[k])
	    			continue;
	    		consumer[k] = new Consumer();
	    		consumer[k].setRequest(request[k]);
	    		consumerThread[k] = new Thread(consumer[k]);
	    		consumerThread[k].setDaemon(false);
	    		consumerThread[k].start();
	    	}
       		
       		try{
       			Thread.sleep(100);
       		}catch(Exception e){
       			System.out.println("exception");
       		}
       		flag = true;
       		for(int k = 0; k < 2; k++)
       		{
       			if(isFinished[k])
       				continue;
       			if(request[k].evaluation())
       				flag = false;
       			else
       			{
       				isFinished[k] = true;
       				System.out.println("request number: " + k + ", number of simulations: " + request[k].getN() + ", the payout is: " + request[k].getMean());
       			}
       		}
       	}while(!flag);
    }
}

class Producer implements Runnable {
	Request request;
	public void setRequest(Request request)
	{
		this.request = request;
	}
	
	/*
	 * 1. create a ConnectionFactory
	 * 2. create a Connection
	 * 3. Connection starts
	 * 4. create a Session
	 * 5. create a Destination
	 * 6. create a Message Producer
	 * 7. send 100 messages
	 * 8. close
	 */
    public void run() {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination sender = session.createQueue("Requests");

            MessageProducer producer = session.createProducer(sender);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            String text = request.getRequest();
            TextMessage requestMessage = session.createTextMessage(text);

            for(int i = 0; i < 100; i++)
            	producer.send(requestMessage);

            session.close();
            connection.close();
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable, ExceptionListener {
	Request request;
	public void setRequest(Request request)
	{
		this.request = request;
	}
	
	/*
	 * 1. create a ConnectionFactory
	 * 2. create a Connection
	 * 3. Connection starts
	 * 4. create a Session
	 * 5. create a Destination
	 * 6. create a Message Consumer
	 * 7. receive 100 messages
	 */
	public void run() {
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination receiver = session.createTopic("" + request.getRequestNum());
            
            MessageConsumer consumer = session.createConsumer(receiver);

            int count = 0;
            while(count < 100){
            // Wait for a message
            //int k = 0;
            //while(k < 1) {
            	Message message = consumer.receive(1);

            	if (message instanceof TextMessage) {
            		TextMessage textMessage = (TextMessage) message;
                	String text = textMessage.getText();
                	Double value = Double.parseDouble(text);
                	request.calc(value);
                	count++;
            	}
            }

        	consumer.close();
            session.close();
            connection.close();
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }
    public synchronized void onException(JMSException ex) {
        System.out.println("JMS Exception occured.  Shutting down client.");
    }
}
