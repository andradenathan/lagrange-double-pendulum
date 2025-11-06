package com.github.andradenathan;

public class Simulation {
    private final double timeStep;
    private final int stepsPerFrame;
    private final int totalFrames;
    private final int xOrigin;
    private final int yOrigin;

    public Simulation(double timeStep, int stepsPerFrame, int totalFrames, int xOrigin, int yOrigin) {
        this.timeStep = timeStep;
        this.stepsPerFrame = stepsPerFrame;
        this.totalFrames = totalFrames;
        this.xOrigin = xOrigin;
        this.yOrigin = yOrigin;
    }

    public static Simulation usingDefault() {
        return new Simulation(0.05, 20, 500, 400, 200);
    }

    public static Simulation accurate() {
        return new Simulation(0.01, 50, 1000, 400, 200);
    }

    public static Simulation faster() {
        return new Simulation(0.1, 30, 300, 400, 200);
    }

    private void validate(double dt, int interval, int frames) {
        if (dt <= 0) {
            throw new IllegalArgumentException("Time step must be positive.");
        }
        if (interval <= 0) {
            throw new IllegalArgumentException("Steps per frame must be positive.");
        }
        if (frames <= 0) {
            throw new IllegalArgumentException("Total frames must be positive.");
        }
    }

    public double getTimeStep() {
        return timeStep;
    }

    public int getStepsPerFrame() {
        return stepsPerFrame;
    }

    public int getTotalFrames() {
        return totalFrames;
    }

    public int getXOrigin() {
        return xOrigin;
    }

    public int getYOrigin() {
        return yOrigin;
    }

    @Override
    public String toString() {
        return String.format("dt=%.3f, intervalo=%dms, maxPontos=%d, origem=(%d,%d)",
                timeStep, stepsPerFrame, totalFrames, xOrigin, yOrigin);
    }
}
