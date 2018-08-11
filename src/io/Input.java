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
 * This class handles the input parsing of the DotFiles.
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
	 */
	public static ScheduleGrph readDotInput(String path) {
		log.debug("Reading input DOT file");
		File file = new File(path);
		Scanner input = null;

		try {
			input = new Scanner(file);
		} catch (FileNotFoundException e) {
			log.error("File was not found!", e);
		}

		List<String> list = new ArrayList<String>();

		// Read .DOT file line by line, only consider useful lines
		while (input.hasNextLine()) {

			String currentLine = input.nextLine();

			// Only add if it doesn't contain '{', '}', or only whitespace.
			if ((currentLine.indexOf('{') == -1) && (currentLine.indexOf('}') == -1)) {
				if (!currentLine.trim().isEmpty()) {
					// String is not empty and not just whitespace
					list.add(currentLine);
				}
			}
		}

		// Distinguish between edges and nodes within input
		List<String> edgesList = new ArrayList<String>();
		// maps the node ID to its Weight value, so that they can be put into
		// the
		// Grph library easily.
		TreeMap<Integer, Integer> nodes = new TreeMap<Integer, Integer>();

		for (String l : list) {
			if (l.contains("[Weight=")) {
				if (l.indexOf('>') >= 0) {
					// It must be an edge
					edgesList.add(l);
				} else {
					// get id/name of task
					Integer label = Integer.parseInt(String.valueOf(l.trim().split("\\s+")[0]));

					// get weight of task
					l = l.substring(l.indexOf("=") + 1);
					l = l.substring(0, l.indexOf("]"));
					int weight = Integer.parseInt(l);

					nodes.put(label, weight);
				}
			}
		}

		// Creates an empty graph
		ScheduleGrph outputGraph = new ScheduleGrph();

		NumericalProperty vertWeights = new NumericalProperty("Weight");
		NumericalProperty vertLabels = new NumericalProperty("Labels");

		// Collections.sort(nodesList);
		// Add each vertex from input file
		for (Map.Entry<Integer, Integer> entry : nodes.entrySet()) {
			Integer key = entry.getKey();
			Integer weight = entry.getValue();

			int vert = outputGraph.addVertex();
			vertLabels.setValue(vert, key);
			vertWeights.setValue(vert, weight);
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
			int newEdge = outputGraph.addSimpleEdge(srcNode, destNode, true);

			// Update the edge's width with the weight
			edgeWeights.setValue(newEdge, weight);

		}

		outputGraph.setEdgeWeightProperty(edgeWeights);

		return outputGraph;
	}

}