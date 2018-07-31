package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import grph.Grph;
import grph.in_memory.InMemoryGrph;

public class Input {

    public static Grph readDotInput(String path) {
       
    	File file = new File(path);
        Scanner input = null;
        
		try {
			input = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println("File was not found!");
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

        	System.out.println("Graph contains: " + outputGraph.getVertices());
        }
        
        
        return null;
    }

}