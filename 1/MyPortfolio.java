import java.util.*;
public class MyPortfolio implements Portfolio{
	private HashMap<String, MyPosition> portfolio;
	public MyPortfolio()
	{
		portfolio = new HashMap<String, MyPosition>();
	}
	public void newTrade(String symbol, int quantity)
	{
		if(portfolio.containsKey(symbol))
		{
			MyPosition position = portfolio.get(symbol);
			position.update(quantity);
		}
		else
		{
			MyPosition myPosition = new MyPosition(symbol, quantity);
			portfolio.put(symbol, myPosition);
		}
	}
	public PositionIter getPositionIter()
	{
		PositionIter it = new MyPositionIter(portfolio);
		return it;
	}
	public static void main(String[] args)
	{
		MyPortfolio myPortfolio = new MyPortfolio();
		myPortfolio.newTrade("IBM", 100);
		myPortfolio.newTrade("Microsoft", 500);
		myPortfolio.newTrade("Apple", 400);
		myPortfolio.newTrade("Goole", 300);
		myPortfolio.newTrade("IBM", -50);
		PositionIter it = myPortfolio.getPositionIter();
		Position myPosition;
		while((myPosition = it.getNextPosition()) != null)
		{
			System.out.println(myPosition.getSymbol() + ": " + myPosition.getQuantity());
		}
	}
}
