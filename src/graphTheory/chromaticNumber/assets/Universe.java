package graphTheory.chromaticNumber.assets;

import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Random;

import graphTheory.chromaticNumber.loader.Driver;
import graphTheory.chromaticNumber.solver.SecretAgents;

public class Universe {

	private static ArrayList<GravityWell> wells;
	private static ArrayList<Agent> agents;

	// private Random r;

	private static Graph toSolve;

	private static double[] dimSize;

	private static int dimensions;

	public Universe(Graph toSolve, int numDims, double dimSize) {
		// r = new Random();

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
		if (SecretAgents.SECRET_TRACING)
			Driver.trace("adding wells");
		for (int i = 0; i < numWells; i++) {
			addWell();
		}
	}

	private void addWell() {
		boolean successful;
		GravityWell tempWell = new GravityWell();

		do {
			successful = true;
			for (int i = 0; i < wells.size(); i++) {

				// check if any are the same - do first to avoid div by 0 error.
				if (Arrays.equals(tempWell.getLocation(), wells.get(i).getLocation())) {
					tempWell.resetLocation();
					successful = false;
					break;
				}

				if (getDist(tempWell.getLocation(), wells.get(i).getLocation()) < (2.5 * GravityWell.getRadius())) {
					tempWell.resetLocation();
					successful = false;
					break;
				}
			}
		} while (!successful);

		wells.add(tempWell);
	}

	public void addAgents(int numAgents) {
		if (SecretAgents.SECRET_TRACING)
			Driver.trace("adding agents");
		for (int i = 1; i <= numAgents; i++) {
			agents.add(new Agent(i));
		}
	}

	public static boolean getIsTorricDistShorter(Double[] wellLoc, Double[] agentLoc, int dim) {
		return Universe.getBounds(dim) - Math.abs(wellLoc[dim] - agentLoc[dim]) < Math.abs(wellLoc[dim] - agentLoc[dim]);
	}

	public static double getDist(Double[] wellLoc, Double[] agentLoc) {
		
		double sum = 0;
		
		for (int i = 0; i < Universe.getDimensions(); i++) {
			sum += Math.pow(Math.min(Math.abs(wellLoc[i] - agentLoc[i]), Universe.getBounds(i) - Math.abs(wellLoc[i] - agentLoc[i])),2);
		}
		
		//Driver.trace("dist "+Math.sqrt(sum));
		return Math.sqrt(sum);
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
