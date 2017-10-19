package Algorithms;

import Graph.*;
import Visualization.GraphVisualization;

class Algorithm5 {
    private static int maxDepth = 0;
    private static double R;
    private static final int WIDTH = GraphVisualization.WIDTH;
    private static final int HEIGHT = GraphVisualization.HEIGHT;

    static void useAlgorithm(Graph tree) {
        Vertex root = null;

        for (Vertex v: tree.getVertices()) {
            if (v.isRoot()) {
                root = v;
                break;
            }
        }

        if (root == null)
            throw new RuntimeException("ERROR root is null");

        calculateDepth(root, 0);

        System.out.println(tree);

        R = ((WIDTH < HEIGHT? WIDTH : HEIGHT) / 2) / maxDepth; //раньше радиус был константный

        tree.getRadials().add(R / (WIDTH < HEIGHT? WIDTH : HEIGHT));

        radialPositions(tree, root, 0, 2 * Math.PI);
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
        }

        int D = v.getDepth();
        System.out.println("D = " + D + " v = " + v.getIndex());

        System.out.println("α = " + alpha + " β = " + beta);
        double theta = alpha;

        double R_D = R + (R * D);
        System.out.println("R_D " + R_D);
        if (!T.getRadials().contains(R_D))
            T.getRadials().add(R_D / (WIDTH < HEIGHT? WIDTH : HEIGHT));

        int k = leavesCounter(v);
        System.out.println("LEAVES COUNTER K = " + k);
        leavesCount = 0;

        for (Vertex c: v.getChild()) {
            int lambda = leavesCounter(c);
            System.out.println("child = " + c.getIndex());
            System.out.println("    LEAVES COUNTER λ = " + lambda);
            leavesCount = 0;

            double mu = theta + ((beta - alpha) * lambda / k);
            System.out.println("    μ = " + mu + " θ " + theta + " α = " + alpha + " β = " + beta);

            c.setX(R_D * Math.cos((theta + mu) / 2));
            c.setY(R_D * Math.sin((theta + mu) / 2));

            System.out.println("    COORDS = " + c.getX() + " " + c.getY());

            if (c.getChild().size() > 0)
                radialPositions(T, c, theta, mu);

            theta = mu;
        }
    }
}
