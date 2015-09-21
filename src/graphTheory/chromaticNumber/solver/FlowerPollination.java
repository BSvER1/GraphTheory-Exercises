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
	double lambda = 1.0; // sample const
	double stepSize = 1.0;

	int currentBestFlower;
	int currentBestCost;

	double switchP = 0.5; // larger = more local pollinations
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
			Driver.trace(getClass(), "Beginning Pollination");
		}

		numColours = toSolve.getMaximalDegree() + 1;
		if (FLOWER_TRACE){
			Driver.trace(getClass(), "Initial k: "+numColours);
		}

		long currentIter = 0;
		while (currentIter < iterationLimit) {
			int internalIterationLimit = 100000;
			// init pop of N flowers
			flowerbed = new Double[numFlowers][toSolve.getNumVertices()];
			for (int flowerNum = 0; flowerNum < numFlowers; flowerNum++) {
				for (int currentVertex = 0; currentVertex < toSolve.getNumVertices(); currentVertex++) {
					flowerbed[flowerNum][currentVertex] = (double) (udist.sample() * numColours);
				}
			}

			findSolution(toSolve);
			// solve using those conditions

			internalSolve(toSolve, internalIterationLimit);
			if (FLOWER_TRACE){
				Driver.trace(getClass(), "k = "+numColours+" cost = "+currentBestCost);
			}
			currentIter++;
		}

	}

	private void internalSolve(Graph toSolve, long iterLimit) {
		long currentIter = 0;
		currentBestCost = Integer.MAX_VALUE;

		while (currentIter < iterLimit) {
			for (int flowerNum = 0; flowerNum < flowerbed.length; flowerNum++) {
				if (FLOWER_TRACE){
					Driver.trace(getClass(), "Pollinating flower "+flowerNum);
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

				if (checkCol(flowerNum)) {
					swap(toSolve, flowerNum);
					Double[] newFlower = doGlobalPoll(flowerNum);
					if (evalCost(toSolve, newFlower) < getCost(toSolve, flowerNum)) {
						
						flowerbed[flowerNum] = newFlower; 
					}
				}
			}
			findSolution(toSolve);
			if (currentBestCost == 0) {
				// legal coloring found
				numColours--;
				//if (FLOWER_TRACE){
					Driver.trace(getClass(), "Proper Coloring Found, now attempting k = " + numColours);
				//}
				break;
			}
			currentIter++;
		}

		if (currentIter >= iterLimit) {
			Driver.trace(getClass(), "hit iteration limit on internal solve");
		}
		// Output best solution
	}

	// g-star
	private void findSolution(Graph toSolve) {

		for (int flowerNum = 0; flowerNum < flowerbed.length; flowerNum++) {
			if (FLOWER_TRACE){
				Driver.trace(getClass(), "Searching for new best solution on flower "+flowerNum);
			}
			int newCost = getCost(toSolve, flowerNum);
			if (FLOWER_TRACE){
				Driver.trace(getClass(), "New cost = "+newCost);
			}
			if (currentBestCost > newCost) {
				currentBestFlower = flowerNum;
				currentBestCost = newCost;
				//if (FLOWER_TRACE){
					Driver.trace(getClass(), "Cost: "+currentBestCost+" on flower: "+currentBestFlower);
				//}
			}
		}
	}

	private int getCost(Graph toSolve, int flowerNum) {
		int sum = 0;
		for (int src = 0; src < toSolve.getNumVertices(); src++) {
			for (int dest = 0; dest < toSolve.getNumVertices(); dest++) {
				sum += getConflict(toSolve, flowerbed[flowerNum], src, dest);
				if (FLOWER_TRACE){
					//Driver.trace(getClass(), "Cost updated to: "+sum+" for flower "+flowerNum+" on source "+src+" to destination"+dest);
				}
			}
		}
		return sum;
	}

	private int getConflict(Graph toSolve, Double[] flower, int srcVert, int destVert) {
		if (flower[srcVert].equals(flower[destVert]) && toSolve.isEdge(srcVert, destVert)) {
			if (FLOWER_TRACE){
				//Driver.trace(getClass(), "Conflict exists");
			}
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
					Driver.trace(getClass(), "color "+col+" is not present in flower "+flowerNum);
				}	
				return false;
			}
		}
		if (FLOWER_TRACE){
			Driver.trace(getClass(), "all colors are present and accounted for");
		}
		return true;
	}

	private double levyFlightStep(double lambda, double stepSize) {

		double levyStep = ldist.sample();
		
		if (FLOWER_TRACE){
			Driver.trace(getClass(), "Levy step is "+levyStep);
		}
		
		return levyStep;

	}

	private Double[] doGlobalPoll(int flowerNum) {
		if (FLOWER_TRACE){
			Driver.trace(getClass(), "Beginning Global Pollination for "+flowerNum);
		}
		return addArrays(flowerbed[flowerNum], applyLevyDist(subtractArrays(flowerbed[currentBestFlower], flowerbed[flowerNum])));

	}

	private Double[] doLocalPoll(int flowerNum) {
		if (FLOWER_TRACE){
			Driver.trace(getClass(), "Beginning Local Pollination for "+flowerNum);
		}
		return addArrays(flowerbed[flowerNum], flowerPermute());
	}

	private Double[] flowerPermute() {
		int flowerOne = (int) Math.floor(udist.sample() * flowerbed.length); //r.nextInt(flowerbed.length);
		int flowerTwo = (int) Math.floor(udist.sample() * flowerbed.length); //r.nextInt(flowerbed.length);
		
		//double scaleFactor = udist.sample();
		
		if (FLOWER_TRACE){
			Driver.trace(getClass(), "Permuting with flowers "+flowerOne+" and "+flowerTwo);
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
					flowerbed[flowerNum][vertNum] = (double) numColours -1;
					if (FLOWER_TRACE){
						//Driver.trace(getClass(), "Too big color reduced");
					}
					
				} else if (flowerbed[flowerNum][vertNum].doubleValue() < 0.0) {
					flowerbed[flowerNum][vertNum] = 0.0;
					if (FLOWER_TRACE){
						//Driver.trace(getClass(), "Too small color increased");
					}
				}
			}
		}

		//System.out.print("");
	}

	private int evalCost(Graph toSolve, Double[] flower) {

		int sum = 0;
		for (int src = 0; src < toSolve.getNumVertices(); src++) {
			for (int dest = 0; dest < toSolve.getNumVertices(); dest++) {
				sum += getConflict(toSolve, flower, src, dest);
			}
		}
		
		return sum;
	}

	private void swap(Graph toSolve, int flowerNum) {
		//int maxConfNode = 0;
		int maxConf = 0;
		int sumConf = 0;
		int maxCol = 0;
		int minCol = numColours;
		int maxColPos = 0;
		int minColPos = 0;
		int[] colorCount = new int[numColours];
		for (int ii = 0; ii < numColours; ii++) {
			colorCount[ii] = 0;
		}

		for (int src = 0; src < toSolve.getNumVertices(); src++) {
			sumConf = 0;
			for (int dest = 0; dest < toSolve.getNumVertices(); dest++) {
				sumConf += getConflict(toSolve, flowerbed[flowerNum], src, dest);
			}
			if (FLOWER_TRACE){
				Driver.trace(getClass(), "Current max conflict = "+maxConf+" contender conflict = "+sumConf);
			}
			if (maxConf < sumConf) {
				maxConf = sumConf;
				//maxConfNode = src;
			}
			colorCount[(int) Math.round(flowerbed[flowerNum][src].doubleValue())]++;
			
			//TODO THIS IS BROKEN need to verify operation
			if (colorCount[(int) Math.round(flowerbed[flowerNum][src].doubleValue())] > maxCol) {
				maxColPos = src;
				maxCol++;
			}
			
			if (colorCount[(int) Math.round(flowerbed[flowerNum][src].doubleValue())] < minCol) {
				minColPos = src;
				minCol--;
			}
			
			System.out.print("");

		}
		Double[] tempFlower = Arrays.copyOf(flowerbed[flowerNum], flowerbed[flowerNum].length);
		tempFlower[maxColPos] = flowerbed[flowerNum][minColPos].doubleValue();
		tempFlower[minColPos] = flowerbed[flowerNum][maxColPos].doubleValue();
		if (FLOWER_TRACE){
			Driver.trace(getClass(), "Swap? "+minColPos+" "+maxColPos);
		}
		
		if (FLOWER_TRACE){
			Driver.trace(getClass(), "Cost: "+getCost(toSolve, flowerNum)+" contender Cost: "+evalCost(toSolve, tempFlower));
		}
		
		if (evalCost(toSolve, tempFlower) < getCost(toSolve, flowerNum)) {
			flowerbed[flowerNum] = tempFlower;

			if (FLOWER_TRACE){
				Driver.trace(getClass(), "Swap Performed");
			}
		}

	}

	public int getResult(){
		if (currentBestCost == 0) {
			return numColours;
		}
		return -1;

	}
}


