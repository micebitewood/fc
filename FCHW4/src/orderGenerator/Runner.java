package orderGenerator;

import java.util.HashMap;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import orderGenerator.Message;
import orderGenerator.NewOrder;
import orderGenerator.OrderCxR;
import orderGenerator.OrdersIterator;

/**
 * 
 * @author John
 *
 */
public class Runner {
	
	HashMap<String, Book> market;
	HashMap<String, NewOrder> cxrBook;
	HashMap<String, String> idToSymbol;
	
	/**
	 * returns a market, which is a HashMap of <OrderSymbol, Book>
	 * @return 		market which is a HashMap of (order.Symbol, Book) pair
	 */
	public HashMap<String, Book> getMarket()
	{
		return market;
	}
	
	/**
	 * @return CxRBook which is a HashMap of (order.Symbol, orderCxR) pair
	 */
	public HashMap<String, NewOrder> getCxR()
	{
		return cxrBook;
	}

	/**
	 * print the final state of the market
	 */
	private void finalPrint()
	{
		//for each book
		for(Iterator<String> bookIt = market.keySet().iterator(); bookIt.hasNext(); )
		{
			String symbol = bookIt.next();
			Book book = market.get(symbol);
			TreeMap<Double, List<NewOrder>> askBook = book.getAskBook();
			TreeMap<Double, List<NewOrder>> bidBook = book.getBidBook();
			
			//for each askBook
			for(Iterator<Double> askBookIt = askBook.keySet().iterator(); askBookIt.hasNext(); )
			{
				Double price = askBookIt.next();
				//for each askOrder
				for(Iterator<NewOrder> askOrderIt = askBook.get(price).iterator(); askOrderIt.hasNext(); )
				{
					NewOrder order = askOrderIt.next();
					if(cxrBook.containsKey(order.getOrderId()) && cxrBook.get(order.getOrderId()) != order)
						continue;
					System.out.println(String.format("%.2f", order.getLimitPrice()) + " ask " + -order.getSize());
				}
			}
			//for each bidBook
			for(Iterator<Double> bidBookIt = bidBook.keySet().iterator(); bidBookIt.hasNext(); )
			{
				Double price = bidBookIt.next();
				//for each bidOrder
				for(Iterator<NewOrder> bidOrderIt = bidBook.get(price).iterator(); bidOrderIt.hasNext(); )
				{
					NewOrder order = bidOrderIt.next();
					if(cxrBook.containsKey(order.getOrderId()) && cxrBook.get(order.getOrderId()) != order)
						continue;
					System.out.println(String.format("%.2f", order.getLimitPrice()) + " bid " + order.getSize());
				}
			}
		}
	}
	
	/**
	 * print the top of each book
	 */
	private void print()
	{
		//for each book
		for(Iterator<String> it = market.keySet().iterator(); it.hasNext(); )
		{
			Book book = market.get(it.next());
			if(!book.getAskBook().isEmpty())
			{
				System.out.print(book.getSymbol() + " ask: ");
				//get the top of askBook and traverse all the orders
				for(Iterator<NewOrder> noit = book.getAskBook().get(book.getAskBook().firstKey()).iterator(); noit.hasNext(); )
				{
					NewOrder order = noit.next();
					System.out.print(-order.getSize() + " x " + String.format("%.2f", order.getLimitPrice()) + " @ " + order.getOrderId() + " | ");
				}
				System.out.println();
				
			}
			if(!book.getBidBook().isEmpty())
			{
				System.out.print(book.getSymbol() + " bid: ");
				//get the top of bidBook and traverse all the orders
				for(Iterator<NewOrder> noit = book.getBidBook().get(book.getBidBook().lastKey()).iterator(); noit.hasNext(); )
				{
					NewOrder order = noit.next();
					System.out.print(order.getSize() + " x " + String.format("%.2f", order.getLimitPrice()) + " @ " + order.getOrderId() + " | ");
				}
				System.out.println();
			}
		}
		
	}
	
	/**
	 * print each trade
	 * @param xxx	orderId1
	 * @param yyy	orderId2
	 */
	private void print(String xxx, String yyy)
	{
		System.out.println("Order " + xxx + " traded with order " + yyy);
	}
	
	/**
	 * initialization of market, cxrBook, and idToSymbol
	 */
	public Runner()
	{
		market = new HashMap<String, Book>();
		cxrBook = new HashMap<String, NewOrder>();
		idToSymbol = new HashMap<String, String>();
	}
	
	/**
	 * 
	 * @param newOrder	a new order
	 * @return			Book based on newOrder.getSymbol()
	 */
	private Book getBook(NewOrder newOrder)
	{
		Book book;
		//if such a symbol is contained in market
		if(market.containsKey(newOrder.getSymbol()))
		{
			book = market.get(newOrder.getSymbol());
		}
		
		//if such a symbol is not contained in market
		else
		{
			book = new Book(newOrder.getSymbol());
			book.setAskBook(new TreeMap<Double, List<NewOrder>>());
			book.setBidBook(new TreeMap<Double, List<NewOrder>>());
			market.put(newOrder.getSymbol(), book);
		}
		
		return book;
	}
	
