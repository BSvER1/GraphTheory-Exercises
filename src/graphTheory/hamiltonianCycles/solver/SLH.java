package graphTheory.hamiltonianCycles.solver;

import java.util.ArrayList;

import graphTheory.chromaticNumber.loader.Driver;
import graphTheory.hamiltonianCycles.assets.Graph;

public class SLH {

	ArrayList<Integer> path;
	
	Graph toSolve;
	
	public SLH(Graph toSolve) {
		this.toSolve = toSolve;
		
		path = new ArrayList<Integer>();
		
		test();
	}
	
	private void test() {
		for (int i = 0; i < 20; i++) {
			path.add(i);
		}
		path.add(0);
		
		gamma(18,9,5);
		//kappa(1,9,5,14);
		
		for (int i = 0; i < path.size(); i++) {
			System.out.print(path.get(i)+" ");
		}
	}
	
	private boolean canGamma(int y, int x, int a) {	
		//if (toSolve.isEdge(y, x))
			if (path.indexOf(y) == path.indexOf(x)+1 || path.indexOf(y) == path.indexOf(x)-1)
				return true;
		return false;
	}
	
	/**
	 * Gamma Isomorphism. This transformation maps an ordering 
	 * (x, ..., b, a, ..., y, x) to ordering (b, ..., x, a, ..., y, b). 
	 * That is, if we define segments A = (x, ..., b) and B = (a, ..., y), 
	 * then gamma(y,x,a) maps ordering (A, B) to ordering (A^R, B). By definition, gamma(y,x,a) is 
	 * only defined for orderings in which x and y are adjacent on the ordering. Vertex b is implicitly 
	 * defined as being the vertex adjacent to a on the segment (y,x,...,a).
	 * @param y - second to last vertex.
	 * @param x - vertex to be made adjacent to a
	 * @param a - start of held vertices
	 */
	private void gamma(int y, int x, int a) {
		
		ArrayList<Integer> newPath = new ArrayList<Integer>();
		
		int pos = path.indexOf(Integer.valueOf(y));
		int counter = 0;
		while (counter < path.size()) {
			newPath.add(path.get((pos)%path.size()));
			Driver.trace("placing "+ newPath.get(newPath.size()-1) +", "+ (pos)%path.size());
			counter++;
			pos++;
			if (pos == path.size()) {
				pos++;
			}
		}
		
		for (int i = 0; i < path.size(); i++) {
			System.out.print(path.get(i)+" ");
		}
		System.out.println();
		for (int i = 0; i < newPath.size(); i++) {
			System.out.print(newPath.get(i)+" ");
		}
		System.out.println();
		
		ArrayList<Integer> orderingA = new ArrayList<Integer>();
		
		while (newPath.get(0) != a) {
			orderingA.add(newPath.remove(0));
		}
		
		newPath.remove(newPath.size()-1);
		
		while (!orderingA.isEmpty()) {
			newPath.add(0, orderingA.remove(0));
		}
		newPath.add(newPath.get(0));
		
		path = newPath;
	}
	
	/**
	 * kappa(x,a,c,d) is only defined for orderings in which segment
	 * (x, ..., c, ..., a) does not contain d.
	 * @param x
	 * @param a
	 * @param c
	 * @param d
	 * @return whether d occurs before a in the ordering
	 */
	private boolean canKappa(int x, int a, int c, int d) {
		
		if (path.indexOf(Integer.valueOf(a)) > path.indexOf(Integer.valueOf(d))) 
			return false;
		return true;
	}
	
	/**
	 * Kappa isomorphism. This transformation maps an ordering
	 * (x, ..., e, c, ..., a, b, ..., f, d, ..., y, x) to an ordering
	 * (e, ..., x, a, ..., c, d, ..., y, f, ..., b, e). That is, if we denote segments
	 * A = (x, ..., e), B = (c, ..., a), C = (b, ..., f) and D = (d, ..., y), then
	 * kappa(x,a,c,d) maps ordering (A, B , C, D) to ordering (A^R, B^R, D, C^R). 
	 * This definition implies that kappa(x,a,c,d) is only defined for orderings in which segment
	 * (x, ..., c, ..., a) does not contain d. This definition allows for e = x, that is, segment 
	 * A contains a single vertex; for y = d, that is, D contains a single vertex; and for segment 
	 * C to be empty (ie. so vertex a directly precedes vertex d on the ordering), or for b = f, 
	 * that is, segment C contains a single vertex.
	 * @param x
	 * @param a
	 * @param c
	 * @param d
	 */
	private void kappa(int x, int a, int c, int d) {
		ArrayList<Integer> orderingA = new ArrayList<Integer>();
		ArrayList<Integer> orderingB = new ArrayList<Integer>();
		ArrayList<Integer> orderingC = new ArrayList<Integer>();
		ArrayList<Integer> orderingD = new ArrayList<Integer>();
		
		
		ArrayList<Integer> newPath = new ArrayList<Integer>();
		
		int pos = path.indexOf(Integer.valueOf(x));
		int counter = 0;
		while (counter < path.size()) {
			newPath.add(path.get((pos)%path.size()));
			counter++;
			pos++;
			if (pos == path.size()) {
				pos++;
			}
		}
		
		for (int i = 0; i < path.size(); i++) {
			System.out.print(path.get(i) + " ");
		}
		System.out.println();
		for (int i = 0; i < newPath.size(); i++) {
			System.out.print(newPath.get(i) + " ");
		}
		System.out.println();
		
		
		
		
		
		newPath.remove(newPath.size()-1);
		
		while (newPath.contains(Integer.valueOf(d))) {
			Driver.trace("creating ordering D");
			orderingD.add(0, newPath.remove(newPath.size()-1));
		}
		if (newPath.get(newPath.size()-1) != a) {
			while (newPath.get(newPath.size()-1) != a) {
				Driver.trace("creating ordering C");
				orderingC.add(0, newPath.remove(newPath.size()-1));
			}
		}
		while (newPath.contains(Integer.valueOf(c))) {
			Driver.trace("creating ordering B");
			orderingB.add(0, newPath.remove(newPath.size()-1));
		}
		while (!newPath.isEmpty()) {
			Driver.trace("creating ordering A");
			orderingA.add(newPath.remove(0));
		}
		
		
		for (int i = 0; i < orderingA.size(); i++) {
			System.out.print(orderingA.get(i)+" ");
		}
		System.out.println();
		for (int i = 0; i < orderingB.size(); i++) {
			System.out.print(orderingB.get(i)+" ");
		}
		System.out.println();
		for (int i = 0; i < orderingC.size(); i++) {
			System.out.print(orderingC.get(i)+" ");
		}
		System.out.println();
		for (int i = 0; i < orderingD.size(); i++) {
			System.out.print(orderingD.get(i)+" ");
		}
		System.out.println();
		
		
		while (!orderingC.isEmpty()) {
			Driver.trace("placing ordering C^R");
			newPath.add(orderingC.remove(orderingC.size()-1));
		}
		while (!orderingD.isEmpty()) {
			Driver.trace("placing ordering D");
			newPath.add(0, orderingD.remove(orderingD.size()-1));
		}
		while (!orderingB.isEmpty()) {
			Driver.trace("placing ordering B^R");
			newPath.add(0, orderingB.remove(0));
		}
		while (!orderingA.isEmpty()) {
			Driver.trace("placing ordering A^R");
			newPath.add(0, orderingA.remove(0));
		}
		
		Driver.trace("placing end");
		newPath.add(newPath.get(0));
		
		path = newPath;
	}
}











