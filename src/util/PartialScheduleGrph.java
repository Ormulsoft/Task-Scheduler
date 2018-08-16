package util;

import java.util.HashSet;

import grph.properties.NumericalProperty;
import it.unimi.dsi.fastutil.ints.IntSet;

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
}
