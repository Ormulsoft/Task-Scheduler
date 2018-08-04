package alg;

import java.util.ArrayList;
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
			if (grph1.getScore() > grph2.getScore())
				return -1;
			else
				return 1;
		}
	}

	public AStarAlgorithm(CostFunction cost) {
		this.cost = cost;
	}

	public ScheduleGrph runAlg(ScheduleGrph input, int numCores, int numProcessors) {
		ScheduleGrph inputSaved = (ScheduleGrph) input.clone();

		PriorityQueue<PartialScheduleGrph> states = new PriorityQueue<PartialScheduleGrph>(1, new WeightChecker());

		// initially, all the sources have no dependencies, so are scheduled
		// first.

		for (int i : input.getSources()) {
			for (int j = 0; j < numProcessors; j++) {
				PartialScheduleGrph g = new PartialScheduleGrph(0);
				g.addVertex(i);
				g.getVertexStartProperty().setValue(i, 0);
				g.getVertexProcessorProperty().setValue(i, j + 1);
				g.getVertexWeightProperty().setValue(i, input.getVertexWeightProperty().getValue(i));
				states.add(g);
			}
		}

		input.removeVertices(input.getSources());

		while (states.size() > 0) {

			PartialScheduleGrph s = states.poll();
			// if is a leaf, return the partial.
			IntSet endVertices = s.getVerticesOfDegree(0, Grph.DIRECTION.out);
			ArrayList<Integer> freeVerts = getFree(inputSaved, s);
			if (freeVerts.size() == 0) {
				return s;
			} else {

				// loop over all free vertices
				for (int vert : freeVerts) {
					for (int pc = 1; pc <= numProcessors; pc++) {

						PartialScheduleGrph next = (PartialScheduleGrph) s.clone();
						// add vertex to the
						next.addVertex(vert);
						next.getVertexWeightProperty().setValue(vert,
								inputSaved.getVertexWeightProperty().getValue(vert));

						next.getVertexProcessorProperty().setValue(vert, pc);

						// set the start time based on earliest first on a
						// processor

						// to get the start time, find the time of most recently
						// finishing vertex on the same processor,
						// and store that, also the finish time of the last
						// dependency. starting time would be the maximum\
						// of the two.
						int dependencyUpperBound = 0;
						for (int i : inputSaved.getInNeighbours(vert)) {
							int edgeTime = (int) inputSaved.getEdgeWidthProperty()
									.getValue(inputSaved.getSomeEdgeConnecting(i, vert));
							int totalTime = (int) (inputSaved.getVertexWeightProperty().getValue(i)
									+ inputSaved.getVertexStartProperty().getValue(i) + edgeTime);
							if (totalTime > dependencyUpperBound) {
								dependencyUpperBound = totalTime;
							}
						}

						int processorUpperBound = 0;
						for (int i : next.getVertices()) {
							if (next.getVertexProcessorProperty().getValue(i) == pc) {
								int totalTime = (int) (inputSaved.getVertexWeightProperty().getValue(i)
										+ inputSaved.getVertexStartProperty().getValue(i));
								if (totalTime > dependencyUpperBound) {
									processorUpperBound = totalTime;
								}
							}
						}

						next.getVertexStartProperty().setValue(vert,
								Math.max(processorUpperBound, dependencyUpperBound));
						cost.applyCost(next);
					}
				}
			}
		}
		return null;
	}

	//
	private ArrayList<Integer> getFree(ScheduleGrph inputSaved, PartialScheduleGrph pg) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int i : pg.getVertices()) {
			for (int outEdge : inputSaved.getOutEdges(i)) {
				int otherVert = inputSaved.getTheOtherVertex(outEdge, i);
				if (!pg.containsVertex(otherVert)) {
					a.add(otherVert);
				}
			}
		}
		return a;
	}

}