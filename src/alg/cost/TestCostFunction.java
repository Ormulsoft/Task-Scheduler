package alg.cost;

import util.PartialScheduleGrph;
import util.ScheduleGrph;

public class TestCostFunction implements CostFunction {
	ScheduleGrph graph = new ScheduleGrph();
	public TestCostFunction (ScheduleGrph input ){
		graph = input;
	}
	public void applyCost(PartialScheduleGrph g,int addedvertex ) {
		int max = 0;
		for(int i : g.getVertices()) {
			int val = (int) g.getVertexStartProperty().getValue(i) + (int) g.getVertexWeightProperty().getValue(i);
			if (val > max) {
				max = val;
			}
		}
		g.setScore(max+this.getComputationalBottomlevel(addedvertex));

	}
	public int  getComputationalBottomlevel(int addedvertex) {
		boolean holder = true;
		int bottomlevel = 0;
		while (holder) {
			int[] a = graph.getOutEdges(addedvertex).toIntArray();
			int max = 0;
			if(graph.getOutEdgeDegree(addedvertex) == 0) {
				holder = false;
			}
			int finalotherVertex = 0;
			for(int edge: a) {
				int othervertex = graph.getTheOtherVertex(edge,addedvertex);
				if(graph.getVertexWeightProperty().getValueAsInt(othervertex) > max) {
					max = graph.getVertexWeightProperty().getValueAsInt(othervertex);
					finalotherVertex = othervertex;
				}

			}
			bottomlevel = bottomlevel+max;
			addedvertex = finalotherVertex;
		}
		return  bottomlevel;
	}
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
