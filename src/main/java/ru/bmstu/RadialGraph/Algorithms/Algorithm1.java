package ru.bmstu.RadialGraph.Algorithms;

import ru.bmstu.RadialGraph.Graph.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Stack;

class Algorithm1 {
    public static class BreadthFirstPaths {
        private boolean[] marked;
        private Vertex[] edgeTo;
        private int[] distTo;
        private final Vertex start;

        BreadthFirstPaths(Graph G, Vertex start) {
            this.marked = new boolean[G.getVertices().size()];
            this.edgeTo = new Vertex[G.getVertices().size()];
            this.distTo = new int[G.getVertices().size()];
            this.start = start;
            bfs(G, this.start);
        }

        //TODO переписать код так, чтобы был учтен случай с несколькими родителями вершины.
        private void bfs(Graph G, Vertex start) {
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

        boolean hasPathTo(Vertex v) { return marked[v.getIndex()]; }

        public Iterable<Vertex> pathTo(Vertex v) {
            if (!hasPathTo(v)) return null;
            Stack<Vertex> path = new Stack<Vertex>();
            Vertex x;
            for (x = v; distTo[x.getIndex()] != 0; x = edgeTo[x.getIndex()])
                path.push(x);
            path.push(x);
            return path;
        }
    }

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
        BreadthFirstPaths bfs = new BreadthFirstPaths(g, g.getVertices().get(0));
        int u = 0;
        int w = 0;
        for (int i = 0; i < g.getVertices().size(); i++)
            if (bfs.distTo[i] > bfs.distTo[u])
                u = i;
        bfs = new BreadthFirstPaths(g, g.getVertices().get(u));
        for (int i = 0; i < g.getVertices().size(); i++)
            if (bfs.distTo[i] > bfs.distTo[w])
                w = i;
        return bfs.distTo[w];
    }

    private static int eccentricity(Graph g, Vertex v) {
        BreadthFirstPaths bfs = new BreadthFirstPaths(g, v);
        int max = bfs.distTo[0];
        //System.out.println("max ecc = " + max + " " + v);
        for (int i = 1; i < g.getVertices().size(); i++) {
            //System.out.println("max = max(" + max + ", " + bfs.distTo[i] + ") " + i);
            max = Math.max(max, bfs.distTo[i]);
        }
        return max;
    }

    private static int graphRadii(Graph g) {
        int min = eccentricity(g, g.getVertices().get(0));
        //System.out.println("min = " + min + " " + g.getVertices().get(0));
        for (int i = 1; i < g.getVertices().size(); i++) {
            min = Math.min(min, eccentricity(g, g.getVertices().get(i)));
            //System.out.println(" min = " + min + " " + g.getVertices().get(i));
        }
        return min;
    }

    private static ArrayList<Vertex> graphCenter(Graph g) {
        ArrayList<Vertex> center = new ArrayList<>();
        int r = graphRadii(g);
        System.out.println("radii = " + r);
        for (Vertex v: g.getVertices()) {
            if (eccentricity(g, v) == r)
                center.add(v);
        }
        return center;
    }

    private static double degree(Graph g, Vertex v) {
//        int degree = v.getChild().size();
//        if (v.getParent() != null)
//            degree += 1;
        double degree = 0;
        BreadthFirstPaths bfs = new BreadthFirstPaths(g, v);
        for (Vertex t: g.getVertices())
            degree += bfs.distTo[t.getIndex()];
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
        //BreadthFirstPaths bfs = new BreadthFirstPaths(graph, s);
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
//                        BreadthFirstPaths bfs = new BreadthFirstPaths(graph, v);
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
                        BreadthFirstPaths bfs = new BreadthFirstPaths(graph, u);
                        sum1x += (1 - t) * graph.getW()[u.getIndex()][v.getIndex()] * (u.getX() + bfs.distTo[v.getIndex()] * (v.getX() - u.getX()) * b(u, v));
                        System.out.println("sum1x = " + sum1x + " v = " + v.getIndex() + " u = " + u.getIndex());
                    }
                }
                sum1x += (t / radials.get(i)) * v.getX() * a(v);

                double sum1y = 0;
                for (Vertex u: graph.getVertices()) {
                    if (v != u) {
                        BreadthFirstPaths bfs = new BreadthFirstPaths(graph, u);
                        sum1x += (1 - t) * graph.getW()[u.getIndex()][v.getIndex()] * (u.getY() + bfs.distTo[v.getIndex()] * (v.getY() - u.getY()) * b(u, v));
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
