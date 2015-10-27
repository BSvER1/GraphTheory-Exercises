package graphTheory.chromaticNumber.solver;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import graphTheory.chromaticNumber.assets.Chromosome;
import graphTheory.chromaticNumber.assets.Graph;
import graphTheory.chromaticNumber.assets.SortedArrayList;
import graphTheory.chromaticNumber.loader.Driver;
import graphTheory.chromaticNumber.loader.ResultsModule;

public class GeneSplicer {

	//Random r;
	
	UniformIntegerDistribution parentPicker, crosspointPicker, colourPicker;
	UniformRealDistribution udist;
	
	SortedArrayList<Chromosome> population;//, newPop;
	
	Chromosome aggregateChromosome;
	
	int currentNumColours;
	
	double wiseCrowdPercent = 0.5;
	double keepDropRatio = 0.75;
	double mutationChance = 0.7;
	double exclusiveMutationChance = 0.7;
	int mutateCount = 1;
	
	int altMethodConflictsCount = 5;
	
	long generationLimit = 20000;
	
	int votingThreshold = 10;
	
	long runStart;
	
	//int generationComfort;
	//int generationComfortLimit = 20000;
	
	//int popSize = 75;
	
	
	
	public GeneSplicer(int numVertices, int numChromosomes) {
		Driver.trace("creating a new gene splicer");
		udist = new UniformRealDistribution();
		parentPicker = new UniformIntegerDistribution(0, numChromosomes-1);
		crosspointPicker = new UniformIntegerDistribution(0, numVertices-1);
	}
	
	public void solve(Graph toSolve, int numChromosomes, long attemptLimit) {
		runStart = System.currentTimeMillis();
		long timeStart = System.currentTimeMillis();
		currentNumColours = toSolve.getMaximalDegree()+1;
		//currentNumColours = 30;
		long currentAttempt = 0;
		aggregateChromosome = new Chromosome(toSolve, currentNumColours);
		
		while (currentAttempt < attemptLimit) {
			if (System.currentTimeMillis() - runStart > 1000*60*60*6) {
				return;
			}
			population = new SortedArrayList<Chromosome>();
			//newPop = new SortedArrayList<Chromosome>();
			colourPicker = new UniformIntegerDistribution(0, currentNumColours);
			//currentAttempt++;
			population.add(aggregateChromosome);
			
			for (int i = 0; i < numChromosomes; i++) {
				population.add(new Chromosome(toSolve, currentNumColours));
			}
			updatePopCosts(toSolve);
			
			Collections.sort(population);
			
			if (!internalSolve(toSolve, numChromosomes, generationLimit)) {
				currentAttempt++;
			} else {
				aggregateChromosome = new Chromosome(toSolve, currentNumColours);
				currentAttempt = 0;
				ResultsModule.writeIncrementalResultToFile(toSolve, GeneSplicer.class, currentNumColours, 
						System.currentTimeMillis()- timeStart, generationLimit);
			}
			
		}
		
		if (currentAttempt >= attemptLimit) {
			Driver.trace("hit attempt limit");
		}
		
	}
	
