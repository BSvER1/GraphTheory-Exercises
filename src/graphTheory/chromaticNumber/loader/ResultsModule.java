package graphTheory.chromaticNumber.loader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import graphTheory.chromaticNumber.assets.Graph;

public class ResultsModule {
	
	
	
	String minimalOutputFileName = "results_best.txt";
	String fullOutputFileName = "results_all.txt";
	
	
	public ResultsModule() {
		
	}
	
	public void writeBestResultToFile(Graph toSolve, Class solver, int colouring, long time) {
		
	}
	
	public void writeResultToFile(Graph toSolve, Class solver, int colouring, long time) {
		
		//read file in
		// This will reference one line at a time
        ArrayList<String> line = new ArrayList<String>();

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
		
		
		
		
		
		
		//Write to file
		try {
            // Assume default encoding.
            FileWriter fileWriter =
                new FileWriter(fullOutputFileName);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter =
                new BufferedWriter(fileWriter);

            // Note that write() does not automatically
            // append a newline character.
            for (int i = 0; i < line.size(); i++) {
            	bufferedWriter.write(line.get(i));
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
