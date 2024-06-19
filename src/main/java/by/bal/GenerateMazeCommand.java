package by.bal;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;

@Command(header = "Generate maze to STDOUT or to the output file.", version = "v0.0.1", mixinStandardHelpOptions = true)
public class GenerateMazeCommand {
    @Option(names = {"-o", "--out"}, description = "Output file (default: print to console)")
    File outputFile;

    @Option(names = {"-p", "--pass-char"}, description = "Character for passes", type = Character.class)
    Character passChar;

    @Option(names = {"-w", "--wall-char"}, description = "Character for walls", type = Character.class)
    Character wallChar;

    @Parameters(index = "0", description = "Number of rows")
    int rows;

    @Parameters(index = "1", description = "Number of columns")
    int columns;
}