	/**
	 * 
	 * @param askBook	the book for looking up
	 * @param bidOrder	the order for trade
	 * @param size		the size of the order
	 * @return			the size of the order after trade
	 */
	private int newBidTrade(List<NewOrder> askBook, NewOrder bidOrder, int size)
	{
		
		for(Iterator<NewOrder> askOrderIt = askBook.iterator(); askOrderIt.hasNext(); )
		{
			boolean hasCxR = false;
			NewOrder askOrder = askOrderIt.next();

			//if the order has been cancelled or replaced
			if(cxrBook.containsKey(askOrder.getOrderId()))
			{
				String id = askOrder.getOrderId();
				if(cxrBook.get(id) != askOrder)
				{
					askOrderIt.remove();
					hasCxR = true;
				}
			}
			
			if(hasCxR)
				continue;
			else
			{
				print(askOrder.getOrderId(), bidOrder.getOrderId());
				//if bidOrder is completely traded but askOrder is not
				if(-askOrder.getSize() > size)
				{
					OrdersIterator oi = new OrdersIterator();
					NewOrder no = oi.new OrderImpl(askOrder.getSymbol(), askOrder.getSize() + size, askOrder.getOrderId(), askOrder.getLimitPrice());
					askOrderIt.remove();
					askBook.add(0, no);
					size = 0;
					break;
				}
				//if askOrder is completely traded but bidOrder is not
				else if(-askOrder.getSize() < size)
				{
					size += askOrder.getSize();
					askOrderIt.remove();
				}
				//if both are completely traded
				else
				{
					askOrderIt.remove();
					size = 0;
					break;
				}
			}
			
		}
		
		return size;
	}
	
	/**
	 * 
	 * @param bidBook	the book for looking up
	 * @param askOrder	the order for trade
	 * @param size		the size of the order
	 * @return			the size of the order after trade
	 */
	private int newAskTrade(List<NewOrder> bidBook, NewOrder askOrder, int size)
	{
		size = -size;
		
		for(Iterator<NewOrder> bidOrderIt = bidBook.iterator(); bidOrderIt.hasNext(); )
		{
			boolean hasCxR = false;
			NewOrder bidOrder = bidOrderIt.next();
			
			//if the order has been cancelled or replaced
			if(cxrBook.containsKey(bidOrder.getOrderId()))
			{
				String id = bidOrder.getOrderId();
				if(cxrBook.get(id) != bidOrder)
				{
					bidOrderIt.remove();
					hasCxR = true;
				}
			}
			
			if(hasCxR)
				continue;
			else
			{
				print(bidOrder.getOrderId(), askOrder.getOrderId());
				//if askOrder is completely traded but bidOrder is not
				if(bidOrder.getSize() > size)
				{
					OrdersIterator oi = new OrdersIterator();
					NewOrder no = oi.new OrderImpl(bidOrder.getSymbol(), bidOrder.getSize() - size, bidOrder.getOrderId(), bidOrder.getLimitPrice());
					bidOrderIt.remove();
					bidBook.add(0, no);
					size = 0;
					break;
				}
				//if bidOrder is completely traded but askOrder is not
				else if(bidOrder.getSize() < size)
				{
					size -= bidOrder.getSize();
					bidOrderIt.remove();
				}
				//if both are completely traded
				else
				{
					bidOrderIt.remove();
					size = 0;
					break;
				}
			}
			
		}
		
		return -size;
	}
	
	/**
	 * 
	 * @param newOrder	the order for trade
	 * @param askBook	the book for looking up
	 * @param bidBook	the book for saving the order after trades
	 * @param size		the size of newOrder
	 * @return			the current newOrder after trades
	 */
	public NewOrder bidOrder(NewOrder newOrder, TreeMap<Double, List<NewOrder>> askBook, TreeMap<Double, List<NewOrder>> bidBook, int size)
	{
		Double limitPrice;
		
		//if market order
		if(Double.isNaN(newOrder.getLimitPrice()))
		{
			limitPrice = Double.MAX_VALUE;
			for(Iterator<Double> askBookIt = askBook.keySet().iterator(); askBookIt.hasNext(); )
			{
				Double price = askBookIt.next();
				size = newBidTrade(askBook.get(price), newOrder, size);
				if(size == 0)
					break;
			}
		}
		else
		{
			limitPrice = newOrder.getLimitPrice();
			if(!askBook.isEmpty())
			{
				//if such an ask order exists, trade between them
				while(askBook.firstKey() <= newOrder.getLimitPrice())
				{
					size = newBidTrade(askBook.get(askBook.firstKey()), newOrder, size);
					if(size == 0)
						break;
					else
					{
						askBook.remove(askBook.firstKey());
						if(askBook.isEmpty())
							break;
					}
				}
			}
		}
		
		if(size != 0)
		{
			OrdersIterator oi = new OrdersIterator();
			NewOrder no = oi.new OrderImpl(newOrder.getSymbol(), size, newOrder.getOrderId(), newOrder.getLimitPrice());
			//if the price queue is contained in bidBook
			if(bidBook.containsKey(limitPrice))
				bidBook.get(limitPrice).add(no);
			//if the price queue is not contained in bidBook
			else
			{
				List<NewOrder> bidOrder = new LinkedList<NewOrder>();
				bidOrder.add(no);
				bidBook.put(limitPrice, bidOrder);
			}
			return no;
		}
		return newOrder;
	}
	
