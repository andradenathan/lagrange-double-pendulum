package com.github.andradenathan;

public class Speed {
    private double theta1;
    private double theta2;
    private double omega1;
    private double omega2;

    public Speed(double theta1, double theta2, double omega1, double omega2) {
        this.theta1 = theta1;
        this.theta2 = theta2;
        this.omega1 = omega1;
        this.omega2 = omega2;
    }

    public static Speed usingDefault() {
        return new Speed(Math.PI / 2, Math.PI / 2, 0.0, 0.0);
    }

    public static Speed usingAngles(double theta1InRadians, double theta2InRadians) {
        return new Speed(Math.toRadians(theta1InRadians), Math.toRadians(theta2InRadians), 0.0, 0.0);
    }

    public void update(double newTheta1, double newTheta2, double newOmega1, double newOmega2) {
        this.theta1 = newTheta1;
        this.theta2 = newTheta2;
        this.omega1 = newOmega1;
        this.omega2 = newOmega2;
    }

    public Speed copy() {
        return new Speed(this.theta1, this.theta2, this.omega1, this.omega2);
    }


    public double getTheta1() {
        return theta1;
    }

    public double getTheta2() {
        return theta2;
    }

    public double getOmega1() {
        return omega1;
    }

    public double getOmega2() {
        return omega2;
    }

    public double getTheta1InDegrees() {
        return Math.toDegrees(theta1);
    }

    public double getTheta2InDegrees() {
        return Math.toDegrees(theta2);
    }

    public void setOmega1(double omega1) {
        this.omega1 = omega1;
    }

    public void setOmega2(double omega2) {
        this.omega2 = omega2;
    }

    public void setTheta1(double theta1) {
        this.theta1 = theta1;
    }

    public void setTheta2(double theta2) {
        this.theta2 = theta2;
    }


}
