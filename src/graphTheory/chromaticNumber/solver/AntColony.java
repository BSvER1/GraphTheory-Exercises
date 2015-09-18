package graphTheory.chromaticNumber.solver;

import java.util.Random;

import graphTheory.chromaticNumber.assets.Graph;
import graphTheory.chromaticNumber.loader.Driver;


public class AntColony {
	
	final boolean AC_TRACING = true;
	
	Random r;
	
	long[][] vertexAnts; // ants currently in the vertex vertexAnts[vertex][colour] = number of ants at vertex with colour.
	long[][] nextIterationAnts; // ants coming to the vertex next iteration
	int[] currentVertexColour; // currently agreed upon vertex colouring for [vertex]
	
	double commonRate = 1.0; //rate of ants that go to (the first found) edge of the same colour
	long refillAmount;
	int antResult; 
	int currentShortest;
	
	long currentIteration;
	long timeStart;
	
	public AntColony() {
		r = new Random();
	}
	
	private void setInitial(Graph g, int numColours) {
		vertexAnts = new long[g.getNumVertices()][numColours];
		nextIterationAnts = new long[g.getNumVertices()][numColours];
		currentVertexColour = new int[g.getNumVertices()];
		refillAmount = (long) Math.pow(g.getNumVertices(),2);
	}
	
	/**
	 * Solves for the optimal number of colours
	 * @param g Graph to solve.
	 * @param iterationLimit maximum number of iterations to perform in any single simulation
	 * @param numColours the number of colours to try and match. 
	 * @return the number of colours successfully matched before reaching the iteration limit.
	 */
	public int solve(Graph g, long iterationLimit, int numColours) {
		boolean simResult = true;
		timeStart = System.currentTimeMillis();
		int iterationColours = numColours;
		//TODO set initial conditions here
		setInitial(g, numColours); //TODO dont do this, just modify data instead.
		populateInitial(g, numColours); 
		resetNext(g);
		
		while (simResult) {
			//Driver.trace(this.getClass(), "setting initial conditions for ants");
			
			for (int i = 0; i < g.getNumVertices(); i++) {
				determineLargestColour(i, iterationColours);
			}
			//Driver.trace(this.getClass(), "finished setting initial conditions");
			
			simResult = runSimulation(g, iterationLimit, iterationColours);
			if (simResult) {
				Driver.trace(this.getClass(), System.currentTimeMillis() - timeStart, "found graph colouring with "+iterationColours+" colours in "+currentIteration+" cycles");
				iterationColours--;
				
				// shorten arrays, making ants move around a little more. dont discard progress. do this by removing the smallest colouring
				for (int i = 0; i < vertexAnts.length; i++) {
					while (vertexAnts[i][currentShortest] > 0) {
						vertexAnts[i][r.nextInt(vertexAnts[i].length)]++;
						vertexAnts[i][currentShortest]--;
					}
					
					long[] temp = new long[vertexAnts[i].length-1];
					for (int j = 0; j < vertexAnts[i].length-1; j++) {
						if (j < currentShortest) {
							temp[j] = vertexAnts[i][j];
						} else {
							temp[j] = vertexAnts[i][j+1];
						}
					}
					vertexAnts[i] = temp;
				}
				
			} else {
				Driver.trace(this.getClass(), "failed to find graph colouring with "+iterationColours+" colours");
			}
		}
		
		antResult = iterationColours+1;
		return iterationColours;
	}
	
