package GraphTheory.BSvER1.structures;

public class BSTree {
	
	BSTree left, right;
	int data;
	boolean isNull = true;
	
	public BSTree() {
		left = null;
		right = null;
	}
	
	public BSTree(int data) {
		this.data = data;
		isNull = false;
		left = null;
		right = null;
	}
	
	public void insert(int data) {
		if (isNull) {
			this.data = data;
		} else {
			if (this.data < data) { //move right
				right.insert(data);
			} else if (this.data > data) { // move left
				left.insert(data);
			} else { // same value as current entry.
				// dont do anything
			}
		}
	}

}
