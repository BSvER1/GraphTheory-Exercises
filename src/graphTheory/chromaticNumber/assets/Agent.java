package graphTheory.chromaticNumber.assets;

import java.util.Arrays;
//import java.util.Random;

import org.apache.commons.math3.distribution.UniformRealDistribution;

import graphTheory.chromaticNumber.loader.Driver;
import graphTheory.chromaticNumber.solver.SecretAgents;

public class Agent {

	//private Random r;
	private UniformRealDistribution udist;
	
	private double accelConst = 100.0;
	private double distPower = 2;
	
	private long comfort;
	
	private int vertexAssociation;
	
	private Double[] dimLoc;
	private Double[] dimVel;
	
	private boolean isCaptured;
	
	
	
	public Agent(int vertex) {
		//r = new Random();
		udist = new UniformRealDistribution();
		comfort = 0;
		isCaptured = false;
		
		vertexAssociation = vertex;
		
		dimLoc = new Double[Universe.getDimensions()];
		dimVel = new Double[Universe.getDimensions()];
		
		for (int i = 0; i < Universe.getDimensions(); i++) {
			dimLoc[i] = 0.0 + udist.sample() * (int) (Universe.getBounds(i) - 20) +10;
			//dimLoc[i] = 0.0 + r.nextInt((int) (Universe.getBounds(i) - 20)) +10;
			dimVel[i] = 0.0;
		}
	}
	
	public boolean calcVelocities() {
		for (int i = 0; i < Universe.getDimensions(); i ++) {
			dimVel[i] = 0.0;
			for (int j = 0; j < Universe.getWells().size(); j++) {
				
				for (int k = 0; k < dimLoc.length; k++) {
					if (dimLoc[k].isNaN()) {
						dimLoc[k] = Universe.getBounds(k)/2;
						Driver.trace("got an agent NaN. resetting the location in dim "+k+" to "+Universe.getBounds(k)/2);
					}
					
					if (Universe.getWells().get(j).getLocation()[k].isNaN()) {
						Universe.getWells().get(j).resetLocation();
						Driver.trace("got a well NaN. resetting the location");
					}
				}
				
				//System.out.println("distance between vertex "+ vertexAssociation+" and well "+ j+" is "+ Universe.getDistance(Universe.getWells().get(j).getLocation(), dimLoc));
				Double distance = Universe.getDistance(Universe.getWells().get(j).getLocation(), dimLoc);
				if (distance < 10.0) {
					if (SecretAgents.SECRET_TRACING) 
						Driver.trace("got a distance smaller than radius of a well. trying to add it.");

					for (int wellNum = 0; wellNum < Universe.getWells().size(); wellNum++) {
						if (Universe.getWells().get(wellNum).canCapture(this)) {
							if (!Universe.getWells().get(wellNum).shouldRepelAgent(this)) {
								Universe.getWells().get(wellNum).capture(this);
								break;
							}
						}
					}

					Double[] tempWellLoc = new Double[Universe.getDimensions()];
					for (int k = 0; k < Universe.getDimensions(); k++) {
						tempWellLoc[k] = Universe.getWells().get(j).getLocation()[k];
					}
					return true;
				}
				
				dimVel[i] += (accelConst*(-dimLoc[i]+Universe.getWells().get(j).getLocation()[i])/(Math.pow(distance, distPower)));
				if (dimVel[i].isNaN()) {
					if (SecretAgents.SECRET_TRACING) 
						Driver.trace("got a NaN in the velocity. means that there is a well at this location, yet isnt being captured.");
					
					for (int wellNum = 0; wellNum < Universe.getWells().size(); wellNum++) {
						if (Universe.getWells().get(wellNum).canCapture(this)) {
							if (!Universe.getWells().get(wellNum).shouldRepelAgent(this)) {
								Universe.getWells().get(wellNum).capture(this);
								break;
							}
						}
					}
					
					Double[] tempWellLoc = new Double[Universe.getDimensions()];
					for (int k = 0; k < Universe.getDimensions(); k++) {
						tempWellLoc[k] = Universe.getWells().get(j).getLocation()[k];
					}
					return true;
				}
			}
		}
		return false;
		
		//System.out.println("Velocity : ["+dimVel[0]+", "+dimVel[1]+"]");
	}
	
	public void applyVelocities() {
		for (int i = 0; i < Universe.getDimensions(); i++) {
			if(dimLoc[i].isNaN()) {
				if (SecretAgents.SECRET_TRACING) Driver.trace("got a NaN for location when applying velocities");
				dimLoc[i] = Universe.getBounds(i)/2;
			}
			
			dimLoc[i] += dimVel[i];
			if (SecretAgents.SECRET_TRACING) {
				Driver.trace("velocity of agent in direction "+i+" is "+dimVel[i]);
				Driver.trace("location of agent direction "+i+" is "+dimLoc[i]);
			}
		}
	}
	
	
	public void resetLocation() {
		for (int i = 0; i < Universe.getDimensions(); i++) {
			dimLoc[i] = 0.0 + udist.sample() * (int) Universe.getBounds(i);
			//dimLoc[i] = 0.0 + r.nextInt((int) Universe.getBounds(i));
			dimVel[i] = 0.0;
		}
		isCaptured = false;
	}
	
	public void increaseComfort() {
		//if (comfort > comfortThreshold)
			comfort++;
		//else
		//	comfort += comfortThreshold;
	}
	
	public void decreaseComfort() {
		//comfort *= comfortMultiplier;
		comfort--;
	}
	
	public Double[] getLocationArray() {
		return dimLoc;
	}
	
	public Double[] getVelArray() {
		return dimVel;
	}
	
	public double getLocation(int dim) {
		return dimLoc[dim];
	}
	
	public void setVelZero() {
		for (int i = 0; i < Universe.getDimensions(); i++) {
			dimVel[i] = 0.0;
		}
	}
	
	public void setLocation(Double[] loc) {
		dimLoc = Arrays.copyOf(loc, loc.length);
	}
	
	public long getComfort() {
		return comfort;
	}

	public int getVertexAssociation() {
		return vertexAssociation;
	}
	
	public boolean isCaptured() {
		return isCaptured;
	}
	
	public void setCaptured(boolean captured) {
		if (!captured) {
			resetLocation();
			comfort = 0;
		} else {
			comfort = 1;
		}
		isCaptured = captured;
		setVelZero();
		
	}
	
	
}
