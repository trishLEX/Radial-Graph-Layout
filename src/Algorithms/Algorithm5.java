package Algorithms;

import Graph.*;
import Visualization.GraphVisualization;

import java.util.ArrayList;
import java.util.Comparator;

class Algorithm5 {
    private static int maxDepth = 0;
    private static double R;
    private static final int WIDTH = GraphVisualization.WIDTH;
    private static final int HEIGHT = GraphVisualization.HEIGHT;
    private static final double RADIAL_COEFFICIENT = 0.7;

    static void useAlgorithm(Graph tree) {
        Vertex root = findRoot(tree);

        calculateDepth(root, 0);

        System.out.println(tree);

        addRadials(tree);

        radialPositions(tree, root, 0, 2 * Math.PI);

//        for (Vertex v: tree.getVertices())
//            System.out.println("v = " + v.getIndex() + " angle =" + v.getAngle());

        deleteIntersections(tree);
    }

    private static void deleteIntersections(Graph tree) {
        ArrayList<ArrayList<Vertex>> verticesByDepth = new ArrayList<ArrayList<Vertex>>();
        for (int i = 0; i <= maxDepth; i++)
            verticesByDepth.add(new ArrayList<>());

        for (Vertex v: tree.getVertices()) {
            verticesByDepth.get(v.getDepth()).add(v);
        }

        System.out.println("SIZE of verticesByDepth = " + verticesByDepth.size());
        System.out.println(verticesByDepth.get(4).get(0).getIndex());

        for (int i = 1; i <= maxDepth; i++) {
            ArrayList<Vertex> currentDepth = verticesByDepth.get(i);
            currentDepth.sort(angleComparator);
            for (Vertex v: currentDepth)
                System.out.println("v = " + v.getIndex() + " angle = " + v.getAngle());
            for (Vertex v: currentDepth) {
                double offset = makeRadialOffsetWithoutIntersections(v, verticesByDepth.get(i - 1), tree);
                if (offset != 0.0) {
                    //System.out.println("INTERSECTION");
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
        //vertices.sort(angleComparator);
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
                        } else return offset;
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
                        } else return offset;
                    }
                }
            }
        }
        return offset;
    }

    private static final double R_OFFSET = 10.0;

    private static double makeRadialOffsetWithoutIntersections(Vertex v, ArrayList<Vertex> vertices, Graph tree) {
        //System.out.println("radial offset v = " + v.getIndex() + " r = " + v.getR());
        double offset = 0.0;
        for (Vertex u: vertices) {
            System.out.println("RADIAL INTERSECTION v = " + v.getIndex() + " u = " + u.getIndex());
            if (u != v) {
                while (isIntersect(v, u)) {
                    int i = tree.getRadials().indexOf(v.getR());
                    double r = tree.getRadials().get(i);
                    r += R_OFFSET;
                    offset += R_OFFSET;
                    tree.getRadials().set(i, r);
                    //System.out.println("in radials r = " + r);
                    v.setR(v.getR() + R_OFFSET);
                    v.setX(r * Math.cos(v.getAngle()));
                    v.setY(r * Math.sin(v.getAngle()));
                }
            }
        }

        //System.out.println("offset = " + offset + " r = " + v.getR());
        return offset;
    }

    private static boolean isIntersect(Vertex v, Vertex u) {
        //System.out.println("v = " + v.getIndex() + " u = " + u.getIndex() + " " + v.isIn(u));
        return v.isIn(u);
    }

