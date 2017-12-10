package ru.bmstu.RadialGraph.Graph;

import org.joml.Vector2d;

import java.util.ArrayList;

/* x и y - координаты центра вершины */
public class Vertex {
    static final double VERTEX_WIDTH = 10.0;
    static final double VERTEX_HEIGHT = 10.0;

    private ArrayList<Vertex> child;
    private Vertex parent;
    private double x, y;
    private int index;
    private int mark;
    private boolean isRoot;
    private int depth;
    private double r;
    private double angle;
    private double width, height;
    private Sign sign;

    Vertex(int i) {
        this.child = new ArrayList<>();
        this.parent = null;
        this.x = this.y = 0;
        this.index = i;
        this.depth = 0;

        this.mark = 0;
        this.isRoot = false;

        this.r = 0;
        this.angle = 0;

        this.width = VERTEX_WIDTH;
        this.height = VERTEX_HEIGHT;

        this.sign = new Sign(x, y - height / 2);
    }

    public ArrayList<Vertex> getChild() {
            return child;
    }

    public void setChild(ArrayList<Vertex> child) {
        this.child = child;
    }

    public Vertex getParent() {
        return parent;
    }

    public void setParent(Vertex parent) {
        this.parent = parent;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
        this.sign.setX(x);
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
        this.sign.setY(y - VERTEX_HEIGHT / 2 - Sign.SIGN_HEIGHT / 2);
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public int getIndex() {
        return index;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
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

    public Sign getSign() {
        return this.sign;
    }

    private ArrayList<Double[]> getPoints() {
        double width2 = this.width / 2;
        double height2 = this.height / 2;
        double xs = this.sign.getX();
        double ys = this.sign.getY();
        double sWidth2 = this.sign.getWidth() / 2;
        double sHeight2 = this.sign.getHeight() / 2;

        ArrayList<Double[]> thisPoints = new ArrayList<>();

        thisPoints.add(new Double[]{x + width2, y + height2});
        thisPoints.add(new Double[]{x - width2, y + height2});
        thisPoints.add(new Double[]{x - width2, y - height2});
        thisPoints.add(new Double[]{x + width2, y - height2});

        thisPoints.add(new Double[]{xs - sWidth2, ys + sHeight2});
        thisPoints.add(new Double[]{xs - sWidth2, ys - sHeight2});
        thisPoints.add(new Double[]{xs + sWidth2, ys - sHeight2});
        thisPoints.add(new Double[]{xs + sWidth2, ys + sHeight2});

        return thisPoints;
    }

    @Override
    public String toString() {
        String res = " ";
        res += this.index + " (" + this.x + ", " + this.y + ") r = " + this.r + " angle = " + this.angle;
        return res;
    }

    public boolean isIn(Vertex v) {
        if (v == this)
            return false;

        ArrayList<Double[]> vPoints = v.getPoints();

        for (Double[] point: this.getPoints()) {
            double px = point[0];
            double py = point[1];
            if (px <= vPoints.get(0)[0] && px >= vPoints.get(1)[0] && py <= vPoints.get(0)[1] && py >= vPoints.get(3)[1]) {
                return true;
            }
        }

        for (Double[] point: this.getPoints()) {
            double px = point[0];
            double py = point[1];
            if (px <= vPoints.get(7)[0] && px >= vPoints.get(4)[0] && py <= vPoints.get(7)[1] && py >= vPoints.get(6)[1]) {
                return true;
            }
        }

        return false;
    }

    public void setVertexByPolar(double r, double angle) {
        this.r = r;
        this.angle = angle;
        this.setX(r * Math.cos(angle));
        this.setY(r * Math.sin(angle));

        if (Double.isNaN(this.x) || Double.isNaN(this.y))
            throw new RuntimeException(" x and y are null" + this);
    }

    public double distTo(Vertex v) {
        return Math.sqrt(Math.pow((this.getX() - v.getX()), 2) + Math.pow((this.getY() - v.getY()), 2));
    }

    public void castToCartesianCoordinates() {
        if (this.getParent() != null && this.getParent().isRoot) {
            this.setX(this.getR() * Math.cos(this.getAngle()));
            this.setY(this.getR() * Math.sin(this.getAngle()));
        }

        else {

            if (!this.isRoot) {
                double r = cosinesLaw(this.getR(), this.getParent().getR(), this.getAngle());
                double phi = this.getParent().getAngle() - Math.asin(this.getR() * Math.sin(this.getAngle()) / cosinesLaw(this.getR(), this.getParent().getR(), this.getAngle()));

                this.setVertexByPolar(r, phi);
            }
        }
    }

    private static double cosinesLaw(double a, double b, double angle) {
        return Math.sqrt(a * a + b * b - 2 * a * b * Math.cos(angle));
    }

    public void setVertexByCartesian(double x, double y) {
        this.setX(x);
        this.setY(y);

        if (x == 0 && y == 0)
            this.angle = 0;
        else if (x > 0 && y >= 0)
            this.angle = Math.atan2(y, x);
        else if (x > 0 && y < 0)
            this.angle = Math.atan2(y, x) + 2 * Math.PI;
        else if (x < 0)
            this.angle = Math.atan2(y ,x) + Math.PI;
        else if (x == 0 && y > 0)
            this.angle = Math.PI / 2;
        else
            this.angle = 3 * Math.PI / 2;

        this.r = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

        if (Double.isNaN(this.r))
            throw new RuntimeException("vertex = " + this + " r is NaN" + " x = " + x + " y = " + y);
    }

    public void moveFromParent(double offset) {
        Vector2d pc = new Vector2d(this.getX() - this.getParent().getX(), this.getY() - this.getParent().getY());
        Vector2d temp = new Vector2d(pc.x, pc.y);
        double pcLength = pc.length();

        if (pcLength == 0.0)
            throw new RuntimeException("Length is null" + this + this.getParent());

        pcLength = (pcLength + offset) / pcLength; //теперь pcLength - коэффициент растяжения
        pc.mul(pcLength);

        if (Double.isNaN(this.getParent().getX() + pc.x) || Double.isNaN(this.getParent().getY() + pc.y))
            throw new RuntimeException("NaN coordinates" + this.getParent().getX() + "+" + pc.x + " , " + this.getParent().getY() + "+" + pc.y + " this = " + this);

        this.setVertexByCartesian(this.getParent().getX() + pc.x, this.getParent().getY() + pc.y);

        pc.sub(temp);

        for (Vertex v: this.child)
            v.moveFromParent(pc);
    }

    public void moveFromParent(Vector2d vector) {
        this.setVertexByCartesian(this.x + vector.x, this.y + vector.y);
        for (Vertex v: this.child)
            v.moveFromParent(vector);
    }

    public boolean isIntersect(Vertex v) {
        return v.isIn(this) || this.isIn(v);
    }

    public void addChild(Vertex v) {
        if (!this.child.contains(v))
            this.child.add(v);
    }

    public void clear() {
        this.setVertexByCartesian(0, 0);

        if (this.getParent() != null) {
            this.addChild(this.getParent());
            this.setParent(null);
        }

        this.setDepth(0);
        this.setRoot(false);
        this.setMark(0);

        this.setWidth(Vertex.VERTEX_WIDTH);
        this.setHeight(Vertex.VERTEX_HEIGHT);

        this.getSign().setX(this.getX());
        this.getSign().setY(this.getY() - this.getHeight() / 2);
        this.getSign().setWidth(Sign.SIGN_WIDTH);
        this.getSign().setHeight(Sign.SIGN_HEIGHT);
    }
}
