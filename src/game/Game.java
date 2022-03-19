package game;

import agents.*;
import algorithm.Algorithm;
import algorithm.IncorrectDataException;
import algorithm.NoPathException;

public class Game {
    /**
     * Size of the field.
     */
    public static final int SIZE = 9;

    /**
     * Array with all agents on the field.
     */
    private final Agent[][] table;
    private final Harry harry;
    private final Exit exit;

    /**
     * Indicator for wrong field. Set in the constructor.
     */
    private boolean badField;

    public Game(Harry harry, Filch filch, Cat cat, Book book, Cloak cloak, Exit exit) {
        this.table = new Agent[SIZE][SIZE];
        this.harry = harry;
        this.exit = exit;

        table[harry.y][harry.x] = harry;

        BadAgent.VisibilityZone[] zones = cat.getPerception();
        for (BadAgent.VisibilityZone zone : zones) {
            if (zone.x == harry.x && zone.y == harry.y)
                badField = true;
            else table[zone.y][zone.x] = zone;
        }
        zones = filch.getPerception();
        for (BadAgent.VisibilityZone zone : zones) {
            if (zone.x == harry.x && zone.y == harry.y)
                badField = true;
            else table[zone.y][zone.x] = zone;
        }

        table[cat.y][cat.x] = cat;
        table[filch.y][filch.x] = filch;
        table[book.y][book.x] = book;
        table[cloak.y][cloak.x] = cloak;
        table[exit.y][exit.x] = exit;
    }

    /**
     * Main function for of the Game. Launch algorithm to find minimal path on specified field.
     * @param algorithm Algorithm which will look for the minimal path.
     * @throws NoPathException If there is no path to objects (book or exit).
     * @throws IncorrectDataException If there is incorrect field.
     */
    public void play(Algorithm algorithm) throws NoPathException, IncorrectDataException {
        if (badField)
            throw new NoPathException();
        System.out.println(algorithm);
        try {
            long time = System.currentTimeMillis();
            Path[] path = algorithm.getMinimalPath(harry, exit);
            time = System.currentTimeMillis() - time;

            System.out.println("Win!");
            System.out.println("Number of steps: " + (path.length - 1));
            System.out.println("Path: " + pathToString(path));
            draw(path);
            System.out.println("Time: " + time + " ms");
        } catch (NoPathException e) {
            System.out.println("Lose!");
        }
    }

    /**
     * Prints the field to the console.
     * @param path Array containing cells for minimal path to the exit with the book.
     */
    public void draw(Path[] path) {
        for (int y = SIZE-1; y >= 0; y--) {
            for (int x = 0; x < SIZE; x++) {
                if (path != null) {
                    boolean isPath = false;
                    for (Path p : path) {
                        if (p.x == x && p.y == y) {
                            System.out.print(" " + p.symbol + " ");
                            isPath = true;
                            break;
                        }
                    }
                    if (isPath) continue;
                }
                if (table[y][x] == null)
                    System.out.print(" . ");
                else
                    System.out.print(" " + table[y][x].symbol + " ");
            }
            System.out.println();
        }
    }

    /**
     * Converts input path to the string object of the following format:
     * [1,2] [2,3]
     * @param path Array containing cells to print.
     * @return String representation of the path in the following format:
     * [1,2] [2,3]
     * If length of the path is zero (0) then returns empty string.
     */
    private String pathToString(Path[] path) {
        if (path.length == 0)
            return "";
        StringBuilder sb = new StringBuilder(path[0].toString());
        for (int i = 1; i < path.length; i++) {
            sb.append(' ');
            sb.append(path[i].toString());
        }
        return sb.toString();
    }

    /**
     * Returns current field with all agents.
     * @return current field with all agents.
     */
    public Agent[][] getTable() {
        return table;
    }
}
