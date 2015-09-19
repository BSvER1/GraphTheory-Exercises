package graphTheory.chromaticNumber.assets;

import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Random;

import graphTheory.chromaticNumber.loader.Driver;
import graphTheory.chromaticNumber.solver.SecretAgents;

public class Universe {
	
	private static ArrayList<GravityWell> wells;
	private static ArrayList<Agent> agents;
	
	//private Random r;
	
	private static Graph toSolve;
	
	private static double[] dimSize;
	
	private static int dimensions;
	
	
	
	public Universe(Graph toSolve, int numDims, double dimSize) {
		//r = new Random();
		
		wells = new ArrayList<GravityWell>();
		agents = new ArrayList<Agent>();
		
		Universe.toSolve = toSolve;
		
		dimensions = numDims;
		
		Universe.dimSize = new double[numDims];
		
		for (int i = 0; i < numDims; i++) {
			Universe.dimSize[i] = dimSize;
		}
		
	}
	
	public void addWells(int numWells) {
		if (SecretAgents.SECRET_TRACING) Driver.trace(getClass(), "adding wells");
		for (int i = 0; i < numWells; i++) {
			addWell(numWells);
		}
	}
	
	private void addWell(int numWells) {
		boolean successful;
		GravityWell tempWell = new GravityWell();
		
		do {
			successful = true;
			for (int i = 0; i < wells.size(); i++) {
			
				//check if any are the same - do first to avoid div by 0 error.
				if (Arrays.equals(tempWell.getLocation(), wells.get(i).getLocation())) {
					tempWell.resetLocation();
					successful = false;
					break;
				}
				
				if (getDistance(tempWell.getLocation(), wells.get(i).getLocation()) < (2.5*tempWell.getRadius())) {
					tempWell.resetLocation();
					successful = false;
					break;
				}
			}
		} while (!successful);
		
		wells.add(tempWell);
	}
	
	public void addAgents(int numAgents) {
		if (SecretAgents.SECRET_TRACING) Driver.trace(getClass(), "adding agents");
		for (int i = 1; i <= numAgents; i++) {
			agents.add(new Agent(i));
		}
	}
	
	public static double getDistance(Double[] wellLoc, Double[] agentLoc) {
		
		for (int i = 0; i < Universe.getDimensions(); i++) {
			if (wellLoc[i].isNaN()) {
				//System.err.println("wellLoc is not a number");
			}
			if (agentLoc[i].isNaN()) {
				//System.err.println("agentLoc is not a number");
			}
		}
		
		Double result = Math.pow(getDistSum(wellLoc, agentLoc), Math.pow(getDimensions(),-1));
		Double torricResult = Math.pow(getTorricDistSum(wellLoc, agentLoc), Math.pow(getDimensions(),-1));
		if(result.isNaN()) {
			//System.err.println("got NaN from distance function. Inputs were"+wellLoc+", "+ agentLoc+", "+ getDimensions());
		}
		if (result.doubleValue() < 5.0) {
			//System.err.println("got 0 from distance function. Inputs were"+wellLoc+", "+ agentLoc+", "+ getDimensions());
		}
		if(torricResult.isNaN()) {
			//System.err.println("got NaN from distance function. Inputs were"+wellLoc+", "+ agentLoc+", "+ getDimensions());
		}
		if (torricResult.doubleValue() < 5.0) {
		//	//System.err.println("got 0 from torric distance function. Inputs were"+wellLoc+", "+ agentLoc+", "+ getDimensions());
		}
		
		
		return Math.min(result, torricResult);
	}
	
	private static double getDistSum(Double[] wellLoc, Double[] agentLoc) {	
		double normalSum = 0;
		//double torricSum = 0;
		
		for (int i = 0; i < wellLoc.length; i++) {
			normalSum += (wellLoc[i] - agentLoc[i]) * (wellLoc[i] - agentLoc[i]);
			//torricSum += (Universe.getBounds(i) - (wellLoc[i] - agentLoc[i])) * (Universe.getBounds(i) - (wellLoc[i] - agentLoc[i]));
		}
		
		//Driver.trace(Universe.class, "got zero distance between agent and well. should be captured. isnt it?");
		
		return normalSum;
	}
	
	private static double getTorricDistSum(Double[] wellLoc, Double[] agentLoc) {
		//double normalSum = 0;
		double torricSum = 0;
		
		for (int i = 0; i < wellLoc.length; i++) {
			//normalSum += (wellLoc[i] - agentLoc[i]) * (wellLoc[i] - agentLoc[i]);
			torricSum += (Universe.getBounds(i) - (wellLoc[i] - agentLoc[i])) * (Universe.getBounds(i) - (wellLoc[i] - agentLoc[i]));
		}
		
		//Driver.trace(Universe.class, "got zero distance between agent and well. should be captured. isnt it?");
		
		return torricSum;
	}
	
	public static int getDimensions() {
		return dimensions;
	}
	
	public static double getBounds(int dim) {
		return dimSize[dim];
	}

	public static ArrayList<GravityWell> getWells() {
		return wells;
	}

	public static ArrayList<Agent> getAgents() {
		return agents;
	}

	public static double[] getDimSize() {
		return dimSize;
	}

	public static Graph getGraph() {
		return toSolve;
	}
}
