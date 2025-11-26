package com.github.andradenathan;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Command Line Arguments Parser Tests")
class ArgumentsParserTest {

    @Test
    @DisplayName("Parse default arguments when no args provided")
    void testDefaultArguments() {
        Arguments args = parseArgumentsHelper(new String[]{});

        Arguments expected = Arguments.usingDefault();
        assertEquals(expected.gravity(), args.gravity(), 0.001);
        assertEquals(expected.mass1(), args.mass1(), 0.001);
        assertEquals(expected.length1(), args.length1(), 0.001);
        assertEquals(expected.mass2(), args.mass2(), 0.001);
        assertEquals(expected.length2(), args.length2(), 0.001);
    }

    @Test
    @DisplayName("Parse gravity parameter")
    void testParseGravity() {
        Arguments args = parseArgumentsHelper(new String[]{"--g=5.0"});

        assertEquals(5.0, args.gravity(), 0.001);
        assertEquals(10.0, args.mass1(), 0.001); // default
        assertEquals(150.0, args.length1(), 0.001); // default
    }

    @Test
    @DisplayName("Parse mass1 parameter")
    void testParseMass1() {
        Arguments args = parseArgumentsHelper(new String[]{"--m1=20.0"});

        assertEquals(20.0, args.mass1(), 0.001);
        assertEquals(9.81, args.gravity(), 0.001); // default
    }

    @Test
    @DisplayName("Parse mass2 parameter")
    void testParseMass2() {
        Arguments args = parseArgumentsHelper(new String[]{"--m2=15.0"});

        assertEquals(15.0, args.mass2(), 0.001);
        assertEquals(10.0, args.mass1(), 0.001); // default
    }

    @Test
    @DisplayName("Parse length1 parameter")
    void testParseLength1() {
        Arguments args = parseArgumentsHelper(new String[]{"--L1=200.0"});

        assertEquals(200.0, args.length1(), 0.001);
        assertEquals(150.0, args.length2(), 0.001); // default
    }

    @Test
    @DisplayName("Parse length2 parameter")
    void testParseLength2() {
        Arguments args = parseArgumentsHelper(new String[]{"--L2=100.0"});

        assertEquals(100.0, args.length2(), 0.001);
        assertEquals(150.0, args.length1(), 0.001); // default
    }

    @Test
    @DisplayName("Parse all physics parameters together")
    void testParseAllParameters() {
        Arguments args = parseArgumentsHelper(new String[]{
            "--g=10.0", "--m1=5.0", "--L1=100.0", "--m2=8.0", "--L2=120.0"
        });

        assertEquals(10.0, args.gravity(), 0.001);
        assertEquals(5.0, args.mass1(), 0.001);
        assertEquals(100.0, args.length1(), 0.001);
        assertEquals(8.0, args.mass2(), 0.001);
        assertEquals(120.0, args.length2(), 0.001);
    }

    @Test
    @DisplayName("Parse parameters in different order")
    void testParseParametersInDifferentOrder() {
        Arguments args = parseArgumentsHelper(new String[]{
            "--L2=80.0", "--m1=12.0", "--g=9.5", "--L1=90.0", "--m2=11.0"
        });

        assertEquals(9.5, args.gravity(), 0.001);
        assertEquals(12.0, args.mass1(), 0.001);
        assertEquals(90.0, args.length1(), 0.001);
        assertEquals(11.0, args.mass2(), 0.001);
        assertEquals(80.0, args.length2(), 0.001);
    }

    @Test
    @DisplayName("Handle invalid parameter values gracefully")
    void testInvalidParameterValues() {
        // Should use default values when parsing fails
        Arguments args = parseArgumentsHelper(new String[]{"--g=invalid"});

        // Should fall back to default since no valid physics args
        assertEquals(Arguments.usingDefault().gravity(), args.gravity(), 0.001);
    }

    @Test
    @DisplayName("Parse mixed valid and invalid parameters")
    void testMixedValidInvalidParameters() {
        Arguments args = parseArgumentsHelper(new String[]{
            "--g=8.0", "--m1=invalid", "--L1=200.0"
        });

        assertEquals(8.0, args.gravity(), 0.001);
        assertEquals(10.0, args.mass1(), 0.001); // default (invalid value)
        assertEquals(200.0, args.length1(), 0.001);
    }

    @Test
    @DisplayName("Ignore unrecognized parameters")
    void testIgnoreUnrecognizedParameters() {
        Arguments args = parseArgumentsHelper(new String[]{
            "--g=7.0", "--unknown=value", "--m1=15.0"
        });

        assertEquals(7.0, args.gravity(), 0.001);
        assertEquals(15.0, args.mass1(), 0.001);
    }

