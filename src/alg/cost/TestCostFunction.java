package alg.cost;

import util.PartialScheduleGrph;
import util.ScheduleGrph;

/**
 * A basic cost function that assigns the end time as the cost.
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
		for(int i : g.getVertices()) {
			// get the end time from the highest start time + weight combination
			int val = (int) g.getVertexStartProperty().getValue(i) + (int) g.getVertexWeightProperty().getValue(i);
			if(val > maxFinish) {
				maxFinish = val;
			}			
		}		
		
		g.setScore(maxFinish + getBottomLevel(addedVertex));
	}

	
	private int getBottomLevel(int addedVertex) {
		
		if(input.getOutEdgeDegree(addedVertex) > 0) {
			int max = (int)(input.getVertexWeightProperty().getValue(addedVertex) + input.getVertexStartProperty().getValue(addedVertex));
			for( int i : input.getOutNeighbors(addedVertex)) {
				int current = (int)(getBottomLevel(i) + input.getVertexWeightProperty().getValue(i));
				if(max < current) {
					max = current;
				}
			}	
			return max;
		}else {
			return (int)( input.getVertexStartProperty().getValue(addedVertex) +input.getVertexWeightProperty().getValue(addedVertex));
		}
	}
	
	public int  getComputationalBottomLevel(int addedvertex) {
		boolean holder = true;
		int bottomlevel = 0;
		while (holder) {
			int[] a = input.getOutEdges(addedvertex).toIntArray();
			int max = 0;
			if(input.getOutEdgeDegree(addedvertex) == 0) {
				holder = false;
			}
			int finalotherVertex = 0;
			for(int edge: a) {
				int othervertex = input.getTheOtherVertex(edge,addedvertex);
				if(input.getVertexWeightProperty().getValueAsInt(othervertex) > max) {
					max = input.getVertexWeightProperty().getValueAsInt(othervertex);
					finalotherVertex = othervertex;
				}

			}
			bottomlevel = bottomlevel+max;
			addedvertex = finalotherVertex;
		}
		return  bottomlevel;
	}


}
