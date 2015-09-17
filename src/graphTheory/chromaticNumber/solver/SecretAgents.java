package graphTheory.chromaticNumber.solver;

import java.awt.EventQueue;

import javax.swing.JFrame;

import graphTheory.chromaticNumber.assets.Graph;
import graphTheory.chromaticNumber.assets.Universe;
import graphTheory.chromaticNumber.loader.Driver;

public class SecretAgents {

	public static boolean SECRET_TRACING = false;
	boolean SLOW_MODE = false;

	long currentIterationNum;
	
	int currentBest;
	
	private JFrame frame;
	
	Universe u;
	
	
	public SecretAgents() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new JFrame();
					frame.setBounds(100, 100, 800, 800);
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.add(new SecretAgentPreview());
					frame.setVisible(true);
					frame.setAlwaysOnTop(true);
					frame.setAlwaysOnTop(false);
					frame.requestFocus();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void solve(Graph toSolve, long iterationLimit) {
		boolean successful;
		boolean solutionFound;
		
		for (int i = toSolve.getMaximalDegree()+1; i > 0; i--) {
		
			Driver.trace(getClass(), "solving with "+ i + " colours");
			
			currentIterationNum = 0;
			
			//create universe
			if (SECRET_TRACING) Driver.trace(getClass(), "creating universe");
			u = new Universe(toSolve, 2, toSolve.getNumVertices()*10);
		
			//create wells
			if (SECRET_TRACING) Driver.trace(getClass(), "adding wells");
			u.addWells(i);
			
			//create agents
			if (SECRET_TRACING) Driver.trace(getClass(), "adding agents");
			u.addAgents(toSolve.getNumVertices());
			
			//do while solution not found and computation limit is not reached
			do {
				
				if (SLOW_MODE) {
					try {
						Thread.sleep(2);
					} catch (InterruptedException e) {}
				}
				
				currentIterationNum++;
				//apply gravitation to agents that are not captured
				//move agents that are not captured as per velocities.
				for (int j = 0; j < Universe.getAgents().size(); j++) {
					if (!Universe.getAgents().get(j).isCaptured()) {
						//Driver.trace(getClass(), "applying gravity to agents");
						Universe.getAgents().get(j).calcVelocities();
						Universe.getAgents().get(j).applyVelocities();
						
						//if agents not captured and are close enough to be captured
						//Driver.trace(getClass(), "checking for captures");
						for (int k = 0; k < Universe.getWells().size(); k++) {
							if (Universe.getWells().get(k).canCapture(Universe.getAgents().get(j))) {
								//if not repelled
								if (!Universe.getWells().get(k).shouldRepelAgent(Universe.getAgents().get(j))) {
									//capture it
									if (SECRET_TRACING) Driver.trace(getClass(), "capturing agent");
									Universe.getWells().get(k).capture(Universe.getAgents().get(j));
								} else {
									//drop comfort of agents
									if (SECRET_TRACING) Driver.trace(getClass(), "check intruding agent and dropping comfort of agents in well.");
									for (int l = 0; l < Universe.getWells().get(k).getCapturedAgents().size(); l++) {
										Universe.getWells().get(k).getCapturedAgents().get(l).decreaseComfort();
										
										//if all agents are of comfort 0
										if (Universe.getWells().get(k).isAllComfortZero()) {
											if (SECRET_TRACING) Driver.trace(getClass(), "all agents in this well are of comfort zero. injecting intruder");
											//inject intruder (and set its comfort to 1)
											Universe.getAgents().get(j).increaseComfort();
											Universe.getWells().get(k).getCapturedAgents().add(Universe.getAgents().get(j));
										} else {
											//reset intruder
											if (SECRET_TRACING) Driver.trace(getClass(), "resetting location of intruder");
											Universe.getAgents().get(j).resetLocation();
										}
									}
								}
							}
							
							//assess comforts on all captured agents
							do {
								successful = true;
								
								//Driver.trace(getClass(), "starting to eject agents that have no comfort level");
								while (Universe.getWells().get(k).ejectAgents());
								
								/*for (int l = 0; l < Universe.getWells().get(k).getCapturedAgents().size(); l++) {
									//if low enough
									if (Universe.getWells().get(k).getCapturedAgents().get(l).getComfort() <= 0) {
										//eject agent
										Universe.getWells().get(k).getCapturedAgents().get(l).resetLocation();
										Universe.getWells().get(k).getCapturedAgents().remove(l);
										successful = false;
										break;
									}
								}/**/
							} while (!successful);
							
							//increase all comfort levels of captured agents
							//Driver.trace(getClass(), "increasing comfort of all agents that are currently in wells.");
							for (int l = 0; l < Universe.getWells().get(k).getCapturedAgents().size(); l++) {
								Universe.getWells().get(k).getCapturedAgents().get(l).increaseComfort();
							}
						}
					}
				}
				
				//if all comfort levels are > 0, solution has been found.
				//Driver.trace(getClass(), "checking to see if all agents have a comfort level above 0.");
				solutionFound = true; 
				for (int j = 0; j < Universe.getAgents().size(); j++) {
					if (!Universe.getAgents().get(j).isCaptured()) {
						solutionFound = false;
					}
				}
				
				
				//Driver.trace(getClass(), "we have a solution: "+ solutionFound);
				
			} while (!solutionFound && currentIterationNum < iterationLimit);
			
			//Driver.trace(getClass(), "we are outside the main loop.");
			if (currentIterationNum >= iterationLimit) {
				Driver.trace(this.getClass(), "hit iteration limit");
				break;
			} else {
				//TODO subtract the number of empty wells
				
				int empties = 0;
				for (int j = 0; j < Universe.getWells().size(); j++) {
					if (Universe.getWells().get(j).getCapturedAgents().size() == 0) {
						empties++;
					}
				}
				
				Driver.trace(getClass(), "found a new best colouring of "+ (i-empties));
				i-=empties;
				currentBest = i;
			}
			
		}
	}
	
	
	public int getResult() {
		return currentBest;
	}
}
