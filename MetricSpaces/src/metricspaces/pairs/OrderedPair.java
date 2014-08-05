package metricspaces.pairs;

public class OrderedPair<FirstType extends Comparable<FirstType>, SecondType>
	extends Pair<FirstType, SecondType> implements Comparable<OrderedPair<FirstType, SecondType>> {

	public OrderedPair(FirstType first, SecondType second) {
		super(first, second);
	}

	@Override
	public int compareTo(OrderedPair<FirstType, SecondType> o) {
		return first.compareTo(o.first);
	}
}
