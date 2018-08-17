package alg.cost;

import util.PartialScheduleGrph;
import util.ScheduleGrph;

public class StaticCostFunction extends AStarCostFunction {

	public StaticCostFunction(ScheduleGrph input) {
		super(input);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void applyCost(PartialScheduleGrph g, int addedVertex, int numProcessors) {
		int max = 0;
		for (int vert : g.getVertices()) {
			int val = this.getComputationalBottomLevel(vert) + g.getVertexStartProperty().getValueAsInt(vert);
			if (val > max) {
				max = val;
			}
		}
		int maxBL = Math.max(g.getLastFBottomLevel(),
				this.getComputationalBottomLevel(addedVertex) + (int) g.getVertexStartProperty().getValue(addedVertex));

		g.setFBottomLevel(maxBL);

		g.setScore(max);

	}
}
