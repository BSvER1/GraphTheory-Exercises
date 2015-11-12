package graphTheory.hamiltonianCycles.loader;

import java.io.File;
import java.nio.file.FileSystemException;
import java.util.Scanner;

import javax.swing.JFileChooser;

import graphTheory.hamiltonianCycles.assets.Graph;


public class Control {

	Scanner sc;
	JFileChooser fc;
	Graph toSolve;
	
	int maxRunHrs = 6;

	public Control() {
		sc = new Scanner(System.in);
		fc = new JFileChooser();

		chooseGraph();

		sc.close();
	}

	public Control(boolean runAllTests) {
		if (!runAllTests) {
			System.out.println("why is the program set to be unmanaged but not running all tests?!");
			System.exit(0);
		}

		performBenchmark();
	}

	private void chooseGraph() {
		System.out.print("Would you like to [I]mport a graph from file, use built-in [B]enchmark graphs or generate a [R]andom graph?\t");
		String userChoice = sc.nextLine();
		if (userChoice.toUpperCase().charAt(0) == 'I') { // import graph
			Driver.trace("importing graph");
			//show open file dialog.
			fc.setCurrentDirectory(new File("src/graphTheory"));
			//fc.setVisible(true);
			int returnVal = fc.showOpenDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				//This is where a real application would open the file.
				Driver.trace("loading file \""+ file.getAbsolutePath() + "\"");

				GraphLoader gl;
				try {
					gl = new GraphLoader(file);
					toSolve = gl.getGraph();
				} catch (FileSystemException e) {
					Driver.trace("the selected file is too large for the program to process");
					System.exit(0);
				}
				

				Driver.trace("Graph imported successfully.");
			} else {
				Driver.trace("user didnt pick a file. quitting");
				System.exit(0);
			}

		} else if (userChoice.toUpperCase().charAt(0) == 'B') { // use benchmark
			System.out.println("The standard benchmarking graphs are not yet implemented");
		} else if (userChoice.toUpperCase().charAt(0) == 'R') { // use benchmark
			System.out.println("The generation of random graphs is not yet implemented");
		}
		else {
			System.out.println("you entered an invalid input. quitting.");
			System.exit(0);
		}

		if (toSolve != null) {
			do {
				System.out.print("\nWhat solving algorithm would you like to use? or [Q]uit\n"
						//+ "\t[B]rute Buckets\n"
						//+ "\t[R]andomized Brute Buckets\n"
						//+ "\t[A]nt Colony\n"
						//+ "\t[S]ecret Agents\n"
						//+ "\t[F]lower Pollination\n"
						//+ "\t[G]enetic Algorithm\n"
						+ "\t");
				userChoice = sc.nextLine();
				if (userChoice.toUpperCase().charAt(0) == 'B') { 
					//initBruteBuckets();
				} 



				else if (userChoice.toUpperCase().charAt(0) == 'Q') {
					Driver.trace("user wants to quit");
				} else {
					Driver.trace("couldnt understand the user's input. quitting.");
					userChoice = "Q";
				}

			} while (userChoice.toUpperCase().charAt(0) != 'Q');
		}

		System.out.println("Computation has finished.");
	}

	public void runMatlabConversion() {
		File dir = new File("graphsToConvert");
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				//run each method
				Driver.trace("loading file \""+ child.getAbsolutePath() + "\"");
				
				
				try {
					GraphLoader gl = new GraphLoader(child);
					gl.outputGraphToMatlab();
				} catch (FileSystemException e) {
					Driver.trace("the selected file is too large for the program to process");
					//System.exit(0);
				}
				
			}
		}
	}
	
	public void performBenchmark() {

		long runStart;
		int testCount = 10;
		
		//for each graph in folder
		File dir = new File("src/graphTheory/hamiltonianCycles/graphs");
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				//run each method
				Driver.trace("loading file \""+ child.getAbsolutePath() + "\"");
				
				try {
					GraphLoader gl = new GraphLoader(child);
					toSolve = gl.getGraph();
				} catch (FileSystemException e) {
					Driver.trace("the selected file is too large for the program to process");
					//System.exit(0);
					toSolve = null;
				}
				
				if (toSolve != null) {
				
					runStart = System.currentTimeMillis();
					//randomised brute buckets - Brendon
					for (int i = 0; i < testCount; i++) {
						long rbbLimit = 100000;
						long timeStart = System.currentTimeMillis();
						//BruteBuckets bb = new BruteBuckets();
						//bb.solveRandom(toSolve, rbbLimit);
						//ResultsModule.writeRuntimeResultToFile(toSolve, BruteBuckets.class, bb.getResult(), 
						//		System.currentTimeMillis()-timeStart, rbbLimit);
						//System.out.println("Random Brute Buckets approach finished with " +bb.getResult() + " buckets");
						Runtime.getRuntime().gc();
						if (System.currentTimeMillis() - runStart > 1000*60*60*maxRunHrs) {
							break;
						}
					}
				}
			}
			Driver.trace("run finished");
			System.exit(0);
		} else {
			Driver.trace("could not find a list of files to iterate over.");
		}
	}

	

}
