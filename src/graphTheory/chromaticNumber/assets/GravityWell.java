package graphTheory.chromaticNumber.assets;

import java.awt.Color;
import java.util.ArrayList;
//import java.util.Random;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.distribution.UniformRealDistribution;

import graphTheory.chromaticNumber.loader.Driver;
import graphTheory.chromaticNumber.solver.SecretAgentPreview;
import graphTheory.chromaticNumber.solver.SecretAgents;

public class GravityWell {

	List<Agent> agents;

	// Random r;
	UniformRealDistribution udist;

	Color col;

	int comfortThreshold = 1;

	public static double radius = 20.0;

	double accelConst = 1.0;
	double velocityDecay = 0.9999999;

	Double[] dimLoc;
	Double[] dimVel;

	public GravityWell() {
		// r = new Random();
		udist = new UniformRealDistribution();

		agents = Collections.synchronizedList(new ArrayList<Agent>());

		dimLoc = new Double[Universe.getDimensions()];
		dimVel = new Double[Universe.getDimensions()];

		for (int i = 0; i < Universe.getDimensions(); i++) {
			dimLoc[i] = 0.0 + udist.sample() * (int) (Universe.getBounds(i) - 20) + 10;
			// dimLoc[i] = 0.0 + r.nextInt((int) (Universe.getBounds(i) - 20))
			// +10;
			dimVel[i] = 0.0;
		}

		col = new Color((int) Math.floor(udist.sample() * 127) + 127, (int) Math.floor(udist.sample() * 127) + 127,
				(int) Math.floor(udist.sample() * 127) + 127);

		// col = new Color(r.nextInt(127)+127, r.nextInt(127)+127,
		// r.nextInt(127)+127);
	}

	public Double[] getLocation() {
		return dimLoc;
	}

	public void resetLocation() {
		for (int i = 0; i < Universe.getDimensions(); i++) {
			dimLoc[i] = udist.sample() * Universe.getBounds(i);
			// dimLoc[i] = 0.0 + r.nextInt((int) Universe.getBounds(i));
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
			dimVel[currentDimension] += accelConst * agent.getVelArray()[currentDimension] / ((2 * agents.size()) + 1);
		}

		Double[] tempLoc = new Double[Universe.getDimensions()];
		for (int currentDimension = 0; currentDimension < Universe.getDimensions(); currentDimension++) {
			tempLoc[currentDimension] = dimLoc[currentDimension];
		}

		if (SecretAgentPreview.drawGradientMap)
			SecretAgents.setMapPixel(agent.getStartLoc(), col.darker());
		
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
	 * Should be run in a while loop to ensure that all agents are ejected
	 * properly. eg: <code> while(ejectAgents()); </code>
	 * 
	 * @return return whether an agent has been ejected.
	 */
	public boolean ejectAgents() {

		for (int i = 0; i < agents.size(); i++) {
			if (agents.get(i).getComfort() < comfortThreshold) {
				// agents.get(i).resetLocation();
				agents.remove(i).setCaptured(false);
				// agents.get(i).setVelZero();
				//agents.remove(i);

				if (SecretAgents.SECRET_TRACING)
					Driver.trace("ejecting an agent");
				return true;
			}
			
			if (!agents.get(i).isCaptured()) {
				agents.remove(i);

				if (SecretAgents.SECRET_TRACING)
					Driver.trace("ejecting an agent that shouldnt be captured");
				return true;
			}
		}

		// Driver.trace(getClass(), "finished checking agents, found none to
		// eject");
		return false;
	}

	public boolean shouldRepelAgent(Agent intruder) {
		if (Universe.getGraph() == null) {
			return true;
		}
		for (int i = 0; i < agents.size(); i++) {
			if (Universe.getGraph().isEdge(agents.get(i).getVertexAssociation() - 1,
					intruder.getVertexAssociation() - 1)) {
				return true;
			}
		}
		return false;
	}

	public List<Agent> getCapturedAgents() {
		return agents;
	}

	public boolean isAllComfortZero() {
		for (int i = 0; i < agents.size(); i++) {
			if (agents.get(i).getComfort() >= comfortThreshold) {
				if (SecretAgents.SECRET_TRACING)
					Driver.trace("this well has at least one agent with more than " + comfortThreshold + " comfort");
				return false;
			}
		}
		if (SecretAgents.SECRET_TRACING)
			Driver.trace("this well has comforts all less than " + comfortThreshold);
		return true;
	}

	public void applyVel() {
		boolean torric = true;

		for (int i = 0; i < Universe.getDimensions(); i++) {
			if (!torric) {
				if (dimLoc[i] < 5 || dimLoc[i] > (Universe.getBounds(i) - 5)) {

					dimVel[i] *= -1;
				}
			} else {
				if (dimLoc[i] < 5) {
					dimLoc[i] = Universe.getBounds(i) - 5;
				} else if (dimLoc[i] > (Universe.getBounds(i) - 5)) {
					dimLoc[i] = 5.0;
				}
			}

			dimVel[i] *= velocityDecay;
			dimLoc[i] += dimVel[i];
		}
	}

	public long getMinComfort() {
		long comfort = Integer.MAX_VALUE;

		for (int i = 0; i < agents.size(); i++) {
			if (comfort > agents.get(i).getComfort()) {
				comfort = agents.get(i).getComfort();
			}
		}

		return comfort;
	}

	public long getMaxComfort() {
		long comfort = 0;

		for (int i = 0; i < agents.size(); i++) {
			if (comfort < agents.get(i).getComfort()) {
				comfort = agents.get(i).getComfort();
			}
		}
		return comfort;
	}

	public Color getColour() {
		return col;
	}

	public static double getRadius() {
		return radius;
	}
	
	public void setLocation(Double[] newLoc) {
		if (newLoc.length != dimLoc.length) {
			System.out.println("new location is invalid");
		} else {
			dimLoc = newLoc;
		}
	}
}
