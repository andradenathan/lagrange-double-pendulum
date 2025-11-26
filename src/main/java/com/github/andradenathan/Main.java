package com.github.andradenathan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Arguments arguments = parseArguments(args);
            Simulation simulation = parseSimulation(args);
            Speed initialSpeed = parseSpeed(args);

            createGui(initialSpeed, simulation, arguments);
        });
    }

    private static Arguments parseArguments(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--config=")) {
                String filePath = arg.substring("--config=".length());
                try {
                    return ArgumentsFileParser.fromFile(filePath);
                } catch (Exception exception) {
                    System.err.println("Erro ao ler arquivo de configuração: " + exception.getMessage());
                    System.err.println("Usando configuração padrão.");
                }
            }
        }

        if (hasPhysicsArgs(args)) {
            return parsePhysicsFromArgs(args);
        }

        return Arguments.usingDefault();
    }

    private static boolean hasPhysicsArgs(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--g=") || arg.startsWith("--m1=") ||
                    arg.startsWith("--L1=") || arg.startsWith("--m2=") ||
                    arg.startsWith("--L2=")) {
                return true;
            }
        }
        return false;
    }

    private static Arguments parsePhysicsFromArgs(String[] args) {
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
                System.err.println("Invalid value: " + arg);
            }
        }

        return new Arguments(g, m1, L1, m2, L2);
    }

    private static Simulation parseSimulation(String[] args) {
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

    private static Speed parseSpeed(String[] args) {
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
                System.err.println("Invalid value: " + arg);
            }
        }

        if (theta1 != null && theta2 != null) {
            return Speed.usingAngles(theta1, theta2);
        }

        return Speed.usingDefault();
    }

    private static void createGui(Speed initialSpeed, Simulation simulation, Arguments arguments) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        JFrame frame = new JFrame("Double Pendulum Simulation");
        DoublePendulum doublePendulum = new DoublePendulum(arguments, simulation, initialSpeed);

        frame.setUndecorated(true);

        JPanel titleBar = createCustomTitleBar(frame);

        frame.setLayout(new BorderLayout());
        frame.add(titleBar, BorderLayout.NORTH);
        frame.add(doublePendulum, BorderLayout.CENTER);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        configureKeys(frame, doublePendulum);

        frame.setVisible(true);
        frame.requestFocus();
    }

    private static JPanel createCustomTitleBar(JFrame frame) {
        JPanel titleBar = new JPanel();
        titleBar.setBackground(new Color(28, 28, 35));
        titleBar.setPreferredSize(new Dimension(1050, 35));
        titleBar.setLayout(new BorderLayout());

        JLabel title = new JLabel("  Double Pendulum - Lagrangian Simulation");
        title.setForeground(new Color(100, 200, 255));
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleBar.add(title, BorderLayout.WEST);


        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(28, 28, 35));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));

        JButton minimizeButton = createTitleBarButton("_");
        minimizeButton.addActionListener(e -> frame.setState(JFrame.ICONIFIED));

        JButton closeButton = createTitleBarButton("X");
        closeButton.addActionListener(e -> System.exit(0));
        closeButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                closeButton.setBackground(new Color(220, 50, 50));
            }

            public void mouseExited(MouseEvent evt) {
                closeButton.setBackground(new Color(28, 28, 35));
            }
        });

        buttonPanel.add(minimizeButton);
        buttonPanel.add(closeButton);
        titleBar.add(buttonPanel, BorderLayout.EAST);

        final Point[] mouseDownCompCoords = {null};
        titleBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseDownCompCoords[0] = e.getPoint();
            }

            public void mouseReleased(MouseEvent e) {
                mouseDownCompCoords[0] = null;
            }
        });
        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                frame.setLocation(currCoords.x - mouseDownCompCoords[0].x,
                        currCoords.y - mouseDownCompCoords[0].y);
            }
        });

        return titleBar;
    }

    private static JButton createTitleBarButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setBackground(new Color(28, 28, 35));
        button.setForeground(new Color(220, 220, 230));
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(45, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                if (!text.equals("X")) {
                    button.setBackground(new Color(50, 50, 60));
                }
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(28, 28, 35));
            }
        });

        return button;
    }

    private static void configureKeys(JFrame frame, DoublePendulum doublePendulum) {
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                switch (event.getKeyCode()) {
                    case KeyEvent.VK_R -> doublePendulum.reload();
                    case KeyEvent.VK_SPACE -> doublePendulum.togglePause();
                    case KeyEvent.VK_ESCAPE -> System.exit(0);
                }
            }
        });
    }
}
