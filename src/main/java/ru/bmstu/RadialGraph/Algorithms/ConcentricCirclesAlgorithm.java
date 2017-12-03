package ru.bmstu.RadialGraph.Algorithms;

import ru.bmstu.RadialGraph.Graph.*;
import ru.bmstu.RadialGraph.Visualization.GraphVisualization;

import java.util.ArrayList;

import static ru.bmstu.RadialGraph.Graph.Vertex.isIntersect;

public final class ConcentricCirclesAlgorithm {
    private static double R;
    private static final int SIZE = GraphVisualization.SIZE;
    private static final double RADIAL_COEFFICIENT = 0.9;
    private static final double R_OFFSET = Graph.R_OFFSET;

    private static void deleteIntersections(Graph tree) {
        for (Vertex v: tree.getVertices()) {
            ArrayList<Vertex> currentDepthWithoutV = new ArrayList<>();
            currentDepthWithoutV.addAll(tree.getVerticesByDepth(v.getDepth()));
            currentDepthWithoutV.remove(v);

            makeRadialOffsetWithoutIntersections(v, currentDepthWithoutV, tree);

            if (v.getDepth() != tree.getMaxDepth())
                makeRadialOffsetWithoutIntersections(v, tree.getVerticesByDepth(v.getDepth() + 1), tree);
        }
    }

    private static void makeRadialOffsetWithoutIntersections(Vertex v, ArrayList<Vertex> vertices, Graph tree) {
        double offset = 0.0;

        boolean wasIntersection = false;

        for (Vertex u: vertices) {
            while (isIntersect(v, u)) {
                wasIntersection = true;
                offset += R_OFFSET;
                u.setVertexByPolar(u.getR() + R_OFFSET, u.getAngle());

                if (u.getDepth() == v.getDepth())
                    v.setVertexByPolar(v.getR() + R_OFFSET, v.getAngle());
            }

            if (wasIntersection) {
                for (Vertex w: vertices) {
                    if (w != u) {
                        w.setVertexByPolar(w.getR() + offset, w.getAngle());
                    }
                }

                for (int i = u.getDepth() + 1; i <= tree.getMaxDepth(); i++)
                    for (Vertex q: tree.getVerticesByDepth(i))
                        q.setVertexByPolar(q.getR() + offset, q.getAngle());
            }

            wasIntersection = false;
            offset = 0.0;
        }
    }

    private static void addFirstRadii(Graph tree) {
        R = SIZE / tree.getMaxDepth() / 2 * RADIAL_COEFFICIENT; //раньше радиус был константный
        //R = 50.0;
        //tree.getRadials().add(R);
    }

    private static int leavesCount = 0;

    private static int leavesCounter(Vertex root) {
        leavesCount = 0;
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

        for (Vertex c: v.getChild()) {
            int lambda = leavesCounter(c);

            double mu = theta + ((beta - alpha) * lambda / k);

            c.setVertexByPolar(R_D, (theta + mu) / 2);

            if (c.getChild().size() > 0)
                radialPositions(T, c, theta, mu);

            theta = mu;
        }
    }

    public static void useAlgorithm(Graph tree) {
        Vertex root = tree.getRoot();

        System.out.println("Root is found: " + root);

        tree.calculateMaxDepth(root);

        System.out.println("Max depth is found: " + tree.getMaxDepth());

        addFirstRadii(tree);

        radialPositions(tree, root, 0, 2 * Math.PI);

        System.out.println("Positions are calculated");

        deleteIntersections(tree);

        System.out.println("Intersections are deleted");

        tree.fillRadialsByConcentricCircle();
    }
}
