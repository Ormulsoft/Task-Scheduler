package util;

import org.apache.commons.lang.SerializationUtils;

import grph.in_memory.GrphIntSet;
import grph.in_memory.InMemoryGrph;
import grph.properties.NumericalProperty;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * This extends the InMemoryGrph to provide additional fields, such as the
 * properties we need for this particular problem
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
		for (int vert : out.getVertices()) {
			if (out.getVertexProcessorProperty().getValue(vert) > numProcessor) {
				numProcessor = out.getVertexProcessorProperty().getValueAsInt(vert);
			}
		}
		// System.out.println(out.getVerticesForProcessor(2).size());
		for (int i = 1; i <= numProcessor; i++) {
			IntSet procVertices = out.getVerticesForProcessor(i);
			int firstOnProc = 1;
			for (int vert : procVertices) {
				if (out.getVertexStartProperty().getValue(firstOnProc) > out.getVertexStartProperty().getValue(vert)) {
					firstOnProc = out.getVertexStartProperty().getValueAsInt(vert);
				}
			}
			for (int vert : procVertices) {
				out.getVertexProcessorProperty().setValue(vert, firstOnProc);
			}
		}
		return out;
	}

}
