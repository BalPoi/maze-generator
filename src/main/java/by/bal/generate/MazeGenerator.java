package by.bal.generate;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MazeGenerator {

    private static final char DEFAULT_PASS_CHAR = ' ';
    private static final char DEFAULT_WALL_CHAR = '#';

    private final int rows;
    private final int columns;
    private final char passChar;
    private final char wallChar;

    private static final Random RANDOM = new Random();

    public MazeGenerator(MazeGenerationConfig config) {
        this.rows = config.rows();
        this.columns = config.columns();
        this.passChar = config.passChar() == null? DEFAULT_PASS_CHAR: config.passChar();
        this.wallChar = config.wallChar() == null? DEFAULT_WALL_CHAR: config.wallChar();
    }

    public String generate() throws IllegalArgumentException {
        assertGeneratingParams();

        char[][] maze = initMaze();

        ArrayDeque<MazeCoordinates> pathHistory = new ArrayDeque<>();
        MazeCoordinates currCoordinates = new MazeCoordinates(1, 1);
        pathHistory.push(currCoordinates);
        do {
            Set<Movement> possibleMovements = getPossibleMovements(maze, currCoordinates);
            if (possibleMovements.isEmpty()) {
                currCoordinates = pathHistory.pop();
                continue;
            }
            Movement movement = pickRandomMovement(possibleMovements);
            currCoordinates = executeMovement(maze, pathHistory.getFirst(), movement);
            pathHistory.push(currCoordinates);
        } while (!pathHistory.isEmpty());

        return mazeToString(maze);
    }

    private char[][] initMaze() {
        char[][] maze;
        try {
            maze = new char[rows][columns];
        } catch (OutOfMemoryError e) {
            String errorMessage = String.format("The size of the maze is too big. A %dx%d maze would require %d bytes.",
                                                rows,
                                                columns,
                                                (long) rows * columns * Character.BYTES);
            throw new IllegalArgumentException(errorMessage, e);
        }

        for (char[] row : maze) {
            Arrays.fill(row, wallChar);
        }

        return maze;
    }

    private MazeCoordinates executeMovement(char[][] maze, MazeCoordinates currCoordinates, Movement movement) {
        MazeCoordinates newCoordinates = calcNewCoordinates(currCoordinates, movement);
        buildPath(maze, currCoordinates, newCoordinates);

        return newCoordinates;
    }

    private void buildPath(char[][] maze, MazeCoordinates currCoordinates, MazeCoordinates newCoordinates) {
        int currRow = currCoordinates.row();
        int currColumn = currCoordinates.column();
        int newRow = newCoordinates.row();
        int newColumn = newCoordinates.column();

        for (int r = Math.min(currRow, newRow); r <= Math.max(currRow, newRow); r++) {
            for (int c = Math.min(currColumn, newColumn); c <= Math.max(currColumn, newColumn); c++) {
                maze[r][c] = passChar;
            }
        }
    }

    private static MazeCoordinates calcNewCoordinates(MazeCoordinates currCoordinates, Movement movement) {
        int newRow = currCoordinates.row();
        int newColumn = currCoordinates.column();
        switch (movement) {
            case UP -> newRow -= 2;
            case DOWN -> newRow += 2;
            case RIGHT -> newColumn += 2;
            case LEFT -> newColumn -= 2;
        }

        return new MazeCoordinates(newRow, newColumn);
    }

    private static Movement pickRandomMovement(Set<Movement> possibleMovements) {
        int size = possibleMovements.size();
        Movement[] movementsArray = new Movement[size];
        possibleMovements.toArray(movementsArray);
        return movementsArray[RANDOM.nextInt(size)];
    }

    private void assertGeneratingParams() {
        if (rows < 3 || columns < 3) {
            throw new IllegalArgumentException("The maze size cannot be less than 3x3.");
        }
        if (isEven(rows) || isEven(columns)) {
            throw new IllegalArgumentException("The maze sides must be odd numbers.");
        }
    }

    private static boolean isEven(int number) {
        return (number & 1) == 0;
    }

    private Set<Movement> getPossibleMovements(char[][] maze, MazeCoordinates currCoordinates) {
        int currRowIndex = currCoordinates.row();
        int currColumnIndex = currCoordinates.column();
        int rowsLastIndex = maze.length - 1;
        int columnsLastIndex = maze[currRowIndex].length - 1;

        Set<Movement> possibleMovements = new HashSet<>();
        if (currRowIndex >= 3 && maze[currRowIndex - 2][currColumnIndex] != passChar) {
            possibleMovements.add(Movement.UP);
        }
        if (currRowIndex <= rowsLastIndex - 3 && maze[currRowIndex + 2][currColumnIndex] != passChar) {
            possibleMovements.add(Movement.DOWN);
        }
        if (currColumnIndex <= columnsLastIndex - 3 && maze[currRowIndex][currColumnIndex + 2] != passChar) {
            possibleMovements.add(Movement.RIGHT);
        }
        if (currColumnIndex >= 3 && maze[currRowIndex][currColumnIndex - 2] != passChar) {
            possibleMovements.add(Movement.LEFT);
        }

        return possibleMovements;
    }

    private static String mazeToString(char[][] maze) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char[] row : maze) {
            stringBuilder.append(row);
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }

    private record MazeCoordinates(int row, int column) {
    }

    /**
     * Configuration of maze generation
     *
     * @param rows     number of rows
     * @param columns  number of columns
     * @param passChar character for passes
     * @param wallChar character for walls
     */
    public record MazeGenerationConfig(int rows, int columns, Character passChar, Character wallChar) {
    }
}
