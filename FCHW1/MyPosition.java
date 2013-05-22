
public class MyPosition implements Position{
	private int quantity;
	private String symbol;
	public MyPosition(String s, int q)
	{
		quantity = q;
		symbol = s;
	}
	public void update(int q)
	{
		quantity += q;
	}
	public int getQuantity()
	{
		return quantity;
	}
	public String getSymbol()
	{
		return symbol;
	}
}
