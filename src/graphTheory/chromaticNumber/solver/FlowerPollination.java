package graphTheory.chromaticNumber.solver;

import java.util.Arrays;
import java.util.Random;

import org.apache.commons.math3.distribution.LevyDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import graphTheory.chromaticNumber.assets.Graph;
import graphTheory.chromaticNumber.loader.Driver;

public class FlowerPollination {

	static boolean FLOWER_TRACE = false;
	
	boolean RANDOM_SEEDED = false;

	Random r;

	Double flowerbed[][];

	double alpha = 1.0;
	double lambda = 1; // sample const
	double stepSize = 1.5;

	Double[] currentBestFlower;
	int currentBestFlowerPos;
	int currentBestCost;

	double switchP = 0.8; // larger = more local pollinations
	int numColours;
	
	LevyDistribution ldist;
	UniformRealDistribution udist;

	public FlowerPollination() {

		r = new Random();

		
		ldist = new LevyDistribution(0, lambda);
		udist = new UniformRealDistribution();
	}

	public void solve(Graph toSolve, int numFlowers, long iterationLimit) {
		
		// do initial setup of conditions
		
		if (FLOWER_TRACE) {
			Driver.trace("Beginning Pollination");
		}

		numColours = toSolve.getMaximalDegree();
		//if (FLOWER_TRACE){
			//Driver.trace("Initial k: "+numColours);
		//}

		long currentIter = 0;
		while (numColours > 0 && currentIter < iterationLimit) {
			Driver.trace("Solving for k: "+numColours);
			int internalIterationLimit = 20000;
			currentBestCost = Integer.MAX_VALUE;
			// init pop of N flowers
			flowerbed = new Double[numFlowers][toSolve.getNumVertices()];
			for (int flowerNum = 0; flowerNum < numFlowers; flowerNum++) {
				for (int currentVertex = 0; currentVertex < toSolve.getNumVertices(); currentVertex++) {
					flowerbed[flowerNum][currentVertex] = (double) Math.round( (udist.sample() * numColours) );
				}
			}

			findSolution(toSolve);
			// solve using those conditions

			internalSolve(toSolve, internalIterationLimit);
			if (FLOWER_TRACE){
				Driver.trace("k = "+numColours+" cost = "+currentBestCost);
			}
			currentIter++;
		}

	}

	private void internalSolve(Graph toSolve, long iterLimit) {
		long currentIter = 0;

		if (evalCost(toSolve, currentBestFlower) != 0) {
			while (numColours > 0 && currentIter < iterLimit) {
				for (int flowerNum = 0; flowerNum < flowerbed.length; flowerNum++) {
					if (FLOWER_TRACE){
						Driver.trace("Pollinating flower "+flowerNum);
					}
					if (udist.sample() > switchP) {
	
						if (FLOWER_TRACE){
							//Driver.trace(getClass(), "Beginning Global Pollination");
						}
						flowerbed[flowerNum] = doGlobalPoll(flowerNum);
	
					} else {
						if (FLOWER_TRACE){
							//Driver.trace(getClass(), "Beginning Local Pollination");
						}
						flowerbed[flowerNum] = doLocalPoll(flowerNum);
	
					}
					
					doDiscAndCorr();
					findSolution(toSolve);
					//Driver.trace("Checking for legal coloring on k = " + numColours);
					//if (FLOWER_TRACE) {
						Driver.trace("Current best flower is: "+ currentBestFlowerPos+" has internals of: ");
						String output = "";
						for (int i = 0; i < currentBestFlower.length; i++) {
							if (i == 0) {
								output = output.concat(""+currentBestFlower[i]);
							} else {
								output = output.concat(", "+currentBestFlower[i]);
							}
						}
						Driver.trace(output);
					//}
					if (evalCost(toSolve, currentBestFlower) == 0) {
						// legal coloring found
						numColours--;
						Driver.trace("Proper Coloring Found, now attempting k = " + numColours);
		
						break;
					}
					
					//if (checkCol(flowerNum)) {
					for (int i = 0; i < Math.sqrt(numColours); i++ ){
						swap(toSolve, flowerNum);
					}
					
					Double[] newFlower = doLocalPoll(flowerNum);
					if (udist.sample() > switchP) {
						newFlower = doGlobalPoll(flowerNum);
					}
					//Double[] newFlower = doGlobalPoll(flowerNum);
					if (evalCost(toSolve, newFlower) < getCost(toSolve, flowerNum)) {
						flowerbed[flowerNum] = newFlower; 
					}
					//}

					doDiscAndCorr();
				}
				findSolution(toSolve);
				//Driver.trace("Checking for legal coloring on k = " + numColours);
				//if (FLOWER_TRACE) {
					Driver.trace("Current best flower is: "+ currentBestFlowerPos+" has internals of: ");
					String output = "";
					for (int i = 0; i < currentBestFlower.length; i++) {
						if (i == 0) {
							output = output.concat(""+currentBestFlower[i]);
						} else {
							output = output.concat(", "+currentBestFlower[i]);
						}
					}
					Driver.trace(output);
				//}
				
				//Driver.trace("Current best cost is:");
				
				if (evalCost(toSolve, currentBestFlower) == 0) {
					// legal coloring found
					numColours--;
					Driver.trace("Proper Coloring Found, now attempting k = " + numColours);
	
					break;
				}
				currentIter++;
			}
		} else {
			// Output best solution
			numColours--;
			Driver.trace("Proper Coloring Found, now attempting k = " + numColours);
			return;
		}

		if (currentIter >= iterLimit) {
			Driver.trace("hit iteration limit on internal solve");
		} 
		
	}

