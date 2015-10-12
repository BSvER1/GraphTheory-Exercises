package graphTheory.chromaticNumber.solver;

import java.awt.EventQueue;
//import java.util.Random;

import javax.swing.JFrame;

import org.apache.commons.math3.distribution.UniformRealDistribution;

import graphTheory.chromaticNumber.assets.Graph;
import graphTheory.chromaticNumber.assets.Universe;
import graphTheory.chromaticNumber.loader.Driver;
import graphTheory.chromaticNumber.loader.ResultsModule;
import net.miginfocom.swing.MigLayout;

public class SecretAgents {

	private boolean VISUALS_ENABLED = true;

	UniformRealDistribution udist;

	public static boolean SECRET_TRACING = false;
	boolean SLOW_MODE = true;
	boolean WELLS_MOVE = false;

	int SLOW_SPEED = 5; // larger is slower. default to 2

	static long currentInternalIterationNum;
	static long currentIterationNum;
	static long currentLargestWellComfort;
	
	long numInternalIterations = 200000;
	long runStart;

	boolean successful;
	boolean solutionFound;

	static int currentBest;

	private JFrame frame;
	private SecretAgentPreview sap;

	Universe u;

	public SecretAgents(String graphName) {

		udist = new UniformRealDistribution();

		if (VISUALS_ENABLED) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						frame = new JFrame("Secret Agents Chromatic Number Solver: " +graphName);
						frame.setBounds(100, 100, 850, 850);
						frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						frame.getContentPane().setLayout(new MigLayout("",
								"[0px,growprio 50,grow,shrinkprio 50][800px][0px,growprio 50,grow,shrinkprio 50]",
								"[0px,growprio 50,grow,shrinkprio 50][800px][0px,growprio 50,grow,shrinkprio 50]"));
						sap = new SecretAgentPreview();
						frame.add(sap, "cell 1 1");
						frame.setVisible(true);
						frame.setAlwaysOnTop(true);
						frame.setAlwaysOnTop(false);
						frame.requestFocus();
					} catch (Exception e) {
					}
				}
			});
		}
	}
	
	public SecretAgents(String graphName, boolean unmanaged) {

		udist = new UniformRealDistribution();
	}

	public void solve(Graph toSolve, long iterationLimit) {
		runStart = System.currentTimeMillis();
		long lastPrintTime = System.currentTimeMillis();
		long timeStart = System.currentTimeMillis();
		currentIterationNum = 0;

		int currentNumColours = toSolve.getMaximalDegree() + 1;
		currentBest = currentNumColours;

		//do {

		while (currentNumColours > 0 && currentIterationNum < iterationLimit) {
			if (System.currentTimeMillis() - runStart > 1000*60*60*6) {
				break;
			}
			currentNumColours--;

			Driver.trace(System.currentTimeMillis() - timeStart, "solving with " + currentNumColours + " colours");

			currentInternalIterationNum = 0;

			setupUniverse(toSolve, currentNumColours);

			// do while solution not found and computation limit is not
			// reached
			do {

				if (SLOW_MODE) {
					try {
						Thread.sleep(SLOW_SPEED);
					} catch (InterruptedException e) {
					}
				}
				
				if (System.currentTimeMillis() - lastPrintTime > 2000) {
					Driver.trace("beginning iteration "+ currentInternalIterationNum);
					lastPrintTime = System.currentTimeMillis();
				}
				currentInternalIterationNum++;

				boolean captureSuccessful = false;
				// if agents not captured and are close enough to be
				// captured
				for (int currentAgent = 0; currentAgent < Universe.getAgents().size(); currentAgent++) {
					if (tryCapture(currentAgent) && !captureSuccessful) {
						captureSuccessful = true;
					}
					// captureSuccessful = tryCapture(currentAgent);
				}

				// move all agents (method checks if they are captured
				// itself)
				for (int currentAgent = 0; currentAgent < Universe.getAgents().size(); currentAgent++) {
					doGravity(currentAgent);
				}

				for (int currentWell = 0; currentWell < Universe.getWells().size(); currentWell++) {
					// apply movement on the wells
					if (WELLS_MOVE) {
						Universe.getWells().get(currentWell).applyVel();
					}

					ejectAgents(currentWell);

					if (captureSuccessful) {
						makeComfort(currentWell);
					}
				}

				checkForSolution(toSolve);

				currentLargestWellComfort = 0;
				for (int i = 0; i < Universe.getWells().size(); i++) {
					if (currentLargestWellComfort < Universe.getWells().get(i).getMaxComfort()) {
						currentLargestWellComfort = Universe.getWells().get(i).getMaxComfort();
					}
				}

			} while (!solutionFound && currentInternalIterationNum < numInternalIterations);

			// Driver.trace(getClass(), "we are outside the main loop.");
			if (currentInternalIterationNum >= numInternalIterations) {
				Driver.trace("hit iteration limit. resetting wells.");
				currentIterationNum++;
				currentNumColours++;
			} else {
				int empties = 0;
				for (int j = 0; j < Universe.getWells().size(); j++) {
					if (Universe.getWells().get(j).getCapturedAgents().size() == 0) {
						empties++;
					}
				}

				currentIterationNum = 0;
				Driver.trace("found a new best colouring of " + (currentNumColours - empties));
				currentNumColours -= empties;
				currentBest = currentNumColours;
				ResultsModule.writeIncrementalResultToFile(toSolve, SecretAgents.class, currentBest, 
						System.currentTimeMillis() - timeStart, numInternalIterations);
			}
		}

		//} while (currentIterationNum < iterationLimit);

		try {
			SecretAgentPreview.setRunning(false);
			frame.dispose();
		} catch (NullPointerException e) {
		}
		

	}

	private void setupUniverse(Graph toSolve, int numWells) {
		// create universe
		if (SECRET_TRACING)
			Driver.trace("creating universe");
		u = new Universe(toSolve, 2, numWells * 25);

		// create wells
		if (SECRET_TRACING)
			Driver.trace("adding wells");
		u.addWells(numWells);

		// create agents
		if (SECRET_TRACING)
			Driver.trace("adding agents");
		u.addAgents(toSolve.getNumVertices());
	}

	private boolean tryCapture(int agentNum) {
		boolean capture = false;

		if (!Universe.getAgents().get(agentNum).isCaptured()) {

			for (int wellNum = 0; wellNum < Universe.getWells().size(); wellNum++) {

				if (Universe.getWells().get(wellNum).canCapture(Universe.getAgents().get(agentNum))) {
					// if not repelled
					if (!Universe.getWells().get(wellNum).shouldRepelAgent(Universe.getAgents().get(agentNum))) {
						// capture it
						if (SECRET_TRACING)
							Driver.trace("capturing agent");
						Universe.getWells().get(wellNum).capture(Universe.getAgents().get(agentNum));
						capture = true;

					} else {
						// drop comfort of agents
						if (SECRET_TRACING)
							Driver.trace("check intruding agent and dropping comfort of agents in well.");
						for (int capturedAgentNum = 0; capturedAgentNum < Universe.getWells().get(wellNum)
								.getCapturedAgents().size(); capturedAgentNum++) {
							Universe.getWells().get(wellNum).getCapturedAgents().get(capturedAgentNum)
							.decreaseComfort();
						}

						// if all agents are of comfort 0
						if (Universe.getWells().get(wellNum).isAllComfortZero()) {
							if (SECRET_TRACING)
								Driver.trace(
										"all agents in well (" + wellNum + ") are low comfort. injecting intruder");
							// inject intruder (and set its comfort to 1)
							Universe.getWells().get(wellNum).capture(Universe.getAgents().get(agentNum));
							capture = true;

						} else {
							// reset intruder
							if (SECRET_TRACING)
								Driver.trace("resetting location of intruder");
							Universe.getAgents().get(agentNum).setCaptured(false);
						}

					}
				}
			}
		}

		return capture;
	}

	private void doGravity(int agentNum) {
		if (!Universe.getAgents().get(agentNum).isCaptured()) {
			if (SECRET_TRACING)
				Driver.trace("applying gravity to agent " + (agentNum + 1));
			Universe.getAgents().get(agentNum).calcVelocities();
			Universe.getAgents().get(agentNum).applyVelocities();
		} else {
			// if (SECRET_TRACING) Driver.trace(getClass(), "skipping vertex
			// "+(agentNum+1)+" because it is already captured");
		}
	}

	private void makeComfort(int wellNum) {

		//Random r = new Random();

		// increase all comfort levels of captured agents
		// Driver.trace(getClass(), "increasing comfort of all agents that are
		// currently in wells.");
		for (int agentNum = 0; agentNum < Universe.getWells().get(wellNum).getCapturedAgents().size(); agentNum++) {
			double comfortProb = (10.0 / Universe.getWells().get(wellNum).getMinComfort()) - 0.05;

			if (udist.sample() < comfortProb) {
				//if (r.nextDouble() < comfortProb) {
				Universe.getWells().get(wellNum).getCapturedAgents().get(agentNum).increaseComfort();
			}

			// update captured Agents location based on the well's location that
			// holds them
			Double[] tempLoc = new Double[Universe.getDimensions()];
			for (int currentDimension = 0; currentDimension < Universe.getDimensions(); currentDimension++) {
				tempLoc[currentDimension] = Universe.getWells().get(wellNum).getLocation()[currentDimension];
			}

			Universe.getWells().get(wellNum).getCapturedAgents().get(agentNum).setLocation(tempLoc);

		}
	}

	private void ejectAgents(int wellNum) {
		do {
			successful = true;

			// Driver.trace(getClass(), "starting to eject agents that have no
			// comfort level");
			while (Universe.getWells().get(wellNum).ejectAgents());

			for (int agentNum = 0; agentNum < Universe.getWells().get(wellNum).getCapturedAgents().size(); agentNum++) {
				if (!Universe.getWells().get(wellNum).getCapturedAgents().get(agentNum).isCaptured()) {
					// Universe.getWells().get(wellNum).getCapturedAgents().get(agentNum).resetLocation();
					// Universe.getWells().get(wellNum).getCapturedAgents().get(agentNum).setVelZero();
					Universe.getWells().get(wellNum).getCapturedAgents().remove(agentNum);
					Driver.trace("kicking out an agent that shouldnt be...");
					successful = false;
					break;
				}
			}

		} while (!successful);
	}

	private void checkForSolution(Graph toSolve) {
		// if all comfort levels are > 0, solution has been found.
		// Driver.trace(getClass(), "checking to see if all agents have a
		// comfort level above 0.");
		solutionFound = true;
		for (int agentNum = 0; agentNum < Universe.getAgents().size(); agentNum++) {
			if (!Universe.getAgents().get(agentNum).isCaptured()) {
				solutionFound = false;
				if (SECRET_TRACING)
					Driver.trace("not a solution because vertex " + (agentNum + 1) + " is not captured");
			}
		}

		boolean finished = false;

		while (!finished) {
			finished = true;

			for (int wellNum = 0; wellNum < Universe.getWells().size(); wellNum++) {
				for (int capturedAgentNum = 0; capturedAgentNum < Universe.getWells().get(wellNum).getCapturedAgents()
						.size(); capturedAgentNum++) {
					for (int secondCapturedAgentNum = capturedAgentNum + 1; secondCapturedAgentNum < Universe.getWells()
							.get(wellNum).getCapturedAgents().size(); secondCapturedAgentNum++) {
						if (toSolve.isEdge(
								Universe.getWells().get(wellNum).getCapturedAgents().get(capturedAgentNum)
								.getVertexAssociation() - 1,
								Universe.getWells().get(wellNum).getCapturedAgents().get(secondCapturedAgentNum)
								.getVertexAssociation() - 1)) {
							solutionFound = false;
							Universe.getWells().get(wellNum).getCapturedAgents().get(secondCapturedAgentNum)
							.setCaptured(false);
							Universe.getWells().get(wellNum).getCapturedAgents().remove(secondCapturedAgentNum);
							finished = false;
						}
					}
				}
			}
		}
		// Driver.trace(getClass(), "we have a solution: "+ solutionFound);
	}

	public int getResult() {
		return currentBest;
	}

	public static long getCurrentInternalIterationNum() {
		return currentInternalIterationNum;
	}

	public static long getCurrentIterationNum() {
		return currentIterationNum;
	}

	public static long getCurrentLargestWellComfort() {
		return currentLargestWellComfort;
	}

	public static int getNumAgents() {
		return Universe.getAgents().size();
	}

	public static int getNumColours() {
		return currentBest - 1;
	}

	public long getNumInternalIterations() {
		return numInternalIterations;
	}

	public void setNumInternalIterations(long numInternalIterations) {
		this.numInternalIterations = numInternalIterations;
	}
}
