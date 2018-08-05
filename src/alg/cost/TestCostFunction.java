package alg.cost;

import util.PartialScheduleGrph;
import util.ScheduleGrph;

/**
 * A basic cost function that assigns the end time as the cost.
 * 
 * @author Matt
 *
 */
public class TestCostFunction implements CostFunction {

	ScheduleGrph input;

	public TestCostFunction(ScheduleGrph input) {
		this.input = input;
	}

	public void applyCost(PartialScheduleGrph g) {

	}

	public void applyCost(PartialScheduleGrph g, int addedVertex) {

		int maxFinish = 0;
		for (int i : g.getVertices()) {
			// get the end time from the highest start time + weight combination
			int val = (int) g.getVertexStartProperty().getValue(i) + (int) g.getVertexWeightProperty().getValue(i);
			if (val > maxFinish) {
				maxFinish = val;
			}
		}

		g.setScore(maxFinish + getComputationalBottomLevel(addedVertex));
	}

	/**
	 * Gets the Computational bottom level value for an added vertex (ie. the
	 * longest path of task weights not including dependency edge weights)
	 */
	private int getComputationalBottomLevel(int addedVertex) {

		if (input.getOutEdgeDegree(addedVertex) > 0) {
			int max = (int) (input.getVertexWeightProperty().getValue(addedVertex)
					+ input.getVertexStartProperty().getValue(addedVertex));
			for (int i : input.getOutNeighbors(addedVertex)) {
				int current = (int) (getComputationalBottomLevel(i) + input.getVertexWeightProperty().getValue(i));
				if (max < current) {
					max = current;
				}
			}
			return max;
		} else {
			return (int) (input.getVertexStartProperty().getValue(addedVertex)
					+ input.getVertexWeightProperty().getValue(addedVertex));
		}
	}

}