	// g-star
	private void findSolution(Graph toSolve) {

		int cost[] = new int[flowerbed.length];
		
		for (int flowerNum = 0; flowerNum < flowerbed.length; flowerNum++) {
			if (FLOWER_TRACE){
				Driver.trace("Searching for new best solution on flower "+flowerNum);
			}
			cost[flowerNum] = getCost(toSolve, flowerNum);
			if (FLOWER_TRACE){
				Driver.trace("New cost = "+cost[flowerNum]);
			}
			if (currentBestCost > cost[flowerNum]) {
				currentBestFlowerPos = flowerNum;
				currentBestCost = cost[flowerNum];
				//if (FLOWER_TRACE){
					Driver.trace("Cost: "+currentBestCost+" on flower: "+currentBestFlowerPos);
				//}
			}
		}
		
		Driver.trace("flower costs:");
		String output = "";
		for (int i = 0; i < flowerbed.length; i++) {
			if (i == 0) {
				output = output.concat(""+cost[i]);
			}
			if (i == currentBestFlowerPos) {
				output = output.concat(", ["+ cost[i]+"]");
			} else {
				output = output.concat(", "+ cost[i]);
			}
		}
		Driver.trace(output);
		
		currentBestFlower = Arrays.copyOf(flowerbed[currentBestFlowerPos], flowerbed[currentBestFlowerPos].length);

	}

	private int getCost(Graph toSolve, int flowerNum) {
		int sum = 0;
		for (int src = 0; src < toSolve.getNumVertices(); src++) {
			for (int dest = src; dest < toSolve.getNumVertices(); dest++) {
				sum += getConflict(toSolve, flowerbed[flowerNum], src, dest);
				if (FLOWER_TRACE){
					//Driver.trace(getClass(), "Cost updated to: "+sum+" for flower "+flowerNum+" on source "+src+" to destination"+dest);
				}
			}
		}
		//Driver.trace( "Number of conflicts is: "+sum+" for flower "+flowerNum);

		return sum;
	}

	private int getConflict(Graph toSolve, Double[] flower, int srcVert, int destVert) {
		if ((Math.round(flower[srcVert].doubleValue()) == Math.round(flower[destVert].doubleValue())) &&
				toSolve.isEdge(srcVert, destVert)) {
			//if (FLOWER_TRACE){
			//	Driver.trace ("Conflict exists");
			//}
			return 1;
		}
		if (FLOWER_TRACE){
			//Driver.trace(getClass(), "No conflict found");
		}
		return 0;
	}

