package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import grph.Grph;

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

        // Read .DOT file line by line
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
                
        for (String l : list) {
        	System.out.println(l);
        	System.out.println("end");
        }

        return null;
    }

}