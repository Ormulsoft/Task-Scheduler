package io;

import java.util.ArrayList;

public class Graph {
    private Vertex _startVertex;
    private Vertex _finishVertex;
    public ArrayList<Edge> EdgeSet;
    private ArrayList<Vertex> VertexSet = new ArrayList<Vertex>();

    public Graph (Vertex _start,Vertex _finish) {
        _startVertex = _start;
        _finishVertex = _finish;
    };
    public Graph () {

    };
    public void addVertex(int _name,boolean start,boolean finish,int weight) {
        Vertex name = new Vertex(_name, start, finish,weight);
        if(name.is_start() == true) {
            this._startVertex = name;
        }
        VertexSet.add(name);
    }

    public ArrayList<Edge> getEdgeSet() {
        for(Vertex name: VertexSet) {
            this.EdgeSet.addAll(name.getEdgeSet());
        }
        return this.getEdgeSet();
    }

    public Vertex getVertex(int _name) {
        return VertexSet.get(_name);
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
