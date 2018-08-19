package util;

import grph.in_memory.GrphIntSet;
import grph.in_memory.InMemoryGrph;
import grph.properties.NumericalProperty;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * This extends the InMemoryGrph to provide additional fields, such as the
 * properties we need for this particular problem.This extends the InMemoryGrph
 * to provide additional fields, such as the properties we need for this
 * particular problem
 * 
 * @author Eugene
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

	/**
	 * No arg constructor for ScheduleGrph, initializes with empty weights, task starts and processor assignments
	 */
	public ScheduleGrph() {
		verticesWeight = new NumericalProperty("Weight");
		verticesStart = new NumericalProperty("Start");
		verticesProcessor = new NumericalProperty("Processor");
	}

	// Getters and setters
	/**
	 * Set the NumericalProperty containing assigned vertex starts
	 * @param vertStarts The NumericalProperty to set as
	 */
	public void setVertexStartProperty(NumericalProperty vertStarts) {
		this.verticesStart = vertStarts;
	}

	/**
	 * Set the NumericalProperty containing assigned vertex weight
	 * @param vertWeights The NumericalProperty to set as
	 */
	public void setVertexWeightProperty(NumericalProperty vertWeights) {
		this.verticesWeight = vertWeights;
	}

	/**
	 * Set the NumericalProperty containing assigned task processors
	 * @param vertProcs The NumericalProperty to set as
	 */
	public void setVertexProcessorProperty(NumericalProperty vertProcs) {
		this.verticesProcessor = vertProcs;
	}

	/**
	 * Set the NumericalProperty containing assigned edge weights
	 * @param edgeWeights The NumericalProperty to set as
	 */
	public void setEdgeWeightProperty(NumericalProperty edgeWeights) {
		this.edgeWeightProperty = edgeWeights;
	}

	/**
	 * Get the NumericalProperty containing assigned task starts
	 * @return 
	 */
	public NumericalProperty getVertexStartProperty() {
		return verticesStart;
	}

	/**
	 * Get the NumericalProperty containing assigned task weights
	 * @return 
	 */
	public NumericalProperty getVertexWeightProperty() {
		return verticesWeight;
	}

	/**
	 * Get the NumericalProperty containing assigned task processors
	 * @return 
	 */
	public NumericalProperty getVertexProcessorProperty() {
		return verticesProcessor;
	}

	/**
	 * Get the NumericalProperty containing assigned edge weights
	 * @return 
	 */
	public NumericalProperty getEdgeWeightProperty() {
		return edgeWeightProperty;
	}


	/**
	 * Return a string containing the .dot representation of this schedule
	 */
	public String toDot() {
		return new ScheduleDotWriter().createDotText(this, false);
	}

	/**
	 * Clone this ScheduleGrph
	 * @return
	 */
	public ScheduleGrph cloneSelf() {
		return (ScheduleGrph) super.clone();
	}

	/**
	 * Get the bottomLevel time of this ScheduleGraph
	 * @return
	 */
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
	 * Get bottomLevel of this ScheduleGraph using a recursive strategy
	 * @param addedVertex The most recently added vertex of this graph
	 * @return
	 */
	private int getBottomLevelRecurse(int addedVertex) {
		if (getOutEdgeDegree(addedVertex) > 0) {
			int max = 0;
			// Iterate through children
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

	/**
	 * Get a set of all the tasks assigned to a processor
	 * @param i the processor to look at
	 * @return
	 */
	public IntSet getVerticesForProcessor(int i) {
		GrphIntSet g = new GrphIntSet(0);
		for (int vert : this.getVertices()) {
			if (this.getVertexProcessorProperty().getValue(vert) == i) {
				g.add(vert);
			}
		}
		return g;
	}

	/**
	 * Check if this ScheduleGraph is valid with regards to tasks only starting after their dependencies
	 * and dependency transfer (if on diff processors) are finished. Note that this method does not check
	 * if tasks overlap on the same processor
	 * @param input The full ScheduleGraph read as input used to generate this version
	 * @return
	 */
	public boolean dependenciesValid(ScheduleGrph input) {
		for (int vert : this.getVertices()) {
			int vertStart = this.getVertexStartProperty().getValueAsInt(vert);
			
			// Iterate through parents
			for (int neighbor : input.getInNeighbors(vert)) {

				// Check if parents have been scheduled
				if (!this.containsVertex(neighbor)) {
					return false;
				} else {
					
					// Check finish and transfer times OK
					int nEnd = this.getVertexStartProperty().getValueAsInt(neighbor)
							+ this.getVertexWeightProperty().getValueAsInt(neighbor);

					if (this.getVertexProcessorProperty().getValue(neighbor) != this.getVertexProcessorProperty()
							.getValue(vert)) {
						nEnd += input.getEdgeWeightProperty()
								.getValueAsInt(input.getSomeEdgeConnecting(neighbor, vert));
					}
					if (nEnd > vertStart) {
						return false;
					}
				}
			}

		}
		
		// If reached here without encountering an incorrectly placed task, must be valid in regards to dependencies
		return true;

	}

}
