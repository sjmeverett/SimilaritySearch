package metricspaces;

public class Pair<FirstType, SecondType> {
	protected FirstType first;
	protected SecondType second;
	
	public Pair(FirstType first, SecondType second) {
		this.first = first;
		this.second = second;
	}
	
	public FirstType getFirst() {
		return first;
	}
	
	public SecondType getSecond() {
		return second;
	}
}