	private boolean internalSolve(Graph toSolve, int numChromosomes, long iterationLimit) {
		long lastPrintTime = System.currentTimeMillis();
		boolean shouldPrint = false;
		long currentIteration = 0;
		long currentPrintIteration = 0;
		int iterationIncrementThreshold = (int) (numChromosomes * keepDropRatio) - 2;
		//generationComfort = 0;
		//int equalLimit = 3*numChromosomes/4 - 6;
		
		while (currentIteration < iterationLimit && population.get(0).calculateCost(toSolve)!=0) {
			if (System.currentTimeMillis() - runStart > 1000*60*60*6) {
				break;
			}
			
			if (System.currentTimeMillis() - lastPrintTime > 2000) {
					Driver.trace("["+currentPrintIteration+"] beginning to solve with "+currentNumColours+" colours");
					lastPrintTime = System.currentTimeMillis();
					shouldPrint = true;
			}
			currentPrintIteration++;
			if (population.get(iterationIncrementThreshold).calculateCost(toSolve) < votingThreshold)
				currentIteration++;
			int[] parents;
			
			
			//while (newPop.size() < numChromosomes) {
				if (population.get(0).getCost() >= altMethodConflictsCount) {
					parents = getParentsA(toSolve);
				} else {
					if (udist.sample() < exclusiveMutationChance) {
						parents = getParentsC(toSolve);
					} else {
						parents = getParentsB(toSolve);
					}
				}
				Chromosome child = crossover(toSolve, parents, toSolve.getNumVertices());
				
				if (udist.sample() < mutationChance) {
					if (population.get(0).getCost() >= altMethodConflictsCount) {
						child = mutateA(toSolve, child);
					} else {
						child = mutateB(toSolve, child);
					}
				}
				
				
				//Driver.trace("child cost is: " + child.getCost(toSolve));
				//newPop.add(child);
				population.add(child);
				updatePopCosts(toSolve);
				//updateNewPopCosts(toSolve);
			//}

			//Collections.sort(newPop);
				Collections.sort(population);
//			int equalNum = 0;
//			
//			for (int i = 0; i < 3*numChromosomes/4; i++) {
//				//Driver.trace("comparing "+ population.get(i).getCost(toSolve) + " with "+newPop.get(i).getCost(toSolve));
//				if (population.get(i).getCost() == newPop.get(i).getCost()) {
//					equalNum++;
//				}
//			}
//			
//			if (equalNum >= equalLimit) {
//				//Driver.trace("equal "+equalNum+"/"+equalLimit);
//				generationComfort+=2;
//			} else {
//				//Driver.trace("not equal "+equalNum+"/"+equalLimit);
//				if (generationComfort > 0) {
//					generationComfort--;
//				}
//			}
			
			//population = new SortedArrayList<Chromosome>();
//			for (int i = 0; i < newPop.size(); i++) {
//				Chromosome tmp = new Chromosome(toSolve.getNumVertices());
//				for (int j = 0; j < newPop.get(i).getLength(); j++) {
//					tmp.setColour(j, newPop.get(i).getColour(j));
//				}
//				population.insertSorted(toSolve, tmp);
//			}
			//population = newPop;
			
			
			
//			if (currentIteration % 100 == 0) {
//				wisdomOfArtificialCrowds(toSolve);
//				population.insertSorted(toSolve, aggregateChromosome);
//			}
			
			while (population.size() > (numChromosomes * keepDropRatio)) {
				population.remove(population.size()-1);
			}
			while (population.size() < numChromosomes) {
				population.insertSorted(toSolve, new Chromosome(toSolve, currentNumColours));
			}
			//updateCosts(toSolve);
			
			if (shouldPrint) {
				//Driver.trace("finished round of mutation with values:");
				String fitness = "";
				for (int i = 0; i < numChromosomes * keepDropRatio-1; i++) {
					fitness = fitness.concat(""+population.get(i).getCost()+" ");
				}
				Driver.trace(fitness);
				shouldPrint = false;
			}
			Thread.yield();
		}
		
		if (currentIteration >= iterationLimit) {
			wisdomOfArtificialCrowds(toSolve);
			if (aggregateChromosome.calculateCost(toSolve) == 0) {
				Driver.trace("["+currentPrintIteration+"] got a solution with k="+ currentNumColours+" from aggregate");
				currentNumColours--;
				return true;
			}
			Driver.trace("["+currentPrintIteration+"] could not find a colouring on this run");
		} 
		if (population.get(0).calculateCost(toSolve) == 0) {
			Driver.trace("["+currentPrintIteration+"] got a solution with k="+ currentNumColours);
			currentNumColours--;
			return true;
		}

		return false;
	}
	
	private int[] getParentsA(Graph toSolve) {
		int[] parents = new int[2];
		int[] tempParents = parentPicker.sample(4);
		
		for (int i = 0; i <= 1; i++) {
			if (population.get(tempParents[2*i]).getCost() < population.get(tempParents[(2*i)+1]).getCost()) {
				parents[i] = tempParents[2*i];
			} else {
				parents[i] = tempParents[(2*i)+1];
			}
		}
		
		//Driver.trace("choosing parents "+parents[0]+" & "+parents[1]);
		return parents;
	}
	
	private int[] getParentsB(Graph toSolve) {
		return new int[] {0, parentPicker.sample()};
	}
	
	private int[] getParentsC(Graph toSolve) {
		//Driver.trace("choosing parent 0");
		return new int[] {0, 0};
	}
	
