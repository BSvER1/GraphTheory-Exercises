package graphTheory.chromaticNumber.assets;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;

public class Chromosome implements Comparable<Chromosome>{
	
	UniformIntegerDistribution uIntDist;
	
	int cost;
	
	int[] colours;
	
	public Chromosome(Graph toSolve, int numColours) {
		uIntDist = new UniformIntegerDistribution(0, numColours-1);
		
		colours = new int[toSolve.getNumVertices()];
		for (int i = 0; i < toSolve.getNumVertices(); i++) {
			colours[i] = uIntDist.sample();
		}
		
		calculateCost(toSolve);
	}
	
	public Chromosome(int length) {
		colours = new int[length];
	}
	
	public int getCost() {
		return cost;
	}
	
	public int calculateCost(Graph toSolve) {
		int sum = 0;
		
		for (int i = 0; i < colours.length; i++) {
			for (int j = i; j < colours.length; j++) {
				if (colours[i] == colours[j] && toSolve.isEdge(i, j)) {
					sum++;
				}
			}
		}
		
		cost = sum;
		return sum;
	}
	
	public void setColour(int pos, int value) {
		colours[pos] = value;
	}
	
	public int getColour(int pos) {
		return colours[pos];
	}
	
	public int getLength() {
		return colours.length;
	}
	
	public int[] getColourArray() {
		return colours;
	}
	
	public Chromosome getCopy(Graph toSolve, int numColours) {
		Chromosome child = new Chromosome(toSolve, numColours);
		for (int i = 0; i < getLength(); i++) {
			child.setColour(i, getColour(i));
		}
		return child;
	}

	@Override
	public int compareTo(Chromosome o) {
		if (o.cost < this.cost) {
			return 1;
		} else if (o.cost == this.cost) {
			return 0;
		} else {
			return -1;
		}
	}
}
