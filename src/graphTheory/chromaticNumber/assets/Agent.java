package graphTheory.chromaticNumber.assets;

import java.util.Arrays;
//import java.util.Random;

import org.apache.commons.math3.distribution.LevyDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import graphTheory.chromaticNumber.loader.Driver;
import graphTheory.chromaticNumber.solver.SecretAgents;

public class Agent {

	//private Random r;
	private UniformRealDistribution udist;
	private LevyDistribution ldist;
	
	private double accelConst = 175.0;
	private double distPower = 1;
	
	private long comfort;
	
	private int vertexAssociation;
	
	private Double[] dimLoc;
	private Double[] dimVel;
	
	private boolean isCaptured;
	
	private Double[] startLoc;
	
	
	
	public Agent(int vertex) {
		//r = new Random();
		udist = new UniformRealDistribution();
		ldist = new LevyDistribution(0,0.1);
		comfort = 0;
		isCaptured = false;
		
		vertexAssociation = vertex;
		
		dimLoc = new Double[Universe.getDimensions()];
		dimVel = new Double[Universe.getDimensions()];
		startLoc = new Double[Universe.getDimensions()];
		
		for (int i = 0; i < Universe.getDimensions(); i++) {
			dimLoc[i] = 0.0 + udist.sample() * (int) (Universe.getBounds(i) - 20) +10;
			startLoc[i] = dimLoc[i];
			//dimLoc[i] = 0.0 + r.nextInt((int) (Universe.getBounds(i) - 20)) +10;
			dimVel[i] = 0.0;
		}
	}
	
