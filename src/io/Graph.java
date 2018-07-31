package io;

import java.util.ArrayList;

public class Graph {
    private Vertex _startVertex;
    private Vertex _finishVertex;
    public ArrayList<Edge> EdgeSet;
    private ArrayList<Vertex> VertexSet;

    public Graph (Vertex _start,Vertex _finish) {
        _startVertex = _start;
        _finishVertex = _finish;
    };

    public void createVertex(int _name,boolean start,boolean finish) {
        Vertex name = new Vertex(_name, start, finish);
        VertexSet.add(name);
    }

    public ArrayList<Edge> getEdgeSet() {
        for(Vertex name: VertexSet) {
            this.EdgeSet.addAll(name.getEdgeSet());
        }
    }

    public Vertex getVertex(int _name) {
        VertexSet.get(_name);
    }
    public Vertex get_startVertex() {
        return _startVertex;
    }

    public Vertex get_finishVertex() {
        return _finishVertex;
    }

    public ArrayList<Vertex> getVertexSet() {
        return VertexSet;
    }
}
