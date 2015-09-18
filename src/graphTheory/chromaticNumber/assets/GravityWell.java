package graphTheory.chromaticNumber.assets;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import graphTheory.chromaticNumber.loader.Driver;
import graphTheory.chromaticNumber.solver.SecretAgents;

public class GravityWell {

	ArrayList<Agent> agents;
	
	Random r;
	
	Color col;
	
	long comfortThreshold = 1;
	
	double radius = 10.0;
	
	double accelConst = 1.0;
	double velocityDecay = 0.99;
	
	Double[] dimLoc;
	Double[] dimVel;
	
	public GravityWell() {
		r = new Random();
		
		agents = new ArrayList<Agent>();
		
		dimLoc = new Double[Universe.getDimensions()];
		dimVel = new Double[Universe.getDimensions()];
		
		for (int i = 0; i < Universe.getDimensions(); i++ ) {
			dimLoc[i] = 0.0 + r.nextInt((int) (Universe.getBounds(i) - 20)) +10;
			dimVel[i] = 0.0;
		}
		
		col = new Color(r.nextInt(127)+127, r.nextInt(127)+127, r.nextInt(127)+127);
	}
	
	public Double[] getLocation() {
		return dimLoc;
	}
	
	public void resetLocation() {
		for (int i = 0; i < Universe.getDimensions(); i++) {
			dimLoc[i] = 0.0 + r.nextInt((int) Universe.getBounds(i));
		}
	}
	
	public boolean canCapture(Agent agent) {
		for (int i = 0; i < Universe.getDimensions(); i++) {
			if (!(Math.abs(dimLoc[i] - agent.getLocation(i)) < radius)) {
				return false;
			}
		}
		return true;
	}
	
	public void capture(Agent agent) {
		for (int currentDimension = 0; currentDimension < Universe.getDimensions(); currentDimension++) {
			dimVel[currentDimension] += accelConst*agent.getVelArray()[currentDimension]/((2*agents.size())+1);
		}
		
		Double[] tempLoc = new Double[Universe.getDimensions()];
		for (int currentDimension = 0; currentDimension < Universe.getDimensions(); currentDimension++) {
			tempLoc[currentDimension] = dimLoc[currentDimension];
		}
		
		agent.setLocation(tempLoc);
		agent.setCaptured(true);
		agent.increaseComfort();
		agents.add(agent);
	}
	
	public void increaseComfort() {
		for (int i = 0; i < agents.size(); i++) {
			agents.get(i).increaseComfort();
		}
	}
	
	public void decreaseComfort() {
		for (int i = 0; i < agents.size(); i++) {
			agents.get(i).decreaseComfort();
		}
	}
	
	/**
	 * Should be run in a while loop to ensure that all agents are ejected properly. eg: <code> while(ejectAgents()); </code>
	 * @return return whether an agent has been ejected.
	 */
	public boolean ejectAgents() {
		
		for (int i = 0; i < agents.size(); i++) {
			if (agents.get(i).getComfort() < comfortThreshold) {
				//agents.get(i).resetLocation();
				agents.get(i).setCaptured(false);
				//agents.get(i).setVelZero();
				agents.remove(i);
				
				if (SecretAgents.SECRET_TRACING) Driver.trace(getClass(), "ejecting an agent");
				return true;
			}
		}
		
		//Driver.trace(getClass(), "finished checking agents, found none to eject");
		return false;
	}
	
	public boolean shouldRepelAgent(Agent intruder) {
		for (int i = 0; i < agents.size(); i++) {
			if (Universe.getGraph().isEdge(agents.get(i).getVertexAssociation()-1, intruder.getVertexAssociation()-1)) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<Agent> getCapturedAgents() {
		return agents;
	}
	
	public boolean isAllComfortZero() {
		for (int i = 0; i < agents.size(); i++) {
			if (agents.get(i).getComfort() >= comfortThreshold) {
				if (SecretAgents.SECRET_TRACING) Driver.trace(getClass(), "this well has at least one agent with more than "+comfortThreshold+" comfort");
				return false;
			}
		}
		if (SecretAgents.SECRET_TRACING) Driver.trace(getClass(), "this well has comforts all less than "+comfortThreshold);
		return true;
	}
	
	public void applyVel() {
		for (int i = 0; i < Universe.getDimensions(); i++) {
			if (dimLoc[i] < 50 || dimLoc[i] > (Universe.getBounds(i) - 50)) {
				dimVel[i] *= -1;
			}
			
			dimVel[i] *= velocityDecay;
			dimLoc[i] += dimVel[i];
		}
	}
	
	public Color getColour() {
		return col;
	}
	
	public double getRadius() {
		return radius;
	}
}
