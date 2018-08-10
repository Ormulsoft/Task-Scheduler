package util;

import java.util.HashSet;

import it.unimi.dsi.fastutil.ints.IntSet;

public class PartialScheduleGrph extends ScheduleGrph {

	/**
	 * A graphing class for an intermediary partial schedule generated during
	 * algorithm runs. Used to represent a potential partial solution for the
	 * problem on the tree.
	 * 
	 * TODO - make this have a reference to the input ScheduleGrph - all
	 * functions about comparisons to original done here?
	 */
	private static final long serialVersionUID = 1L;
	int score;

	public PartialScheduleGrph(int score) {
		super();
		this.score = score;
	}

	public int getScore() {
		return this.score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public PartialScheduleGrph copy() {
		PartialScheduleGrph g = new PartialScheduleGrph(0);
		g.addVertices(this.getVertices());
		g.setVerticesLabel(this.getVertexLabelProperty());
		for (int i : this.getVertices()) {
			g.getVertexWeightProperty().setValue(i, this.getVertexWeightProperty().getValue(i));
			g.getVertexProcessorProperty().setValue(i, this.getVertexProcessorProperty().getValue(i));
			g.getVertexStartProperty().setValue(i, this.getVertexStartProperty().getValue(i));
		}

		g.setScore(score);

		return g;

	}

	public PartialScheduleGrph cloneSelf() {
		return (PartialScheduleGrph) super.clone();
	}

	public PartialScheduleGrph getNormalizedCopy() {
		PartialScheduleGrph out = this.copy();
		int numProcessor = 0;

		for (int vert : this.getVertices()) {
			if (this.getVertexProcessorProperty().getValue(vert) > numProcessor) {
				numProcessor = this.getVertexProcessorProperty().getValueAsInt(vert);
			}
		}
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

}
