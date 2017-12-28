package ru.bmstu.RadialGraph.Visualization;

import org.joml.Vector2d;
import org.lwjgl.glfw.*;
import ru.bmstu.RadialGraph.Graph.*;

import org.lwjgl.opengl.GL;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

class Drawer {
    private static final int NUMBER_OF_SIDES = 50;
    private static final int MAX_SIZE = GraphVisualization.MAX_SIZE;

    private static final int ALGORITHM_TYPE_1 = 1;
    private static final int ALGORITHM_TYPE_2 = 2;
    private static final int ALGORITHM_TYPE_3 = 3;
    private static final int ALGORITHM_TYPE_4 = 4;

    private static final double[] GRAY  = {0.8, 0.8, 0.8};
    private static final double[] RED   = {1.0, 0.0, 0.0};
    private static final double[] BLACK = {0.0, 0.0, 0.0};
    private static final double[] WHITE = {1.0, 1.0, 1.0};

    private static final String NAME = "Radial Graph";

    private long window;
    private int windowSize = GraphVisualization.SIZE;

    private Graph graph;
    private int type;

    private double cursorX;
    private double cursorY;

    private boolean toDrawRadials = true;
    private boolean toDrawDeleted = false;
    private boolean toDrawSigns = true;
    private boolean isRedraw = false;

