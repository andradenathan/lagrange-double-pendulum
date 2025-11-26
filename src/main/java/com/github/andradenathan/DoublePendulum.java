package com.github.andradenathan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class DoublePendulum extends JPanel implements ActionListener {
    private final Arguments arguments;
    private final Simulation simulation;
    private final Speed initialSpeed;

    private Speed currentSpeed;
    private final Lagrange lagrange;
    private final Trajectory trajectory;

    private Timer timer;
    private int frameCount = 0;
    private long startTime;


    private static final int SIDEBAR_WIDTH = 250;
    private static final Color BACKGROUND_COLOR = new Color(18, 18, 24);
    private static final Color SIDEBAR_COLOR = new Color(28, 28, 35);
    private static final Color GRID_COLOR = new Color(40, 40, 50);
    private static final Color TEXT_COLOR = new Color(220, 220, 230);
    private static final Color ACCENT_COLOR = new Color(100, 200, 255);


    public DoublePendulum(Arguments arguments, Simulation simulation, Speed initialSpeed) {
        this.arguments = arguments;
        this.simulation = simulation;
        this.initialSpeed = initialSpeed.copy();
        this.currentSpeed = initialSpeed.copy();

        this.lagrange = new Lagrange(arguments);
        this.trajectory = new Trajectory(simulation.getTotalFrames());

        this.startTime = System.currentTimeMillis();

        configure();
        start();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        computeFrame();
        updateTrajectory();
        frameCount++;
        repaint();
    }

    private void configure() {
        setPreferredSize(new Dimension(1050, 700));
        setBackground(BACKGROUND_COLOR);
    }

    private void start() {
        timer = new Timer(simulation.getStepsPerFrame(), this);
        timer.start();
    }

    private void computeFrame() {
        lagrange.integrate(currentSpeed, simulation.getTimeStep());
    }

    private void updateTrajectory() {
        Point2D.Double position = calculatePendulumSecondPosition();
        trajectory.addPoint(position.x, position.y);
    }

    private Point2D.Double calculatePendulumFirstPosition() {
        int xOrigin = simulation.getXOrigin();
        int yOrigin = simulation.getYOrigin();

        double l1 = arguments.length1();
        double theta1 = currentSpeed.getTheta1();

        double x = xOrigin + l1 * Math.sin(theta1);
        double y = yOrigin + l1 * Math.cos(theta1);

        return new Point2D.Double(x, y);
    }

    private Point2D.Double calculatePendulumSecondPosition() {
        Point2D.Double firstPosition = calculatePendulumFirstPosition();

        double l2 = arguments.length2();
        double theta2 = currentSpeed.getTheta2();

        double x = firstPosition.x + l2 * Math.sin(theta2);
        double y = firstPosition.y + l2 * Math.cos(theta2);

        return new Point2D.Double(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawBackground(g2d);
        drawSidebar(g2d);
        drawTrajectory(g2d);
        drawPendulums(g2d);
        drawControls(g2d);
    }

    private void drawBackground(Graphics2D g2d) {
        g2d.setColor(GRID_COLOR);
        g2d.setStroke(new BasicStroke(1));

        int gridSize = 50;
        for (int x = 0; x < getWidth() - SIDEBAR_WIDTH; x += gridSize) {
            g2d.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y < getHeight(); y += gridSize) {
            g2d.drawLine(0, y, getWidth() - SIDEBAR_WIDTH, y);
        }


        int originX = simulation.getXOrigin();
        int originY = simulation.getYOrigin();
        g2d.setColor(new Color(60, 60, 80, 100));
        g2d.fillOval(originX - 100, originY - 100, 200, 200);
    }

    private void drawSidebar(Graphics2D g2d) {
        int sidebarX = getWidth() - SIDEBAR_WIDTH;

        g2d.setColor(SIDEBAR_COLOR);
        g2d.fillRect(sidebarX, 0, SIDEBAR_WIDTH, getHeight());

        g2d.setColor(ACCENT_COLOR);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(sidebarX, 0, sidebarX, getHeight());

        int x = sidebarX + 15;
        int y = 30;
        int lineHeight = 25;

        g2d.setColor(ACCENT_COLOR);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2d.drawString("Double Pendulum", x, y);
        y += 35;

        g2d.setColor(GRID_COLOR);
        g2d.fillRect(x, y, SIDEBAR_WIDTH - 30, 2);
        y += 20;

        g2d.setColor(ACCENT_COLOR);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2d.drawString("Parameters", x, y);
        y += 20;

        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2d.drawString(String.format("Mass 1: %.2f kg", arguments.mass1()), x, y);
        y += lineHeight;
        g2d.drawString(String.format("Mass 2: %.2f kg", arguments.mass2()), x, y);
        y += lineHeight;
        g2d.drawString(String.format("Length 1: %.1f m", arguments.length1() / 100), x, y);
        y += lineHeight;
        g2d.drawString(String.format("Length 2: %.1f m", arguments.length2() / 100), x, y);
        y += lineHeight;
        g2d.drawString(String.format("Gravity: %.2f m/s²", arguments.gravity()), x, y);
        y += 30;

        g2d.setColor(ACCENT_COLOR);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2d.drawString("Current State", x, y);
        y += 20;

        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2d.drawString(String.format("θ₁: %.2f°", currentSpeed.getTheta1InDegrees()), x, y);
        y += lineHeight;
        g2d.drawString(String.format("θ₂: %.2f°", currentSpeed.getTheta2InDegrees()), x, y);
        y += lineHeight;
        g2d.drawString(String.format("ω₁: %.2f rad/s", currentSpeed.getOmega1()), x, y);
        y += lineHeight;
        g2d.drawString(String.format("ω₂: %.2f rad/s", currentSpeed.getOmega2()), x, y);
        y += 30;

        g2d.setColor(ACCENT_COLOR);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2d.drawString("Energy", x, y);
        y += 20;

        double energy = lagrange.calculateEnergy(currentSpeed);
        g2d.setColor(new Color(100, 255, 150));
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2d.drawString(String.format("Total: %.3f J", energy), x, y);
        y += 30;

        g2d.setColor(ACCENT_COLOR);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2d.drawString("Simulation", x, y);
        y += 20;

        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2d.drawString(String.format("Frames: %d", frameCount), x, y);
        y += lineHeight;
        g2d.drawString(String.format("dt: %.3f s", simulation.getTimeStep()), x, y);
        y += lineHeight;
        g2d.drawString(String.format("Points: %d", trajectory.getPoints().size()), x, y);
        y += lineHeight;

        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        g2d.drawString(String.format("Time: %02d:%02d", elapsed / 60, elapsed % 60), x, y);
        y += 30;

        String status = isPaused() ? "PAUSED" : "RUNNING";
        g2d.setColor(isPaused() ? new Color(255, 150, 100) : new Color(100, 255, 150));
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2d.drawString(status, x, y);
    }

    private void drawTrajectory(Graphics2D g2d) {
        ArrayList<Point2D.Double> points = trajectory.getPoints();

        if (points.size() < 2) return;

        for(int point = 1; point < points.size(); point++) {
            Point2D.Double p1 = points.get(point - 1);
            Point2D.Double p2 = points.get(point);

            float progress = (float) point / points.size();
            float alpha = progress * 0.9f;

            int r = (int) (0 + progress * 255);
            int g = (int) (200 - progress * 100);
            int b = (int) (255 - progress * 100);

            g2d.setColor(new Color(r, g, b, (int)(alpha * 255)));
            g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
        }
    }

    private void drawPendulums(Graphics2D g2d) {
        int originX = simulation.getXOrigin();
        int originY = simulation.getYOrigin();

        Point2D.Double firstPos = calculatePendulumFirstPosition();
        Point2D.Double secondPos = calculatePendulumSecondPosition();

        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.setStroke(new BasicStroke(5));
        g2d.drawLine(originX + 3, originY + 3, (int)firstPos.x + 3, (int)firstPos.y + 3);
        g2d.drawLine((int)firstPos.x + 3, (int)firstPos.y + 3, (int)secondPos.x + 3, (int)secondPos.y + 3);

        g2d.setColor(new Color(200, 200, 210));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawLine(originX, originY, (int)firstPos.x, (int)firstPos.y);
        g2d.drawLine((int)firstPos.x, (int)firstPos.y, (int)secondPos.x, (int)secondPos.y);

        drawBall(g2d, originX, originY, 10, new Color(150, 150, 160));

        drawBall(g2d, (int)firstPos.x, (int)firstPos.y, 18, new Color(255, 100, 100));

        drawBall(g2d, (int)secondPos.x, (int)secondPos.y, 15, new Color(100, 255, 150));
    }

    private void drawBall(Graphics2D g2d, int x, int y, int radius, Color color) {
        g2d.setColor(new Color(0, 0, 0, 60));
        g2d.fillOval(x - radius + 3, y - radius + 3, radius * 2, radius * 2);

        GradientPaint gradient = new GradientPaint(
            x - radius, y - radius, color.brighter(),
            x + radius, y + radius, color.darker()
        );
        g2d.setPaint(gradient);
        g2d.fillOval(x - radius, y - radius, radius * 2, radius * 2);

        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.fillOval(x - radius + 3, y - radius + 3, radius / 2, radius / 2);

        g2d.setColor(color.darker().darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    private void drawControls(Graphics2D g2d) {
        int y = getHeight() - 20;

        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(10, y - 30, 450, 40, 10, 10);

        g2d.setColor(ACCENT_COLOR);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2d.drawString("R", 20, y - 8);
        g2d.drawString("SPACE", 120, y - 8);
        g2d.drawString("ESC", 310, y - 8);

        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2d.drawString("Restart", 40, y - 8);
        g2d.drawString("Pause/Resume", 180, y - 8);
        g2d.drawString("Close", 345, y - 8);
    }

    public void reload() {
        currentSpeed = initialSpeed.copy();
        trajectory.clear();
        frameCount = 0;
        startTime = System.currentTimeMillis();
    }

    public void togglePause() {
        if (isPaused()) {
            timer.start();
        } else {
            timer.stop();
        }
    }

    private boolean isPaused() {
        return !timer.isRunning();
    }
}
