package by.bal;

import by.bal.generate.MazeGenerator;
import by.bal.generate.MazeGenerator.MazeGenerationConfig;
import picocli.CommandLine;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class MazeApp {

    public static void main(String[] args) {
        GenerateMazeCommand generateMazeCommand = new GenerateMazeCommand();
        try {
            ParseResult parseResult = new CommandLine(generateMazeCommand).parseArgs(args);
            if (!CommandLine.printHelpIfRequested(parseResult)) {
                runProgram(generateMazeCommand);
            }
        } catch (ParameterException ex) {
            // command line arguments could not be parsed
            System.err.println(ex.getMessage());
            ex.getCommandLine().usage(System.err);
        }
    }

    private static void runProgram(GenerateMazeCommand command) {
        MazeGenerationConfig generationConfig =
                new MazeGenerationConfig(command.rows, command.columns, command.passChar, command.wallChar);
        MazeGenerator mazeGenerator = new MazeGenerator(generationConfig);

        String maze = mazeGenerator.generate();

        outputMaze(maze, command.outputFile);
    }

    private static void outputMaze(String maze, File outputFile) {
        if (outputFile == null) {
            PrintWriter printWriter = new PrintWriter(System.out, true, StandardCharsets.UTF_8);
            printWriter.println(maze);
        } else {
            try (FileWriter fileWriter = new FileWriter(outputFile, StandardCharsets.UTF_8)) {
                fileWriter.write(maze);
                fileWriter.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
