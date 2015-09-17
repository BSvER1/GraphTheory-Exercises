package graphTheory.chromaticNumber.assets;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import graphTheory.chromaticNumber.loader.Driver;

public class GravityWell {

	ArrayList<Agent> agents;
	
	Random r;
	
	Color col;
	
	double radius = 10.0;
	
	double[] dimLoc;
	
	public GravityWell(double radius) {
		r = new Random();
		
		agents = new ArrayList<Agent>();
		
		this.radius = 10.0;
		
		dimLoc = new double[Universe.getDimensions()];
		
		for (int i = 0; i < Universe.getDimensions(); i++ ) {
			dimLoc[i] = r.nextInt((int) (Universe.getBounds(i) - 20)) +10;
		}
		
		col = new Color(r.nextInt(127)+127, r.nextInt(127)+127, r.nextInt(127)+127);
	}
	
	public double[] getLocation() {
		return dimLoc;
	}
	
	public void resetLocation() {
		for (int i = 0; i < Universe.getDimensions(); i++) {
			dimLoc[i] = r.nextInt((int) Universe.getBounds(i));
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
		agent.setVelZero();
		agent.setLocation(Arrays.copyOf(dimLoc, dimLoc.length));
		agent.setCaptured(true);
		agents.add(agent);
		agent.increaseComfort();
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
			if (agents.get(i).getComfort() <= 0) {
				agents.get(i).resetLocation();
				agents.get(i).setCaptured(false);
				agents.remove(i);
				
				Driver.trace(getClass(), "ejecting an agent");
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
			if (agents.get(i).getComfort() > 0) {
				return false;
			}
		}
		return true;
	}
	
	public Color getColour() {
		return col;
	}
	
	public double getRadius() {
		return radius;
	}
}
