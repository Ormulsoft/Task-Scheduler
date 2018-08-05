package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import grph.properties.NumericalProperty;
import util.ScheduleGrph;

public class Input {

	final static Logger log = Logger.getLogger(Input.class);

	public static ScheduleGrph readDotInput(String path) {
		log.info("Reading input DOT file");
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
		List<String> nodesList = new ArrayList<String>();
		List<String> edgesList = new ArrayList<String>();

		for (String l : list) {

			if (l.indexOf('>') >= 0) {
				// It must be an edge
				edgesList.add(l);

			} else {
				nodesList.add(l);
			}
		}

		// Creates an empty graph
		ScheduleGrph outputGraph = new ScheduleGrph();

		NumericalProperty vertWeights = new NumericalProperty("Weight");
		NumericalProperty vertLabels = new NumericalProperty("Labels");

		// Add each vertex from input file
		for (String n : nodesList) {

			String label = String.valueOf(n.trim().split("\\s+"));
			int vert = outputGraph.addVertex();

			vertLabels.setValue(vert, label);

			// Used to get the weight of vertex
			Pattern p = Pattern.compile("-?\\d+");
			Matcher m = p.matcher(n);
			while (m.find()) {
				vertWeights.setValue(vert, Integer.parseInt(m.group()));
			}

			//outputGraph.addVertex(Integer.parseInt(String.valueOf(n.trim().charAt(0))));

			/* TODO: Grph object should also represent weight of each vertex */

			log.info("Graph contains: " + outputGraph.getVertices().toString());

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

			// System.out.println("Source node is: " + srcNode + " Destination
			// node is: " + destNode + " and weight is: " + weight);

			// Add edge to graph
			int newEdge = outputGraph.addSimpleEdge(srcNode, destNode, true);

			// Update the edge's width with the weight
			edgeWeights.setValue(newEdge, weight);

		}

		outputGraph.setEdgeWeightProperty(edgeWeights);
		// //Cost of edge with index 5.

		return outputGraph;
	}

}