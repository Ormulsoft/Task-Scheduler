package alg;

import java.util.Comparator;
import java.util.PriorityQueue;

import alg.cost.CostFunction;
import grph.Grph;
import it.unimi.dsi.fastutil.ints.IntSet;
import util.PartialScheduleGrph;
import util.ScheduleGrph;

/**
 * 
 * @author matt frost
 *
 */
public class AStarAlgorithm implements Algorithm {

	private CostFunction cost;

	private class WeightChecker implements Comparator<PartialScheduleGrph> {

		public int compare(PartialScheduleGrph grph1, PartialScheduleGrph grph2) {
			if (grph1.getScore() < grph2.getScore())
				return -1;
			else
				return 1;
		}
	}

	public AStarAlgorithm(CostFunction cost) {
		this.cost = cost;
	}

	public ScheduleGrph runAlg(ScheduleGrph input, int numCores, int numProcessors) {

		PriorityQueue<PartialScheduleGrph> states = new PriorityQueue<PartialScheduleGrph>(1, new WeightChecker());

		for (int i : input.getSources()) {
			PartialScheduleGrph g = new PartialScheduleGrph(0);
			g.addVertex(i);
			g.getVertexStartProperty().setValue(i, 0);
			g.getVertexWeightProperty().setValue(i, input.getVertexWeightProperty().getValue(i));
			states.add(g);
		}

		input.removeVertices(input.getSources());

		while (states.size() > 0) {

			PartialScheduleGrph s = states.poll();
			// if is a leaf, return the partial.
			IntSet endVertices = s.getVerticesOfDegree(0, Grph.DIRECTION.out);
			if (target(endVertices, input)) {
				return s;
			} else {
				// expand all new states
				IntSet sourceVerts = input.getSources();
				for (int i : sourceVerts) {
					// create new
					for (int j = 0; j < numProcessors; j++) {
						PartialScheduleGrph next = (PartialScheduleGrph) s.clone();
						next.addVertex(i);
						next.getVertexProcessorProperty().setValue(i, j);
						next.getVertexWeightProperty().setValue(i, input.getVertexWeightProperty().getValue(i));
						int minStart = 0;
						for (int nb : next.getInNeighbours(i)) {
							int nextStart = (int) next.getVertexStartProperty().getValue(nb);
							if (next.getVertexStartProperty().getValue(nb) > minStart) {
								minStart = nextStart;
							}
						}
						next.getVertexStartProperty().setValue(i, minStart);
						cost.applyCost(next);
						states.add(next);
					}

				}
				input.removeVertices(sourceVerts);
			}
		}

		return null;
	}

	private boolean target(IntSet v, ScheduleGrph input) {
		for (int i : v) {
			if (input.getVertexDegree(i) > 0) {
				return false;
			}
		}
		return true;
	}

}
