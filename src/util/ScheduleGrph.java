package util;

import grph.Grph;
import grph.in_memory.InMemoryGrph;
import grph.properties.NumericalProperty;
import toools.collections.primitive.LucIntSet;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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


    public ArrayList<Integer> getAllStartVertexIndices() {
        ArrayList<Integer> _StartVertices = new ArrayList<Integer>();
        Object[] a = this.getVertices().toArray();
        for (int i = 0; i < this.getVertices().size(); i++) {
            int index = Integer.parseInt(a[i].toString());
            if (this.getInEdges(index).size() == 0) {
                _StartVertices.add(index);
            } else {
                break;
            }
        }
        return _StartVertices;

    }

    public ArrayList<Integer> getAllEndVertexIndices() {
        ArrayList<Integer> _endVertices = new ArrayList<Integer>();
        Object[] a = this.getVertices().toArray();
        for (int i = 0; i < this.getVertices().size(); i++) {
            int index = Integer.parseInt(a[i].toString());
            if (this.getOutEdges(index).size() == 0) {
                _endVertices.add(index);
            } else {
                break;
            }
        }
        return _endVertices;

    }

    public int getupperBound() {

        int startVertex = this.getAllStartVertexIndices().get(0);
        int upperBound = (int) this.getVertexWeightProperty().getValue(startVertex);
        HashMap<Integer, Boolean> Visted = new HashMap<Integer, Boolean>();
        for (int i = 0; i < this.getVertices().size(); i++) {
            Visted.put(i, false);
        }
        Visted.put(startVertex, true);

        while (Visted.values().contains(false)) {
            int vertex = 0;
            for (int i = 0; i < Visted.size(); i++) {
                if (Visted.get(i).booleanValue() == true) {
                    vertex = i;
                    int[] edgeSet = this.getOutEdges(vertex).toIntArray();
                    for (int j = 0; j < edgeSet.length; j++) {
                        if (Visted.get(this.getTheOtherVertex(edgeSet[j], vertex)).booleanValue() == false) {
                            upperBound = (int) this.getEdgeWidthProperty().getValue(edgeSet[j]) + upperBound;
                            Visted.put(this.getTheOtherVertex(edgeSet[j], vertex), true);
                        }
                    }
                }
            }

        }
        return upperBound;
    }

	public NumericalProperty getEdgeWeightProperty() {
		return edgeWeightProperty;
	}

}
