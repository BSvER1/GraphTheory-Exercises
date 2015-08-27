package graphTheory.chromaticNumber.solver;

import java.util.ArrayList;

import graphTheory.chromaticNumber.assets.Bucket;
import graphTheory.chromaticNumber.assets.Graph;
import graphTheory.chromaticNumber.loader.Driver;

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
		if (BB_TRACING) Driver.trace(this.getClass(), "sorting vertex " + vertex);
		boolean broken = false;
		for (int j = 0; j < buckets.size(); j++) {
			if (validBucket(g, buckets.get(j), vertex)) {
				buckets.get(j).insert(vertex);
				broken = true;
				if (BB_TRACING) Driver.trace(this.getClass(), "found valid bucket for vertex " + vertex + " in "+ j);
				break;
			}
		}
		
		if (!broken) {
			if (BB_TRACING) Driver.trace(this.getClass(), "ran out of buckets when searching "
					+ "for a place for "+vertex+", creating a new bucket for it now");
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
	
	public int getResult() {
		return currentBest;
	}
	
	public boolean validBucket(Graph g, Bucket b, int vertex) {
		for (int i = 0; i < g.getNumVertices(); i++) {
			if (b.contains(i)) {
				//Driver.trace(this.getClass(), "comparing "+ vertex+ " with " + i);
				if (g.isEdge(i, vertex)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	
	public ArrayList<ArrayList<Integer>> permute(int[] num, long limit, Graph g) {
		ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
		permute(num, 0, result, limit, g);
		return result;
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
						if (BB_TRACING) Driver.trace(this.getClass(), "found equality in number of buckets, moving on");
						break;
					}
				}
				if (!broken) {
					currentBest = buckets.size();
					Driver.trace(this.getClass(), "successfully found a new best colouring: "+ currentBest);
				}
				
			} else {
				if (BB_TRACING) Driver.trace(this.getClass(), "hit limit of permutations");
				return;
			}
			
			//result.add(item);
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
