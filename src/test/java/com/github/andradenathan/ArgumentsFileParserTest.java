package com.github.andradenathan;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Configuration File Parser Tests")
class ArgumentsFileParserTest {

    @Test
    @DisplayName("Parse valid configuration file with all parameters")
    void testParseValidConfigFile(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("config.txt");
        String content = """
                g=10.0
                m1=5.0
                L1=100.0
                m2=8.0
                L2=120.0
                """;
        Files.writeString(configFile, content);

        Arguments args = ArgumentsFileParser.fromFile(configFile.toString());

        assertEquals(10.0, args.gravity(), 0.001);
        assertEquals(5.0, args.mass1(), 0.001);
        assertEquals(100.0, args.length1(), 0.001);
        assertEquals(8.0, args.mass2(), 0.001);
        assertEquals(120.0, args.length2(), 0.001);
    }

    @Test
    @DisplayName("Parse config file with partial parameters (uses defaults)")
    void testParsePartialConfigFile(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("config.txt");
        String content = """
                g=5.0
                m1=15.0
                """;
        Files.writeString(configFile, content);

        Arguments args = ArgumentsFileParser.fromFile(configFile.toString());

        assertEquals(5.0, args.gravity(), 0.001);
        assertEquals(15.0, args.mass1(), 0.001);
        assertEquals(150.0, args.length1(), 0.001); // default
        assertEquals(10.0, args.mass2(), 0.001); // default
        assertEquals(150.0, args.length2(), 0.001); // default
    }

    @Test
    @DisplayName("Parse config file with comments")
    void testParseConfigFileWithComments(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("config.txt");
        String content = """
                # This is a comment
                g=9.5
                # Another comment
                m1=12.0
                L1=180.0
                # m2=999.0 (commented out)
                L2=160.0
                """;
        Files.writeString(configFile, content);

        Arguments args = ArgumentsFileParser.fromFile(configFile.toString());

        assertEquals(9.5, args.gravity(), 0.001);
        assertEquals(12.0, args.mass1(), 0.001);
        assertEquals(180.0, args.length1(), 0.001);
        assertEquals(10.0, args.mass2(), 0.001); // default (commented out)
        assertEquals(160.0, args.length2(), 0.001);
    }

    @Test
    @DisplayName("Parse config file with empty lines")
    void testParseConfigFileWithEmptyLines(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("config.txt");
        String content = """
                
                g=8.0
                
                m1=6.0
                
                L1=140.0
                
                """;
        Files.writeString(configFile, content);

        Arguments args = ArgumentsFileParser.fromFile(configFile.toString());

        assertEquals(8.0, args.gravity(), 0.001);
        assertEquals(6.0, args.mass1(), 0.001);
        assertEquals(140.0, args.length1(), 0.001);
    }

    @Test
    @DisplayName("Parse config file with whitespace around values")
    void testParseConfigFileWithWhitespace(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("config.txt");
        String content = """
                g  =  7.5
                m1=   9.0
                L1  =150.0  
                m2  =  11.0  
                L2=   130.0
                """;
        Files.writeString(configFile, content);

        Arguments args = ArgumentsFileParser.fromFile(configFile.toString());

        assertEquals(7.5, args.gravity(), 0.001);
        assertEquals(9.0, args.mass1(), 0.001);
        assertEquals(150.0, args.length1(), 0.001);
        assertEquals(11.0, args.mass2(), 0.001);
        assertEquals(130.0, args.length2(), 0.001);
    }

    @Test
    @DisplayName("Parse config file with invalid values (uses defaults)")
    void testParseConfigFileWithInvalidValues(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("config.txt");
        String content = """
                g=invalid
                m1=5.0
                L1=not_a_number
                m2=8.0
                L2=120.0
                """;
        Files.writeString(configFile, content);

        Arguments args = ArgumentsFileParser.fromFile(configFile.toString());

        assertEquals(9.81, args.gravity(), 0.001); // default (invalid)
        assertEquals(5.0, args.mass1(), 0.001);
        assertEquals(150.0, args.length1(), 0.001); // default (invalid)
        assertEquals(8.0, args.mass2(), 0.001);
        assertEquals(120.0, args.length2(), 0.001);
    }

