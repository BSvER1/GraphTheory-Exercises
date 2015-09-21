package graphTheory.chromaticNumber.assets;

public class Bucket {

	boolean[] verts;

	public Bucket(int numVertices) {
		verts = new boolean[numVertices];
		for (int i = 0; i < numVertices; i++) {
			verts[i] = false;
		}
	}

	public boolean contains(int vertex) {
		return verts[vertex];
	}

	public void insert(int vertex) {
		verts[vertex] = true;
	}

}