	public void calcVelocities() {
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
				Double distance = distanceToWell(j);
				if (distance < GravityWell.radius) {
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
					//resetLocation();
					//return true;
				}
				
				
				dimVel[i] += getForce(j, i, distance);
				


				dimVel[i] += udist.sample() - 0.5;

				
				if (dimVel[i].isNaN()) {
					if (SecretAgents.SECRET_TRACING) 
						Driver.trace("got a NaN in the velocity. means that there is a well at this location, yet isnt being captured. " + distance);
					
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
					//return true;
				}
			}
		}
		//return false;
		
		//System.out.println("Velocity : ["+dimVel[0]+", "+dimVel[1]+"]");
	}

	public double distanceToWell(int wellNum) {
		return Universe.getDist(Universe.getWells().get(wellNum).getLocation(), dimLoc);
	}
	
	public double clamp(double val, double lowerLimit, double higherLimit) {
		if (val > lowerLimit && val < higherLimit) {
			return val;
		}
		if (val <= lowerLimit) {
			return lowerLimit;
		}
		if (val >= higherLimit) {
			return higherLimit;
		}
		return 0;
	}
	
	public double getForce(int wellNum, int dim) {
		double dist;
		if (Universe.getIsTorricDistShorter(Universe.getWells().get(wellNum).getLocation(), dimLoc, dim)) {
			if (Universe.getWells().get(wellNum).getLocation()[dim] - dimLoc[dim] < 0) {
				//dist = 15;
				dist = clamp(Universe.getWells().get(wellNum).getLocation()[dim] - dimLoc[dim], -50, 0);

			} else {
				//dist = -15;
				dist = clamp(Universe.getWells().get(wellNum).getLocation()[dim] - dimLoc[dim], 0, 50);

			}
			dist*=-1;
		} else {
			if (Universe.getWells().get(wellNum).getLocation()[dim] - dimLoc[dim] > 0) {// well is to the right
				//dist = 15;
				dist = clamp(Universe.getWells().get(wellNum).getLocation()[dim] - dimLoc[dim], 0, 50);
			} else {
				//dist = -15;
				dist = clamp(Universe.getWells().get(wellNum).getLocation()[dim] - dimLoc[dim], -50, 0);
			}
			//dist = clamp(0 - Universe.getWells().get(wellNum).getLocation()[dim] + dimLoc[dim], -15, 15);
		}
		//dist = Math.min((Universe.getWells().get(wellNum).getLocation()[dim] - dimLoc[dim]), 
		//			Universe.getBounds(dim) - (Universe.getWells().get(wellNum).getLocation()[dim] - dimLoc[dim]));
		
		//dist = Universe.getWells().get(wellNum).getLocation()[dim] - dimLoc[dim];
		
		if (dist > 0) {
			dist += 0.5;
		} else if (dist < 0) {
			dist -= 0.5;
		} else {
			dist = udist.sample() - 0.5;
		}
		Double force;
		double distToWell = distanceToWell(wellNum);
		if (distToWell < 1) {
			force = Double.MAX_VALUE/2;
		} else {
			force = (accelConst*(dist)/(Math.pow(distToWell, distPower))) * 5;
		}
		if (force.isInfinite()) {
			Driver.trace("inf");
		}
		if (force.isNaN()) {
			Driver.trace("NaN");
		}
		
		//double force = (accelConst*(-dimLoc[i]+Universe.getWells().get(j).getLocation()[i])/(Math.pow(distance, distPower)));
		//Driver.trace("force is: " + force);
		
		return force;
	}
	
	public double getForce(int wellNum, int dim, double distance) {
		double dist;
		if (Universe.getIsTorricDistShorter(Universe.getWells().get(wellNum).getLocation(), dimLoc, dim)) {
			if (Universe.getWells().get(wellNum).getLocation()[dim] - dimLoc[dim] < 0) {
				//dist = 15;
				dist = clamp(Universe.getWells().get(wellNum).getLocation()[dim] - dimLoc[dim], -50, 0);

			} else {
				//dist = -15;
				dist = clamp(Universe.getWells().get(wellNum).getLocation()[dim] - dimLoc[dim], 0, 50);

			}
			dist*=-1;
			//dist = clamp(Universe.getWells().get(wellNum).getLocation()[dim] - dimLoc[dim], -15, 15);
		} else {
			if (Universe.getWells().get(wellNum).getLocation()[dim] - dimLoc[dim] > 0) {// well is to the right
				//dist = 15;
				dist = clamp(Universe.getWells().get(wellNum).getLocation()[dim] - dimLoc[dim], 0, 50);
			} else {
				//dist = -15;
				dist = clamp(Universe.getWells().get(wellNum).getLocation()[dim] - dimLoc[dim], -50, 0);
			}
			//dist = clamp(0 - Universe.getWells().get(wellNum).getLocation()[dim] + dimLoc[dim], -15, 15);
		}
		//dist = Math.min((Universe.getWells().get(wellNum).getLocation()[dim] - dimLoc[dim]), 
		//			Universe.getBounds(dim) - (Universe.getWells().get(wellNum).getLocation()[dim] - dimLoc[dim]));
		
		//dist = Universe.getWells().get(wellNum).getLocation()[dim] - dimLoc[dim];
		
		if (dist > 0) {
			dist += 0.5;
		} else if (dist < 0) {
			dist -= 0.5;
		} else {
			dist = udist.sample() - 0.5;
		}
		
		Double force;
		if (distance < 1) {
			force = Double.MAX_VALUE/2;
		} else {
			force = (accelConst*(dist)/(Math.pow(distance, distPower))) * 5;
		}
		if (force.isInfinite()) {
			Driver.trace("inf");
			force = Double.MAX_VALUE;
		}
		if (force.isNaN()) {
			Driver.trace("NaN");
		}
		
		//double force = (accelConst*(-dimLoc[i]+Universe.getWells().get(j).getLocation()[i])/(Math.pow(distance, distPower)));
		//Driver.trace("force is: " + force);
		
		return force;
	}
	
	public void applyVelocities() {
		for (int i = 0; i < Universe.getDimensions(); i++) {
			if(dimLoc[i].isNaN()) {
				//if (SecretAgents.SECRET_TRACING) 
					Driver.trace("got a NaN for location when applying velocities");
				dimLoc[i] = Universe.getBounds(i)/2;
			}
			
			if (dimVel[i].isNaN()) {
				//if (SecretAgents.SECRET_TRACING) 
					Driver.trace("got a NaN for velocity when applying velocities");
			}
			
			//Driver.trace("applying velocity of "+dimVel[i]+" in dim "+i+" to get loc of "+(dimLoc[i] + dimVel[i]));
			dimLoc[i] += dimVel[i];
			dimLoc[i] = (dimLoc[i] + Universe.getBounds(i))%Universe.getBounds(i);
			
			dimLoc[i] = (Universe.getBounds(i)+dimLoc[i]) % Universe.getBounds(i);
			if (SecretAgents.SECRET_TRACING) {
				Driver.trace("velocity of agent in direction "+i+" is "+dimVel[i]);
				Driver.trace("location of agent direction "+i+" is "+dimLoc[i]);
			}
		}
	}
	
	
	public void resetLocation() {
		for (int i = 0; i < Universe.getDimensions(); i++) {
			dimLoc[i] = 0.0 + udist.sample() * (int) Universe.getBounds(i);
			startLoc[i] = dimLoc[i];
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
		for (int i = 0; i < loc.length; i++) {
			dimLoc[i] = loc[i];
		}
		//dimLoc = Arrays.copyOf(loc, loc.length);
	}
	
	public void setStartLocation(Double[] loc) {
		setLocation(loc);
		for (int i = 0; i < loc.length; i++) {
			startLoc[i] = loc[i];
		}
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
			
			for (int i = 0; i < Universe.getWells().size(); i++) {
				Universe.getWells().get(i).getCapturedAgents().remove(this);
			}
		} else {
			comfort = 1;
		}
		isCaptured = captured;
		setVelZero();
	}
	
	public Double[] getStartLoc() {
		return startLoc;
	}
}