	private Chromosome crossover(Graph toSolve, int[] parents, int chromosomeLength) {
		//Driver.trace("beginning crossover");
		int crosspoint = crosspointPicker.sample();
		double first = udist.sample();
		
		Chromosome child = new Chromosome(toSolve, currentNumColours);
		
		if (first < 0.5) {
			for (int i = 0; i < currentNumColours; i++) {
				if (i <= crosspoint) {
					child.setColour(i, population.get(parents[0]).getColour(i));
				} else {
					child.setColour(i, population.get(parents[1]).getColour(i));
				}
			}
		} else {
			for (int i = 0; i < currentNumColours; i++) {
				if (i <= crosspoint) {
					child.setColour(i, population.get(parents[1]).getColour(i));
				} else {
					child.setColour(i, population.get(parents[0]).getColour(i));
				}
			}
		}
		
		return child;
	}
	
	private Chromosome mutateA(Graph toSolve, Chromosome chromosome) {
		//Driver.trace("mutating (A)");
		
		Chromosome child = chromosome.getCopy(toSolve, currentNumColours);
		
		for (int i = 0; i < chromosome.getLength(); i++) {
			boolean shouldMutate = false;
			
			for (int j = 0; j < chromosome.getLength(); j++) {
				if (chromosome.getColour(i) == chromosome.getColour(j) 
						&& toSolve.isEdge(i, j)) {
					shouldMutate = true;
					break;
				}
			}
			
			if (shouldMutate) {
				ArrayList<Integer> adjacentColours = new ArrayList<Integer>();
				for (int j = 0; j < chromosome.getLength(); j++) {
					if (toSolve.isEdge(i, j)) {
						adjacentColours.add(chromosome.getColour(j));
					}
				}
				
				ArrayList<Integer> validColours = new ArrayList<Integer>();
				for (int j = 0; j < currentNumColours; j++) {
					validColours.add(j);
				}
				validColours.removeAll(adjacentColours);
				
				if (!validColours.isEmpty()) {
					if (colourPicker.getSupportUpperBound() != validColours.size()-1) {
						colourPicker = new UniformIntegerDistribution(0, validColours.size()-1);
					}
					child.setColour(i, validColours.get(colourPicker.sample()));
				} else {
					if (colourPicker.getSupportUpperBound() != currentNumColours-1) {
						colourPicker = new UniformIntegerDistribution(0, currentNumColours-1);
					}
					child.setColour(i, colourPicker.sample());
				}
			}
		}
		
		return child;
	}
	
	private Chromosome mutateB(Graph toSolve, Chromosome chromosome) {
		//Driver.trace("mutating (B)");
		
		Chromosome child = chromosome.getCopy(toSolve, currentNumColours);
		
		for (int i = 0; i < chromosome.getLength(); i++) {
			boolean shouldMutate = false;
			
			for (int j = 0; j < chromosome.getLength(); j++) {
				if (chromosome.getColour(i) == chromosome.getColour(j) 
						&& toSolve.isEdge(i, j)) {
					shouldMutate = true;
					break;
				}
			}
			
			if (shouldMutate) {
				if (colourPicker.getSupportUpperBound() != currentNumColours-1) {
					colourPicker = new UniformIntegerDistribution(0, currentNumColours-1);
				}
				child.setColour(i, colourPicker.sample());
			}
		}
		
		return child;
	}
	
	private void wisdomOfArtificialCrowds(Graph toSolve) {
		//Driver.trace("Applying wisdom");
		
		aggregateChromosome = population.get(0).getCopy(toSolve, currentNumColours);
		
		for (int vertNum = 0; vertNum < toSolve.getNumVertices(); vertNum++) {
			for (int toVert = 0; toVert < toSolve.getNumVertices(); toVert++) {
				if (aggregateChromosome.getColour(vertNum) == aggregateChromosome.getColour(toVert)
						&& toSolve.isEdge(vertNum, toVert)) {
					
					int[] colourVote = new int[currentNumColours];
					for (int chromosomeNum = 0; chromosomeNum < population.size() * wiseCrowdPercent; chromosomeNum++) {
						colourVote[population.get(chromosomeNum).getColour(vertNum)]++;
					}
					
					int maxColour = 0;
					for (int maxColourIter = 1; maxColourIter < colourVote.length; maxColourIter++) {
						if (colourVote[maxColour] < colourVote[maxColourIter]) {
							maxColour = maxColourIter;
						}
					}
					
					aggregateChromosome.setColour(vertNum, maxColour);
				}
			}	
		}
		aggregateChromosome.calculateCost(toSolve);
	}
	
	
	private void updatePopCosts(Graph toSolve) {
		//Driver.trace("updating costs");
		
		for (int i = 0; i < population.size(); i++) {
			population.get(i).calculateCost(toSolve);
		}
	}
	
	public int getResult() {
		return currentNumColours+1;
	}
}
