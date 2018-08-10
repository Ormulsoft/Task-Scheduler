package util;

import org.apache.commons.lang.SerializationUtils;

import grph.in_memory.GrphIntSet;
import grph.in_memory.InMemoryGrph;
import grph.properties.NumericalProperty;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * <<<<<<< HEAD This extends the InMemoryGrph to provide additional fields, such
 * as the properties we need for this particular problem ======= This extends
 * the InMemoryGrph to provide additional fields, such as the properties we need
 * for this particular problem >>>>>>> a8e40dea54e9b2cc3289608bb61892fe3407f8d6
 * 
 * @author Gino
 *
 */
public class ScheduleGrph extends InMemoryGrph {

	private static final long serialVersionUID = 1L;

	// Object keeps track of weight (runtime), start, and assigned processor for
	// each vertex (task)

	private NumericalProperty verticesWeight;
	private NumericalProperty verticesStart;
	private NumericalProperty verticesProcessor;
	// Keeps track of the weight (data transfer time) of each dependency
	private NumericalProperty edgeWeightProperty;

	public ScheduleGrph() {
		super();
		verticesWeight = new NumericalProperty("Weight");
		verticesStart = new NumericalProperty("Start");
		verticesProcessor = new NumericalProperty("Processor");
	}

	// Getters and setters
	public void setVertexStartProperty(NumericalProperty vertStarts) {
		this.verticesStart = vertStarts;
	}

	public void setVertexWeightProperty(NumericalProperty vertWeights) {
		this.verticesWeight = vertWeights;
	}

	public void setVertexProcessorProperty(NumericalProperty vertProcs) {
		this.verticesProcessor = vertProcs;
	}

	public void setEdgeWeightProperty(NumericalProperty edgeWeights) {
		this.edgeWeightProperty = edgeWeights;
	}

	public NumericalProperty getVertexStartProperty() {
		return verticesStart;
	}

	public NumericalProperty getVertexWeightProperty() {
		return verticesWeight;
	}

	public NumericalProperty getVertexProcessorProperty() {
		return verticesProcessor;
	}

	public NumericalProperty getEdgeWeightProperty() {
		return edgeWeightProperty;
	}

	public String toDot() {
		return new ScheduleDotWriter().createDotText(this, false);
	}

	public ScheduleGrph cloneSelf() {
		return (ScheduleGrph) super.clone();
	}

	public int getBottomLevel() {
		int max = 0;
		for (int addedVertex : getVertices()) {
			int val = this.getBottomLevelRecurse(addedVertex);
			if (val > max) {
				max = val;
			}
		}
		return max;
	}

	/**
	 * TODO need edges in these now
	 * 
	 * @param addedVertex
	 * @return
	 */
	private int getBottomLevelRecurse(int addedVertex) {
		if (getOutEdgeDegree(addedVertex) > 0) {
			int max = 0;
			for (int i : getOutNeighbors(addedVertex)) {
				int current = (int) (getBottomLevelRecurse(i));
				if (max < current) {
					max = current;
				}
			}
			return max + (int) getVertexWeightProperty().getValue(addedVertex);
		} else {
			return (int) (getVertexWeightProperty().getValue(addedVertex));
		}
	}

	public IntSet getVerticesForProcessor(int i) {
		GrphIntSet g = new GrphIntSet(0);
		for (int vert : this.getVertices()) {
			if (this.getVertexProcessorProperty().getValue(vert) == i) {
				g.add(vert);
			}
		}
		return g;
	}

	public ScheduleGrph getNormalizedCopy() {
		ScheduleGrph out = (ScheduleGrph) SerializationUtils.clone(this);
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

}
