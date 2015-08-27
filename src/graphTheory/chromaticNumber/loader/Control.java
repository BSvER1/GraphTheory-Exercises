package graphTheory.chromaticNumber.loader;

import java.io.File;
import java.util.Scanner;

import javax.swing.JFileChooser;

import graphTheory.chromaticNumber.assets.Graph;
import graphTheory.chromaticNumber.solver.BruteBuckets;

public class Control {
 
	Scanner sc;
	JFileChooser fc;
	Graph toSolve;
	
	public Control() {
		sc = new Scanner(System.in);
		fc = new JFileChooser();
		
		chooseGraph();
		
		sc.close();
	}
	
	private void chooseGraph() {
		System.out.print("Would you like to [I]mport a graph from file, use built-in [B]enchmark graphs or generate a [R]andom graph?\t");
		String userChoice = sc.nextLine();
		if (userChoice.toUpperCase().charAt(0) == 'I') { // import graph
			Driver.trace(this.getClass(), "importing graph");
			//show open file dialog.
			int returnVal = fc.showOpenDialog(null);
			 
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                Driver.trace(this.getClass(), "loading file \""+ file.getAbsolutePath() + "\"");
                
                GraphLoader gl = new GraphLoader(file);
                toSolve = gl.getGraph();
                
                Driver.trace(this.getClass(), "Graph imported successfully.");
            }
			
		} else if (userChoice.toUpperCase().charAt(0) == 'B') { // use benchmark
			System.out.println("The standard benchmarking graphs are not yet implemented");
		}else if (userChoice.toUpperCase().charAt(0) == 'R') { // use benchmark
			System.out.println("Generation of random graphs is not yet implemented");
		} else {
			System.out.println("you entered an invalid input. quitting.");
			System.exit(0);
		}
		
		
		System.out.print("What solving algorithm would you like to use?\n"
				+ "\t[B]rute Buckets\n"
				+ "\t[R]andomized Brute Buckets\n"
				+ "\t");
		userChoice = sc.nextLine();
		if (userChoice.toUpperCase().charAt(0) == 'B') { //Brute buckets
			initBruteBuckets();
		} else if (userChoice.toUpperCase().charAt(0) == 'R') { //Random Brute buckets
			initRandomBruteBuckets();
		}
		
	}
	
	
	public void initBruteBuckets() {
		long limit = -1;
		System.out.println("This algorithm is very inefficient. "
				+ "Would you like to set a bound on how many iterations it can perform? "
				+ "Enter [N]o to run unhindered or any number to bound the computation.");
		
		String userChoice = sc.nextLine();
		if (userChoice.toUpperCase().matches("N(.*)")) {
			limit = Long.MAX_VALUE;
			Driver.trace(this.getClass(), "user indicated that they wanted to continue uninterupted.");
		} else if (userChoice.matches("[0-9]+")) {
			limit = Integer.valueOf(userChoice);
			Driver.trace(this.getClass(), "setting limit on permutations to be "+ limit);
		} else {
			Driver.trace(this.getClass(), "got nonsensical data from the user. ");
			return;
		}
		
		BruteBuckets bb = new BruteBuckets();
		bb.solve(toSolve, limit);
		System.out.println("Brute Buckets approach finished with " +bb.getResult() + " buckets");
	}
	
	public void initRandomBruteBuckets() {
		long limit = -1;
		System.out.println("This algorithm is very inefficient. "
				+ "Would you like to set how many iterations it can perform? "
				+ "Enter [N]o to run a single computation.");
		
		String userChoice = sc.nextLine();
		if (userChoice.toUpperCase().matches("N(.*)")) {
			limit = 1;
			Driver.trace(this.getClass(), "user indicated that they wanted to run a single random bucket sort.");
		} else if (userChoice.matches("[0-9]+")) {
			limit = Integer.valueOf(userChoice);
			Driver.trace(this.getClass(), "setting runtime to be "+ limit);
		} else {
			Driver.trace(this.getClass(), "got nonsensical data from the user. ");
			return;
		}
		
		BruteBuckets bb = new BruteBuckets();
		bb.solveRandom(toSolve, limit);
		System.out.println("Random Brute Buckets approach finished with " +bb.getResult() + " buckets");
	}
	
	
	
}
