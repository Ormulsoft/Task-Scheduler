package util;

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

	public boolean dependenciesValid(ScheduleGrph input) {
		for (int vert : this.getVertices()) {
			int vertStart = this.getVertexStartProperty().getValueAsInt(vert);
			for (int neighbor : input.getInNeighbors(vert)) {

				if (!this.containsVertex(neighbor)) {
					return false;
				} else {
					int nEnd = this.getVertexStartProperty().getValueAsInt(neighbor)
							+ this.getVertexWeightProperty().getValueAsInt(neighbor);

					if (this.getVertexProcessorProperty().getValue(neighbor) != this.getVertexProcessorProperty()
							.getValue(vert)) {
						nEnd += input.getEdgeWeightProperty()
								.getValueAsInt(input.getSomeEdgeConnecting(neighbor, vert));
					}
					if (nEnd > vertStart) {
						// System.out.println(vert);
						// System.out.println(neighbor);
						// System.out.println(vertStart);
						// System.out.println(nEnd);
						// System.out.println(this.toDot());
						return false;
					}
				}
			}

		}
		return true;

	}

}
