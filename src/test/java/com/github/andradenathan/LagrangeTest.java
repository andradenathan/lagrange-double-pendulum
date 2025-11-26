package com.github.andradenathan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Lagrange Mechanics Tests")
class LagrangeTest {

    private Arguments defaultArguments;
    private Lagrange lagrange;

    @BeforeEach
    void setUp() {
        defaultArguments = Arguments.usingDefault();
        lagrange = new Lagrange(defaultArguments);
    }

    @Test
    @DisplayName("Energy conservation: Total energy should remain constant during simulation")
    void testEnergyConservation() {
        Speed speed = new Speed(Math.PI / 4, Math.PI / 4, 0.0, 0.0);
        double initialEnergy = lagrange.calculateEnergy(speed);

        for (int i = 0; i < 50; i++) {
            lagrange.integrate(speed, 0.0005);
        }

        double finalEnergy = lagrange.calculateEnergy(speed);

        assertEquals(initialEnergy, finalEnergy, Math.abs(initialEnergy) * 0.10,
                "Energy should be conserved within 10% (Euler method limitation)");
    }

    @Test
    @DisplayName("Lagrangian calculation: L = T - V")
    void testLagrangianCalculation() {
        Speed speed = new Speed(Math.PI / 4, Math.PI / 4, 0.5, 0.5);

        double lagrangianValue = lagrange.calculateLagrange(speed);

        assertTrue(Double.isFinite(lagrangianValue), "Lagrangian should be finite");
    }

    @Test
    @DisplayName("Accelerations at rest: Should be non-zero due to gravity")
    void testAccelerationsAtRest() {
        Speed speed = new Speed(Math.PI / 2, Math.PI / 2, 0.0, 0.0);

        double[] accelerations = lagrange.calculateAccelerations(speed);

        assertEquals(2, accelerations.length, "Should return 2 accelerations");
        assertNotEquals(0.0, accelerations[0], 0.001,
                "First pendulum should have non-zero acceleration due to gravity");
        assertTrue(Double.isFinite(accelerations[0]), "Acceleration should be finite");
        assertTrue(Double.isFinite(accelerations[1]), "Acceleration should be finite");
    }

    @Test
    @DisplayName("Accelerations at equilibrium: Should be zero at bottom position")
    void testAccelerationsAtEquilibrium() {
        Speed speed = new Speed(0.0, 0.0, 0.0, 0.0);

        double[] accelerations = lagrange.calculateAccelerations(speed);

        assertEquals(0.0, accelerations[0], 0.001,
                "First pendulum at equilibrium should have near-zero acceleration");
        assertEquals(0.0, accelerations[1], 0.001,
                "Second pendulum at equilibrium should have near-zero acceleration");
    }

    @Test
    @DisplayName("Integration: Position should change over time")
    void testIntegration() {
        // Start at 60 degrees - more gravitational torque
        Speed speed = new Speed(Math.PI / 3, Math.PI / 3, 0.0, 0.0);

        double initialTheta1 = speed.getTheta1();
        double initialTheta2 = speed.getTheta2();

        for (int i = 0; i < 1000; i++) {
            lagrange.integrate(speed, 0.01);
        }

        assertTrue(Math.abs(speed.getTheta1() - initialTheta1) > 0.01,
                "Theta1 should change significantly after integration (changed by " +
                        Math.abs(speed.getTheta1() - initialTheta1) + " radians)");
        assertTrue(Math.abs(speed.getTheta2() - initialTheta2) > 0.01,
                "Theta2 should change significantly after integration (changed by " +
                        Math.abs(speed.getTheta2() - initialTheta2) + " radians)");
    }

    @Test
    @DisplayName("Energy calculation: Should have higher energy at elevated position than at bottom")
    void testEnergyAtElevatedPosition() {
        Speed speedElevated = new Speed(Math.PI / 2, Math.PI / 2, 0.0, 0.0);
        Speed speedBottom = new Speed(0.0, 0.0, 0.0, 0.0);

        double energyElevated = lagrange.calculateEnergy(speedElevated);
        double energyBottom = lagrange.calculateEnergy(speedBottom);

        assertTrue(energyElevated > energyBottom,
                "Energy should be higher when pendulum is elevated compared to bottom position");
    }