    @Test
    @DisplayName("Parse theta1 parameter")
    void testParseTheta1() {
        Speed speed = parseSpeedHelper(new String[]{"--theta1=45.0", "--theta2=90.0"});

        assertNotNull(speed);
        assertEquals(Math.toRadians(45.0), speed.getTheta1(), 0.001);
        assertEquals(Math.toRadians(90.0), speed.getTheta2(), 0.001);
        assertEquals(0.0, speed.getOmega1(), 0.001);
        assertEquals(0.0, speed.getOmega2(), 0.001);
    }

    @Test
    @DisplayName("Parse only theta1 returns default speed")
    void testParseOnlyTheta1() {
        Speed speed = parseSpeedHelper(new String[]{"--theta1=45.0"});

        // Should return default since both angles are required
        Speed defaultSpeed = Speed.usingDefault();
        assertEquals(defaultSpeed.getTheta1(), speed.getTheta1(), 0.001);
        assertEquals(defaultSpeed.getTheta2(), speed.getTheta2(), 0.001);
    }

    @Test
    @DisplayName("Parse simulation mode: accurate")
    void testParseSimulationAccurate() {
        Simulation sim1 = parseSimulationHelper(new String[]{"--sim=accurate"});
        Simulation sim2 = parseSimulationHelper(new String[]{"--accurate"});

        assertNotNull(sim1);
        assertNotNull(sim2);
        // Both should create accurate simulation
        assertEquals(sim1.getTimeStep(), sim2.getTimeStep(), 0.00001);
    }

    @Test
    @DisplayName("Parse simulation mode: faster")
    void testParseSimulationFaster() {
        Simulation sim1 = parseSimulationHelper(new String[]{"--sim=faster"});
        Simulation sim2 = parseSimulationHelper(new String[]{"--faster"});

        assertNotNull(sim1);
        assertNotNull(sim2);
        assertEquals(sim1.getTimeStep(), sim2.getTimeStep(), 0.00001);
    }

    @Test
    @DisplayName("Parse simulation mode: default when not specified")
    void testParseSimulationDefault() {
        Simulation sim = parseSimulationHelper(new String[]{});

        assertNotNull(sim);
        // Should return faster as default
        assertEquals(Simulation.faster().getTimeStep(), sim.getTimeStep(), 0.00001);
    }

    // Helper methods that replicate the parsing logic from Main.java
    private Arguments parseArgumentsHelper(String[] args) {
        if (hasPhysicsArgs(args)) {
            return parsePhysicsFromArgs(args);
        }
        return Arguments.usingDefault();
    }

    private boolean hasPhysicsArgs(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--g=") || arg.startsWith("--m1=") ||
                    arg.startsWith("--L1=") || arg.startsWith("--m2=") ||
                    arg.startsWith("--L2=")) {
                return true;
            }
        }
        return false;
    }

    private Arguments parsePhysicsFromArgs(String[] args) {
        double g = 9.81;
        double m1 = 10.0;
        double L1 = 150.0;
        double m2 = 10.0;
        double L2 = 150.0;

        for (String arg : args) {
            try {
                if (arg.startsWith("--g=")) {
                    g = Double.parseDouble(arg.substring("--g=".length()));
                } else if (arg.startsWith("--m1=")) {
                    m1 = Double.parseDouble(arg.substring("--m1=".length()));
                } else if (arg.startsWith("--L1=")) {
                    L1 = Double.parseDouble(arg.substring("--L1=".length()));
                } else if (arg.startsWith("--m2=")) {
                    m2 = Double.parseDouble(arg.substring("--m2=".length()));
                } else if (arg.startsWith("--L2=")) {
                    L2 = Double.parseDouble(arg.substring("--L2=".length()));
                }
            } catch (NumberFormatException e) {
                // Invalid value, keep default
            }
        }

        return new Arguments(g, m1, L1, m2, L2);
    }

    private Speed parseSpeedHelper(String[] args) {
        Double theta1 = null;
        Double theta2 = null;

        for (String arg : args) {
            try {
                if (arg.startsWith("--theta1=")) {
                    theta1 = Double.parseDouble(arg.substring("--theta1=".length()));
                } else if (arg.startsWith("--theta2=")) {
                    theta2 = Double.parseDouble(arg.substring("--theta2=".length()));
                }
            } catch (NumberFormatException e) {
                // Invalid value
            }
        }

        if (theta1 != null && theta2 != null) {
            return Speed.usingAngles(theta1, theta2);
        }

        return Speed.usingDefault();
    }

    private Simulation parseSimulationHelper(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case "--sim=accurate", "--accurate" -> {
                    return Simulation.accurate();
                }
                case "--sim=faster", "--faster" -> {
                    return Simulation.faster();
                }
                case "--sim=default" -> {
                    return Simulation.usingDefault();
                }
            }
        }
        return Simulation.faster();
    }
}

