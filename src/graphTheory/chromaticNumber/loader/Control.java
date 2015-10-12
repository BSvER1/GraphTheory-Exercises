package graphTheory.chromaticNumber.loader;

import java.io.File;
import java.nio.file.FileSystemException;
import java.util.Scanner;

import javax.swing.JFileChooser;

import graphTheory.chromaticNumber.assets.Graph;
import graphTheory.chromaticNumber.generator.RandomGen;
import graphTheory.chromaticNumber.solver.AntColony;
import graphTheory.chromaticNumber.solver.BruteBuckets;
import graphTheory.chromaticNumber.solver.FlowerPollination;
import graphTheory.chromaticNumber.solver.GeneSplicer;
import graphTheory.chromaticNumber.solver.SecretAgents;

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
			fc.setCurrentDirectory(new File("src/graphTheory/chromaticNumber/graphs"));
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
			
			File file = new File("src/graphTheory/chromaticNumber/graphs/queen7_7.col");
			Driver.trace("loading file \""+ file.getAbsolutePath() + "\"");

			try {
				GraphLoader gl = new GraphLoader(file);
				toSolve = gl.getGraph();
			} catch (FileSystemException e) {
				Driver.trace("the selected file is too large for the program to process");
				System.exit(0);
			}

		} else if (userChoice.toUpperCase().charAt(0) == 'R') { // use benchmark
			genRandom();
		} else if (userChoice.toUpperCase().charAt(0) == 'Z') { //convert to Matlab
			runMatlabConversion();
		//} else if (userChoice.toUpperCase().charAt(0) == 'F') { //just for fun
		//	justForFun();
		}
		
		else {
			System.out.println("you entered an invalid input. quitting.");
			System.exit(0);
		}

		do {
			System.out.print("\nWhat solving algorithm would you like to use? or [Q]uit\n"
					+ "\t[B]rute Buckets\n"
					+ "\t[R]andomized Brute Buckets\n"
					+ "\t[A]nt Colony\n"
					+ "\t[S]ecret Agents\n"
					+ "\t[F]lower Pollination\n"
					+ "\t[G]enetic Algorithm\n"
					+ "\t");
			userChoice = sc.nextLine();
			if (userChoice.toUpperCase().charAt(0) == 'B') { //Brute buckets
				initBruteBuckets();
			} else if (userChoice.toUpperCase().charAt(0) == 'R') { //Random Brute buckets
				initRandomBruteBuckets();
			} else if (userChoice.toUpperCase().charAt(0) == 'A') { // ants
				initAntColony();
			} else if (userChoice.toUpperCase().charAt(0) == 'S') { // secret agents
				initSecretAgents();
			} else if (userChoice.toUpperCase().charAt(0) == 'F') { // flower pollination
				initFlowerPollination();
			} else if (userChoice.toUpperCase().charAt(0) == 'G') { // genetic algorithm
				initGeneticAlgorithm();
			} //More options here



			else if (userChoice.toUpperCase().charAt(0) == 'Q') {
				Driver.trace("user wants to quit");
			} else {
				Driver.trace("couldnt understand the user's input. quitting.");
				userChoice = "Q";
			}

		} while (userChoice.toUpperCase().charAt(0) != 'Q');

		System.out.println("Computation has finished.");
	}

	public void runMatlabConversion() {
		File dir = new File("src/graphTheory/chromaticNumber/graphsToConvert");
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
	
	public void justForFun() {		
		File dir = new File("src/graphTheory/chromaticNumber/graphsToConvert");
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				//run each method
				Driver.trace("loading file \""+ child.getAbsolutePath() + "\"");
				
				try {
					GraphLoader gl = new GraphLoader(child);
					gl.outputGraphToMatlab();
					toSolve = gl.getGraph();
				} catch (FileSystemException e) {
					Driver.trace("the selected file is too large for the program to process");
					//System.exit(0);
					toSolve = null;
				}
				
				if (toSolve != null) {
					long rbbLimit = 20000;
					long timeStart = System.currentTimeMillis();
					BruteBuckets bb = new BruteBuckets();
					bb.solveRandom(toSolve, rbbLimit);
					ResultsModule.writeRuntimeResultToFile(toSolve, BruteBuckets.class, bb.getResult(), 
							System.currentTimeMillis()-timeStart, rbbLimit);
					System.out.println("Random Brute Buckets approach finished with " +bb.getResult() + " buckets");
				}	
			}
		}
	}
	
	public void performBenchmark() {

		long runStart;
		int testCount = 20;
		
		//for each graph in folder
		File dir = new File("src/graphTheory/chromaticNumber/graphs");
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
						BruteBuckets bb = new BruteBuckets();
						bb.solveRandom(toSolve, rbbLimit);
						ResultsModule.writeRuntimeResultToFile(toSolve, BruteBuckets.class, bb.getResult(), 
								System.currentTimeMillis()-timeStart, rbbLimit);
						System.out.println("Random Brute Buckets approach finished with " +bb.getResult() + " buckets");
						if (System.currentTimeMillis() - runStart > 1000*60*60*maxRunHrs) {
							break;
						}
					}
					
					runStart = System.currentTimeMillis();
					//Secret Agents - Brendon
					for (int i = 0; i < testCount; i++) {
						long saLimit = 10;
						long timeStart = System.currentTimeMillis();
						SecretAgents sa = new SecretAgents(toSolve.getGraphName(), true);
						sa.solve(toSolve, saLimit);
						ResultsModule.writeRuntimeResultToFile(toSolve, SecretAgents.class, sa.getResult(), 
								System.currentTimeMillis()-timeStart, sa.getNumInternalIterations());
						System.out.println("secret agent approach finished with " +sa.getResult() + " colours");
						if (System.currentTimeMillis() - runStart > 1000*60*60*maxRunHrs) {
							break;
						}
					}
					
					runStart = System.currentTimeMillis();
					//Flower Pollination - Scott
					for (int i = 0; i < testCount; i++) {
						long fpLimit = 15;
						int flowerCount = 40;
						long timeStart = System.currentTimeMillis();
						FlowerPollination fpa = new FlowerPollination();
						fpa.solve(toSolve, flowerCount, fpLimit);
						ResultsModule.writeRuntimeResultToFile(toSolve, FlowerPollination.class, fpa.getResult(), 
								System.currentTimeMillis()-timeStart, fpLimit);
						System.out.println("flower power approach finished with " +fpa.getResult() + " colours");
						if (System.currentTimeMillis() - runStart > 1000*60*60*maxRunHrs) {
							break;
						}
					}
					
					runStart = System.currentTimeMillis();
					//Genetic Algorithm - Collaborative
					for (int i = 0; i < testCount; i++) {
						long gaLimit = 15;
						int chromosomeCount = 50;
						long timeStart = System.currentTimeMillis();
						GeneSplicer gcp = new GeneSplicer(toSolve.getNumVertices(), chromosomeCount);
						gcp.solve(toSolve, chromosomeCount, gaLimit);
						ResultsModule.writeRuntimeResultToFile(toSolve, GeneSplicer.class, gcp.getResult(), 
								System.currentTimeMillis()-timeStart, 20000);
						System.out.println("secret agent approach finished with " +gcp.getResult() + " colours");
						if (System.currentTimeMillis() - runStart > 1000*60*60*maxRunHrs) {
							break;
						}
					}/**/
				}
				
			}
		} else {
			Driver.trace("could not find a list of files to iterate over.");
		}
	}

	public void genRandom() {
		RandomGen gen;
		int vertNum = 0;
		long edgeLimit = 0;
		float edgeProb = 0;

		System.out.print("Please enter the number of vertices the graph will contain:\t");
		String userChoice = sc.nextLine();

		if (userChoice.matches("[0-9]+")) {
			vertNum = Integer.valueOf(userChoice);
			Driver.trace("setting vertex count for generation to be "+ vertNum);
		} else {
			Driver.trace("got nonsensical data from the user. ");
			System.exit(0);
		}

		System.out.print("Please enter the maximum number of edges the graph can contain:\t");
		userChoice = sc.nextLine();

		if (userChoice.matches("[0-9]+")) {
			edgeLimit = Integer.valueOf(userChoice);
			if (edgeLimit > (vertNum*vertNum)/2) {
				edgeLimit = (vertNum * (vertNum-1))/2;
				System.out.println("that limit is too high! limit must be less than " + edgeLimit+". Setting the limit to that instead.");
			}
			Driver.trace("setting edgeLimit for generation to be "+ edgeLimit);
		} else {
			Driver.trace("got nonsensical data from the user. ");
			System.exit(0);
		}

		System.out.print("Please enter the probability that an edge will be generated:\t");
		userChoice = sc.nextLine();

		if (userChoice.matches("^[-+]?[0-9]*.?[0-9]+([eE][-+]?[0-9]+)?$")) {
			edgeProb = Float.valueOf(userChoice);
			Driver.trace("setting edge probability for generation to be "+ edgeProb);
		} else {
			Driver.trace("got nonsensical data from the user. ");
			System.exit(0);
		}

		gen = new RandomGen(vertNum, edgeLimit, edgeProb);

		toSolve = gen.getGraph();
	}

	public void initBruteBuckets() {
		long limit = -1;
		System.out.println("This algorithm is very inefficient. "
				+ "Would you like to set a bound on how many iterations it can perform? "
				+ "Enter [N]o to run unhindered or any number to bound the computation.");

		String userChoice = sc.nextLine();
		if (userChoice.toUpperCase().matches("N(.*)")) {
			limit = Long.MAX_VALUE;
			Driver.trace("user indicated that they wanted to continue uninterupted.");
		} else if (userChoice.matches("[0-9]+")) {
			limit = Integer.valueOf(userChoice);
			Driver.trace("setting limit on permutations to be "+ limit);
		} else {
			Driver.trace("got nonsensical data from the user. ");
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
				+ "Enter [N]o to run the default computation.");

		String userChoice = sc.nextLine();
		if (userChoice.toUpperCase().matches("N(.*)")) {
			limit = 20000;
			Driver.trace("user indicated that they wanted to run the default random bucket sort.");
		} else if (userChoice.matches("[0-9]+")) {
			limit = Integer.valueOf(userChoice);
			Driver.trace("setting runtime to be "+ limit);
		} else {
			Driver.trace("got nonsensical data from the user. ");
			return;
		}
		
		int runCount = 0;
		System.out.println("How many times would you like to run the solver?");
		userChoice = sc.nextLine();
		if (userChoice.toUpperCase().matches("N(.*)")) {
			runCount = 1;
			Driver.trace("user indicated that they wanted to run a single random bucket sort round.");
		} else if (userChoice.matches("[0-9]+")) {
			runCount = Integer.valueOf(userChoice);
			Driver.trace("setting number of rounds to be "+ runCount);
		} else {
			Driver.trace("got nonsensical data from the user. ");
			return;
		}
		
		for (int i = 0; i < runCount; i++) {
			long timeStart = System.currentTimeMillis();
			BruteBuckets bb = new BruteBuckets();
			bb.solveRandom(toSolve, limit);
			ResultsModule.writeRuntimeResultToFile(toSolve, BruteBuckets.class, bb.getResult(), 
					System.currentTimeMillis()-timeStart, limit);
			System.out.println("Random Brute Buckets approach finished with " +bb.getResult() + " buckets");
		}
	}

	public void initAntColony() {
		long limit = -1;
		System.out.println("This algorithm requires a specified exit point. "
				+ "enter a number to specify the maximum number of iterations the simulation is allowed to perform. "
				+ "or enter [n]o for a single round of solving.");

		String userChoice = sc.nextLine();
		if (userChoice.toUpperCase().matches("N(.*)")) {
			limit = 1;
			Driver.trace("user indicated that they wanted to run a single ant colony sort.");
		} else if (userChoice.matches("[0-9]+")) {
			limit = Integer.valueOf(userChoice);
			Driver.trace("setting runtime to be "+ limit);
		} else {
			Driver.trace("got nonsensical data from the user. ");
			return;
		}

		AntColony ac = new AntColony();
		//get naive limit
		BruteBuckets bb = new BruteBuckets();
		bb.solve(toSolve, 1);
		Driver.trace("getting naive initial colouring from buckets for Ant Colony of " +bb.getResult() + " colours");

		//bb.getResult()-1
		ac.solve(toSolve, limit, toSolve.getNumVertices());
		System.out.println("Ant Colony approach finished with " +ac.getResult() + " colours");
	}

	public void initSecretAgents() {
		long limit = -1;
		System.out.println("This algorithm requires a specified exit point. "
				+ "enter a number to specify the maximum number of iterations the simulation is allowed to perform. "
				+ "or enter [n]o for the default number of iterations.");

		String userChoice = sc.nextLine();
		if (userChoice.toUpperCase().matches("N(.*)")) {
			limit = 20;
			Driver.trace("user indicated that they wanted to run the default secret agent simulation.");
		} else if (userChoice.matches("[0-9]+")) {
			limit = Integer.valueOf(userChoice);
			Driver.trace("setting runtime to be "+ limit);
		} else {
			Driver.trace("got nonsensical data from the user. ");
			return;
		}

		long timeStart = System.currentTimeMillis();
		SecretAgents sa = new SecretAgents(toSolve.getGraphName());
		sa.solve(toSolve, limit);
		ResultsModule.writeRuntimeResultToFile(toSolve, SecretAgents.class, sa.getResult(), 
				System.currentTimeMillis()-timeStart, limit);
		System.out.println("secret agent approach finished with " +sa.getResult() + " colours");
	}

	public void initFlowerPollination() {
		long limit = -1;
		int flowers = 0;
		System.out.println("This algorithm requires a specified exit point. "
				+ "enter a number to specify the maximum number of iterations the simulation is allowed to perform. "
				+ "or enter [n]o for the default number of iterations.");

		String userChoice = sc.nextLine();
		if (userChoice.toUpperCase().matches("N(.*)")) {
			limit = 20;
			Driver.trace("user indicated that they wanted to run the default flower pollination simulation.");
		} else if (userChoice.matches("[0-9]+")) {
			limit = Integer.valueOf(userChoice);
			Driver.trace("setting runtime to be "+ limit);
		} else {
			Driver.trace("got nonsensical data from the user. ");
			return;
		}

		System.out.println("enter the number of flowers to pollinate or [n] for the default configuration");

		userChoice = sc.nextLine();
		if (userChoice.toUpperCase().matches("N(.*)")) {
			flowers = 20;
			Driver.trace("user indicated that they wanted to run the default flower pollination simulation.");
		} else if (userChoice.matches("[0-9]+")) {
			flowers = Integer.valueOf(userChoice);
			Driver.trace("setting number of flowers to be "+ flowers);
		} else {
			Driver.trace("got nonsensical data from the user. ");
			return;
		}

		FlowerPollination fpa = new FlowerPollination();
		fpa.solve(toSolve, flowers, limit);
		
		System.out.println("flower power approach finished with " +fpa.getResult() + " colours");
	}
	
	public void initGeneticAlgorithm() {
		long limit;
		int chromosomeCount;
		System.out.println("This algorithm requires a specified exit point. "
				+ "enter a number to specify the maximum number of iterations the simulation is allowed to perform. "
				+ "or enter [n]o for the default number of iterations.");

		String userChoice = sc.nextLine();
		if (userChoice.toUpperCase().matches("N(.*)")) {
			limit = 20;
			Driver.trace("user indicated that they wanted to run the default flower pollination simulation.");
		} else if (userChoice.matches("[0-9]+")) {
			limit = Integer.valueOf(userChoice);
			Driver.trace("setting runtime to be "+ limit);
		} else {
			Driver.trace("got nonsensical data from the user. ");
			return;
		}
		
		System.out.println("enter the number of chromosomes to mutate or [n] for the default configuration");

		userChoice = sc.nextLine();
		if (userChoice.toUpperCase().matches("N(.*)")) {
			chromosomeCount = 50;
			Driver.trace("user indicated that they wanted to run the default genetic algorithm.");
		} else if (userChoice.matches("[0-9]+")) {
			chromosomeCount = Integer.valueOf(userChoice);
			Driver.trace("setting number of chromosomes to be "+ chromosomeCount);
		} else {
			Driver.trace("got nonsensical data from the user. ");
			return;
		}


		GeneSplicer gcp = new GeneSplicer(toSolve.getNumVertices(), chromosomeCount);
		gcp.solve(toSolve, chromosomeCount, limit);
		
		System.out.println("flower power approach finished with " +gcp.getResult() + " colours");
	}


}
