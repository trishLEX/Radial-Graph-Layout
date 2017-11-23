package ru.bmstu.RadialGraph.Algorithms;

import ru.bmstu.RadialGraph.Graph.*;

import java.util.ArrayList;

final class Algorithm1 {
    private static final int LENGTH_OF_EDGE = 100;
    private static final int NUMBER_OF_ITERATIONS = 1000;

    private static double euclideanNorm(Vertex v) {
        return Math.sqrt(Math.pow(v.getX(), 2) + Math.pow(v.getY(), 2));
    }

    private static double b(Vertex u, Vertex v) {
        double norm = v.distTo(u);
        if (norm > 0)
            return 1 / norm;
        else
            return 0;
    }

    private static double a(Vertex v) {
        double norm = euclideanNorm(v);
        return norm > 0? (1 / norm) : 0;
    }

    private static double c(Vertex v, Graph g, Matrix D) {
        double sum = 0.0;
        for (Vertex t: g.getVertices()) {
            sum += D.get(v.getIndex(), t.getIndex());
        }

        return 1 / sum;
    }

    private static double minMeasure(Graph g, Matrix D) {
        double min = c(g.getVertices().get(0), g, D);
        for (int i = 1; i < g.getVertices().size(); i++)
            min = Math.min(min, c(g.getVertices().get(i), g, D));
        return min;
    }

    private static double maxMeasure(Graph g, Matrix D) {
        double max = c(g.getVertices().get(0), g, D);
        for (int i = 1; i < g.getVertices().size(); i++)
            max = Math.max(max, c(g.getVertices().get(i), g, D));
        return max;
    }

    private static void focusingOnNode(Graph graph, ArrayList<double[]> coords) {
        Matrix D = new Matrix(graph.getSize());
        Matrix W = new Matrix(graph.getSize());
        Matrix Z = new Matrix(graph.getSize());

        for (Vertex v: graph.getVertices()) {
            BreadthFirstSearch bfs = new BreadthFirstSearch(graph, v);
            for (Vertex u: graph.getVertices()) {
                D.set(v.getIndex(), u.getIndex(), bfs.getDistTo(u.getIndex()) * LENGTH_OF_EDGE);
                D.set(u.getIndex(), v.getIndex(), bfs.getDistTo(u.getIndex()) * LENGTH_OF_EDGE);
            }
        }

        for (int i = 0; i < graph.getSize(); i++) {
            for (int j = 0; j < graph.getSize(); j++) {
                double d = D.get(i, j) != 0? (1 / D.get(i, j) / D.get(i, j)) : 0;
                W.set(i, j, d);
            }
        }

        int rootIndex = graph.getRoot().getIndex();
        for (int i = 0; i < graph.getSize(); i++) {
            Z.set(i, rootIndex, W.get(i, rootIndex));
            Z.set(rootIndex, i, W.get(rootIndex, i));
        }

        for (double t = 0.0; t <= 1; t += 1 / NUMBER_OF_ITERATIONS) {
            for (Vertex u: graph.getVertices()) {
                double sum2 =  0.0;
                double sum1x = 0.0;
                double sum1y = 0.0;

                for (Vertex v: graph.getVertices()) {
                    if (v != u) {
                        sum2 += (1 - t) * W.get(u.getIndex(), v.getIndex()) + t * Z.get(u.getIndex(), v.getIndex());

                        sum1x += ((1 - t) * W.get(u.getIndex(), v.getIndex()) + t * Z.get(u.getIndex(), v.getIndex())) *
                                (coords.get(v.getIndex())[0] + D.get(u.getIndex(), v.getIndex()) * (coords.get(u.getIndex())[0] - coords.get(v.getIndex())[0]) * b(u, v));

                        sum1y += ((1 - t) * W.get(u.getIndex(), v.getIndex()) + t * Z.get(u.getIndex(), v.getIndex())) *
                                (coords.get(v.getIndex())[1] + D.get(u.getIndex(), v.getIndex()) * (coords.get(u.getIndex())[1] - coords.get(v.getIndex())[1]) * b(u, v));
                    }
                }

                u.setVertexByCartesian(sum1x / sum2, sum1y / sum2);
            }

            for (Vertex v: graph.getVertices()){
                double[] coord = new double[2];
                coord[0] = v.getX();
                coord[1] = v.getY();
                coords.set(v.getIndex(), coord);
            }
        }
    }

    private static ArrayList<double[]> memorizeCoords(Graph graph) {
        ArrayList<double[]> coords = new ArrayList<>();

        for (Vertex v: graph.getVertices()) {
            double[] coord = new double[2];
            coord[0] = v.getX();
            coord[1] = v.getY();
            coords.add(coord);
        }

        return coords;
    }

    static void useAlgorithm(Graph graph) {
        System.out.println(graph);

        ArrayList<double[]> coords = memorizeCoords(graph);

        focusingOnNode(graph, coords);

        System.out.println("Centrality drawing is prepared");

        graph.fillRadials5();
    }
}
