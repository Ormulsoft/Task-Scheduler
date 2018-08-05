package util;

import grph.in_memory.InMemoryGrph;
import grph.properties.NumericalProperty;

/**
 * This extends the InMemoryGrph to provide additional fields.
 * 
 * 
 * @author Gino
 *
 */
public class ScheduleGrph extends InMemoryGrph {

	private static final long serialVersionUID = 1L;
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
}
