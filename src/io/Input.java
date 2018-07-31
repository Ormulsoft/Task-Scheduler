package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import grph.Grph;
import grph.in_memory.InMemoryGrph;

public class Input {
	
	final static Logger log = Logger.getLogger(Input.class);	
    public static Grph readDotInput(String path) {
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
        Grph outputGraph = new InMemoryGrph();
        
        // Add each vertex from input file
        for (String n : nodesList) {
        	
        	outputGraph.addVertex(Integer.parseInt(String.valueOf(n.trim().charAt(0))));
        	
        	/* TODO: Grph object should also represent weight of each vertex */

        	System.out.println("Graph contains: " + outputGraph.getVertices());
        }
        
        
        // Add each edge from input file
        System.out.println("Edges to be added: ");
        for (String e : edgesList) {
        	
        	// Split on whitespace
        	String[] splitStr = e.trim().split("\\s+");
        	
        	int srcNode = Integer.parseInt(String.valueOf(e.trim().charAt(0)));     	       	
        	int destNode = Integer.parseInt(splitStr[2]);
        	
        	// Retrieve the substring between the '=' and ']' characters, this is the weight of the edge. 
        	e = e.substring(e.indexOf("=") + 1);
        	e = e.substring(0, e.indexOf("]"));
        	
        	int weight = Integer.parseInt(e);
        	
        	System.out.println("Source node is: " + srcNode + " Destination node is: " + destNode + " and weight is: " + weight);
        	
        	// Add edge to graph TODO
        	//outputGraph.addSimpleEdge(srcNode, destNode, true);
        	
        	
        }
        
        
        return null;
    }

}