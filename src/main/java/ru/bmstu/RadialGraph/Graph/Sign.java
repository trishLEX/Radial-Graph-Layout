package ru.bmstu.RadialGraph.Graph;

public class Sign {
    static final double SIGN_WIDTH = 7.0 * Vertex.VERTEX_WIDTH;
    static final double SIGN_HEIGHT = 3.0 * Vertex.VERTEX_HEIGHT;

    private double x, y;
    private double width;
    private double height;

    Sign(double x, double y) {
        this.width = SIGN_WIDTH;
        this.height = SIGN_HEIGHT;
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
