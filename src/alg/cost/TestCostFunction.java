package alg.cost;

import util.PartialScheduleGrph;

public class TestCostFunction implements CostFunction {

	public void applyCost(PartialScheduleGrph g) {
		int max = 0;
		for(int i : g.getVertices()) {
			int val = (int) g.getVertexStartProperty().getValue(i) + (int) g.getVertexWeightProperty().getValue(i);
			if(val > max) {
				max = val;
			}
			
		}
		g.setScore(max);

	}

}