	private boolean checkCol(int flowerNum) {
		for (Double col = 0.0; col < numColours; col += 1.0) {
			if (!Arrays.asList(flowerbed[flowerNum]).contains(col)) {
				if (FLOWER_TRACE){
					Driver.trace("color "+col+" is not present in flower "+flowerNum);
				}	
				return false;
			}
		}
		if (FLOWER_TRACE){
			Driver.trace("all colors are present and accounted for");
		}
		return true;
	}

	private double levyFlightStep(double lambda, double stepSize) {

		Double levyStep = ldist.sample();
		
		if (FLOWER_TRACE){
			Driver.trace("Levy step is "+levyStep);
		}
		
		if (levyStep.isInfinite()) {
			return 1.0;
		}
		
		return levyStep;

	}

	private Double[] doGlobalPoll(int flowerNum) {
		if (FLOWER_TRACE){
			Driver.trace("Beginning Global Pollination for "+flowerNum);
		}
		return addArrays(flowerbed[flowerNum], applyLevyDist(subtractArrays(currentBestFlower, flowerbed[flowerNum])));

	}

	private Double[] doLocalPoll(int flowerNum) {
		if (FLOWER_TRACE){
			Driver.trace("Beginning Local Pollination for "+flowerNum);
		}
		return addArrays(flowerbed[flowerNum], flowerPermute());
	}

	private Double[] flowerPermute() {
		int flowerOne = (int) Math.floor(udist.sample() * flowerbed.length); //r.nextInt(flowerbed.length);
		int flowerTwo = (int) Math.floor(udist.sample() * flowerbed.length); //r.nextInt(flowerbed.length);
		
		//double scaleFactor = udist.sample();
		
		if (FLOWER_TRACE){
			Driver.trace("Permuting with flowers "+flowerOne+" and "+flowerTwo);
		}
		
		//double scaleFactor = r.nextDouble();

		return applyUniformDist(subtractArrays(flowerbed[flowerOne], flowerbed[flowerTwo]));

	}

	public Double[] subtractArrays(Double[] arrayOne, Double[] arrayTwo) {

		if (arrayOne.length != arrayTwo.length){
			throw new RuntimeException("Lengths differ on subtraction");
		}
		Double[] tmpArray = new Double[arrayOne.length];

		for (int vertNum = 0; vertNum < arrayOne.length; vertNum++) {
			tmpArray[vertNum] = (arrayOne[vertNum] - arrayTwo[vertNum]);
		}
		return tmpArray;
	}

	public Double[] addArrays(Double[] arrayOne, Double[] arrayTwo) {

		if (arrayOne.length != arrayTwo.length){
			throw new RuntimeException("Lengths differ on addition");
		}
		
		Double[] tmpArray = new Double[arrayOne.length];

		for (int vertNum = 0; vertNum < arrayOne.length; vertNum++) {
			tmpArray[vertNum] = (arrayOne[vertNum] + arrayTwo[vertNum]);
		}

		return tmpArray;
	}
	
	public Double[] applyLevyDist(Double[] arrayOne) {
		
		Double[] tmpArray = new Double[arrayOne.length];

		for (int vertNum = 0; vertNum < arrayOne.length; vertNum++) {
			tmpArray[vertNum] = (alpha * levyFlightStep(lambda, stepSize)) * (arrayOne[vertNum]);
		}

		return tmpArray;
	}
	
	public Double[] applyUniformDist(Double[] arrayOne) {
			
			Double[] tmpArray = new Double[arrayOne.length];
	
			for (int vertNum = 0; vertNum < arrayOne.length; vertNum++) {
				tmpArray[vertNum] = udist.sample() * (arrayOne[vertNum]);
			}
	
			return tmpArray;
	}

