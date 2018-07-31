package io;

public class Edge {
    public int _weight;
    public Vertex _src;
    public Vertex _dest;

    Edge(int weight,Vertex src,Vertex dest) {
        _weight = weight;
        _src = src;
        _dest = dest;
    }

}
