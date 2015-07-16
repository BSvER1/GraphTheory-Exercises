package GraphTheory.BSvER1.structures;

public class BTree {

	private int tol = 5;
	private boolean splitDown = true;
	
	private BTree children[];
	private int data[];
	private int usedCount = 0;
	
	public BTree(int data) {
		this.data[0] = data;
		usedCount++;
		children = new BTree[tol+1];
	}
	
	public void insert(int data) {
		if (usedCount == tol) {
			splitTree();
		}
		
	}
	
	public BTree splitTree() {
		
		BTree medianTree = new BTree(getMedian()); // TODO
		
		return medianTree;
	}
	
	public int getMedian() {
		if ((tol%2) == 1) {// odd number of entries
			return data[tol/2];
		} else { // even number of entries - determine split
			if (!splitDown) {
				return data[tol/2];
			} else {
				return data[(tol/2)-1];
			}
		}
	}
	
	
	
}
