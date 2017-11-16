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

//    private static int diam(Graph g) {
//        BreadthFirstSearch bfs = new BreadthFirstSearch(g, g.getVertices().get(0));
//        int u = 0;
//        int w = 0;
//        for (int i = 0; i < g.getVertices().size(); i++)
//            if (bfs.getDistTo(i) > bfs.getDistTo(u))
//                u = i;
//        bfs = new BreadthFirstSearch(g, g.getVertices().get(u));
//        for (int i = 0; i < g.getVertices().size(); i++)
//            if (bfs.getDistTo(i) > bfs.getDistTo(w))
//                w = i;
//        return bfs.getDistTo(w);
//    }


//    private static ArrayList<Vertex> graphCenter(Graph g) {
//        ArrayList<Vertex> center = new ArrayList<>();
//        int r = g.graphRadii();
//        System.out.println("radii = " + r);
//        for (Vertex v: g.getVertices()) {
//            if (g.eccentricity(v) == r)
//                center.add(v);
//        }
//        return center;
//    }

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
        System.out.println(graph);

//        ArrayList<Vertex> center = graphCenter(graph);
//        System.out.println("center = " + center.toString());
//        int diam = diam(graph);
//
//        int c = center.size() > 1? 50 : 0;
//        System.out.println("c = " + c);
//
//        ArrayList<Double> radials = new ArrayList<>();
//
//        double min = minDegree(graph);
//        double max = maxDegree(graph);
//
//        for (Vertex v: graph.getVertices()) {
//            double x = 1 - (degree(graph, v) - min) / (max - min + c);
//            radials.add(diam * x / 2);
//        }
//
//        graph.setRadials(radials);
//
//        for (Vertex v: graph.getVertices()) {
//            graph.getRadials().add(euclidianNorm(v));
//        }
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
//        Matrix D = new Matrix(13);
//        Matrix W = new Matrix(13);
//        Matrix Z = new Matrix(13);
//
//        double[] row0 = {0, 1, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 0};
//        D.setRow(0, row0);
//
//        double[] row1 = {1, 0, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 100};
//        D.setRow(1, row1);
//
//        double[] row2 = {1, 2, 0, 2, 2, 3, 3, 3, 3, 3, 4, 4, 100};
//        D.setRow(2, row2);
//
//        double[] row3 = {1, 2, 2, 0, 2, 3, 3, 3, 3, 3, 2, 2, 100};
//        D.setRow(3, row3);
//
//        double[] row4 = {1, 2, 2, 2, 0, 1, 1, 3, 3, 3, 4, 4, 100};
//        D.setRow(4, row4);
//
//        double[] row5 = {2, 3, 3, 3, 1, 0, 2, 3, 3, 3, 5, 5, 200};
//        D.setRow(5, row5);
//
//        double[] row6 = {2, 3, 3, 3, 1, 2, 0, 4, 4, 4, 5, 5, 200};
//        D.setRow(6, row6);
//
//        double[] row7 = {2, 3, 3, 1, 3, 4, 4, 0, 2, 2, 3, 3, 200};
//        D.setRow(7, row7);
//
//        double[] row8 = {2, 3, 3, 1, 3, 4, 4, 2, 0, 2, 1, 1, 200};
//        D.setRow(8, row8);
//
//        double[] row9 = {2, 3, 3, 1, 3, 4, 4, 2, 3, 0, 3, 3, 200};
//        D.setRow(9, row9);
//
//        double[] row10 = {3, 4, 4, 2, 4, 5, 5, 3, 1, 3, 0, 2, 300};
//        D.setRow(10, row10);
//
//        double[] row11 = {3, 4, 4, 2, 4, 5, 5, 3, 1, 3, 2, 0, 300};
//        D.setRow(11, row11);
//
//        double[] row12 = {0, 100, 100, 100, 100, 200, 200, 200, 200, 200, 300, 300, 0};
//        D.setRow(12, row12);
//
//        System.out.println(D);
//
//        for (int i = 0; i < 12; i++) {
//            for (int j = 0; j < 12; j++) {
//                double d = D.get(i, j) != 0? (1 / D.get(i, j) / D.get(i, j)) : 0;
//                W.set(i, j, d);
//            }
//        }
//
//        System.out.println(W);
//
//        for (int i = 1; i < 12; i++) {
//            double d = D.get(0, i) != 0? (1 / D.get(0, i) / D.get(0, i)): 0;
//            Z.set(0, i, d);
//            Z.set(i, 0, d);
//        }
//
//        System.out.println(Z);
        Matrix D = new Matrix(graph.getVertices().size() + 1);
        Matrix W = new Matrix(graph.getVertices().size() + 1);

        for (Vertex v: graph.getVertices()) {
            BreadthFirstSearch bfs = new BreadthFirstSearch(graph, v);
            for (Vertex u: graph.getVertices()) {
                D.set(v.getIndex(), u.getIndex(), bfs.getDistTo(u.getIndex()));
                D.set(u.getIndex(), v.getIndex(), bfs.getDistTo(u.getIndex()));
            }
            D.set(graph.getVertices().size(), v.getIndex(), v.getR());
            D.set(v.getIndex(), graph.getVertices().size(), v.getR());
        }

        System.out.println(D);

        for (int i = 0; i < graph.getVertices().size(); i++) {
            for (int j = 0; j < graph.getVertices().size(); j++) {
                double d = D.get(i, j) != 0? (1 / D.get(i, j) / D.get(i, j)) : 0;
                W.set(i, j, d);
            }
        }

        System.out.println(W);

