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

	final static Logger log = Logger.getLogger(Output.class);

	public static void export(ScheduleGrph outputGraph, String outputPath) throws IOException {

		log.info("Exporting graph");
		ScheduleDotWriter d = new ScheduleDotWriter();

		d.writeGraph(outputGraph, new RegularFile(outputPath));
	}
}
