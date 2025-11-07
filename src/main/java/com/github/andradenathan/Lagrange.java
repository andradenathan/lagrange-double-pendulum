package com.github.andradenathan;

public class Lagrange {
    private final Arguments arguments;

    public Lagrange(Arguments arguments) {
        this.arguments = arguments;
    }


    public double[] calculateAccelerations(Speed speed) {
        double theta1 = speed.getTheta1();
        double theta2 = speed.getTheta2();
        double omega1 = speed.getOmega1();
        double omega2 = speed.getOmega2();

        double mass1 = arguments.mass1();
        double mass2 = arguments.mass2();
        double length1 = arguments.length1();
        double length2 = arguments.length2();
        double gravity = arguments.gravity();

        double delta = theta2 - theta1;
        double cosDelta = Math.cos(delta);
        double sinDelta = Math.sin(delta);

        double numerator1 = -gravity * (2 * mass1 + mass2) * Math.sin(theta1)
                - mass2 * gravity * Math.sin(theta1 - 2 * theta2)
                - 2 * sinDelta * mass2 * (omega2 * omega2 * length2
                + omega1 * omega1 * length1 * cosDelta);

        double numerator2 = 2 * sinDelta * (omega1 * omega1 * length1 * (mass1 + mass2)
                + gravity * (mass1 + mass2) * Math.cos(theta1)
                + omega2 * omega2 * length2 * mass2 * cosDelta);

        double denominator1 = length1 * (2 * mass1 + mass2 - mass2 * Math.cos(2 * delta));
        double denominator2 = length2 * (2 * mass1 + mass2 - mass2 * Math.cos(2 * delta));


        return new double[] { numerator1 / denominator1, numerator2 / denominator2 };
    }

    public void integrate(Speed speed, double timeStep) {
        double[] accelerations = calculateAccelerations(speed);

        double newOmega1 = speed.getOmega1() + accelerations[0] * timeStep;
        double newOmega2 = speed.getOmega2() + accelerations[1] * timeStep;

        double newTheta1 = speed.getTheta1() + newOmega1 * timeStep;
        double newTheta2 = speed.getTheta2() + newOmega2 * timeStep;

        speed.update(newTheta1, newTheta2, newOmega1, newOmega2);
    }

    public double calculateEnergy(Speed speed) {
        double theta1 = speed.getTheta1();
        double theta2 = speed.getTheta2();
        double omega1 = speed.getOmega1();
        double omega2 = speed.getOmega2();

        double mass1 = arguments.mass1();
        double mass2 = arguments.mass2();
        double length1 = arguments.length1();
        double length2 = arguments.length2();
        double gravity = arguments.gravity();

        double y1 = -length1 * Math.cos(theta1);
        double y2 = y1 - length2 * Math.cos(theta2);

        double xSpeed1 = length1 * omega1 * Math.cos(theta1);
        double ySpeed1 = length2 * omega2 * Math.cos(theta2);
        double xSpeed2 = xSpeed1 + length2 * omega2 * Math.cos(theta2);
        double ySpeed2 = ySpeed1 + length2 * omega2 * Math.cos(theta2);

        double kineticEnergy = 0.5 * mass1 * (xSpeed1 * xSpeed1 + ySpeed1 * ySpeed1)
                + 0.5 * mass2 * (xSpeed2 * xSpeed2 + ySpeed2 * ySpeed2);

        double potentialEnergy = mass1 * gravity * y1 + mass2 * gravity * y2;

        return kineticEnergy + potentialEnergy;
    }

    public double calculateLagrange(Speed speed) {
        double theta1 = speed.getTheta1();
        double theta2 = speed.getTheta2();
        double omega1 = speed.getOmega1();
        double omega2 = speed.getOmega2();

        double mass1 = arguments.mass1();
        double mass2 = arguments.mass2();
        double length1 = arguments.length1();
        double length2 = arguments.length2();
        double gravity = arguments.gravity();

        double y1 = -length1 * Math.cos(theta1);
        double y2 = y1 - length2 * Math.cos(theta2);

        double xSpeed1 = length1 * omega1 * Math.cos(theta1);
        double ySpeed1 = length2 * omega2 * Math.cos(theta2);
        double xSpeed2 = xSpeed1 + length2 * omega2 * Math.cos(theta2);
        double ySpeed2 = ySpeed1 + length2 * omega2 * Math.cos(theta2);

        double kineticEnergy = 0.5 * mass1 * (xSpeed1 * xSpeed1 + ySpeed1 * ySpeed1)
                + 0.5 * mass2 * (xSpeed2 * xSpeed2 + ySpeed2 * ySpeed2);

        double potentialEnergy = mass1 * gravity * y1 + mass2 * gravity * y2;

        return kineticEnergy - potentialEnergy;
    }
}
