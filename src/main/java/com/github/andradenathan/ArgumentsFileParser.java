package com.github.andradenathan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ArgumentsFileParser {
    public static Arguments fromFile(String filePath) throws IOException {
        Map<String, Double> params = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    try {
                        double value = Double.parseDouble(parts[1].trim());
                        params.put(key, value);
                    } catch (NumberFormatException e) {
                        System.err.println("Valor inválido na linha: " + line);
                    }
                }
            }
        }

        double g = params.getOrDefault("g", 9.81);
        double m1 = params.getOrDefault("m1", 10.0);
        double L1 = params.getOrDefault("L1", 150.0);
        double m2 = params.getOrDefault("m2", 10.0);
        double L2 = params.getOrDefault("L2", 150.0);

        return new Arguments(g, m1, L1, m2, L2);
    }
}
