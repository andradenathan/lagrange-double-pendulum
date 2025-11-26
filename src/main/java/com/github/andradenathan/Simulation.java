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
        return new Simulation(0.05, 20, 500, 400, 350);
    }

    public static Simulation accurate() {
        return new Simulation(0.01, 50, 1000, 400, 350);
    }

    public static Simulation faster() {
        return new Simulation(0.1, 1, 1000, 400, 350);
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