	/**
	 * 
	 * @param newOrder	the order for trade
	 * @param bidBook	the book for looking up
	 * @param askBook	the book for saving the order after trades
	 * @param size		the size of newOrder
	 * @return			the current newOrder after trades
	 */
	public NewOrder askOrder(NewOrder newOrder, TreeMap<Double, List<NewOrder>> bidBook, TreeMap<Double, List<NewOrder>> askBook, int size)
	{
		Double limitPrice;

		//if market order
		if(Double.isNaN(newOrder.getLimitPrice()))
		{
			limitPrice = 0D;
			for(Iterator<Double> bidBookIt = bidBook.keySet().iterator(); bidBookIt.hasNext(); )
			{
				Double price = bidBookIt.next();
				size = newAskTrade(bidBook.get(price), newOrder, size);
				if(size == 0)
					break;
			}
		}
		else
		{
			limitPrice = newOrder.getLimitPrice();
			if(!bidBook.isEmpty())
			{
				//if such an ask order exists, trade between them
				while(bidBook.lastKey() >= newOrder.getLimitPrice())
				{
					size = newAskTrade(bidBook.get(bidBook.lastKey()), newOrder, size);
					if(size == 0)
						break;
					else
					{
						bidBook.remove(bidBook.lastKey());
						if(bidBook.isEmpty())
							break;
					}
				}
			}
		}
		
		if(size != 0)
		{
			OrdersIterator oi = new OrdersIterator();
			NewOrder no = oi.new OrderImpl(newOrder.getSymbol(), size, newOrder.getOrderId(), newOrder.getLimitPrice());
			//if the price queue is contained in bidBook
			if(askBook.containsKey(limitPrice))
				askBook.get(limitPrice).add(no);
			//if the price queue is not contained in bidBook
			else
			{
				List<NewOrder> askOrder = new LinkedList<NewOrder>();
				askOrder.add(no);
				askBook.put(limitPrice, askOrder);
			}
			return no;
		}
		return newOrder;
	}
	
	/**
	 * classify order type, which includes NewOrder and OrderCxR.
	 * If instanceof NewOrder, make trades and then save it to books.
	 * If instanceof OrderCxR, create a NewOrder, make trades, save the order to books, save the orderCxR to cxrBook
	 */
	public void start()
	{
		TreeMap<Double, List<NewOrder>> askBook = new TreeMap<Double, List<NewOrder>>();
		TreeMap<Double, List<NewOrder>> bidBook = new TreeMap<Double, List<NewOrder>>();
		Message msg;
		Book book;
		Iterator<Message> it = OrdersIterator.getIterator();
		
		for(; it.hasNext(); )
		{
			msg = it.next();
			
			if(msg instanceof NewOrder)
			{
				NewOrder newOrder = (NewOrder) msg;
				if(idToSymbol.containsKey(newOrder.getOrderId()))
				{
					System.out.println("an order with the same order id \'" + newOrder.getOrderId() + "\' already exists");
					continue;
				}
				idToSymbol.put(newOrder.getOrderId(), newOrder.getSymbol());
				
				//initialization of askBook and bidBook
				book = getBook(newOrder);
				askBook = book.getAskBook();
				bidBook = book.getBidBook();
				
				//save the size of the newOrder
				int size = newOrder.getSize();
				
				//if it's a bid order
				if(size > 0)
					bidOrder(newOrder, askBook, bidBook, size);
				//if it's an ask order
				else
					askOrder(newOrder, bidBook, askBook, size);
			}
			
			//Cancel/Replace order
			else if(msg instanceof OrderCxR)
			{
				OrderCxR orderCxR = (OrderCxR) msg;
 				String id = orderCxR.getOrderId();

 				//if no such order exists
				if(!idToSymbol.containsKey(id))
				{
					System.out.println("no such order exists");
					continue;
				}
				String symbol = idToSymbol.get(id);
				

				book = market.get(symbol);
				OrdersIterator oi = new OrdersIterator();
				NewOrder no = oi.new OrderImpl(symbol, orderCxR.getSize(), id, orderCxR.getLimitPrice());
				bidBook = book.getBidBook();
				askBook = book.getAskBook();
				
				//bid
				if(orderCxR.getSize() > 0)
				{
					no = bidOrder(no, askBook, bidBook, no.getSize());
				}
				//ask
				else if(orderCxR.getSize() < 0)
				{
					no = askOrder(no, bidBook, askBook, no.getSize());
				}

				//if any OrderCxR with the same orderId exists
				if(cxrBook.containsKey(id))
				{
					cxrBook.remove(id);
				}
				cxrBook.put(id, no);
			}
			print();
			System.out.println();
		}
		finalPrint();
	}
	
	/**
	 * main function
	 * @param args
	 */
	public static void main(String[] args)
	{
		Runner runner = new Runner();
		
		runner.start();
		
	}
	
}
