package graphTheory.chromaticNumber.assets;

import java.util.ArrayList;

import graphTheory.chromaticNumber.loader.Driver;

//import java.util.ArrayList;

public class Graph {
	
	boolean directed = true;
	boolean sparce;
	
	double density;
	private int numVertices;

	private String graphName;
	//public ArrayList<Vertex> vertices;
	private int[][] adj;
	
	ArrayList<Edge> edges;
	
	public Graph(int numVertices, long numEdges) {
		//vertices = new ArrayList<Vertex>(numVertices);
		density = (double)numEdges/(double)(numVertices * (numVertices-1));
		this.numVertices = numVertices;
		
		if (density < 1.0/64.0) {
			//sparce = true;
			sparce = false;
		} else {
			sparce = false;
		}
		
		Driver.trace("have graph with density "+ density+", using sparce mode: "+sparce);
		
		if (!sparce) {
			adj = new int[numVertices][numVertices];

			for (int i = 0; i < numVertices; i ++) {
				for (int j = 0; j < numVertices; j++) {
					adj[i][j] = 0;
				}
			}
		} else {
			edges = new ArrayList<Edge>();
		}
	}
	
	public void addEdge(int from, int to) {
		if (!sparce) {
			adj[from][to]++;

			if (!directed && !isEdge(to,from)) {
				adj[to][from]++;
			}
		} else {
			edges.add(new Edge(new Vertex(from), new Vertex(to)));
		}
	}
	
	public boolean isEdge(int from, int to) {
		if (!sparce)
			return adj[from][to] > 0;
		else {
			for (int i = 0; i < edges.size(); i++) {
				if (edges.get(i).getStart().getID() == from && edges.get(i).getEnd().getID() == to) {
					return true;
				}
			}
		}
		return false;
	}
	
	public int getEdgeCount(int from, int to) {
		if (!sparce) 
			return adj[from][to];
		else {
			int sum = 0;
			for (int i = 0; i < edges.size(); i++) {
				if (edges.get(i).getStart().getID() == from && edges.get(i).getEnd().getID() == to) {
					sum++;
				}
			}
			return sum;
		}
	}
	
//	public void printAdjacency(int from) {
//		System.out.println("Printing adjacency of vertex "+ from +":");
//		for (int i = 0; i < adj.length; i++) {
//			if (adj[from][i] > 0) {
//				System.out.print(i+ " ");
//			}
//		}
//		System.out.println();
//	}
	
	public int getNumVertices() {
		return numVertices;
	}
	
	public int getMaximalDegree() {
		if (!sparce) {
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
			
		} else {
			int[] edgeCount = new int[numVertices];
			for (int i = 0; i < numVertices; i++) {
				for (int j = i; j < numVertices; j++) {
					if (isEdge(i,j)) {
						edgeCount[i]++;
						edgeCount[j]++;
					}
				}
			}
			int currentLargest = 0;
			for (int i = 0; i < numVertices; i++) {
				if (edgeCount[i] > currentLargest) {
					currentLargest = edgeCount[i];
				}
			}
			return currentLargest;
		}
	}

	public String getGraphName() {
		return graphName;
	}

	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}
}
