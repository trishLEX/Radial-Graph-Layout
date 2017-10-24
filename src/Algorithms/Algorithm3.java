package Algorithms;

import Graph.*;
import Visualization.GraphVisualization;

class Algorithm3 {
    private static final double R = 200;
    private static final double PHI = Math.PI;
    private static final int WIDTH = GraphVisualization.WIDTH;
    private static final int HEIGHT = GraphVisualization.HEIGHT;

    //TODO можно отсортировать по возрастанию угла и тогда брать проосто по индексу
    private static Vertex findNearestSibling(Vertex v) {
        Vertex parent = v.getParent();
        double minAngle = Math.PI * 2;
        double delta;
        Vertex sibling = null;

        for (Vertex u: parent.getChild()) {
            if (u != v) {
                delta = makeInFirstQuarter(v.getAngle() - u.getAngle());
                System.out.println("SIBLINGing v = " + v.getIndex() + " u = " + u.getIndex() + " delta = " + delta);

                if (delta < minAngle) {
                    minAngle = delta;
                    sibling = u;
                }
            }
        }

        if (sibling == null)
            throw new RuntimeException("Sibling is null");

        System.out.println("SIBLING v = " + v.getIndex() + " sibling = " + sibling.getIndex());
        return sibling;
    }

    static double makeInFirstQuarter(double angle) {
        if (angle <= 0) {
            while (angle <= 0)
                angle += Math.PI;
        }
        else {
            if (angle > Math.PI) {
                while (angle > Math.PI)
                    angle -= Math.PI;
            }
        }

        return angle;
    }

    private static double cosinesLaw(double angle, double side) {
        return Math.sqrt(2 * side * side - 2 * side * side * Math.cos(angle));
    }

    private static double cosinesLaw(double a, double b, double angle) {
        return Math.sqrt(a * a + b * b - 2 * a * b * Math.cos(angle));
    }

    static void useAlgorithm(Graph tree) {
        Vertex root = null;

        for (Vertex v: tree.getVertices()) {
            if (v.isRoot()) {
                root = v;
                break;
            }
        }

        if (root == null)
            throw new RuntimeException("Root is null");

        for (Vertex v: tree.getVertices()) {
            if (v.isRoot()) {
                root.setX(0);
                root.setY(0);
                root.setR(R);
            }
            else {
                if (v.getParent() == root) {
                    v.setAngle(2 * Math.PI * root.getChild().indexOf(v) / root.getChild().size());
                    v.setR(R);
                    System.out.println("v = " + v.getIndex() + " polar coords = (" + v.getAngle() + ", " + v.getR() + ")");
                }
                else {
                    v.setAngle(Math.PI - PHI / 2 + PHI * v.getParent().getChild().indexOf(v) / v.getParent().getChild().size() + PHI / (2 * v.getParent().getChild().size()));

                    if (v.getParent().getParent().getChild().size() == 1) {
                        v.setR(v.getParent().getR()); //здесь было R / 2
                        System.out.println("v = " + v.getIndex() + " polar coords = (" + v.getAngle() + ", " + v.getR() + ")");
                    }
                    else {
                        Vertex sibling = findNearestSibling(v.getParent());

                        System.out.println("v = " + v.getIndex() + " v.parent = " + v.getParent().getIndex() + " v.parent's sibling = " + sibling.getIndex());
                        System.out.println("deltaOld = " + (v.getParent().getAngle() - sibling.getAngle()));

                        double delta = makeInFirstQuarter(v.getParent().getAngle() - sibling.getAngle()) / 2;

                        System.out.println("r = " + cosinesLaw(delta, v.getParent().getR()) + " delta = " + delta);

                        v.setR(cosinesLaw(delta, v.getParent().getR()));

                        System.out.println("v = " + v.getIndex() + " polar coords = (" + v.getAngle() + ", " + v.getR() + ")");
                    }
                }
            }
            tree.getRadials().add(v.getR() / (WIDTH < HEIGHT? WIDTH : HEIGHT));
        }

        for (Vertex v: tree.getVertices()) {

            if (v.getParent() == root) {
                v.setX(v.getR() * Math.cos(v.getAngle()));
                v.setY(v.getR() * Math.sin(v.getAngle()));
            }

            else {

                if (v != root) {
                    double r = cosinesLaw(v.getR(), v.getParent().getR(), v.getAngle());
                    double phi = v.getParent().getAngle() - Math.asin(v.getR() * Math.sin(v.getAngle()) / cosinesLaw(v.getR(), v.getParent().getR(), v.getAngle()));

                    System.out.println("new coords v = " + v.getIndex() + " (" + r + ", " + phi + ")" + " r = " + v.getR() + " angle = " + v.getAngle());

                    v.setX(r * Math.cos(phi));
                    v.setY(r * Math.sin(phi));
                    v.setAngle(phi);
                    v.setR(r);
                }
            }
        }

        for (Vertex v: tree.getVertices()) {
            tree.getVertices().get(v.getIndex()).setX(v.getX());
            tree.getVertices().get(v.getIndex()).setY(v.getY());
        }
    }
}