    private void background() {
        glClearColor(1, 1, 1, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
    }

    Drawer(Graph graph, int type) {
        this.graph = graph;
        this.type = type;

        this.cursorX = 0;
        this.cursorY = 0;

        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        final int SIZE = this.getWindowSize();

        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        this.window = GLFW.glfwCreateWindow(SIZE, SIZE, NAME, 0, 0);

        if (window == 0) {
            throw new RuntimeException("Failed to create window");
        }

        GLFW.glfwMakeContextCurrent(window);

        GL.createCapabilities();

        glfwSetKeyCallback(window, GLFWKeyCallback.create((window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_R && action == GLFW_PRESS) {
                toDrawRadials = !toDrawRadials;
            }
            else if (key == GLFW_KEY_D && action == GLFW_PRESS) {
                toDrawDeleted = !toDrawDeleted;
            }
            else if (key == GLFW_KEY_1 && action == GLFW_PRESS) {
                this.type = ALGORITHM_TYPE_1;
                this.isRedraw = true;
                this.graph.rebuild(this.type);
                convertCoordinates(true, this.type);
            }
            else if (key == GLFW_KEY_2 && action == GLFW_PRESS) {
                this.type = ALGORITHM_TYPE_2;
                this.isRedraw = true;
                this.graph.rebuild(this.type);
                convertCoordinates(true, this.type);
            }
            else if (key == GLFW_KEY_3 && action == GLFW_PRESS) {
                this.type = ALGORITHM_TYPE_3;
                this.isRedraw = true;
                this.graph.rebuild(this.type);
                convertCoordinates(true, this.type);
            }
            else if (key == GLFW_KEY_4 && action == GLFW_PRESS) {
                this.type = ALGORITHM_TYPE_4;
                this.isRedraw = true;
                this.graph.rebuild(this.type);
                convertCoordinates(true, this.type);
            }
            else if (key == GLFW_KEY_S && action == GLFW_PRESS) {
                toDrawSigns = !toDrawSigns;
            }
        }));

        glfwSetCursorPosCallback(window, GLFWCursorPosCallback.create((window, xpos, ypos) -> {
            this.cursorX = (xpos - SIZE / 2) / SIZE * 2;
            this.cursorY = (SIZE / 2 - ypos) / SIZE * 2;
        }));

        glfwSetMouseButtonCallback(window, GLFWMouseButtonCallback.create((window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                if (this.graph.rebuild(cursorX, cursorY, this.type)) {
                    this.isRedraw = true;
                    convertCoordinates(true, this.type);
                }
            }
        }));
    }

    private int getWindowSize() {
        convertCoordinates(isRedraw, type);
        return windowSize;
    }

    private void convertCoordinates(boolean isRedraw, int type) {
        calculateWidthAndHeight(isRedraw, type);

        for (Vertex v: graph.getVertices()) {
            double sx = 0;
            double sy = 0;

            if (graph.isSigns()) {
                sx = v.getSign().getX();
                sy = v.getSign().getY();
            }

            v.setX(v.getX() / windowSize * 2);
            v.setY(v.getY() / windowSize * 2);
            v.setWidth(v.getWidth() / windowSize * 2);
            v.setHeight(v.getHeight() / windowSize * 2);

            if (graph.isSigns()) {
                v.getSign().setX(sx / windowSize * 2);
                v.getSign().setY(sy / windowSize * 2);
                v.getSign().setWidth(v.getSign().getWidth() / windowSize * 2);
                v.getSign().setHeight(v.getSign().getHeight() / windowSize * 2);
            }
        }

        for (int i = 0; i < graph.getRadials().size(); i++) {
            double r = graph.getRadials().get(i) / windowSize * 2;
            graph.getRadials().set(i, r);
        }
    }

    private void calculateWidthAndHeight(boolean isRedraw, int type) {
        double[] corners = findCorners();

        double up = corners[0];
        double down = corners[1];
        double right = corners[2];
        double left = corners[3];
        double width = corners[4];
        double height = corners[5];

        if (width > windowSize || height > windowSize) {
            double side = width > height ? width : height;

            if (!isRedraw) {
                windowSize = (int) side + 150;

                if (side > MAX_SIZE)
                    windowSize = MAX_SIZE;
            }

            double resizeCoeff = windowSize / side;

            resizeCoords(resizeCoeff, type);
        }

        corners = findCorners();
        up = corners[0];
        down = corners[1];
        right = corners[2];
        left = corners[3];

        Vector2d vectorToCenter = new Vector2d(-(left + right) / 2, -(down + up) / 2);
        graph.translate(vectorToCenter);
    }

    private double[] findCorners() {
        double up    = Double.NEGATIVE_INFINITY;
        double down  = Double.POSITIVE_INFINITY;
        double right = up;
        double left  = down;

        for (Vertex v: graph.getVertices()) {
            if (graph.isSigns()) {
                up = Math.max(v.getY() + v.getHeight() / 2, up);
                down = Math.min(v.getSign().getY() - v.getSign().getHeight() / 2, down);
                right = Math.max(v.getSign().getX() + v.getSign().getWidth() / 2, right);
                left = Math.min(v.getSign().getX() - v.getSign().getWidth() / 2, left);
            } else {
                up = Math.max(v.getY() + v.getHeight() / 2, up);
                down = Math.min(v.getY() - v.getHeight() / 2, down);
                right = Math.max(v.getX() + v.getWidth() / 2, right);
                left = Math.min(v.getX() - v.getWidth() / 2, left);
            }
        }

        return new double[] {up + 5, down - 5, right + 5, left - 5, right - left + 10, up - down + 10};
    }

    private void resizeCoords(double coefficient, int type) {
        for (Vertex v : graph.getVertices()) {
            v.setVertexByCartesian(v.getX() * coefficient, v.getY() * coefficient);
            v.setHeight(v.getHeight() * coefficient);
            v.setWidth(v.getWidth() * coefficient);

            if (graph.isSigns()) {
                v.getSign().setHeight(v.getSign().getHeight() * coefficient);
                v.getSign().setWidth(v.getSign().getWidth() * coefficient);
            }
        }

        if (type == 3)
            graph.fillRadialsByParentCentered();
        else
            graph.fillRadialsByConcentricCircle();
    }

    private void drawQuads(double x, double y, double width, double height) {
        glVertex2d(x + width / 2, y + height / 2);
        glVertex2d(x - width / 2, y + height / 2);
        glVertex2d(x - width / 2, y - height / 2);
        glVertex2d(x + width / 2, y - height / 2);
    }

    private void drawVertex(Vertex v) {
        glLineWidth(1);

        double x = v.getX();
        double y = v.getY();
        double width = v.getWidth();
        double height = v.getHeight();

        glBegin(GL_LINE_LOOP);
        {
            glColor3dv(BLACK);
            drawQuads(x, y, width, height);
        }
        glEnd();

        glBegin(GL_QUADS);
        {
            glColor3dv(RED);
            drawQuads(x, y, width, height);
        }
        glEnd();

        if (toDrawSigns && v.getSign() != null) {
            Sign sign = v.getSign();
            double sx = sign.getX();
            double sy = sign.getY();
            double sWidth = sign.getWidth();
            double sHeight = sign.getHeight();


            glBegin(GL_LINE_LOOP);
            {
                glColor3dv(BLACK);
                drawQuads(sx, sy, sWidth, sHeight);
            }
            glEnd();

            glBegin(GL_QUADS);
            {
                glColor3dv(WHITE);
                drawQuads(sx, sy, sWidth, sHeight);
            }
            glEnd();
        }
    }

    private void drawLine(Vertex v1, Vertex v2) {
        glLineWidth(2);
        glBegin(GL_LINES);
        {
            glColor3dv(BLACK);
            glVertex2d(v1.getX(), v1.getY());
            glVertex2d(v2.getX(), v2.getY());
        }
        glEnd();
    }

    private void drawGraph(Graph graph) {
        for (Vertex v: graph.getVertices()) {
            for (Vertex u: v.getChild()) {
                drawVertex(u);
            }
            drawVertex(v);

            for (Vertex u: v.getChild()) {
                drawLine(v, u);
            }

            if (type == 3 && toDrawRadials) {
                drawCircles(v);
            }
        }

        if (toDrawDeleted) {
            for (Vertex[] deletedConnection: graph.getDeleted()) {
                drawLine(deletedConnection[0], deletedConnection[1]);
            }
        }
    }

    private void drawCircles() {
        glColor3dv(GRAY);
        for (Double r: graph.getRadials()) {
            glBegin(GL_LINE_LOOP);
            {
                for (int i = 0; i < NUMBER_OF_SIDES; i++) {
                    glVertex2d(this.graph.getRoot().getX() + r * cos(i * PI * 2 / NUMBER_OF_SIDES),
                               this.graph.getRoot().getY() + r * sin(i * PI * 2 / NUMBER_OF_SIDES));
                }
            }
            glEnd();
        }
    }

    private void drawCircles(Vertex v){
        int k = v.getIndex();

        glLineWidth(1);
        glColor3dv(GRAY);

        glBegin(GL_LINE_LOOP);
        {
            for (int i = 0; i < NUMBER_OF_SIDES; i++) {
                glVertex2d(v.getX() + graph.getRadials().get(k) * cos(i * PI * 2 / NUMBER_OF_SIDES),
                           v.getY() + graph.getRadials().get(k) * sin(i * PI * 2 / NUMBER_OF_SIDES));
            }
        }
        glEnd();
    }

    private void draw() {
        glPointSize(1);

        drawGraph(this.graph);

        if ((type == 4 || type == 1 || type == 2) && toDrawRadials) {
            glLineWidth(1);
            drawCircles();
        }
    }

    void startLoop(){
        while (!GLFW.glfwWindowShouldClose(this.window)) {
            background();

            draw();

            GLFW.glfwSwapBuffers(this.window);
            GLFW.glfwPollEvents();
        }
    }
}
