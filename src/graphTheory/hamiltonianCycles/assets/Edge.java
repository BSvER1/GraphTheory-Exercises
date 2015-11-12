package graphTheory.hamiltonianCycles.assets;

public class Edge {

	Vertex start, end;
	
	public Edge(Vertex start, Vertex end) {
		this.start = start;
		this.end = end;
	}
	
	public Vertex getStart() {
		return start;
	}
	
	public Vertex getEnd() {
		return end;
	}
	
	public void setStart(Vertex start) {
		this.start = start;
	}
	
	public void setEnd(Vertex end) {
		this.end = end;
	}
}
