package GraphTheory.BSvER1.structures;

public class BTreeNode {
	
	int data[];
	int used;
	
	public BTreeNode(int data, int size) {
		this.data = new int[size];
		this.data[0] = data;
		used++;
	}
	
}
