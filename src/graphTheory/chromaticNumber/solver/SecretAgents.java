package graphTheory.chromaticNumber.solver;

import java.awt.EventQueue;
import java.util.Random;

import javax.swing.JFrame;

import graphTheory.chromaticNumber.assets.Graph;
import graphTheory.chromaticNumber.assets.Universe;
import graphTheory.chromaticNumber.loader.Driver;
import net.miginfocom.swing.MigLayout;

public class SecretAgents {

	private boolean VISUALS_ENABLED = true;

	public static boolean SECRET_TRACING = false;
	boolean SLOW_MODE = false;
	boolean WELLS_MOVE = false;

	int SLOW_SPEED = 50; // larger is slower. default to 2

	static long currentInternalIterationNum;
	static long currentIterationNum;
	static long currentLargestWellComfort;

	boolean successful;
	boolean solutionFound;

	static int currentBest;

	private JFrame frame;

	Universe u;


	public SecretAgents() {
		if (VISUALS_ENABLED) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						frame = new JFrame();
						frame.setBounds(100, 100, 850, 850);
						frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						frame.getContentPane().setLayout(new MigLayout("", "[0px,growprio 50,grow,shrinkprio 50][800px][0px,growprio 50,grow,shrinkprio 50]", "[0px,growprio 50,grow,shrinkprio 50][800px][0px,growprio 50,grow,shrinkprio 50]"));
						frame.add(new SecretAgentPreview(), "cell 1 1");
						frame.setVisible(true);
						frame.setAlwaysOnTop(true);
						frame.setAlwaysOnTop(false);
						frame.requestFocus();
					} catch (Exception e) {}
				}
			});
		}
	}

	public void solve(Graph toSolve, long iterationLimit) {
		currentIterationNum = 0;
		
		int currentNumColours = toSolve.getMaximalDegree()+1;

		do {

			while (currentNumColours > 0) { currentNumColours--; 
			
				Driver.trace(getClass(), "solving with "+ currentNumColours + " colours");

				currentInternalIterationNum = 0;

				setupUniverse(toSolve, currentNumColours);

				//do while solution not found and computation limit is not reached
				do {

					if (SLOW_MODE) {
						try {
							Thread.sleep(SLOW_SPEED);
						} catch (InterruptedException e) {}
					}

					currentInternalIterationNum++;

					boolean captureSuccessful = false;
					//if agents not captured and are close enough to be captured
					for (int currentAgent = 0; currentAgent < Universe.getAgents().size(); currentAgent++) {
						if (tryCapture(currentAgent) && !captureSuccessful) {
							captureSuccessful = true;
						}
						//captureSuccessful = tryCapture(currentAgent);
					}

					//move all agents (method checks if they are captured itself)
					for (int currentAgent = 0; currentAgent < Universe.getAgents().size(); currentAgent++) {
						doGravity(currentAgent);
					}

					for (int currentWell = 0; currentWell < Universe.getWells().size(); currentWell++) {
						//apply movement on the wells
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
					

				} while (!solutionFound && currentInternalIterationNum < (100*Math.pow(toSolve.getNumVertices(), 2)));

				//Driver.trace(getClass(), "we are outside the main loop.");
				if (currentInternalIterationNum >= (100*Math.pow(toSolve.getNumVertices(), 2))) {
					Driver.trace(this.getClass(), "hit iteration limit. resetting wells.");
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
					Driver.trace(getClass(), "found a new best colouring of "+ (currentNumColours - empties));
					currentNumColours -= empties;
					currentBest = currentNumColours;
				}
			}
			
		} while (currentIterationNum < iterationLimit);


	}

	private void setupUniverse(Graph toSolve, int numWells) {
		//create universe
		if (SECRET_TRACING) Driver.trace(getClass(), "creating universe");
		u = new Universe(toSolve, 2, numWells*20);

		//create wells
		if (SECRET_TRACING) Driver.trace(getClass(), "adding wells");
		u.addWells(numWells);

		//create agents
		if (SECRET_TRACING) Driver.trace(getClass(), "adding agents");
		u.addAgents(toSolve.getNumVertices());
	}

	private boolean tryCapture(int agentNum) {
		boolean capture = false;

		if (!Universe.getAgents().get(agentNum).isCaptured()) {

			for (int wellNum = 0; wellNum < Universe.getWells().size(); wellNum++) {

				if (Universe.getWells().get(wellNum).canCapture(Universe.getAgents().get(agentNum))) {
					//if not repelled
					if (!Universe.getWells().get(wellNum).shouldRepelAgent(Universe.getAgents().get(agentNum))) {
						//capture it
						if (SECRET_TRACING) Driver.trace(getClass(), "capturing agent");
						Universe.getWells().get(wellNum).capture(Universe.getAgents().get(agentNum));
						capture = true;

					} else {
						//drop comfort of agents
						if (SECRET_TRACING) Driver.trace(getClass(), "check intruding agent and dropping comfort of agents in well.");
						for (int capturedAgentNum = 0; capturedAgentNum < Universe.getWells().get(wellNum).getCapturedAgents().size(); capturedAgentNum++) {
							Universe.getWells().get(wellNum).getCapturedAgents().get(capturedAgentNum).decreaseComfort();
						}

						//if all agents are of comfort 0
						if (Universe.getWells().get(wellNum).isAllComfortZero()) {
							if (SECRET_TRACING) Driver.trace(getClass(), "all agents in well ("+wellNum+") are low comfort. injecting intruder");
							//inject intruder (and set its comfort to 1)
							Universe.getWells().get(wellNum).capture(Universe.getAgents().get(agentNum));
							capture = true;

						} else {
							//reset intruder
							if (SECRET_TRACING) Driver.trace(getClass(), "resetting location of intruder");
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
			if (SECRET_TRACING) Driver.trace(getClass(), "applying gravity to agent "+(agentNum+1));
			Universe.getAgents().get(agentNum).calcVelocities(); 
			Universe.getAgents().get(agentNum).applyVelocities();
		} else {
			//if (SECRET_TRACING) Driver.trace(getClass(), "skipping vertex "+(agentNum+1)+" because it is already captured");
		}
	}

	private void makeComfort(int wellNum) {

		Random r = new Random();

		//increase all comfort levels of captured agents
		//Driver.trace(getClass(), "increasing comfort of all agents that are currently in wells.");
		for (int agentNum = 0; agentNum < Universe.getWells().get(wellNum).getCapturedAgents().size(); agentNum++) {
			double comfortProb = 10.0/Universe.getWells().get(wellNum).getMinComfort();
			
			if (r.nextDouble() < comfortProb) {
				Universe.getWells().get(wellNum).getCapturedAgents().get(agentNum).increaseComfort();
			}

			//update captured Agents location based on the well's location that holds them
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

			//Driver.trace(getClass(), "starting to eject agents that have no comfort level");
			while (Universe.getWells().get(wellNum).ejectAgents());

			for (int agentNum = 0; agentNum < Universe.getWells().get(wellNum).getCapturedAgents().size(); agentNum++) {
				if (!Universe.getWells().get(wellNum).getCapturedAgents().get(agentNum).isCaptured()) {
					//Universe.getWells().get(wellNum).getCapturedAgents().get(agentNum).resetLocation();
					//Universe.getWells().get(wellNum).getCapturedAgents().get(agentNum).setVelZero();
					Universe.getWells().get(wellNum).getCapturedAgents().remove(agentNum);
					Driver.trace(getClass(), "kicking out an agent that shouldnt be...");
					successful = false;
					break;
				}
			}

		} while (!successful);
	}

	private void checkForSolution(Graph toSolve) {
		//if all comfort levels are > 0, solution has been found.
		//Driver.trace(getClass(), "checking to see if all agents have a comfort level above 0.");
		solutionFound = true; 
		for (int agentNum = 0; agentNum < Universe.getAgents().size(); agentNum++) {
			if (!Universe.getAgents().get(agentNum).isCaptured()) {
				solutionFound = false;
				if (SECRET_TRACING) 
					Driver.trace(getClass(), "not a solution because vertex "+(agentNum+1)+" is not captured");
			}
		}

		boolean finished = false;

		while (!finished) {
			finished = true;

			for (int wellNum = 0; wellNum < Universe.getWells().size(); wellNum++) {
				for (int capturedAgentNum = 0; capturedAgentNum < Universe.getWells().get(wellNum).getCapturedAgents().size(); capturedAgentNum++) {
					for (int secondCapturedAgentNum = capturedAgentNum+1; secondCapturedAgentNum < Universe.getWells().get(wellNum).getCapturedAgents().size(); secondCapturedAgentNum++) {
						if (toSolve.isEdge(Universe.getWells().get(wellNum).getCapturedAgents().get(capturedAgentNum).getVertexAssociation()-1, 
								Universe.getWells().get(wellNum).getCapturedAgents().get(secondCapturedAgentNum).getVertexAssociation()-1)) {
							solutionFound = false;
							Universe.getWells().get(wellNum).getCapturedAgents().get(secondCapturedAgentNum).setCaptured(false);
							Universe.getWells().get(wellNum).getCapturedAgents().remove(secondCapturedAgentNum);
							finished = false;
						}
					}
				}
			}
		}
		//Driver.trace(getClass(), "we have a solution: "+ solutionFound);
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
		return currentBest-1;
	}
}
