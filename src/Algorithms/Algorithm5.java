package Algorithms;

import Graph.*;
import Visualization.GraphVisualization;

import java.util.ArrayList;
import java.util.Comparator;

class Algorithm5 {
    static int maxDepth = 0;

    private static double R;
    private static final int WIDTH = GraphVisualization.WIDTH;
    private static final int HEIGHT = GraphVisualization.HEIGHT;
    private static final double RADIAL_COEFFICIENT = 0.7;

    private static void deleteIntersections(Graph tree) {
        ArrayList<ArrayList<Vertex>> verticesByDepth = new ArrayList<ArrayList<Vertex>>();

        for (int i = 0; i <= maxDepth; i++)
            verticesByDepth.add(new ArrayList<>());

        for (Vertex v: tree.getVertices()) {
            verticesByDepth.get(v.getDepth()).add(v);
        }

        for (int i = 1; i <= maxDepth; i++) {
            ArrayList<Vertex> currentDepth = verticesByDepth.get(i);
            currentDepth.sort(angleComparator);

            for (Vertex v: currentDepth) {
                double offset = makeRadialOffsetWithoutIntersections(v, verticesByDepth.get(i - 1), tree);

                if (offset != 0.0) {

                    for (Vertex u: currentDepth) {

                        if (u != v) {
                            u.setVertex(u.getR() + offset, u.getAngle());
                        }
                    }
                }
            }

            for (Vertex v: currentDepth) {
                double angleOffset = makeAngleOffsetWithoutIntersections(v, currentDepth);

                if (angleOffset == -1.0) {
                    double radialOffset = makeRadialOffsetWithoutIntersections(v, currentDepth, tree);

                    for (Vertex u: currentDepth) {

                        if (u != v) {
                            u.setVertex(u.getR() + radialOffset, u.getAngle());
                        }
                    }
                }
            }
        }
    }

    private static final double ANGLE_OFFSET = Math.PI / 18; //10 градусов
    private static Comparator<Vertex> angleComparator = (Comparator<Vertex>) (o1, o2) -> {
        double angle1 = o1.getAngle();
        double angle2 = o2.getAngle();
        return Double.compare(angle1, angle2);
    };

    private static double makeAngleOffsetWithoutIntersections(Vertex v, ArrayList<Vertex> vertices) {
        double offset = 0.0;
        for (Vertex u: vertices) {
            if (u != v) {
                boolean intersection = isIntersect(v, u);
                if (intersection) {
                    double angle = v.getAngle();
                    double x = v.getX();
                    double y = v.getY();
                    double delta = v.getAngle() - u.getAngle();

                    int indexOfV = vertices.indexOf(v);
                    Vertex w = vertices.get(delta < 0 ? (indexOfV - 1) % vertices.size() : (indexOfV + 1) % vertices.size());

                    if (delta > 0.0) {
                        boolean VWintersection = false;

                        while (isIntersect(v, u) && !VWintersection) {
                            v.setAngle(v.getAngle() + ANGLE_OFFSET);
                            offset += ANGLE_OFFSET;
                            v.setX(v.getR() * Math.cos(v.getAngle()));
                            v.setY(v.getR() * Math.sin(v.getAngle()));
                            VWintersection = isIntersect(v, w);
                        }

                        if (VWintersection) {
                            v.setAngle(angle);
                            v.setX(x);
                            v.setY(y);
                            return -1.0;

                        } else
                            return offset;
                    } else {
                        boolean VWintersection = false;

                        while (isIntersect(v, u) && !VWintersection) {
                            v.setAngle(v.getAngle() - ANGLE_OFFSET);
                            offset -= ANGLE_OFFSET;
                            v.setX(v.getR() * Math.cos(v.getAngle()));
                            v.setY(v.getR() * Math.sin(v.getAngle()));
                            VWintersection = isIntersect(v, w);
                        }

                        if (VWintersection) {
                            v.setAngle(angle);
                            v.setX(x);
                            v.setY(y);
                            return -1.0;

                        } else
                            return offset;
                    }
                }
            }
        }
        return offset;
    }

    static final double R_OFFSET = 10.0;

    private static double makeRadialOffsetWithoutIntersections(Vertex v, ArrayList<Vertex> vertices, Graph tree) {
        double offset = 0.0;

        for (Vertex u: vertices) {

            if (u != v) {

                System.out.println("INTERSECTION? v = " + v.getIndex() + " u = " + u.getIndex() + " " + isIntersect(v, u));

                while (isIntersect(v, u)) {
                    int i = tree.getRadials().indexOf(v.getR());
                    double r = tree.getRadials().get(i);

                    r += R_OFFSET;
                    offset += R_OFFSET;
                    tree.getRadials().set(i, r);

                    v.setVertex(v.getR() + R_OFFSET, v.getAngle());
                }
            }
        }

        return offset;
    }

    static boolean isIntersect(Vertex v, Vertex u) {
        return v.isIn(u);
    }

    private static void addFirstRadii(Graph tree) {
        R = (WIDTH < HEIGHT? WIDTH : HEIGHT)/ maxDepth * RADIAL_COEFFICIENT; //раньше радиус был константный

        tree.getRadials().add(R);
    }

    static Vertex findRoot(Graph tree) {
        Vertex root = null;

        for (Vertex v: tree.getVertices()) {
            if (v.isRoot()) {
                root = v;
                break;
            }
        }

        if (root == null)
            throw new RuntimeException("ERROR root is null");
        else
            return root;
    }

    static void calculateDepth(Vertex root, int depth) {
        for (Vertex v: root.getChild()) {
            v.setDepth(depth + 1);
            if (depth + 1 > maxDepth)
                maxDepth = depth + 1;
            calculateDepth(v, depth + 1);
        }
    }

    private static int leavesCount = 0;
    private static int leavesCounter(Vertex root) {
        countLeaves(root);
        return leavesCount;
    }

    private static void countLeaves(Vertex root) {
        if (root.getChild().size() != 0) {
            for (Vertex v: root.getChild())
                countLeaves(v);
        }
        else
            leavesCount++;
    }

    private static void radialPositions(Graph T, Vertex v, double alpha, double beta){
        if (v.isRoot()) {
            v.setX(0);
            v.setY(0);

            v.setR(0);
            v.setAngle(0);
        }

        int D = v.getDepth();

        double theta = alpha;

        double R_D = R + (R * D);

        if (!T.getRadials().contains(R_D))
            T.getRadials().add(R_D);

        int k = leavesCounter(v);
        leavesCount = 0;

        for (Vertex c: v.getChild()) {
            int lambda = leavesCounter(c);
            leavesCount = 0;

            double mu = theta + ((beta - alpha) * lambda / k);

            c.setVertex(R_D, (theta + mu) / 2);

            if (c.getChild().size() > 0)
                radialPositions(T, c, theta, mu);

            theta = mu;
        }
    }

    static void useAlgorithm(Graph tree) {
        Vertex root = findRoot(tree);

        calculateDepth(root, 0);

        System.out.println(tree);

        addFirstRadii(tree);

        radialPositions(tree, root, 0, 2 * Math.PI);

        deleteIntersections(tree);
    }
}
