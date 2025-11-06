package com.github.andradenathan;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Trajectory {
    private final ArrayList<Point2D.Double> points;
    private final int maxPoints;

    public Trajectory(int maxPoints) {
        this.points = new ArrayList<>();
        this.maxPoints = maxPoints;
    }

    public void add(double x, double y) {
        points.add(new Point2D.Double(x,y));

        if (points.size() > maxPoints) {
            points.remove(0);
        }
    }

    public ArrayList<Point2D.Double> getPoints() {
        return points;
    }

    public void clear() {
        points.clear();
    }

    public int size() {
        return points.size();
    }

    public boolean isEmpty() {
        return points.isEmpty();
    }

    public Point2D.Double getLastPoint() {
        if (points.isEmpty()) {
            return null;
        }

        return points.get(points.size() - 1);
    }
}
