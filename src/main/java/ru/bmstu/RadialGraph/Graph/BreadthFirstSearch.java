package ru.bmstu.RadialGraph.Graph;

import java.util.ArrayDeque;

public final class BreadthFirstSearch {
    private boolean[] marked;
    private Vertex[] edgeTo;
    private int[] distTo;

    public BreadthFirstSearch(Graph G, Vertex start) {
        this.marked = new boolean[G.getVertices().size()];
        this.edgeTo = new Vertex[G.getVertices().size()];
        this.distTo = new int[G.getVertices().size()];
        bfs(start);
    }

    private void bfs(Vertex start) {
        ArrayDeque<Vertex> queue = new ArrayDeque<>();
        distTo[start.getIndex()] = 0;
        marked[start.getIndex()] = true;
        queue.add(start);
        while (!queue.isEmpty()) {
            Vertex v = queue.poll();
            for (Vertex w: v.getChild())
                if (!marked[w.getIndex()]) {
                    edgeTo[w.getIndex()] = v;
                    distTo[w.getIndex()] = distTo[v.getIndex()] + 1;
                    marked[w.getIndex()] = true;
                    queue.add(w);
                }
            Vertex w = v.getParent();
            if (w != null && !marked[w.getIndex()]) {
                edgeTo[w.getIndex()] = v;
                distTo[w.getIndex()] = distTo[v.getIndex()] + 1;
                marked[w.getIndex()] = true;
                queue.add(w);
            }
        }
    }

    public Vertex[] getEdgeTo() {
        return edgeTo;
    }

    public Vertex getEdgeTo(int i) {
        return edgeTo[i];
    }

    public int[] getDistTo() {
        return distTo;
    }

    public int getDistTo(int i) {
        return distTo[i];
    }
}
