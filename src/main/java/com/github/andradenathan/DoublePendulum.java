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


    public DoublePendulum(Arguments arguments, Simulation simulation, Speed initialSpeed) {
        this.arguments = arguments;
        this.simulation = simulation;
        this.initialSpeed = initialSpeed.copy();
        this.currentSpeed = initialSpeed.copy();

        this.lagrange = new Lagrange(arguments);
        this.trajectory = new Trajectory(simulation.getTotalFrames());

        configure();
        start();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        computeFrame();
        updateTrajectory();
        repaint();
    }

    private void configure() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
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
        trajectory.add(position.x, position.y);
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

        drawTrajectory(g2d);
        drawPendulums(g2d);
        drawInformations(g2d);
    }

    private void drawTrajectory(Graphics2D g2d) {
        ArrayList<Point2D.Double> points = trajectory.getPoints();

        for(int point = 1; point < points.size(); point++) {
            Point2D.Double p1 = points.get(point - 1);
            Point2D.Double p2 = points.get(point);

            // calculating the gradient for transparency effect
            float alpha = (float) point / points.size();

            g2d.setColor(new Color(0, 1.0f, 1.0f, alpha * 0.8f));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
        }
    }

    private void drawPendulums(Graphics2D g2d) {
        int originX = simulation.getXOrigin();
        int originY = simulation.getYOrigin();

        Point2D.Double firstPos = calculatePendulumFirstPosition();
        Point2D.Double secondPos = calculatePendulumSecondPosition();

        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(originX, originY, (int)firstPos.x, (int)firstPos.y);
        g2d.drawLine((int)firstPos.x, (int)firstPos.y, (int)secondPos.x, (int)secondPos.y);

        g2d.setColor(Color.GRAY);
        g2d.fillOval(originX - 8, originY - 8, 16, 16);

        g2d.setColor(Color.RED);
        g2d.fillOval((int)firstPos.x - 15, (int)firstPos.y - 15, 30, 30);

        g2d.setColor(Color.GREEN);
        g2d.fillOval((int) secondPos.x - 12, (int)secondPos.y - 12, 24, 24);
    }

    private void drawInformations(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));

        int y = 20;
        int line = 20;

        g2d.drawString(String.format("Angle 1: %.2f°", currentSpeed.getTheta1InDegrees()), 10, y);
        y += line;

        g2d.drawString(String.format("Angle 2: %.2f°", currentSpeed.getTheta2InDegrees()), 10, y);
        y += line;

        double energy = lagrange.calculateEnergy(currentSpeed);
        g2d.drawString(String.format("Total Energy: %.2f J", energy), 10, y);
        y += line;

        g2d.setColor(Color.ORANGE);
        g2d.drawString("Lagrange Equations (Euler-Lagrange)", 10, y);

        g2d.setColor(Color.BLACK);
        g2d.drawString("R: Reload | Space: Pause/Resume | Esc: Close", 10, getHeight() - 10);
    }

    public void reload() {
        currentSpeed = initialSpeed.copy();
        trajectory.clear();
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
