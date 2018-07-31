package alg;

import org.apache.log4j.Logger;

import grph.Grph;

public interface Algorithm {

	final static Logger log = Logger.getLogger(Algorithm.class);

	public Grph runAlg(Grph input, int numCores);

}
