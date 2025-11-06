package com.github.andradenathan;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Arguments arguments = Arguments.usingDefault();
            Simulation simulation = Simulation.faster();
            Speed initialSpeed = Speed.usingDefault();

            createGui(initialSpeed, simulation, arguments);
        });
    }

    private static void createGui(Speed initialSpeed, Simulation simulation, Arguments arguments) {
        JFrame frame = new JFrame("Double Pendulum Simulation");
        DoublePendulum doublePendulum = new DoublePendulum(arguments, simulation, initialSpeed);

        frame.add(doublePendulum);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        configurateKeys(frame, doublePendulum);

        frame.setVisible(true);
        frame.requestFocus();
    }

    private static void configurateKeys(JFrame frame, DoublePendulum doublePendulum) {
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                switch(event.getKeyCode()) {
                    case KeyEvent.VK_R -> doublePendulum.reload();
                    case KeyEvent.VK_SPACE -> doublePendulum.togglePause();
                    case KeyEvent.VK_ESCAPE -> System.exit(0);
                }
            }
        });
    }
}
