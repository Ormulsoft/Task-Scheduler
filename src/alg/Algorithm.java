package alg;

import org.apache.log4j.Logger;
import util.ScheduleGrph;

import grph.Grph;

public interface Algorithm {

	final static Logger log = Logger.getLogger(Algorithm.class);

	public ScheduleGrph runAlg(ScheduleGrph input, int numCores);

}
