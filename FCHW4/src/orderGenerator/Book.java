package orderGenerator;

import java.util.List;
import java.util.TreeMap;

import orderGenerator.NewOrder;

/**
 * 
 * @author John
 *
 */
public class Book {
	String symbol;
	TreeMap<Double, List<NewOrder>> askBook;
	TreeMap<Double, List<NewOrder>> bidBook;
	
	/**
	 * 
	 * @param symbol	the book symbol, like "IBM", "MSFT"
	 */
	public Book(String symbol)
	{
		this.symbol = symbol;
	}
	
	/**
	 * 
	 * @return			book symbol
	 */
	public String getSymbol()
	{
		return symbol;
	}
	
	/**
	 * 
	 * @param askBook	set an askBook
	 */
	public void setAskBook(TreeMap<Double, List<NewOrder>> askBook)
	{
		this.askBook = askBook;
	}
	
	/**
	 * 
	 * @param bidBook	set a bidBook
	 */
	public void setBidBook(TreeMap<Double, List<NewOrder>> bidBook)
	{
		this.bidBook = bidBook;
	}
	
	/**
	 * 
	 * @return			askBook
	 */
	public TreeMap<Double, List<NewOrder>> getAskBook()
	{
		return this.askBook;
	}
	
	/**
	 * 
	 * @return			bidBook
	 */
	public TreeMap<Double, List<NewOrder>> getBidBook()
	{
		return this.bidBook;
	}
}
