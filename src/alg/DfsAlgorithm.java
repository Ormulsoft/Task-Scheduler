package alg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import toools.collections.primitive.LucIntSet;
import util.ScheduleGrph;

public class DfsAlgorithm implements Algorithm {

	int cost = 0;

	List<List<Integer>> processorTasks = new ArrayList<List<Integer>>();

	Map<Integer, Integer> visited = new HashMap<Integer, Integer>();

	List<Integer> verticeList = new ArrayList<Integer>();

	public ScheduleGrph runAlg(ScheduleGrph input, int numCores, int numProcessors) {
		// TODO assign all the parameters to the right value for
		// partialSol function

		return null;
	}

	public void PartialSol(int indexOfVerticeList, int numProcessors, int cost, List<List<Integer>> list,
			ScheduleGrph input) {

		for (int i : verticeList) {
			for (int j = 0; j < numProcessors; j++) {

				visited.put(i, j);

				if (visited.containsKey(i) && visited.containsValue(j)) {
					// Skip the task if task and the processor has been seen
				} else {

					LucIntSet parents = input.getInNeighbors(i);
					boolean parentVisited = true;

					for (int k : parents) {
						if (!visited.containsKey(k)) {
							parentVisited = false;
							break;
						}
					}

					// If parents are all visited so that dependencies are all
					// checked
					if (parentVisited = true) {
						PartialSol(i, j, cost, list, input);
					}

				}

			}
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
