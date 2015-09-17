package graphTheory.chromaticNumber.assets;

import java.util.Random;

public class Agent {

	private Random r;
	
	private double accelConst = 20.0;
	private double distPower = 2.0;
	
	private int comfort;
	
	private int vertexAssociation;
	
	private double[] dimLoc;
	private double[] dimVel;
	
	private boolean isCaptured;
	
	
	
	public Agent(int vertex) {
		r = new Random();
		comfort = 0;
		isCaptured = false;
		
		vertexAssociation = vertex;
		
		dimLoc = new double[Universe.getDimensions()];
		dimVel = new double[Universe.getDimensions()];
		
		for (int i = 0; i < Universe.getDimensions(); i++) {
			dimLoc[i] = r.nextInt((int) (Universe.getBounds(i) - 20)) +10;
			dimVel[i] = 0;
		}
	}
	
	public void calcVelocities() {
		for (int i = 0; i < Universe.getDimensions(); i ++) {
			dimVel[i] = 0;
			for (int j = 0; j < Universe.getWells().size(); j++) {
				
				//System.out.println("distance between vertex "+ vertexAssociation+" and well "+ j+" is "+ Universe.getDistance(Universe.getWells().get(j).getLocation(), dimLoc));
				dimVel[i] += (accelConst*(-dimLoc[i]+Universe.getWells().get(j).getLocation()[i])/(Math.pow(Universe.getDistance(Universe.getWells().get(j).getLocation(), dimLoc), distPower)));
			}
		}
		
		//System.out.println("Velocity : ["+dimVel[0]+", "+dimVel[1]+"]");
	}
	
	public void applyVelocities() {
		for (int i = 0; i < Universe.getDimensions(); i++) {
			dimLoc[i] += dimVel[i];
		}
	}
	
	
	public void resetLocation() {
		for (int i = 0; i < Universe.getDimensions(); i++) {
			dimLoc[i] = r.nextInt((int) Universe.getBounds(i));
			dimVel[i] = 0;
		}
		isCaptured = false;
	}
	
	public void increaseComfort() {
		comfort++;
	}
	
	public void decreaseComfort() {
		comfort--;
	}
	
	public double[] getLocationArray() {
		return dimLoc;
	}
	
	public double getLocation(int dim) {
		return dimLoc[dim];
	}
	
	public void setVelZero() {
		for (int i = 0; i < Universe.getDimensions(); i++) {
			dimVel[i] = 0;
		}
	}
	
	public void setLocation(double[] loc) {
		dimLoc = loc;
	}
	
	public int getComfort() {
		return comfort;
	}

	public int getVertexAssociation() {
		return vertexAssociation;
	}
	
	public boolean isCaptured() {
		return isCaptured;
	}
	
	public void setCaptured(boolean captured) {
		isCaptured = captured;
	}
	
	
}
