package graphTheory.chromaticNumber.assets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import graphTheory.chromaticNumber.loader.Driver;
import graphTheory.chromaticNumber.solver.SecretAgents;

public class Universe {
	
	private static ArrayList<GravityWell> wells;
	private static ArrayList<Agent> agents;
	
	private Random r;
	
	private static Graph toSolve;
	
	private static double[] dimSize;
	
	private static int dimensions;
	
	
	
	public Universe(Graph toSolve, int numDims, double dimSize) {
		r = new Random();
		
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
		GravityWell tempWell = new GravityWell(Math.log(numWells));
		
		do {
			successful = true;
			for (int i = 0; i < wells.size(); i++) {
				
				// TODO 'far enough' away function will go here
				
				if (Arrays.equals(tempWell.getLocation(), wells.get(i).getLocation())) {
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
	
	public static double getDistance(double[] wellLoc, double[] agentLoc) {
		return Math.pow(getDistSum(wellLoc, agentLoc), 0.5);///getDimensions());
	}
	
	private static double getDistSum(double[] wellLoc, double[] agentLoc) {
		double sum = 0;
		for (int i = 0; i < wellLoc.length; i++) {
			sum += Math.pow((wellLoc[i] - agentLoc[i]), 2);
		}
		return sum;
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
