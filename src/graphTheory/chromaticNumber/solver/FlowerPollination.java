package graphTheory.chromaticNumber.solver;

import java.util.Arrays;
import java.util.Random;

import graphTheory.chromaticNumber.assets.Graph;
import graphTheory.chromaticNumber.loader.Driver;

public class FlowerPollination {

	static boolean FLOWER_TRACE = true;

	Random r;

	Double flowerbed[][];

	double alpha = 1.0;
	double lambda = 1.5;
	double stepSize = 1.0;

	int currentBestFlower;
	int currentBestCost;

	double switchP = 0.8;
	int numColours;

	public FlowerPollination() {

		r = new Random();

	}

	public void solve(Graph toSolve, int numFlowers, long iterationLimit) {

		// do initial setup of conditions
		
		if (FLOWER_TRACE) {
			Driver.trace(getClass(), "Beginning Pollination");
		}

		numColours = toSolve.getNumVertices(); //toSolve.getMaximalDegree() + 1;
		if (FLOWER_TRACE){
			Driver.trace(getClass(), "Initial k: "+numColours);
		}

		long currentIter = 0;
		while (currentIter < iterationLimit) {
			int internalIterationLimit = 1;
			// init pop of N flowers
			flowerbed = new Double[numFlowers][toSolve.getNumVertices()];
			for (int flowerNum = 0; flowerNum < numFlowers; flowerNum++) {
				for (int currentVertex = 0; currentVertex < toSolve.getNumVertices(); currentVertex++) {
					flowerbed[flowerNum][currentVertex] = (double) r.nextInt(numColours);
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
				if (r.nextDouble() > switchP) {

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
					if (evalCost(toSolve, newFlower) < getCost(toSolve, flowerNum)) {// returns
						// a
						flowerbed[flowerNum] = newFlower; // solution
						// vector to
						// be
						// compared
						// with
						// current
					}
				}
			}
			findSolution(toSolve);
			if (currentBestCost == 0) {
				// legal coloring found
				numColours--;
				if (FLOWER_TRACE){
					Driver.trace(getClass(), "Proper Coloring Found, now attempting k = " + numColours);
				}
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
				if (FLOWER_TRACE){
					Driver.trace(getClass(), "Cost: "+currentBestCost+" on flower: "+currentBestFlower);
				}
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
		if (flower[srcVert] == flower[destVert] && toSolve.isEdge(srcVert, destVert)) {
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
		for (Double col = 0.0; col < numColours; col++) {
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
		double levyStep = (lambda * gamma(lambda) * Math.sin(Math.PI * lambda / 2)) / (Math.PI * Math.pow(stepSize, (1 + lambda)));
		if (FLOWER_TRACE){
			Driver.trace(getClass(), "Levy step is "+levyStep);
		}
		return levyStep;
	}

	private Double[] doGlobalPoll(int flowerNum) {
		if (FLOWER_TRACE){
			Driver.trace(getClass(), "Beginning Global Pollination for "+flowerNum);
		}
		return addArrays(flowerbed[flowerNum], subtractArrays(flowerbed[currentBestFlower], flowerbed[flowerNum],
				(alpha * levyFlightStep(lambda, stepSize))), 1.0);

	}

	private Double[] doLocalPoll(int flowerNum) {
		if (FLOWER_TRACE){
			Driver.trace(getClass(), "Beginning Local Pollination for "+flowerNum);
		}
		return addArrays(flowerbed[flowerNum], flowerPermute(), 1.0);
	}

	private Double[] flowerPermute() {
		int flowerOne = r.nextInt(flowerbed.length);
		int flowerTwo = r.nextInt(flowerbed.length);
		
		if (FLOWER_TRACE){
			Driver.trace(getClass(), "Permuting with flowers "+flowerOne+" and "+flowerTwo);
		}
		
		double scaleFactor = r.nextDouble();

		return subtractArrays(flowerbed[flowerOne], flowerbed[flowerTwo], scaleFactor);

	}

	public Double[] subtractArrays(Double[] arrayOne, Double[] arrayTwo, double scale) {

		if (arrayOne.length != arrayTwo.length){
			throw new RuntimeException("Lengths differ on subtraction");
		}
		Double[] tmpArray = new Double[arrayOne.length];

		for (int vertNum = 0; vertNum < arrayOne.length; vertNum++) {
			tmpArray[vertNum] = scale * (arrayOne[vertNum] - arrayTwo[vertNum]);
		}
		return tmpArray;
	}

	public Double[] addArrays(Double[] arrayOne, Double[] arrayTwo, double scale) {

		if (arrayOne.length != arrayTwo.length){
			throw new RuntimeException("Lengths differ on addition");
		}
		
		Double[] tmpArray = new Double[arrayOne.length];

		for (int vertNum = 0; vertNum < arrayOne.length; vertNum++) {
			tmpArray[vertNum] = scale * (arrayOne[vertNum] + arrayTwo[vertNum]);
		}

		return tmpArray;
	}

	public static double logGamma(double x) {
		double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
		double ser = 1.0 + 76.18009173 / (x + 0) - 86.50532033 / (x + 1) + 24.01409822 / (x + 2) - 1.231739516 / (x + 3)
				+ 0.00120858003 / (x + 4) - 0.00000536382 / (x + 5);
		return tmp + Math.log(ser * Math.sqrt(2 * Math.PI));
	}

	public static double gamma(double x) {
		if (FLOWER_TRACE){
			Driver.trace(FlowerPollination.class, "Gamma sample = "+Math.exp(logGamma(x)));
		}
		return Math.exp(logGamma(x));
	}

	private void doDiscAndCorr() {
		for (int flowerNum = 0; flowerNum < flowerbed.length; flowerNum++) {
			for (int vertNum = 0; vertNum < flowerbed[flowerNum].length; vertNum++) {
				flowerbed[flowerNum][vertNum] = (double) Math.round(flowerbed[flowerNum][vertNum]);
				if (flowerbed[flowerNum][vertNum] > numColours -1) {
					flowerbed[flowerNum][vertNum] = (double) numColours -1;
					if (FLOWER_TRACE){
						Driver.trace(getClass(), "Too big color reduced");
					}
					
				} else if (flowerbed[flowerNum][vertNum] < 0.0) {
					flowerbed[flowerNum][vertNum] = 0.0;
					if (FLOWER_TRACE){
						Driver.trace(getClass(), "Too small color increased");
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
		int maxConfNode = 0;
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
				Driver.trace(getClass(), "CUrrent max conflict = "+maxConf+" contender conflict = "+sumConf);
			}
			if (sumConf > maxConf) {
				maxConf = sumConf;
				maxConfNode = src;
			}
			colorCount[(int) Math.round(flowerbed[flowerNum][src])]++;
			if (colorCount[(int) Math.round(flowerbed[flowerNum][src])] > maxCol) {
				maxColPos = src;
				maxCol++;
			}
			if (colorCount[(int) Math.round(flowerbed[flowerNum][src])] < minCol) {
				minColPos = src;
				minCol--;
			}

		}
		Double[] tempFlower = Arrays.copyOf(flowerbed[flowerNum], flowerbed[flowerNum].length);
		tempFlower[maxColPos] = flowerbed[flowerNum][minColPos];
		tempFlower[minColPos] = flowerbed[flowerNum][maxColPos];
		if (FLOWER_TRACE){
			Driver.trace(getClass(), "Swap? "+minColPos+" "+maxColPos);
		}
		
		if (FLOWER_TRACE){
			Driver.trace(getClass(), "Cost: "+getCost(toSolve, flowerNum)+" contender Cost: "+evalCost(toSolve, tempFlower));
		}
		
		if (evalCost(toSolve, tempFlower) <= getCost(toSolve, flowerNum)) {
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


