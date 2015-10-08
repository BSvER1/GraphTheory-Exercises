package graphTheory.chromaticNumber.solver;

import java.util.ArrayList;
import java.util.Random;

import graphTheory.chromaticNumber.assets.Bucket;
import graphTheory.chromaticNumber.assets.Graph;
import graphTheory.chromaticNumber.loader.Driver;
import graphTheory.chromaticNumber.loader.ResultsModule;

public class BruteBuckets {

	final boolean BB_TRACING = false;
	boolean printPermutations = false;

	int currentBest;
	long permuteCount;

	ArrayList<Bucket> buckets;

	public BruteBuckets() {
		buckets = new ArrayList<Bucket>();
		currentBest = Integer.MAX_VALUE;
	}

	public void sortVertex(Graph g, int vertex) {
		if (BB_TRACING)
			Driver.trace("sorting vertex " + vertex);
		boolean broken = false;
		for (int j = 0; j < buckets.size(); j++) {
			if (validBucket(g, buckets.get(j), vertex)) {
				buckets.get(j).insert(vertex);
				broken = true;
				if (BB_TRACING)
					Driver.trace("found valid bucket for vertex " + vertex + " in " + j);
				break;
			}
		}

		if (!broken) {
			if (BB_TRACING)
				Driver.trace("ran out of buckets when searching " + "for a place for " + vertex
						+ ", creating a new bucket for it now");
			Bucket b2 = new Bucket(g.getNumVertices());
			b2.insert(vertex);
			buckets.add(b2);
		}
	}

	public void solve(Graph g, long limit) {
		int[] permutations = new int[g.getNumVertices()];
		for (int i = 0; i < permutations.length; i++) {
			permutations[i] = i;
		}
		permute(permutations, limit, g);
	}

	public void solveRandom(Graph g, long limit) {
		long timeStart = System.currentTimeMillis();
		long lastPrintTime = System.currentTimeMillis();
		for (int j = 0; j < limit; j++) {
			if (System.currentTimeMillis() - lastPrintTime > 1000) {
				Driver.trace("starting round "+ j);
				lastPrintTime = System.currentTimeMillis();
			}
			Random r = new Random();
			buckets = new ArrayList<Bucket>();
			// boolean broken = false;
			ArrayList<Integer> random = new ArrayList<Integer>(g.getNumVertices());
			for (int i = 0; i < g.getNumVertices(); i++) {
				random.add(i);
			}

			while (random.size() > 0) {
				if (buckets.size() < currentBest) {
					sortVertex(g, random.remove(r.nextInt(random.size())));
				} else {
					// broken = true;
					if (BB_TRACING)
						Driver.trace("found equality in number of buckets, moving on");
					break;
				}
			}

			if (random.isEmpty() && buckets.size() < currentBest) {
				currentBest = buckets.size();
				ResultsModule.writeIncrementalResultToFile(g, BruteBuckets.class, currentBest, System.currentTimeMillis()- timeStart, limit);
				Driver.trace("["+j+"] successfully found a new best colouring: " + currentBest);
			}
		}
	}

	public int getResult() {
		return currentBest;
	}

	public boolean validBucket(Graph g, Bucket b, int vertex) {
		for (int i = 0; i < g.getNumVertices(); i++) {
			if (b.contains(i)) {
				// Driver.trace(this.getClass(), "comparing "+ vertex+ " with "
				// + i);
				if (g.isEdge(vertex, i)) {
					return false;
				}
			}
		}

		return true;
	}

	public void permute(int[] num, long limit, Graph g) {
		ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
		permute(num, 0, result, limit, g);
	}

	void permute(int[] num, int start, ArrayList<ArrayList<Integer>> result, long limit, Graph g) {

		if (start >= num.length) {
			ArrayList<Integer> item = convertArrayToList(num);

			if (permuteCount < limit) {
				permuteCount++;
				if (printPermutations) {
					for (int i = 0; i < item.size(); i++) {
						System.out.print(item.get(i) + " ");
					}
					System.out.println();
				}

				buckets = new ArrayList<Bucket>();
				boolean broken = false;
				for (int i = 0; i < item.size(); i++) {
					if (buckets.size() < currentBest) {
						sortVertex(g, i);
					} else {
						broken = true;
						if (BB_TRACING)
							Driver.trace("found equality in number of buckets, moving on");
						break;
					}
				}
				if (!broken) {
					currentBest = buckets.size();
					Driver.trace("successfully found a new best colouring: " + currentBest);
				}

			} else {
				if (BB_TRACING)
					Driver.trace("hit limit of permutations");
				return;
			}

			// result.add(item);
		}

		for (int j = start; j <= num.length - 1; j++) {
			if (limit != -1 && permuteCount < limit) {
				swap(num, start, j);
				permute(num, start + 1, result, limit, g);
				swap(num, start, j);
			}
		}
	}

	private ArrayList<Integer> convertArrayToList(int[] num) {
		ArrayList<Integer> item = new ArrayList<Integer>();
		for (int h = 0; h < num.length; h++) {
			item.add(num[h]);
		}
		return item;
	}

	private void swap(int[] a, int i, int j) {
		int temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}
}
