package graphTheory.chromaticNumber.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import graphTheory.chromaticNumber.assets.Graph;

public class GraphLoader {

	Graph g;

	public GraphLoader(File file) {

		init(file);
	}

	private void init(File file) {
		String[] fileExt = file.getAbsolutePath().split("\\.");

		if (fileExt.length < 2) {
			Driver.trace("the file supplied does not have a readable file format: " + fileExt.length);
			System.exit(0);
		} else if (fileExt[fileExt.length - 1].equals("col")) { // DIMACS col format
			loadGraphCol(file);
		} else {
			Driver.trace("the file supplied does not have the correct file format");
			System.exit(0);
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
						g = new Graph(Integer.valueOf(line.split(" ")[2]));
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
		} catch (IOException e) {
			System.err.println("Something went wrong when reading file " + file.getAbsolutePath());
		}
	}

	public Graph getGraph() {
		return g;
	}
}
