package io;

import java.util.ArrayList;

public class Vertex {
    private int _index;
    private boolean _start;
    private boolean _finish;
    private ArrayList<Edge> EdgeSet = new ArrayList<Edge>();

    Vertex(int _name,boolean start,boolean finish) {
        _index = _name;
        _start =start;
        _finish = finish;
    }

    public ArrayList<Edge> getEdgeSet() {
        return EdgeSet;
    }
    public void addEdge(Vertex dest,int weight) {
        Edge e = new Edge(weight,this,dest);
        EdgeSet.add(e);
    }
    public int get_index() {
        return _index;
    }

    public boolean is_finish() {
        return _finish;
    }

    public boolean is_start() {
        return _start;
    }

    public ArrayList<Vertex> getAllConnectedVertexs() {
        ArrayList<Vertex> _output = new ArrayList<Vertex>();
        for(Edge e:EdgeSet) {
            if(e._src == this) {
                _output.add(e._dest);
            }

        }
        return  _output;
    }
}
