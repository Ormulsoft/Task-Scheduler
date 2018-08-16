package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import grph.properties.NumericalProperty;
import it.unimi.dsi.fastutil.ints.IntSet;
import toools.collections.primitive.LucIntSet;

public class PartialScheduleGrph extends ScheduleGrph implements Comparable {

	/**
	 * A graphing class for an intermediary partial schedule generated during
	 * algorithm runs. Used to represent a potential partial solution for the
	 * problem on the tree.
	 * 
	 * TODO - make this have a reference to the input ScheduleGrph - all
	 * functions about comparisons to original done here?
	 */
	private static final long serialVersionUID = 1L;
	private double score;
	private int _idleTime = 0; // the MOST RECENTLY CALCULATED idle time. can be
								// idle time of parent until getIdleTime is run
								// on this in the cost func
	private int _fBTW = 0;
	private String serialized = null;
	private long timeAdded = 0;

	public PartialScheduleGrph(int score) {
		super();
		this.score = score;
	}

	public double getScore() {
		return this.score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public int getLastIdleTime() {
		return this._idleTime;
	}

	public void setIdleTime(int idleTime) {
		this._idleTime = idleTime;
	}

	public void setTimeAdded(long t) {
		this.timeAdded = t;
	}

	public long getTimeAdded() {
		return this.timeAdded;
	}

	public int getLastFBottomLevel() {
		return this._fBTW;
	}

	public void setFBottomLevel(int fbtw) {
		this._fBTW = fbtw;
	}

	public static long time = 0;

	public PartialScheduleGrph copy() {
		long start = System.nanoTime();
		PartialScheduleGrph g = new PartialScheduleGrph(0);
		time += System.nanoTime() - start;
		g.addVertices(this.getVertices());
		g.setVerticesLabel(this.getVertexLabelProperty());

		g.setVertexWeightProperty(this.getVertexWeightProperty());
		NumericalProperty procs = g.getVertexProcessorProperty();
		NumericalProperty starts = g.getVertexStartProperty();
		// weights.setValue(.getValue(i));
		for (int i : this.getVertices()) {

			procs.setValue(i, this.getVertexProcessorProperty().getValue(i));
			starts.setValue(i, this.getVertexStartProperty().getValue(i));
		}

		g.setScore(score);
		g.setIdleTime(_idleTime);
		g.setFBottomLevel(_fBTW);

		return g;

	}

	public PartialScheduleGrph getNormalizedCopy(int numProcessor) {
		PartialScheduleGrph out = this.copy();

		// System.out.println(out.getVerticesForProcessor(2).size());
		for (int i = 1; i <= numProcessor; i++) {
			try {
				IntSet procVertices = this.getVerticesForProcessor(i);
				int firstOnProc = procVertices.toIntArray()[0];
				for (int vert : procVertices) {
					if (this.getVertexStartProperty().getValue(firstOnProc) > this.getVertexStartProperty()
							.getValue(vert)) {
						firstOnProc = vert;
					}
				}
				// real proc number may over lap with new proc name
				for (int vert : procVertices) {
					out.getVertexProcessorProperty().setValue(vert, firstOnProc);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
		}
		return out;
	}

	public String serialize() {
		String serialized = "";
		// NumericalProperty weights = this.getVertexWeightProperty();
		NumericalProperty procs = this.getVertexProcessorProperty();
		NumericalProperty starts = this.getVertexStartProperty();
		for (int i : this.getVertices()) {
			serialized += i;
			// serialized += weights.getValueAsString(i);
			serialized += procs.getValueAsString(i);
			serialized += starts.getValueAsString(i);
		}
		return serialized;

	}

	public HashSet<Integer> getFree(ScheduleGrph inputSaved) {
		HashSet<Integer> a = new HashSet<Integer>();
		// get all source nodes (no in edges) that are not in the
		// partialschedule
		for (int srcTask : inputSaved.getSources()) {
			if (!this.containsVertex(srcTask)) {
				a.add(srcTask);
			}
		}
		/*
		 * iterate over tasks in the partial schedule, and add to output ones
		 * that are free and not contained in the current partial
		 */
		for (int task : this.getVertices()) {
			for (int outEdge : inputSaved.getOutEdges(task)) {
				int otherVert = inputSaved.getTheOtherVertex(outEdge, task);
				// check that not contained in current
				if (!this.containsVertex(otherVert)) {
					boolean add = true;
					// check that dependencies are satisfied.
					for (int e : inputSaved.getInEdges(otherVert)) {
						if (!this.containsVertex(inputSaved.getTheOtherVertex(e, otherVert))) {
							add = false;
							break;
						}
					}
					if (add) {
						a.add(otherVert);
					}
				}
			}
		}
		return a;
	}

	public void setSerialized(String s) {
		this.serialized = s;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PartialScheduleGrph) {
			PartialScheduleGrph g = (PartialScheduleGrph) o;
			if (this.serialized == null || g.serialized == null) {
				return false;
			}
			if (this.serialized.compareToIgnoreCase(g.serialized) == 0) {
				return true;
			}

		}
		return false;
	}

	public int compareTo(Object o) {
		PartialScheduleGrph g = (PartialScheduleGrph) o;
		if (this.getScore() == g.getScore()) {
			if (this.timeAdded > g.getTimeAdded())
				return -1;
			else
				return 1;
		}
		if (this.getScore() < g.getScore())
			return -1;
		else
			return 1;
	}

	/**
	 * Not currently working, seems to break optimal on input 10, more testing
	 * needed
	 * 
	 * Checks whether or not a schedule has any equivalent schedules, and should
	 * therefore not be expanded. i.e leaves it to the last equivalentschedule
	 * to be expanded. RELIES ON THE ALGORITHM EVENTUALLY CHECKING THE
	 * EQUIVALENT SCHEDULE WITH ALL TASKS THAT ARE ON THE SAME PROCESSOR AS
	 * LASTADDED BEING IN THE INDEX ORDER WHERE POSSIBLE
	 * 
	 * @param sched
	 * @param lastAdded
	 * @return
	 */
	private boolean equivalenceCheck(ScheduleGrph input, int lastAdded, int numProcessors) {
		ArrayList<Integer> tasksOnP = new ArrayList<Integer>();
		final NumericalProperty starts = this.getVertexStartProperty();
		NumericalProperty procs = this.getVertexProcessorProperty();
		NumericalProperty vertWeights = this.getVertexWeightProperty();
		NumericalProperty edgeWeights = this.getEdgeWeightProperty();

		// Create an ordered list of all the tasks on the same processor as
		// lastAdded
		int p = procs.getValueAsInt(lastAdded);
		final HashMap<Integer, Integer> newStarts = new HashMap<Integer, Integer>();

		for (int task : this.getVertices()) {
			if (procs.getValueAsInt(task) == p) {
				tasksOnP.add(task);
				newStarts.put(task, starts.getValueAsInt(task));
			}
		}

		// Sort on start time
		Comparator<Integer> comp = new Comparator<Integer>() {

			public int compare(Integer o1, Integer o2) {
				return ((Integer) newStarts.get(o1)).compareTo(newStarts.get(o2));
			}

		};
		Collections.sort(tasksOnP, comp);

		int i = tasksOnP.size() - 2;

		// While a task can "bubble up"
		while (i >= 0 && lastAdded < tasksOnP.get(i) && !this.areVerticesAdjacent(tasksOnP.get(i), lastAdded)) {

			// swap lastadded with task before it
			Collections.swap(tasksOnP, tasksOnP.indexOf((Integer) lastAdded), i);

			// for each affected task (now thisuled after lastAdded) rethisule
			// at new earliest time
			for (int j = i; j < tasksOnP.size(); j++) {
				int thisTask = tasksOnP.get(j);
				int time = 0;

				if (j > 0) {
					time = newStarts.get(tasksOnP.get(j - 1)) + vertWeights.getValueAsInt(tasksOnP.get(j - 1));
				}

				// Find earliest time based on dependencies
				for (int dep : this.getInNeighbors(thisTask)) {
					int drt = starts.getValueAsInt(dep) + vertWeights.getValueAsInt(dep);

					if (procs.getValueAsInt(dep) != p) {
						int edge = (Integer) this.getEdgesConnecting(dep, thisTask).toArray()[0];
						drt += edgeWeights.getValueAsInt(edge);
					}

					time = Math.max(drt, time);
				}

				newStarts.put(thisTask, time);
			}

			// If processor end is the same, and outgoing dependency edges are
			// "unaffected" - see ougoingCommsOK method - this thisule has
			// equivalents
			if ((newStarts.get(tasksOnP.get(tasksOnP.size() - 1))
					+ vertWeights.getValueAsInt(tasksOnP.get(tasksOnP.size() - 1))) <= (starts.getValueAsInt(lastAdded)
							+ vertWeights.getValueAsInt(lastAdded))
					&& this.outgoingCommsOK(input, newStarts, numProcessors)) {
				return true;
			}

			i--;
		}

		return false;
	}

	/**
	 * Helper method for equivalenceCheck which checks that all outgoing
	 * datatransfers of a set of tasks are unaffected by a change in
	 * order/position
	 * 
	 * @param input
	 * @param newStarts
	 *            A map of task id to start time, after the change
	 * @param numProcessors
	 * @return
	 */
	public boolean outgoingCommsOK(ScheduleGrph input, HashMap<Integer, Integer> newStarts, int numProcessors) {

		NumericalProperty starts = this.getVertexStartProperty();
		NumericalProperty weights = this.getVertexWeightProperty();
		NumericalProperty edgeWeights = input.getEdgeWeightProperty();
		NumericalProperty procs = this.getVertexProcessorProperty();
		LucIntSet placedTasks = this.getVertices();

		// Check that the children of each affected task are unaffected
		for (int task : newStarts.keySet()) {
			if (newStarts.get(task) > starts.getValueAsInt(task)) {
				for (int child : input.getOutNeighbors(task)) {
					int edge = (Integer) input.getEdgesConnecting(task, child).toArray()[0];
					int time = (newStarts.get(task) + weights.getValueAsInt(task)) + edgeWeights.getValueAsInt(edge);
					if (placedTasks.contains(child)) {
						// If child is in the thisule, check that the start
						// time does not contradict the new DRTs
						if (time > starts.getValueAsInt(child)) {
							return false;
						}
					} else {
						for (int i = 1; i <= numProcessors; i++) {
							// Verify that the changed end time of task is
							// outweighed by DRTs of at least one other
							// dependency on ALL processors
							boolean atLeastOneLater = false;

							for (int parent : input.getInNeighbours(child)) {

								if (parent != task) {
									if (placedTasks.contains(parent)) {
										int parentTime = starts.getValueAsInt(parent) + weights.getValueAsInt(parent);
										if (procs.getValueAsInt(parent) != i) {
											parentTime += edgeWeights.getValueAsInt(
													(Integer) input.getEdgesConnecting(parent, child).toArray()[0]);
										}
										if (parentTime >= time) {
											atLeastOneLater = true;
										}
									}
								}
							}

							if (!atLeastOneLater) {
								// If the child is affected by the changes when
								// placed on any processor, this is not an
								// equivalentthis
								return false;
							}
						}

					}
				}
			}
		}
		return true;
	}
}
