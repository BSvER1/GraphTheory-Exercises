package graphTheory.chromaticNumber.generator;
import java.util.Scanner;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;

import graphTheory.chromaticNumber.assets.Graph;
import graphTheory.chromaticNumber.loader.Driver;

public class GraphProducer {

	UniformIntegerDistribution uIntDist;
	
	Scanner sc;
	int regularity = 0;
	
	Graph g1, g2, result;
	
	int[] g1regularity, g2regularity;
	
	public GraphProducer() {
	
		sc = new Scanner(System.in);
		String userResponse;
		Driver.trace("Starting Graph producer");
		
		System.out.print("enter the number vertices in G1 or G2\t");
		userResponse = sc.nextLine();
		
		if (userResponse.matches("[0-9]+")) {
			g1 = new Graph(Integer.valueOf(userResponse));
			g1.setGraphName("G1");
			g2 = new Graph(g1.getNumVertices());
			g2.setGraphName("G2");
			Driver.trace("setting vert count to be "+ g1.getNumVertices());
		} else {
			Driver.trace("got nonsensical data from the user. ");
			return;
		}
		
		System.out.print("enter the k number\t");
		userResponse = sc.nextLine();
		
		if (userResponse.matches("[0-9]+")) {
			regularity = Integer.valueOf(userResponse);
			Driver.trace("setting k-regular to be "+ regularity);
		} else {
			Driver.trace("got nonsensical data from the user. ");
			return;
		}
		
		if (regularity >= g1.getNumVertices()) {
			Driver.trace("got nonsensical data from the user. ");
			System.exit(0);
		}
		
		g1regularity = new int[g1.getNumVertices()];
		g2regularity = new int[g2.getNumVertices()];
		
		for (int i = 0; i < g1.getNumVertices(); i++) {
			g1regularity[i] = 0;
			g2regularity[i] = 0;
		}
		
		uIntDist = new UniformIntegerDistribution(0, g1.getNumVertices()-1);
		while (!isG1Full()) {
			
		}
	}
	
	private boolean isG1Full() {
		for (int i = 0; i < g1regularity.length; i++) {
			if (g1regularity[i] != regularity) {
				return false;
			}
		}
		return true;
	}
	
}