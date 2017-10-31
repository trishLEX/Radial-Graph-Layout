package Algorithms;

import Graph.*;

import java.util.ArrayList;

import static Graph.Vertex.isIntersect;

class Algorithm3 {
    private static final double R = 100;
    private static final double PHI = Math.PI;
    private static final double R_OFFSET = Graph.R_OFFSET;

    private static Vertex findNearestSibling(Vertex v) {
        Vertex parent = v.getParent();
        double minAngle = Math.PI * 2;
        double delta;
        Vertex sibling = null;

        for (Vertex u: parent.getChild()) {
            if (u != v) {
                delta = makeInFirstQuarter(v.getAngle() - u.getAngle());

                if (delta < minAngle) {
                    minAngle = delta;
                    sibling = u;
                }
            }
        }

        if (sibling == null)
            throw new RuntimeException("Sibling is null");

        return sibling;
    }

    private static double makeInFirstQuarter(double angle) {
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

    private static void radialPositions(Graph tree, Vertex root) {
        int currentDepth = root.getDepth();

        if (currentDepth != 0)
            throw new RuntimeException("Depth of the root is not null");

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
                }
                else {
                    v.setAngle(Math.PI - PHI / 2 + PHI * v.getParent().getChild().indexOf(v) / v.getParent().getChild().size() + PHI / (2 * v.getParent().getChild().size()));

                    if (v.getParent().getParent().getChild().size() == 1) {
                        v.setR(v.getParent().getR()); //здесь было R / 2
                    }
                    else {
                        Vertex sibling = findNearestSibling(v.getParent());

                        double delta = makeInFirstQuarter(v.getParent().getAngle() - sibling.getAngle()) / 2;

                        v.setR(cosinesLaw(delta, v.getParent().getR()));
                    }
                }
            }
            tree.getRadials().add(v.getR());
        }
    }

    private static void deleteIntersections(Graph tree) {
        for (Vertex v: tree.getVertices()) {
            makeRadialOffsetWithoutIntersections(v, v.getChild());

            for (Vertex u: v.getChild()) {
                ArrayList<Vertex> siblingsOfU = new ArrayList<>();
                siblingsOfU.addAll(v.getChild());
                siblingsOfU.remove(u);

                makeRadialOffsetWithoutIntersections(u, siblingsOfU);
            }
        }


        for (Vertex v: tree.getVertices()){
            for (Vertex u: tree.getVertices()) {
                if (v != u && isIntersect(v, u)) {
                    makeRadialOffsetWithoutIntersections(v, u);
                }
            }
        }
    }

    private static void makeRadialOffsetWithoutIntersections(Vertex v, Vertex u) {
        Vertex tempV = v;
        Vertex tempU = u;

        if (tempV.getDepth() > tempU.getDepth())
            while (tempV.getDepth() != tempU.getDepth())
                tempV = tempV.getParent();
        else
            while (tempV.getDepth() != tempU.getDepth())
                tempU = tempU.getParent();

        Vertex vP = tempV.getParent();
        Vertex uP = tempU.getParent();

        while (vP != uP){
            tempV = vP;
            tempU = uP;
            vP = tempV.getParent();
            uP = tempU.getParent();
        }

        while (isIntersect(v, u)) {
            for (Vertex w: vP.getChild())
                w.moveFromParent(R_OFFSET);
        }
    }

    /* Смещаются вершины в vertices, v (брат или родитель) при выполнении этого алгоритма остается на месте */
    private static void makeRadialOffsetWithoutIntersections(Vertex v, ArrayList<Vertex> vertices) {
        double offset = 0.0;

        boolean wasIntersection = false;

        for (Vertex u: vertices) {
            while (isIntersect(v, u)) {
                wasIntersection = true;
                offset += R_OFFSET;
                u.moveFromParent(R_OFFSET);
            }

            if (wasIntersection) {
                for (Vertex w : vertices) {
                    if (w != u) {
                        w.moveFromParent(offset);
                    }
                }

                if (!v.getChild().contains(vertices.get(0))) {
                    v.moveFromParent(offset);
                }
            }
            wasIntersection = false;
            offset = 0.0;
            System.out.println("\n");
        }
    }

    static void useAlgorithm(Graph tree) {
        Vertex root = tree.findRoot();

        tree.calculateMaxDepth(root);

        radialPositions(tree, root);

        for (Vertex v: tree.getVertices())
            v.castToCartesianCoordinates();

        deleteIntersections(tree);

        tree.fillRadials3();
    }
}
