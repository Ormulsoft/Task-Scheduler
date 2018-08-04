package io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import alg.AlgorithmStub;
import util.ScheduleGrph;

/**
 * Entry point for the task scheduling assignment
 * 
 * @author
 *
 */
public class Main {

	final static Logger log = Logger.getLogger(Main.class);

	private static final String DEFAULT_OUTPUT_TEMPLATE = "test_output/%s-OUTPUT.dot";

	/**
	 * Inital setup / entry point
	 * 
	 * @param args
	 * @throws URISyntaxException
	 */
	public static void main(String[] args) throws URISyntaxException {

		loggerSetup();

		boolean visualization = false;
		int numCores = 1;
		int numProcessors = Integer.parseInt(args[1]);

		String inputFile = args[0];
		String outputFile = String.format(DEFAULT_OUTPUT_TEMPLATE, FilenameUtils.getBaseName(inputFile));

		CommandLine cli = parseCLIArgs(args);
		if (cli.hasOption('p')) {
			numCores = Integer.parseInt(cli.getOptionValue('p'));
		}

		if (cli.hasOption('v')) {
			visualization = true;
		}

		if (cli.hasOption('o')) {
			outputFile = cli.getOptionValue('o') + ".dot";
		}
		log.info(outputFile);
		startScheduling(inputFile, outputFile, visualization, numCores, numProcessors);
	}

	private static void loggerSetup() {
		Properties props = new Properties();

		// try log properties load from file, otherwise use basic
		try {
			props.load(new FileInputStream("src/resources/log4j.properties"));
			PropertyConfigurator.configure(props);
			return;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		BasicConfigurator.configure();
	}

	private static CommandLine parseCLIArgs(String[] args) {

		// Get various options.
		CommandLineParser parser = new DefaultParser();
		Options options = new Options();

		options.addOption("p", true, "The number of cores used for execution in parallel.");
		options.addOption("v", false, "Visualize the scheduling process");
		options.addOption("o", true, "The intended output file name");

		try {
			return parser.parse(options, args);
		} catch (ParseException e) {
			log.info("There was an processing your command line options.");
			System.exit(1);
			return null;
		}

	}

	/**
	 * Begins the task scheduling process
	 */
	private static void startScheduling(String inputFile, String outputFile, boolean visualization, int numCores,
			int numProcessors) {

		log.info("Started scheduling");

		ScheduleGrph in = Input.readDotInput(inputFile);
		ScheduleGrph out = new AlgorithmStub().runAlg(in, numCores, numProcessors);

		try {
			Output.export(out, outputFile);
		} catch (IOException e) {
			log.error("Failed to export file - is your filepath invalid?", e);
		}
	}
}