    @Test
    @DisplayName("Parse config file with malformed lines")
    void testParseConfigFileWithMalformedLines(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("config.txt");
        String content = """
                g=7.0
                this is not a valid line
                m1=5.0
                another invalid line without equals
                L1=100.0
                too=many=equals=signs
                m2=8.0
                """;
        Files.writeString(configFile, content);

        Arguments args = ArgumentsFileParser.fromFile(configFile.toString());

        // Should successfully parse valid lines and ignore invalid ones
        assertEquals(7.0, args.gravity(), 0.001);
        assertEquals(5.0, args.mass1(), 0.001);
        assertEquals(100.0, args.length1(), 0.001);
        assertEquals(8.0, args.mass2(), 0.001);
        assertEquals(150.0, args.length2(), 0.001); // default
    }

    @Test
    @DisplayName("Parse empty config file (uses all defaults)")
    void testParseEmptyConfigFile(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("config.txt");
        Files.writeString(configFile, "");

        Arguments args = ArgumentsFileParser.fromFile(configFile.toString());

        Arguments defaults = Arguments.usingDefault();
        assertEquals(defaults.gravity(), args.gravity(), 0.001);
        assertEquals(defaults.mass1(), args.mass1(), 0.001);
        assertEquals(defaults.length1(), args.length1(), 0.001);
        assertEquals(defaults.mass2(), args.mass2(), 0.001);
        assertEquals(defaults.length2(), args.length2(), 0.001);
    }

    @Test
    @DisplayName("Parse config file with only comments (uses all defaults)")
    void testParseConfigFileWithOnlyComments(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("config.txt");
        String content = """
                # Configuration file
                # All parameters are commented out
                # g=10.0
                # m1=5.0
                # L1=100.0
                """;
        Files.writeString(configFile, content);

        Arguments args = ArgumentsFileParser.fromFile(configFile.toString());

        Arguments defaults = Arguments.usingDefault();
        assertEquals(defaults.gravity(), args.gravity(), 0.001);
        assertEquals(defaults.mass1(), args.mass1(), 0.001);
        assertEquals(defaults.length1(), args.length1(), 0.001);
    }

    @Test
    @DisplayName("Parse config file with duplicate parameters (last value wins)")
    void testParseConfigFileWithDuplicates(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("config.txt");
        String content = """
                g=5.0
                m1=10.0
                g=7.0
                m1=12.0
                """;
        Files.writeString(configFile, content);

        Arguments args = ArgumentsFileParser.fromFile(configFile.toString());

        // Last value should win
        assertEquals(7.0, args.gravity(), 0.001);
        assertEquals(12.0, args.mass1(), 0.001);
    }

    @Test
    @DisplayName("Throw IOException for non-existent file")
    void testParseNonExistentFile() {
        assertThrows(IOException.class, () -> {
            ArgumentsFileParser.fromFile("non_existent_file.txt");
        }, "Should throw IOException for non-existent file");
    }

    @Test
    @DisplayName("Parse config file with negative values")
    void testParseConfigFileWithNegativeValues(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("config.txt");
        String content = """
                g=-9.81
                m1=-5.0
                L1=100.0
                """;
        Files.writeString(configFile, content);

        Arguments args = ArgumentsFileParser.fromFile(configFile.toString());

        // Parser should accept negative values (validation would be elsewhere)
        assertEquals(-9.81, args.gravity(), 0.001);
        assertEquals(-5.0, args.mass1(), 0.001);
        assertEquals(100.0, args.length1(), 0.001);
    }

    @Test
    @DisplayName("Parse config file with decimal values")
    void testParseConfigFileWithDecimals(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("config.txt");
        String content = """
                g=9.807
                m1=10.5
                L1=150.75
                m2=8.333
                L2=120.125
                """;
        Files.writeString(configFile, content);

        Arguments args = ArgumentsFileParser.fromFile(configFile.toString());

        assertEquals(9.807, args.gravity(), 0.001);
        assertEquals(10.5, args.mass1(), 0.001);
        assertEquals(150.75, args.length1(), 0.001);
        assertEquals(8.333, args.mass2(), 0.001);
        assertEquals(120.125, args.length2(), 0.001);
    }

    @Test
    @DisplayName("Parse config file with scientific notation")
    void testParseConfigFileWithScientificNotation(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("config.txt");
        String content = """
                g=9.81e0
                m1=1.0e1
                L1=1.5e2
                """;
        Files.writeString(configFile, content);

        Arguments args = ArgumentsFileParser.fromFile(configFile.toString());

        assertEquals(9.81, args.gravity(), 0.001);
        assertEquals(10.0, args.mass1(), 0.001);
        assertEquals(150.0, args.length1(), 0.001);
    }
}

