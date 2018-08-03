package io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import alg.AlgorithmStub;
import grph.Grph;

/**
 * Entry point for the task scheduling assignment
 * 
 * @author
 *
 */
public class Main {

	final static Logger log = Logger.getLogger(Main.class);

	/**
	 * Inital setup / entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		Properties props = new Properties();

		// try log properties load from file, otherwise use basic
		try {
			props.load(new FileInputStream("src/resources/log4j.properties"));
			PropertyConfigurator.configure(props);
		} catch (FileNotFoundException e) {
			BasicConfigurator.configure();
			e.printStackTrace();
		} catch (IOException e) {
			BasicConfigurator.configure();
			e.printStackTrace();
		}
		
		parseCLIArgs(args);
		startProcess();
	}
	

	private static void parseCLIArgs(String[] args) {
		
		System.out.println("Input filename is : " + args[0] + " Number of processors to use: " + args[1]); 
		
	}




	/**
	 * Begins the task scheduling process
	 */
	private static void startProcess() {
		log.info("Started scheduling");

		String inputPath = "Nodes_7_OutTree.dot";
		String outputPath = "exp";
		int cores = 1;

		Grph in = Input.readDotInput(inputPath);
		Grph out = new AlgorithmStub().runAlg(in, cores);

		try {
			Output.export(out, outputPath);
		} catch (IOException e) {
			log.error("Failed to export file", e);
		}
	}
}