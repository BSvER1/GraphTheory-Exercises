package graphTheory.chromaticNumber.assets;

import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("serial")
public class SortedArrayList<T> extends ArrayList<T> {

	@SuppressWarnings("unchecked")
	public void insertSorted(Graph toSolve, T e) {
		if (e instanceof Chromosome) {
			((Chromosome) e).calculateCost(toSolve);
		}
		add(e);
		Comparable<T> cmp = (Comparable<T>) e;
		for (int i = size()-1; i > 0 && cmp.compareTo(get(i-1)) < 0; i--)
			Collections.swap(this, i, i-1);
	}
}
