package ru.bmstu.RadialGraph.Algorithms;

import ru.bmstu.RadialGraph.Graph.*;

import java.util.ArrayList;

final class Algorithm1 {
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

    private static void centralityDrawing(Graph graph, ArrayList<double[]> coords) {
        Matrix D = new Matrix(graph.getSize() + 1);
        Matrix W = new Matrix(graph.getSize() + 1);

        for (Vertex v: graph.getVertices()) {
            BreadthFirstSearch bfs = new BreadthFirstSearch(graph, v);
            for (Vertex u: graph.getVertices()) {
                D.set(v.getIndex(), u.getIndex(), bfs.getDistTo(u.getIndex()) * 100);
                D.set(u.getIndex(), v.getIndex(), bfs.getDistTo(u.getIndex()) * 100);
            }
            //D.set(graph.getSize(), v.getIndex(), v.getR());
            //D.set(v.getIndex(), graph.getSize(), v.getR());
        }

        ArrayList<Double> radials = new ArrayList<>();

        double c = graph.getCenter().size() == 1 ? 0 : 50;

        for (Vertex v: graph.getVertices()) {
            double r = graph.getDiam() * 100 / 2 * (1 - (c(v, graph, D) - minMeasure(graph, D)) / (maxMeasure(graph, D) - minMeasure(graph, D) + c));
            radials.add(r);
        }

        for (int i = 0; i < radials.size(); i++) {
            D.set(graph.getSize(), i, radials.get(i));
            D.set(i, graph.getSize(), radials.get(i));
        }

        System.out.println(D);

        for (int i = 0; i < graph.getSize(); i++) {
            for (int j = 0; j < graph.getSize(); j++) {
                double d = D.get(i, j) != 0? (1 / D.get(i, j) / D.get(i, j)) : 0;
                W.set(i, j, d);
            }
        }

        System.out.println(W);

        double K = 5.0;
        for (double t = 0; t <= 1; t += (double)1 / K) {
            for (Vertex v: graph.getVertices()) {
                System.out.println();
                System.out.println("v = " + v.getIndex());
                double sum2 = 0;
                double r;
                if (D.get(graph.getSize(), v.getIndex()) != 0)
                    r = D.get(graph.getSize(), v.getIndex());
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

    private static void focusingOnNode(Graph graph, ArrayList<double[]> coords) {
        Matrix D = new Matrix(graph.getSize());
        Matrix W = new Matrix(graph.getSize());
        Matrix Z = new Matrix(graph.getSize());

        for (Vertex v: graph.getVertices()) {
            BreadthFirstSearch bfs = new BreadthFirstSearch(graph, v);
            for (Vertex u: graph.getVertices()) {
                D.set(v.getIndex(), u.getIndex(), bfs.getDistTo(u.getIndex())*100);
                D.set(u.getIndex(), v.getIndex(), bfs.getDistTo(u.getIndex())*100);
            }
        }

        System.out.println(D);

        for (int i = 0; i < graph.getSize(); i++) {
            for (int j = 0; j < graph.getSize(); j++) {
                double d = D.get(i, j) != 0? (1 / D.get(i, j) / D.get(i, j)) : 0;
                W.set(i, j, d);
            }
        }

        System.out.println(W);

        int rootIndex = graph.getRoot().getIndex();
        for (int i = 0; i < graph.getSize(); i++) {
            Z.set(i, rootIndex, W.get(i, rootIndex));
            Z.set(rootIndex, i, W.get(rootIndex, i));
        }

        System.out.println(Z);

        double K = 10000.0;
        for (double t = 0.0; t <= 1; t += 1 / K) {
            for (Vertex u: graph.getVertices()) {
                double sum2 = 0.0;
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

                //u.setX(sum1x / sum2);
                //u.setY(sum1y / sum2);
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

    static void useAlgorithm(Graph graph) {
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
//            graph.getRadials().add(euclideanNorm(v));
//        }

        System.out.println(graph);

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
        //centralityDrawing(graph, coords);
        focusingOnNode(graph, coords);

        graph.fillRadials5();
    }
}
