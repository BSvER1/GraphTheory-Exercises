package graphTheory.chromaticNumber.generator;

import java.util.Random;

import graphTheory.chromaticNumber.assets.Graph;

public class RandomGen {

	Graph generated;
	
	Random r;
	
	
	
	public RandomGen(int vertNum, long edgeLimit, float edgeProb) {
		
		r = new Random();
		generated = new Graph(vertNum);
		
		init(edgeLimit, edgeProb);
	}
	
	private void init(long edgeLimit, float edgeProb) {
		
		int vertA = r.nextInt(generated.getNumVertices()), vertB = r.nextInt(generated.getNumVertices());
		
		for (long i = 0; i < edgeLimit; i++) {
			
			try {
				if ((i % (edgeLimit/25)) == 0) {
			
					System.out.print(".");
				}
			} catch (ArithmeticException e) {}
			
			while (generated.isEdge(vertA, vertB)) {
				vertA = r.nextInt(generated.getNumVertices());
				vertB = r.nextInt(generated.getNumVertices());
			}
			
			if (edgeProb > r.nextFloat()) {
				generated.addEdge(vertA, vertB); 
			}
		}
		System.out.println();
	}
	
	public Graph getGraph() {
		return generated;
	}
	
	
}
