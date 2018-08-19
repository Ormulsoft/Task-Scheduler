package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import grph.properties.NumericalProperty;
import util.ScheduleGrph;

/**
 * This class handles the parsing of the .DOT files into a format that is 
 * appropriate for use in our algorithm implementations (i.e. A ScheduleGrph object)
 * 
 * @author Shane Barboza
 *
 */
public class Input {

	private final static Logger log = Logger.getLogger(Input.class);

	/**
	 * The main method of this class, takes the filepath to the dot file, and
	 * returns the parsed Grph Graph.
	 * 
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 */
	public static ScheduleGrph readDotInput(String path) throws FileNotFoundException {
		log.debug("Reading input DOT file");
		File file = new File(path);
		Scanner input = null;

		input = new Scanner(file);

		List<String> list = new ArrayList<String>();

		// Read .DOT file line by line, only consider useful lines
		while (input.hasNextLine()) {

			String currentLine = input.nextLine();

			// Only add if it doesn't contain '{', '}', or only whitespace.
			if ((currentLine.indexOf('{') == -1) && (currentLine.indexOf('}') == -1)) {
				if (!currentLine.trim().isEmpty()) {

					String appendedLine = currentLine;

					// If the line starts with a number.
					if (Character.isDigit(currentLine.trim().charAt(0))) {

						String thisLine = currentLine;

						while (!thisLine.contains("Weight=") && input.hasNextLine()) {
							String theLineAfter = input.nextLine();
							appendedLine += theLineAfter;
							thisLine = theLineAfter;
						}

						list.add(appendedLine);
					}
				}
			}
		}

		// Distinguish between edges and nodes within input
		List<String> edgesList = new ArrayList<String>();

		// Maps the node ID to its Weight value, so that they can be put into the
		// Grph library easily.
		TreeMap<Integer, Integer> nodes = new TreeMap<Integer, Integer>();

		for (String l : list) {
			if (l.contains("Weight=")) {
				if (l.indexOf('>') >= 0) { // It must be an edge
					
					edgesList.add(l);
				} else {				   // It must be a node
					
					// Get id/name of task
					Integer label = Integer.parseInt(String.valueOf(l.trim().split("\\s+")[0]));

					// Get weight of task
					l = l.substring(l.indexOf("Weight=") + 7);
					l = l.substring(0, l.indexOf("];"));
					int weight = Integer.parseInt(l);

					nodes.put(label, weight);
				}
			}
		}

		// Creates an empty graph
		ScheduleGrph outputGraph = new ScheduleGrph();

		NumericalProperty vertWeights = new NumericalProperty("Weight");
		NumericalProperty vertLabels = new NumericalProperty("Labels");

		TreeMap<Integer, Integer> nodesToId = new TreeMap<Integer, Integer>();

		// Add each vertex from input file
		for (Map.Entry<Integer, Integer> entry : nodes.entrySet()) {

			Integer key = entry.getKey();
			Integer weight = entry.getValue();

			int vert = outputGraph.addVertex();
			vertLabels.setValue(vert, key);
			vertWeights.setValue(vert, weight);

			nodesToId.put(key, vert);
		}
		outputGraph.setVertexWeightProperty(vertWeights);
		outputGraph.setVerticesLabel(vertLabels);

		NumericalProperty edgeWeights = new NumericalProperty("Weight");
		
		// Add each edge from input file
		for (String e : edgesList) {

			// Split on whitespace
			String[] splitStr = e.trim().split("\\s+");

			int srcNode = Integer.parseInt(splitStr[0]);
			int destNode = Integer.parseInt(splitStr[2]);

			// Retrieve and parse the substring between the '=' and ']'
			// characters, this is the weight of the edge.
			e = e.substring(e.indexOf("=") + 1);
			e = e.substring(0, e.indexOf("]"));

			int weight = Integer.parseInt(e);
			
			// Add edge to graph
			int newEdge = outputGraph.addSimpleEdge(nodesToId.get(srcNode), nodesToId.get(destNode), true);

			// Update the edge's width with the weight
			edgeWeights.setValue(newEdge, weight);

		}
		
		input.close();
		outputGraph.setEdgeWeightProperty(edgeWeights);
		
		// 'outputGraph' is now populated with the information retrieved from the .DOT file. 
		return outputGraph;
	}

}