	/**
	 * Performs simulation with given amounts of ants, with a limit on the simulation time.
	 * @param g Graph to perform sim on.
	 * @param iterationLimit maximum number of cycles allowed in any simulation.
	 * @param numColours number of colours to try and match.
	 * @return Returns true if a valid colouring is encountered. otherwise false (reached computation limit)
	 */
	private boolean runSimulation(Graph g, long iterationLimit, int numColours) {
		currentIteration = 0;
		
		while(currentIteration < iterationLimit && !isValidColouring(g)) {
			currentIteration++;
			//Driver.trace(this.getClass(), "Starting iteration "+ currentIteration);
			for (int i = 0; i < g.getNumVertices(); i++) {
				deployForeigners(g, i);
			}
			
			promoteNewToCurrent();
			
			for (int i = 0; i < g.getNumVertices(); i++) {
				determineLargestColour(i, numColours);
			}
			
			breakConflicts(g);
			
			for (int i = 0; i < g.getNumVertices(); i++) {
				determineLargestColour(i, numColours);
			}
			
			for (int i = 0; i < g.getNumVertices(); i++) {
				if (isEmpty(g, i)) {
					refillVertex(g, i, numColours);
				}
			}
			
			for (int i = 0; i < g.getNumVertices(); i++) {
				determineLargestColour(i, numColours);
			}
			currentIteration++;
			currentIteration--;
		}
		
		return isValidColouring(g);
	}

	private void populateInitial(Graph g, int numColours) {
		for (int i = 0; i < g.getNumVertices(); i++) { 
			refillVertex(g, i, numColours);
		}
	}
	
	private void breakConflicts(Graph g) {
		for (int i = 0; i < vertexAnts.length; i++) { //for all vertices
			for (int j = i + 1; j < vertexAnts.length; j++) { // for all other vertices
				if (g.isEdge(i, j) && currentVertexColour[i] == currentVertexColour[j]) {
					//Driver.trace(this.getClass(), "found a conflict. fixing");
					if (vertexAnts[i][currentVertexColour[i]] > vertexAnts[j][currentVertexColour[j]]) {
						// colouring of all ants in j become colour of i
						vertexAnts[i][currentVertexColour[i]] += vertexAnts[j][currentVertexColour[j]];
						vertexAnts[j][currentVertexColour[j]] = 0;
					} else {
						// colouring of all ants in i become colour of j
						vertexAnts[j][currentVertexColour[j]] += vertexAnts[i][currentVertexColour[i]];
						vertexAnts[i][currentVertexColour[i]] = 0;
					}
				}
			}
		}
	}
	
	/**
	 * Reset the array of ants currently in the next position
	 * @param g Graph object with vertices to iterate over.
	 */
	private void resetNext(Graph g) {
		for (int i = 0; i < nextIterationAnts.length; i++) {
			for (int j = 0; j < nextIterationAnts[i].length; j++) {
				nextIterationAnts[i][j] = 0;
			}
		}
	}

	/**
	 * Fill a vertex with n random coloured ants (of one of n colours).
	 * @param g Graph object that vertices are part of.
	 * @param vertex Specified vertex to fill with ants.
	 * @param numColours the number of colours to spread ants across
	 */
	private void refillVertex(Graph g, int vertex, int numColours) {
		//Driver.trace(this.getClass(), "found that vertex "+ vertex+ " has no ants. filling it back up");
		for (int k = 0; k < refillAmount; k++) {
			vertexAnts[vertex][r.nextInt(numColours)]++;
		}
	}

	/**
	 * shift all ants currently in the next iteration phase to the current phase by adding them to the current pool.
	 * resets the next iteration ant pool ready to start again.
	 */
	private void promoteNewToCurrent() {
		//Driver.trace(this.getClass(), "promoting ants waiting to come into vertices to counts");
		for (int i = 0; i < vertexAnts.length; i++) {
			for (int j = 0; j < vertexAnts[i].length; j++) {
				vertexAnts[i][j] += nextIterationAnts[i][j];
				nextIterationAnts[i][j] = 0;
			}
		}
	}
	
