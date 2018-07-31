package io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import grph.Grph;
import grph.io.DotWriter;
import toools.io.file.RegularFile;

/**
 * This class outputs a dotfile representation of a graph
 * @author matt
 *
 */
public class Output {
	final static Logger log = Logger.getLogger(Output.class);
	public static void export(Grph outputGraph, String outputPath) throws IOException {
		
		log.info("Exporting graph");
		DotWriter d = new DotWriter();		
		// convert graph to *.dot file
		d.writeGraph(outputGraph, new RegularFile(outputPath));


	}
}