//    private static boolean intersect(Vertex v, Vertex u) {
//        System.out.println("v = " + v.getIndex() + " u = " + u.getIndex());
//        boolean res = false;
//
//        double[] xp = new double[8];
//
//        double x = v.getX();
//        double y = v.getY();
//        double width2 = v.getWidth() / 2;
//        double height2 = v.getHeight() / 2;
//        double xs = v.getSign().getX();
//        double ys = v.getSign().getY();
//        double sWidth2 = v.getSign().getWidth() / 2;
//        double sHeight2 = v.getSign().getHeight() / 2;
//
//        xp[0] = x + width2;
//        xp[1] = x - width2;
//        xp[2] = x - width2;
//        xp[3] = xs - sWidth2;
//        xp[4] = xs - sWidth2;
//        xp[5] = xs + sWidth2;
//        xp[6] = xs + sWidth2;
//        xp[7] = x + width2;
//
//        //System.out.println(xp[0]);
//
//        double[] yp = new double[8];
//
//        yp[0] = y + height2;
//        yp[1] = y + height2;
//        yp[2] = y - height2;
//        yp[3] = ys + sHeight2;
//        yp[4] = ys - sHeight2;
//        yp[5] = ys - sHeight2;
//        yp[6] = ys + sHeight2;
//        yp[7] = y - height2;
//
//        ArrayList<Double[]> ups = new ArrayList<>();
//
//        x = u.getX();
//        y = u.getY();
//        width2 = u.getWidth() / 2;
//        height2 = u.getHeight() / 2;
//        xs = u.getSign().getX();
//        ys = u.getSign().getY();
//        sWidth2 = u.getSign().getWidth() / 2;
//        sHeight2 = u.getSign().getHeight() / 2;
//
//        ups.add(new Double[]{x + width2, y + height2});
//        ups.add(new Double[]{x - width2, y + height2});
//        ups.add(new Double[]{x - width2, y - height2});
//        ups.add(new Double[]{xs - sWidth2, ys + sHeight2});
//        ups.add(new Double[]{xs - sWidth2, ys - sHeight2});
//        ups.add(new Double[]{xs + sWidth2, ys - sHeight2});
//        ups.add(new Double[]{xs + sWidth2, ys + sHeight2});
//        ups.add(new Double[]{x + width2, y - height2});
//
//        for (Double[] mas: ups) {
//            x = mas[0];
//            y = mas[1];
//            int i;
//            int j = 7;
//            for (i = 0; i < 8; i++) {
//                if ((((yp[i]<=y) && (y<yp[j])) || ((yp[j]<=y) && (y<yp[i]))) &&
//                        (x > (xp[j] - xp[i]) * (y - yp[i]) / (yp[j] - yp[i]) + xp[i]))
//                    res = !res;
//                j = i;
//            }
//            if (res) {
//                System.out.println("TRUE");
//                return res;
//            }
//        }
//
//        System.out.println("FALSE");
//        return res;
//    }

    private static void addRadials(Graph tree) {
        R = (WIDTH < HEIGHT? WIDTH : HEIGHT)/ maxDepth * RADIAL_COEFFICIENT; //раньше радиус был константный

        tree.getRadials().add(R);
    }

    private static Vertex findRoot(Graph tree) {
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

    private static void calculateDepth(Vertex root, int depth) {
        for (Vertex v: root.getChild()) {
            v.setDepth(depth + 1);
            if (depth + 1 > maxDepth)
                maxDepth = depth + 1;
            calculateDepth(v, depth + 1);
        }
    }

    //private static final double XI = 50;

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
        //System.out.println("D = " + D + " v = " + v.getIndex());

        //System.out.println("α = " + alpha + " β = " + beta);
        double theta = alpha;

        double R_D = R + (R * D);
        //System.out.println("R_D " + R_D);
        if (!T.getRadials().contains(R_D))
            T.getRadials().add(R_D);

        int k = leavesCounter(v);
        //System.out.println("LEAVES COUNTER K = " + k);
        leavesCount = 0;

        for (Vertex c: v.getChild()) {
            int lambda = leavesCounter(c);
            System.out.println("    child = " + c.getIndex());
            //System.out.println("    LEAVES COUNTER λ = " + lambda);
            leavesCount = 0;

            double mu = theta + ((beta - alpha) * lambda / k);
            //System.out.println("    μ = " + mu + " θ " + theta + " α = " + alpha + " β = " + beta);

            c.setX(R_D * Math.cos((theta + mu) / 2));
            c.setY(R_D * Math.sin((theta + mu) / 2));

            c.setR(R_D);
            c.setAngle((theta + mu) / 2);

            System.out.println("    COORDS = " + c.getIndex() + " " + c.getX() + " " + c.getY());

            if (c.getChild().size() > 0)
                radialPositions(T, c, theta, mu);

            theta = mu;
        }
    }
}