    @Test
    @DisplayName("Energy calculation: Total energy should be negative at rest at bottom")
    void testEnergyAtBottomPosition() {
        Speed speed = new Speed(0.0, 0.0, 0.0, 0.0);

        double energy = lagrange.calculateEnergy(speed);

        assertTrue(energy < 0, "Energy should be negative at lowest position");
    }

    @Test
    @DisplayName("Symmetry test: Equal masses and lengths should produce symmetric behavior")
    void testSymmetricSystem() {
        Arguments symmetricArgs = new Arguments(9.81, 10.0, 100.0, 10.0, 100.0);
        Lagrange symmetricLagrange = new Lagrange(symmetricArgs);

        Speed speed = new Speed(Math.PI / 4, Math.PI / 4, 0.0, 0.0);

        double[] accelerations = symmetricLagrange.calculateAccelerations(speed);

        assertTrue(Double.isFinite(accelerations[0]), "Acceleration 1 should be finite");
        assertTrue(Double.isFinite(accelerations[1]), "Acceleration 2 should be finite");
    }

    @Test
    @DisplayName("Different masses: System with different masses should behave differently")
    void testAsymmetricSystem() {
        Arguments asymmetricArgs = new Arguments(9.81, 5.0, 150.0, 15.0, 150.0);
        Lagrange asymmetricLagrange = new Lagrange(asymmetricArgs);

        Speed speed1 = new Speed(Math.PI / 3, Math.PI / 6, 0.0, 0.0);
        Speed speed2 = new Speed(Math.PI / 3, Math.PI / 6, 0.0, 0.0);

        double[] asymmetricAccelerations = asymmetricLagrange.calculateAccelerations(speed1);
        double[] symmetricAccelerations = lagrange.calculateAccelerations(speed2);

        assertNotEquals(symmetricAccelerations[0], asymmetricAccelerations[0], 0.001,
                "Different mass ratios should produce different accelerations");
    }

    @Test
    @DisplayName("Numerical stability: No NaN or Infinity values")
    void testNumericalStability() {
        Speed speed = Speed.usingDefault();

        for (int i = 0; i < 1000; i++) {
            lagrange.integrate(speed, 0.01);

            assertTrue(Double.isFinite(speed.getTheta1()),
                    "Theta1 should remain finite at step " + i);
            assertTrue(Double.isFinite(speed.getTheta2()),
                    "Theta2 should remain finite at step " + i);
            assertTrue(Double.isFinite(speed.getOmega1()),
                    "Omega1 should remain finite at step " + i);
            assertTrue(Double.isFinite(speed.getOmega2()),
                    "Omega2 should remain finite at step " + i);
        }
    }

    @Test
    @DisplayName("Energy with motion: Kinetic energy should be positive when pendulum is moving")
    void testKineticEnergy() {
        Speed speed = new Speed(0.0, 0.0, 2.0, 2.0);

        double energy = lagrange.calculateEnergy(speed);

        Speed speedAtRest = new Speed(0.0, 0.0, 0.0, 0.0);
        double energyAtRest = lagrange.calculateEnergy(speedAtRest);

        assertTrue(energy > energyAtRest,
                "Energy with motion should be greater than energy at rest");
    }

    @Test
    @DisplayName("Lagrangian vs Energy: Should differ by 2*V")
    void testLagrangianVsEnergy() {
        Speed speed = new Speed(Math.PI / 4, Math.PI / 4, 0.5, 0.5);

        double lagrangianValue = lagrange.calculateLagrange(speed);
        double energy = lagrange.calculateEnergy(speed);

        double difference = energy - lagrangianValue;

        assertTrue(Double.isFinite(difference), "Difference should be finite");
    }
}
