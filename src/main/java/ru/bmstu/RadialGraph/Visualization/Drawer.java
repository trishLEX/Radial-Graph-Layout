package ru.bmstu.RadialGraph.Visualization;

import org.lwjgl.glfw.GLFWKeyCallback;
import ru.bmstu.RadialGraph.Algorithms.Calculation;
import ru.bmstu.RadialGraph.Graph.*;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

class Drawer {
    private static final int NUMBER_OF_SIDES = 50;

    private static final double[] GRAY  = {0.8, 0.8, 0.8};
    private static final double[] RED   = {1.0, 0.0, 0.0};
    private static final double[] BLACK = {0.0, 0.0, 0.0};
    private static final double[] WHITE = {1.0, 1.0, 1.0};

    private static final String NAME = "Radial Graph";
    private static final int WIDTH = Calculation.WIDTH;
    private static final int HEIGHT = Calculation.HEIGHT;

    private long window;

    private Graph graph;

    private int type;

    private boolean toDrawRadials = true;
    private boolean toDrawDeleted = false;

    private void background() {
        glClearColor(1, 1, 1, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
    }

    Drawer(Graph graph, int type) {
        this.graph = graph;
        this.type = type;

        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("unable to initialize GLFW");

        System.out.println("WINDOW width = " + WIDTH + " height = " + HEIGHT);
        this.window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, NAME, 0, 0);

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
        }));
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

        if ((type == 5 || type == 1) && toDrawRadials) {
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
