package ru.bmstu.RadialGraph.Algorithms;

public class Matrix {
    private double[][] matrix;
    private int size;

    public Matrix(int n) {
        this.matrix = new double[n][n];
        this.size = n;
    }

    public void set(int i, int j, double x) {
        this.matrix[i][j] = x;
    }

    public double get(int i, int j) {
        return this.matrix[i][j];
    }

    public void setRow(int i, double[] row) {
        this.matrix[i] = row;
    }

    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < size; i++) {
            res += "(";
            for (int j = 0; j < size; j++) {
                res += " " + matrix[i][j];
            }
            res += " )\n";
        }
        return res;
    }
}