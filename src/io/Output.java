package io;

import java.io.IOException;

import org.apache.log4j.Logger;

import toools.io.file.RegularFile;
import util.ScheduleDotWriter;
import util.ScheduleGrph;

/**
 * This class outputs a dotfile representation of a graph
 * 
 * @author matt
 *
 */
public class Output {

	private final static Logger log = Logger.getLogger(Output.class);

	/**
	 * The main method of this class, takes a graph and an output path and
	 * exports it to the file defined in the path variable.
	 * 
	 * @param outputGraph
	 * @param outputPath
	 * @throws IOException
	 */
	public static void export(ScheduleGrph outputGraph, String outputPath) throws IOException {

		log.debug("Exporting graph");
		ScheduleDotWriter d = new ScheduleDotWriter();

		d.writeGraph(outputGraph, new RegularFile(outputPath));
	}
}