	/**
	 * Determine the current optimal colouring of a vertex. split ties by the lower vertex number.
	 * This method updates the global variable {@code currentVertexColour} at {@code vertex} at the same time. 
	 * @param vertex The vertex to consider when counting the ants
	 * @return the colouring the vertex currently has as voted on by the ants currently there.
	 */
	private int determineLargestColour(int vertex, int numColours) {
		currentVertexColour[vertex] = 0;
		currentShortest = 0;
		
		for (int i = 1; i < vertexAnts[vertex].length; i++) {
			if (vertexAnts[vertex][currentVertexColour[vertex]] < vertexAnts[vertex][i]) {
				currentVertexColour[vertex] = i;
			}
			
			if (vertexAnts[vertex][currentShortest] > vertexAnts[vertex][i]) {
				currentShortest = i;
			}
		}
		//Driver.trace(this.getClass(), "found largest colour of vertex "+vertex+" to be "+currentVertexColour[vertex]);
		return currentVertexColour[vertex];
	}

	/**
	 * Send ants that don't suit the current vertex colouring to the adjacent vertices.<br>
	 * 
	 * <b>Note:</b> must be run <u>AFTER</u> determineLargestColour()!
	 * @param g Graph containing vertices and edges.
	 * @param vertex Vertex to send ants away from.
	 */
	private void deployForeigners(Graph g, int vertex) {
		//Driver.trace(this.getClass(), "beginning deployment of ants from vertex "+ vertex);
		for (int i = 0; i < vertexAnts[vertex].length; i++) {
			if (i != currentVertexColour[i] && g.isEdge(vertex, i)) {
				int sendTo = findAdjacentWithColour(g, vertex, i);
				if (sendTo >= 0) {
					nextIterationAnts[sendTo][i] += (int) (commonRate * vertexAnts[vertex][i]);
					vertexAnts[vertex][i] -= (int) (commonRate * vertexAnts[vertex][i]);
				}
				
				while (vertexAnts[vertex][i] > 0) {
					sendTo = r.nextInt(g.getNumVertices());
					if (g.isEdge(vertex, sendTo)) {
						nextIterationAnts[sendTo][i]++;
						vertexAnts[vertex][i]--;
					}
				}
			}
		}
		//Driver.trace(this.getClass(), "finished deploying ants from vertex "+vertex);
	}
	
	/**
	 * Finds an adjacent vertex with the specified colour.
	 * @param g Graph containing vertices and edges.
	 * @param src number of the source vertex
	 * @param col id of the colour to match
	 * @return the vertex number or -1 if no such vertex exists.
	 */
	private int findAdjacentWithColour(Graph g, int src, int col) {
		for (int i = 0; i < g.getNumVertices(); i++) {
			if (g.isEdge(src, i)) { // is edge adjacent?
				if (currentVertexColour[i] == col) { // is adjacent vertex the same colour.
					//Driver.trace(this.getClass(), "found that vertex "+i+" has colour matching vertex "+src+" ("+col+")");
					return i; // return this vertex
				}
			}
		}
		//Driver.trace(this.getClass(), "could not find a vertex adjacent to "+src+ " with colour "+ col);
		return -1; // else return a reserved response to indicate failure.
	}
	
	/**
	 * Check to see if the current graph colouring is valid
	 * @param g Graph to check
	 * @return Boolean indicating whether the graph colouring is valid or not.
	 */
	private boolean isValidColouring(Graph g) {
		for (int start = 0; start < g.getNumVertices(); start++) {
			for (int end = 0; end < g.getNumVertices(); end++) {
				if (start != end && g.isEdge(start, end) 
						&& currentVertexColour[start] == currentVertexColour[end]) {
					//Driver.trace(this.getClass(), "found that the current colouring is invalid");
					return false;
				}
			}
		}
		
		//Driver.trace(this.getClass(), "found that the current colouring is valid");
		return true;
	}
	
	/**
	 * Determine if all the ants have left a vertex. Indicates that it needs a refill.
	 * @param g Graph of vertices and edges
	 * @param vertex vertex to check
	 * @return return if the vertex is empty or not.
	 */
	private boolean isEmpty(Graph g, int vertex) {
		for(int i = 0; i < vertexAnts[vertex].length; i++) {
			if (vertexAnts[vertex][i] > 0) {
				return false;
			}
		}
		
		return true;
	}

	public int getResult() {
		return antResult;
	}
}
