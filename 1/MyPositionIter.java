import java.util.*;
public class MyPositionIter implements PositionIter{
	private Iterator<String> it;
	private HashMap<String, MyPosition> hashMap;
	public MyPositionIter(HashMap<String, MyPosition> hashMap)
	{
		Set<String> set = hashMap.keySet();
		this.hashMap = hashMap;
		it = set.iterator();
	}
	public Position getNextPosition()
	{
		if(!it.hasNext())
		{
			return null;
		}
		return hashMap.get(it.next());
	}
}
