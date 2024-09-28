import java.util.LinkedList;
import java.util.List;

/* DO NOT CHANGE:  This file is used in evaluation */

public class Stack<Arbitrary> {
	private List<Arbitrary> la = new LinkedList<Arbitrary>();

	public Arbitrary pop() {
		if (la.isEmpty()) {
			return null;
		}
		return la.remove(la.size() - 1);

	}

	public Arbitrary push(Arbitrary element) {
		la.add(element);
		return null;
	}

	public Arbitrary top() {
		if (la.isEmpty()) {
			return null;
		}
		return la.get(la.size() - 1);
	}

	public boolean isEmpty() {
		return la.isEmpty();
	}
}
