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

public class Client implements Runnable, ExceptionListener {
	private int clientID;
	public Client(int i)
	{
		clientID = i;
	}
	
	/*
	 * 1. create a ConnectionFactory
	 * 2. create a Connection
	 * 3. Connection starts
	 * 4. create a Session
	 * 5. create a Destination
	 * 6. create a Message Consumer
	 * 7. keep receiving messages
	 * 8. once received a request, generate a stockpath and get two payouts using decorator pattern
	 * 9. create a Destination, create a Message Producer, and then send the results back to Server
	 */
    public void run() {
    	ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

    	try {
    		Connection connection = connectionFactory.createConnection();
    		connection.start();

    		connection.setExceptionListener(this);

    		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

    		Destination receiver = session.createQueue("Requests");
                
    		MessageConsumer consumer = session.createConsumer(receiver);

    		int i = 0;
    		while(i < 1){
    			Message message = consumer.receive(1);

    			if (message instanceof TextMessage) {
    				TextMessage textMessage = (TextMessage) message;
    				String text = textMessage.getText();
    				String[] request = text.split(" ");
                    	
    				//parsing
    				int requestNum = Integer.parseInt(request[0]);
    				PayOut payOut;
    				switch(request[1].toLowerCase())
    				{
                    	case "asian": payOut = new AsianPayOut(); break;
                    	default: payOut = new AmericanPayOut();
    				}
    				double price = Double.parseDouble(request[2]);
    				double r = Double.parseDouble(request[3]);
    				double sigma = Double.parseDouble(request[4]);
    				int days = Integer.parseInt(request[5]);
    				double strikePrice = Double.parseDouble(request[6]);
    				//end of parsing
                    	
    				MyStockPath myStockPath = new MyStockPath();
    				myStockPath.setDays(days);
    				myStockPath.setPrice(price);
    				myStockPath.setR(r);
    				myStockPath.setSigma(sigma);
                    	
    				
    				Destination sender = session.createTopic("" + requestNum);
    				MessageProducer producer = session.createProducer(sender);
    				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                        
    				/*
    				 * generate one result
    				 */
    				double result = payOut.getPayout(myStockPath) - strikePrice;
    				result = result > 0 ? result : 0;
    				text = "" + result;
                    	
    				System.out.println("ClientID: " + clientID + ", RequestNum: " + requestNum + ", result: " + text);
    				TextMessage backMessage = session.createTextMessage(text);
    				producer.send(backMessage);
    			}
    		}

    		consumer.close();
    		session.close();
    		connection.close();
    	} catch (Exception e) {
    		System.out.println("Caught: " + e);
    		e.printStackTrace();
    	}
	}
        
	public synchronized void onException(JMSException ex) {
		System.out.println("JMS Exception occured.  Shutting down client.");
	}

	/*
	 * create as many clients as we need, each client has a unique thread
	 */
	public static void main(String[] args)
	{
		Thread[] thread = new Thread[3];
		for(int i = 0; i < 3; i++)
		{
			thread[i] = new Thread(new Client(i));
			thread[i].setDaemon(false);
			thread[i].start();
		}
	}
}
