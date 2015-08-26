package graphTheory.chromaticNumber.assets;

import java.util.ArrayList;

public class Vertex {
	
	String friendlyName;
	int identifier;
	
	ArrayList<Edge> edges;
	
	public Vertex() {
		init();
	}
	
	public Vertex(int identifier, String friendlyName) {
		this.identifier = identifier;
		this.friendlyName = friendlyName;
		
		init();
	}
	
	private void init() {
		edges = new ArrayList<Edge>();
	}
	
}
