package ru.bmstu.RadialGraph.Algorithms;

import ru.bmstu.RadialGraph.Graph.*;

import java.util.ArrayList;

final class Algorithm1 {
    private static double euclidianNorm(Vertex u, Vertex v) {
        return Math.sqrt(Math.pow((u.getX() - v.getX()), 2) + Math.pow((u.getY() - v.getY()), 2));
    }

    private static double euclidianNorm(Vertex v) {
        return Math.sqrt(Math.pow(v.getX(), 2) + Math.pow(v.getY(), 2));
    }

    private static double b(Vertex u, Vertex v) {
        double norm = euclidianNorm(u, v);
        if (norm > 0)
            return 1 / norm;
        else
            return 0;
    }

    private static int diam(Graph g) {
        BreadthFirstSearch bfs = new BreadthFirstSearch(g, g.getVertices().get(0));
        int u = 0;
        int w = 0;
        for (int i = 0; i < g.getVertices().size(); i++)
            if (bfs.getDistTo(i) > bfs.getDistTo(u))
                u = i;
        bfs = new BreadthFirstSearch(g, g.getVertices().get(u));
        for (int i = 0; i < g.getVertices().size(); i++)
            if (bfs.getDistTo(i) > bfs.getDistTo(w))
                w = i;
        return bfs.getDistTo(w);
    }


    private static ArrayList<Vertex> graphCenter(Graph g) {
        ArrayList<Vertex> center = new ArrayList<>();
        int r = g.graphRadii();
        System.out.println("radii = " + r);
        for (Vertex v: g.getVertices()) {
            if (g.eccentricity(v) == r)
                center.add(v);
        }
        return center;
    }

    private static double degree(Graph g, Vertex v) {
        double degree = 0;
        BreadthFirstSearch bfs = new BreadthFirstSearch(g, v);
        for (Vertex t: g.getVertices())
            degree += bfs.getDistTo(t.getIndex());
        degree = 1 / degree;
        return degree;
    }

    private static double minDegree(Graph g) {
        double min = degree(g, g.getVertices().get(0));
        for (int i = 1; i < g.getVertices().size(); i++)
            min = Math.min(min, degree(g, g.getVertices().get(i)));
        return min;
    }

    private static double maxDegree(Graph g) {
        double max = -1;
        for (Vertex v: g.getVertices())
            max = Math.max(max, degree(g, v));
        return max;
    }

    private static double a(Vertex v) {
        double norm = euclidianNorm(v);
        return norm > 0? (1 / norm) : 0;
    }

    static void useAlgorithm(Graph graph) {
        ArrayList<Vertex> center = graphCenter(graph);
        System.out.println("center = " + center.toString());
        int diam = diam(graph);

        int c = center.size() > 1? 50 : 0;
        System.out.println("c = " + c);

        ArrayList<Double> radials = new ArrayList<>();

        double min = minDegree(graph);
        double max = maxDegree(graph);

        for (Vertex v: graph.getVertices()) {
            double x = 1 - (degree(graph, v) - min) / (max - min + c);
            radials.add(diam * x / 2);
        }

        graph.setRadials(radials);

        for (Vertex v: graph.getVertices()) {
            graph.getRadials().add(euclidianNorm(v));
        }
//        for (int i = 0; i < 1; i++) {
//            float sum1x = 0;
//            float sum2 = 0;
//            float sum1y = 0;
//            for (Vertex u: graph.getVertices()) {
//                for (Vertex v : graph.getVertices()) {
//                    if (v != u) {
//                        BreadthFirstSearch bfs = new BreadthFirstSearch(graph, v);
//
//                        //sum1x += graph.getW()[v.getIndex()][u.getIndex()] * (v.getX() + bfs.distTo[u.getIndex()] * (u.getX() - v.getX()) * b(u, v));
//                        sum1x += ((v.getX() + euclidianNorm(v, u) * (u.getX() - v.getX()) * b(u, v)) / (float) euclidianNorm(v, u)) / (float) euclidianNorm(v, u);
//                        System.out.println(sum1x);
//
//                        //sum2 += graph.getW()[v.getIndex()][u.getIndex()];
//                        sum2 += (1 / euclidianNorm(v, u)) / euclidianNorm(v, u);
//                        //System.out.println("sum2 = " + sum2 + " + " + graph.getW()[v.getIndex()][u.getIndex()]);
//
//                        //sum1y += graph.getW()[v.getIndex()][u.getIndex()] * (v.getY() + bfs.distTo[u.getIndex()] * (u.getY() - v.getY()) * b(u, v));
//                        sum1y += ((v.getY() + euclidianNorm(v, u) * (u.getY() - v.getY()) * b(u, v)) / euclidianNorm(v, u)) / euclidianNorm(v, u);
//                    }
//                }
//
//                u.setX(sum1x / sum2);
//                u.setY(sum1y / sum2);
//            }
//        }

        int K = 1;

        for (double t = 0; t <= 1; t += (double)1 / K) {
            int i = 0;
            for (Vertex v: graph.getVertices()) {
                double sum2 = 0;
                for (Vertex u: graph.getVertices()) {
                    if (u != v) {
                        sum2 += (1 - t) * graph.getW()[u.getIndex()][v.getIndex()];
                    }
                }
                System.out.println("t = " + t + " radii = " + radials.get(i));
                sum2 += (t / radials.get(i)) / radials.get(i);
                System.out.println("sum2 = " + sum2);

                double sum1x = 0;
                for (Vertex u: graph.getVertices()) {
                    if (v != u) {
                        BreadthFirstSearch bfs = new BreadthFirstSearch(graph, u);
                        sum1x += (1 - t) * graph.getW()[u.getIndex()][v.getIndex()] * (u.getX() + bfs.getDistTo(v.getIndex()) * (v.getX() - u.getX()) * b(u, v));
                        System.out.println("sum1x = " + sum1x + " v = " + v.getIndex() + " u = " + u.getIndex());
                    }
                }
                sum1x += (t / radials.get(i)) * v.getX() * a(v);

                double sum1y = 0;
                for (Vertex u: graph.getVertices()) {
                    if (v != u) {
                        BreadthFirstSearch bfs = new BreadthFirstSearch(graph, u);
                        sum1x += (1 - t) * graph.getW()[u.getIndex()][v.getIndex()] * (u.getY() + bfs.getDistTo(v.getIndex()) * (v.getY() - u.getY()) * b(u, v));
                        System.out.println("sum1y = " + sum1y + " v = " + v.getIndex() + " u = " + u.getIndex());
                    }
                }
                sum1y += (t / radials.get(i)) * v.getY() * a(v);

                //System.out.println("sum1y = " + sum1y);

                v.setX(sum1x / sum2);
                v.setY(sum1y / sum2);

                i++;
            }
        }
    }
}
