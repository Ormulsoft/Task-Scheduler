package alg;


import grph.algo.VertexAdjacencyAlgorithm;
import io.Edge;
import io.Graph;
import io.Main;
import io.Vertex;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;


public class AlgorithmStubv2 {
    final static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Main.class);
        public int total_cost;
        HashMap<Vertex,Boolean> VertexSet = new HashMap<Vertex,Boolean>();
    public int runAlg(Graph input, int numCores) {
        input.get_startVertex();
        for(Vertex v: input.getVertexSet()) {
            VertexSet.put(v,false);
        }
        this.total_cost = input.get_startVertex().get_weight();
        this.VertexSet.put(input.get_startVertex(),true);
        this.getCost(input.get_startVertex());
        return total_cost;
    }

    public  int  getCost(Vertex A) {
        HashMap<Integer,Vertex> cheapestEdge = new HashMap<Integer, Vertex>();
        if(this.VertexSet.get(A).booleanValue() == true) {
            log.info(this.total_cost);
            return 1;
        }
        int min = 100000;
        for(Edge b:A.getEdgeSet()) {
               cheapestEdge.put(b._weight,b._dest);
            if(min>b._weight) {
                min = b._weight;
            }
        }
        this.VertexSet.put(A,true);
        this.total_cost = this.total_cost + min;
        this.getCost(cheapestEdge.get(min));
        return this.total_cost;
    }

}