	private void doDiscAndCorr() {
		for (int flowerNum = 0; flowerNum < flowerbed.length; flowerNum++) {
			for (int vertNum = 0; vertNum < flowerbed[flowerNum].length; vertNum++) {
				flowerbed[flowerNum][vertNum] = (double) Math.round(flowerbed[flowerNum][vertNum]);
				if (flowerbed[flowerNum][vertNum].doubleValue() > (numColours -1)) {
					flowerbed[flowerNum][vertNum] = (double) (numColours -1);
//					if (FLOWER_TRACE){
//						Driver.trace(getClass(), "Too big color reduced");
//					}
					
				} else if (flowerbed[flowerNum][vertNum].doubleValue() < 0.0) {
					flowerbed[flowerNum][vertNum] = 0.0;
//					if (FLOWER_TRACE){
//						Driver.trace(getClass(), "Too small color increased");
//					}
				} else if (flowerbed[flowerNum][vertNum].isInfinite() || flowerbed[flowerNum][vertNum].isNaN()) {
					flowerbed[flowerNum][vertNum] = (double) (numColours -1);
				}
			}
		}

		//System.out.print("");
	}

	private int evalCost(Graph toSolve, Double[] flower) {

		int sum = 0;
		
		for (int src = 0; src < toSolve.getNumVertices(); src++) {
			for (int dest = src; dest < toSolve.getNumVertices(); dest++) {
				sum += getConflict(toSolve, flower, src, dest);
			}
		}
		
		return sum;
	}

	private void swap(Graph toSolve, int flowerNum) {
		//int maxConfNode = 0;
		int maxConf = 0;
		int maxConfPos = 0;
		int sumConf = 0;
		//int maxCol = 0;
		int minCol = numColours;
		//int maxColPos = 0;
		int minColPos = 0;
		int[] colorCount = new int[numColours];
		for (int ii = 0; ii < numColours; ii++) {
			colorCount[ii] = 0;
		}
		for (int vert = 0; vert < toSolve.getNumVertices(); vert++) {
			colorCount[(int) Math.round(flowerbed[flowerNum][vert].doubleValue())]++;
		}
		
		for (int src = 0; src < toSolve.getNumVertices(); src++) {
			sumConf = 0;
			for (int dest = 0; dest < toSolve.getNumVertices(); dest++) {
				sumConf += getConflict(toSolve, flowerbed[flowerNum], src, dest);
			}
			if (FLOWER_TRACE){
				Driver.trace("Current max conflict = "+maxConf+" contender conflict = "+sumConf);
			}
			if (maxConf < sumConf) {
				maxConf = sumConf;
				maxConfPos = src;
			}
			
			//TODO THIS IS BROKEN need to verify operation
//			if (colorCount[(int) Math.round(flowerbed[flowerNum][src].doubleValue())] > maxCol) {
//				maxColPos = src;
//				maxCol++;
//			}
			
			if (colorCount[(int) Math.round(flowerbed[flowerNum][src].doubleValue())] < minCol) {
				minColPos = src;
				minCol--;
			}
			
			System.out.print("");

		}
		Double[] tempFlower = Arrays.copyOf(flowerbed[flowerNum], flowerbed[flowerNum].length);
		tempFlower[maxConfPos] = flowerbed[flowerNum][minColPos].doubleValue();
		tempFlower[minColPos] = flowerbed[flowerNum][maxConfPos].doubleValue();
		if (FLOWER_TRACE){
			Driver.trace("Swap? "+minColPos+" "+maxConfPos);
		}
		
		if (FLOWER_TRACE){
			Driver.trace("Cost: "+getCost(toSolve, flowerNum)+" contender Cost: "+evalCost(toSolve, tempFlower));
		}
		
		if (evalCost(toSolve, tempFlower) < getCost(toSolve, flowerNum)) {
			flowerbed[flowerNum] = tempFlower;

			if (FLOWER_TRACE){
				Driver.trace("Swap Performed");
			}
		}

	}

	public int getResult(){
		if (currentBestCost == 0) {
			return numColours+1;
		}
		return -1;

	}
}


