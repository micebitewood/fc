
public interface Portfolio {
	public void newTrade(String symbol, int quantity);
	public PositionIter getPositionIter();
}
