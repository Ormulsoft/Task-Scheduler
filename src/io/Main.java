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

import alg.AStarAlgorithm;
import alg.cost.AStarCostFunction;
import util.ScheduleGrph;

/**
 * Entry point for the task scheduling assignment
 * 
 * @author All
 *
 */
public class Main {
	private static final int DEFAULT_CORES = 1;
	private static final boolean DEFAULT_VISUALISATION = false;

	final static Logger log = Logger.getLogger(Main.class);

	private static final String DEFAULT_OUTPUT_TEMPLATE = "%s-OUTPUT.dot";

	/**
	 * Inital setup / entry point
	 * 
	 * @param args
	 * @throws URISyntaxException
	 */
	public static void main(String[] args) throws URISyntaxException {

		log.info("Task scheduler launched");
		boolean visualization = DEFAULT_VISUALISATION;
		int numCores = DEFAULT_CORES;

		// interpret CLI options and args
		try {
			int numProcessors = Integer.parseInt(args[1]);
			String inputFile = args[0];

			// Input file argument
			String outputFile = String.format(DEFAULT_OUTPUT_TEMPLATE, FilenameUtils.getBaseName(inputFile));

			CommandLine cli = parseCLIArgs(args);
			// Number of cores
			if (cli.hasOption('p')) {
				numCores = Integer.parseInt(cli.getOptionValue('p'));
			}

			// Whether or not to enable visualization
			if (cli.hasOption('v')) {
				visualization = true;
			}

			// Specific output location
			if (cli.hasOption('o')) {
				outputFile = cli.getOptionValue('o') + ".dot";
			}
			startScheduling(inputFile, outputFile, visualization, numCores, numProcessors);

		} catch (ArrayIndexOutOfBoundsException e) {
			// e.printStackTrace();
			log.error("Please pass the required inputs - { <input file name> <number of processors> }");
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Defines logging properties
	 */
	private static void loggerSetup() {
		Properties props = new Properties();
		// try log properties load from file, otherwise use basic
		try {
			props.load(new FileInputStream("log4j.properties"));
			PropertyConfigurator.configure(props);
			return;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		BasicConfigurator.configure();
	}

	/**
	 * Parse command line arguments and return CLI object
	 * 
	 * @param args
	 * @return
	 */
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

		log.info("Reading input file");
		ScheduleGrph in = Input.readDotInput(inputFile);
		log.info("Started scheduling algorithm with params: " + numProcessors + " processor(s), " + numCores
				+ " core(s)");
		ScheduleGrph out = new AStarAlgorithm(new AStarCostFunction(in)).runAlg(in, numCores, numProcessors);

		log.info("Is valid?: " + out.dependenciesValid(in));
		log.info("Outputting solution to file: " + outputFile);

		try {
			Output.export(out, outputFile);
		} catch (IOException e) {
			log.error("Failed to export file - is your output filepath valid?", e);
		}

		log.info("Finished!");
	}
}