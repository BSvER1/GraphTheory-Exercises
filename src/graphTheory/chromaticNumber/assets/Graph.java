package graphTheory.chromaticNumber.assets;

//import java.util.ArrayList;

public class Graph {
	
	final boolean DIRECTED = false;

	public String graphName;
	//public ArrayList<Vertex> vertices;
	public boolean[][] adj;
	
	public Graph(int numVertices) {
		//vertices = new ArrayList<Vertex>(numVertices);
		adj = new boolean[numVertices][numVertices];
		
		for (int i = 0; i < numVertices; i ++) {
			for (int j = 0; j < numVertices; j++) {
				adj[i][j] = false;
			}
		}
	}
	
	public void addEdge(int from, int to) {
		adj[from][to] = true;
		
		if (!DIRECTED) {
			adj[to][from] = true;
		}
	}
	
	public boolean isEdge(int from, int to) {
		return adj[from][to];
	}
	
	public void printAdjacency(int from) {
		System.out.println("Printing adjacency of vertex "+ from +":");
		for (int i = 0; i < adj.length; i++) {
			if (adj[from][i]) {
				System.out.print(i+ " ");
			}
		}
		System.out.println();
	}
	
	public int getNumVertices() {
		return adj.length;
	}
	
	public int getMaximalDegree() {
		int maxDegree = 0;
		for (int i = 0; i < getNumVertices(); i++) {
			int sum = 0;
			for (int j = 0; j < getNumVertices(); j++) {
				if (isEdge(i,j)) {
					sum++;
				}
			}
			if (sum > maxDegree) {
				maxDegree = sum;
			}
		}
		
		
		return maxDegree;
	}
}
