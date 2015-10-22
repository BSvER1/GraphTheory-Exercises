package graphTheory.chromaticNumber.loader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;

import graphTheory.chromaticNumber.assets.Graph;

public class GraphLoader {

	Graph g;

	public GraphLoader(File file) throws FileSystemException {

		init(file);
		
		if (g == null) {
			Driver.trace("graph not produced");
		} else {
			Driver.trace("graph produced. max degree is "+g.getMaximalDegree());
		}
	}

	private void init(File file) throws FileSystemException {
		String[] fileExt = file.getAbsolutePath().split("\\.");

		if (file.length() > 1000000) {
			//System.out.println(file.getName()+" has length "+file.length());
			throw new FileSystemException("file is too large!");
		}
		
		if (fileExt.length < 2) {
			Driver.trace("the file supplied does not have a readable file format");
			loadGraphCol(file);
			//System.exit(0);
		} else if (fileExt[fileExt.length - 1].equals("col")) { // DIMACS col format
			loadGraphCol(file);
		} else if (fileExt[fileExt.length-1].equals("bliss")) {
			loadGraphCol(file);
		} else {
			Driver.trace("the file supplied does not have the correct file format");
			//System.exit(0);
		}
	}

	private void loadGraphCol(File file) {
		
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			for (String line; (line = br.readLine()) != null;) {
				
				// process the line.
				if (line.length() > 0) {
					if (line.toLowerCase().charAt(0) == 'c') {
						// encountered a comment line
					} else if (line.toLowerCase().charAt(0) == 'p') {
						g = new Graph(Integer.valueOf(line.split(" ")[2]), Integer.valueOf(line.split(" ")[3]));
					} else if (line.toLowerCase().charAt(0) == 'e') {
						if (g == null) {
							Driver.trace("encountered an edge before the graph was set up. this shouldnt happen!");
							System.exit(0);
						}
						String[] split = line.split(" ");
						g.addEdge(Integer.valueOf(split[1]) - 1, Integer.valueOf(split[2]) - 1);
					}
				}
			}

			br.close();
			
			g.setGraphName(file.getName());
		} catch (IOException e) {
			System.err.println("Something went wrong when reading file " + file.getAbsolutePath());
		}
	}

	public Graph getGraph() {
		return g;
	}
	
	public void outputGraphToMatlab() {
//		ArrayList<String> line = new ArrayList<String>();		
		
//		line.add(""+g.getGraphName()+"=["+System.lineSeparator());
//		
//		for (int i = 0; i < g.getNumVertices(); i++) {
//			String tmpLine = "";
//			for (int j = 0; j < g.getNumVertices(); j++) {
//				if (j == 0)
//					tmpLine = tmpLine.concat(""+g.getEdgeCount(i,j));
//				else if (j == g.getNumVertices()-1)
//					tmpLine = tmpLine.concat(","+g.getEdgeCount(i, j)+";"+System.lineSeparator());
//				else
//					tmpLine = tmpLine.concat(","+g.getEdgeCount(i, j));
//			}
//			line.add(tmpLine);
//		}
//		line.add("];"+System.lineSeparator()+System.lineSeparator());
		
		
		try {
			Files.delete(new File("cnv/"+g.getGraphName()+".mat").toPath());
		} catch (IOException e) {}
		
		//Write to file
		try {
			// Assume default encoding.
			FileWriter fileWriter =
					new FileWriter(new File("cnv/"+g.getGraphName()+".mat"));

			// Always wrap FileWriter in BufferedWriter.
			BufferedWriter bufferedWriter =
					new BufferedWriter(fileWriter);
			
			bufferedWriter.write(""+g.getGraphName()+"=["+System.lineSeparator());
			
			for (int i = 0; i < g.getNumVertices(); i++) {
				String tmpLine = "";
				for (int j = 0; j < g.getNumVertices(); j++) {
					if (j == 0)
						tmpLine = tmpLine.concat(""+g.getEdgeCount(i,j));
					else if (j == g.getNumVertices()-1)
						tmpLine = tmpLine.concat(","+g.getEdgeCount(i, j)+";"+System.lineSeparator());
					else
						tmpLine = tmpLine.concat(","+g.getEdgeCount(i, j));
				}
				bufferedWriter.write(tmpLine);
				
				if (i % 20 == 0) {
					Runtime.getRuntime().gc();
				}
			}
			bufferedWriter.write("];"+System.lineSeparator()+System.lineSeparator());
			
//			for (int i = 0; i < line.size(); i++) {
//				bufferedWriter.write(line.get(i));
//			}
			

			// Always close files.
			bufferedWriter.close();
		}
		catch(IOException ex) {
			System.out.println(
					"Error writing to file '"+"cnv/"+g.getGraphName()+".mat"+"'");
			// Or we could just do this:
			// ex.printStackTrace();
		}
	} 
	
}