//        for (int i = 0; i < graph.getVertices().size(); i++) {
//            double d = D.get(i, graph.getVertices().size()) != 0? (1 / D.get(i, graph.getVertices().size()) / D.get(i, graph.getVertices().size())): 0;
//            Z.set(i, graph.getVertices().size(), d);
//            Z.set(graph.getVertices().size(), i, d);
//        }
//
//        System.out.println(Z);

        ArrayList<double[]> coords = new ArrayList<>();
        for (Vertex v: graph.getVertices()) {
            double[] coord = new double[2];
            coord[0] = v.getX();
            coord[1] = v.getY();
            coords.add(coord);
        }

        for (double[] coord: coords)
            System.out.println("x = " + coord[0] + " y = " + coord[1]);

//        for (int i = 0; i < 1; i++) {
//            double sum1x = 0;
//            double sum2 = 0;
//            double sum1y = 0;
//            for (Vertex u: graph.getVertices()) {
//                for (Vertex v : graph.getVertices()) {
//                    if (v != u) {
//                        //sum1x += graph.getW()[v.getIndex()][u.getIndex()] * (v.getX() + bfs.distTo[u.getIndex()] * (u.getX() - v.getX()) * b(u, v));
//                        sum1x += W.get(v.getIndex(), u.getIndex()) * (coords.get(v.getIndex())[0] +
//                                D.get(v.getIndex(), u.getIndex()) * (coords.get(u.getIndex())[0] - coords.get(v.getIndex())[0]) * b(u, v));
//                        //System.out.println(sum1x);
//
//                        //sum2 += graph.getW()[v.getIndex()][u.getIndex()];
//                        sum2 += W.get(v.getIndex(), u.getIndex());
//                        //System.out.println("sum2 = " + sum2 + " + " + graph.getW()[v.getIndex()][u.getIndex()]);
//
//                        //sum1y += graph.getW()[v.getIndex()][u.getIndex()] * (v.getY() + bfs.distTo[u.getIndex()] * (u.getY() - v.getY()) * b(u, v));
//                        sum1y += W.get(v.getIndex(), u.getIndex()) * (coords.get(v.getIndex())[1] +
//                                D.get(v.getIndex(), u.getIndex()) * (coords.get(u.getIndex())[1] - coords.get(v.getIndex())[1]) * b(u, v));
//                    }
//                }
//
//                u.setX(sum1x / sum2);
//                u.setY(sum1y / sum2);
//            }
//
//            for (Vertex v: graph.getVertices()){
//                double[] coord = new double[2];
//                coord[0] = v.getX();
//                coord[1] = v.getY();
//                coords.set(v.getIndex(), coord);
//            }
//        }

        double K = 1.0;
        for (double t = 0; t <= 1; t += (double)1 / K) {
            for (Vertex v: graph.getVertices()) {
                System.out.println();
                System.out.println("v = " + v.getIndex());
                double sum2 = 0;
                double r;
                if (D.get(graph.getVertices().size(), v.getIndex()) != 0)
                    r = D.get(graph.getVertices().size(), v.getIndex());
                else
                    r = Double.POSITIVE_INFINITY;
                for (Vertex u: graph.getVertices()) {
                    if (u != v) {
                        sum2 += W.get(v.getIndex(), u.getIndex());
                        sum2 *= (1 - t);
                    }
                }
                sum2 += (t / r) / r;

                System.out.println("sum2 = " + sum2);

                double sum1x = 0;
                for (Vertex u: graph.getVertices()) {
                    if (v != u) {
                        sum1x += (1 - t) * W.get(v.getIndex(), u.getIndex()) * (coords.get(u.getIndex())[0] +
                                D.get(v.getIndex(), u.getIndex()) * (coords.get(v.getIndex())[0] - coords.get(u.getIndex())[0]) * b(u, v));
                        System.out.println("W = " + W.get(v.getIndex(), u.getIndex()) + " ux = " + u.getX() + " d = " + D.get(v.getIndex(), u.getIndex()) + " xv - xu = " + (v.getX() - u.getX()) + " b = " + b(u, v));
                        System.out.println("sum1x = " + sum1x + " v = " + v.getIndex() + " u = " + u.getIndex());
                    }
                }
                sum1x += (t / r) * coords.get(v.getIndex())[0] * a(v);

                System.out.println();

                double sum1y = 0;
                for (Vertex u: graph.getVertices()) {
                    if (v != u) {
                        sum1y += (1 - t) * W.get(v.getIndex(), u.getIndex()) * (coords.get(u.getIndex())[1] +
                                D.get(v.getIndex(), u.getIndex()) * (coords.get(v.getIndex())[1] - coords.get(u.getIndex())[1]) * b(u, v));
                        System.out.println("W = " + W.get(v.getIndex(), u.getIndex()) + " uy = " + u.getY() + " d = " + D.get(v.getIndex(), u.getIndex()) + " yv - yu = " + (v.getY() - u.getY()) + " b = " + b(u, v));
                        System.out.println("sum1y = " + sum1y + " v = " + v.getIndex() + " u = " + u.getIndex());
                    }
                }
                sum1y += (t / r) * coords.get(v.getIndex())[1] * a(v);

                if (sum2 == 0)
                    sum2 = 1;

                v.setX(sum1x / sum2);
                v.setY(sum1y / sum2);

                System.out.println(" v = " + v.getIndex() + " sum1x = " + sum1x + " sum2 = " + sum2 + " x = " + sum1x / sum2 + " " + v.getX());
            }

            for (Vertex v: graph.getVertices()){
                double[] coord = new double[2];
                coord[0] = v.getX();
                coord[1] = v.getY();
                coords.set(v.getIndex(), coord);
            }
        }
    }
}
