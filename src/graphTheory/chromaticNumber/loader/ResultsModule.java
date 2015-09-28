package graphTheory.chromaticNumber.loader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

import graphTheory.chromaticNumber.assets.Graph;

public class ResultsModule {

	static String minimalOutputFileName = "results_total_runtime.txt";
	static String fullOutputFileName = "results_incremental_runtime.txt";
	
	static String fileFormat = "# Graph | Solver | Colouring | Avg Time (ms) | Solve Count | Iteration Limit";


	@SuppressWarnings("rawtypes")
	public static void writeRuntimeResultToFile(Graph toSolve, Class solver, int colouring, long time, long iterCount) {
		//read file in
		// This will reference one line at a time
		ArrayList<String> line = new ArrayList<String>();
		//line.add(fileFormat);

		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = 
					new FileReader(minimalOutputFileName);

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = 
					new BufferedReader(fileReader);

			while(line.add(bufferedReader.readLine())) {
				if (line.get(line.size()-1) == null) {
					break;
				}
			}   

			// Always close files.
			bufferedReader.close();         
		}
		catch(FileNotFoundException ex) {
			System.out.println(
					"Unable to open file '" + 
							minimalOutputFileName + "'");                
		}
		catch(IOException ex) {
			System.out.println(
					"Error reading file '" 
							+ minimalOutputFileName + "'");                  
			// Or we could just do this: 
			// ex.printStackTrace();
		}

		//do stuff with the data
		boolean broken = false;

		for (int lineNum = 0; lineNum < line.size(); lineNum++) {
			if (line.get(lineNum) != null) {
				if (line.get(lineNum).charAt(0) != '#') {
					String[] lineString = line.get(lineNum).split(",");
					if (lineString[0].equalsIgnoreCase(toSolve.getGraphName()) &&
							lineString[1].equalsIgnoreCase(solver.getSimpleName()) &&
							lineString[2].equalsIgnoreCase(""+colouring) &&
							lineString[5].equalsIgnoreCase("" + iterCount)) {
						lineString[3] = "" + ((((Double.valueOf(lineString[4])-1) * Double.valueOf(lineString[3]))+time)/Double.valueOf(lineString[4]));
						lineString[4] = "" + (Integer.valueOf(lineString[4])+1);
						line.add(""+lineString[0]+","+lineString[1]+","+lineString[2]+","+lineString[3]+","+lineString[4]+","+lineString[5]);
						line.remove(lineNum);
						broken = true;
						break;
					}
				}
			}
		}
		if (!broken) {
			line.add(""+toSolve.getGraphName()+","+solver.getSimpleName()+","+colouring+","+time+",1,"+iterCount);
		}
		
		//removeNulls
		while(line.remove(null));
		
		//add heading line
		if (!line.get(0).equalsIgnoreCase(fileFormat)) {
			line.add(0, fileFormat);
		}
		
		//sort list
		String[] sorted = (String[]) (line.toArray(new String[0]));
		Arrays.sort(sorted);

		try {
			Files.delete(new File(minimalOutputFileName).toPath());
		} catch (IOException e) {

		}
		//Write to file
		try {
			// Assume default encoding.
			FileWriter fileWriter =
					new FileWriter(new File(minimalOutputFileName));

			// Always wrap FileWriter in BufferedWriter.
			BufferedWriter bufferedWriter =
					new BufferedWriter(fileWriter);

			// Note that write() does not automatically
			// append a newline character.
			for (int i = 0; i < sorted.length; i++) {
				bufferedWriter.write(sorted[i]);
				bufferedWriter.newLine();
			}

			// Always close files.
			bufferedWriter.close();
		}
		catch(IOException ex) {
			System.out.println(
					"Error writing to file '"
							+ minimalOutputFileName + "'");
			// Or we could just do this:
			// ex.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	public static void writeIncrementalResultToFile(Graph toSolve, Class solver, int colouring, long time, long iterCount) {

		//read file in
		// This will reference one line at a time
		ArrayList<String> line = new ArrayList<String>();
		//line.add(fileFormat);

		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = 
					new FileReader(fullOutputFileName);

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = 
					new BufferedReader(fileReader);

			while(line.add(bufferedReader.readLine())) {
				if (line.get(line.size()-1) == null) {
					break;
				}
			}   

			// Always close files.
			bufferedReader.close();         
		}
		catch(FileNotFoundException ex) {
			System.out.println(
					"Unable to open file '" + 
							fullOutputFileName + "'");                
		}
		catch(IOException ex) {
			System.out.println(
					"Error reading file '" 
							+ fullOutputFileName + "'");                  
			// Or we could just do this: 
			// ex.printStackTrace();
		}

		//do stuff with the data
		boolean broken = false;

		for (int lineNum = 0; lineNum < line.size(); lineNum++) {
			if (line.get(lineNum) != null) {
				if (line.get(lineNum).charAt(0) != '#') {
					String[] lineString = line.get(lineNum).split(",");
					if (lineString[0].equalsIgnoreCase(toSolve.getGraphName()) &&
							lineString[1].equalsIgnoreCase(solver.getSimpleName()) &&
							lineString[2].equalsIgnoreCase(""+colouring) &&
							lineString[5].equalsIgnoreCase("" + iterCount)) {
						lineString[3] = "" + ((((Double.valueOf(lineString[4])-1) * Double.valueOf(lineString[3]))+time)/Double.valueOf(lineString[4]));
						lineString[4] = "" + (Integer.valueOf(lineString[4])+1);
						line.add(""+lineString[0]+","+lineString[1]+","+lineString[2]+","+lineString[3]+","+lineString[4]+","+lineString[5]);
						line.remove(lineNum);
						broken = true;
						break;
					}
				}
			}
		}
		if (!broken) {
			line.add(""+toSolve.getGraphName()+","+solver.getSimpleName()+","+colouring+","+time+",1,"+iterCount);
		}
		
		//remove nulls
		while(line.remove(null));
		
		//add heading line
		if (!line.get(0).equalsIgnoreCase(fileFormat)) {
			line.add(0, fileFormat);
		}
		
		//sort list
		String[] sorted = (String[]) (line.toArray(new String[0]));
		Arrays.sort(sorted);

		try {
			Files.delete(new File(fullOutputFileName).toPath());
		} catch (IOException e) {

		}
		//Write to file
		try {
			// Assume default encoding.
			FileWriter fileWriter =
					new FileWriter(new File(fullOutputFileName));

			// Always wrap FileWriter in BufferedWriter.
			BufferedWriter bufferedWriter =
					new BufferedWriter(fileWriter);

			// Note that write() does not automatically
			// append a newline character.
			for (int i = 0; i < sorted.length; i++) {
				bufferedWriter.write(sorted[i]);
				bufferedWriter.newLine();
			}

			// Always close files.
			bufferedWriter.close();
		}
		catch(IOException ex) {
			System.out.println(
					"Error writing to file '"
							+ fullOutputFileName + "'");
			// Or we could just do this:
			// ex.printStackTrace();
		}
	}
}
