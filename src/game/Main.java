package game;

import agents.*;
import algorithm.AStar;
import algorithm.Backtracking;
import algorithm.IncorrectDataException;
import algorithm.NoPathException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    /**
     * Main function of the application.
     * @param args - not required (it will be skipped).
     */
    public static void main(String[] args) {
        try {
            Game game = getGameFromInput();

            System.out.println("-----   Initial MAP   -----");
            game.draw(null);
            System.out.println("---------------------------");

            game.play(new Backtracking(game.getTable()));
            System.out.println("---------------------------");
            game.play(new AStar(game.getTable()));
        } catch (IncorrectDataException e) {
            System.out.println("Sorry, but input data is incorrect");
            System.out.println(e.getMessage());
        } catch (NoPathException e) {
            System.out.println("Loss due to wrong field");
        }
    }

    /**
     * Interacts with user via console. The method validates input data.
     * Input data:
     * coordinates of the 6 agents
     * scenario for the game
     * Example:
     * [0,0] [4,2] [2,7] [7,4] [0,8] [1,4]
     * 1
     * @return Game object containing the agents specified by user.
     * @throws IncorrectDataException when user tries to enter incorrect data.
     */
    private static Game getGameFromInput() throws IncorrectDataException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String input = reader.readLine();
            int scenario;
            try {
                scenario = Integer.parseInt(reader.readLine());
            } catch (NumberFormatException e) {
                throw new IncorrectDataException("The input scenario must be integer (1 or 2)");
            }

            if (input.isEmpty())
                return getRandomGame(scenario);

            String[] actors = input.split(" ");
            if (actors.length != 6)
                throw new IncorrectDataException("The amount of input coordinates must be 6. Given " + actors.length);
            if (scenario != 1 && scenario != 2)
                throw new IncorrectDataException("Expected values for scenario: 1 and 2. Given: " + scenario);

            ArrayList<Integer> possibleCells = new ArrayList<>();
            for (int i = 0; i < Game.SIZE * Game.SIZE; i++) {
                possibleCells.add(i);
            }
            int[] coordinates = getCoordinates(actors[0], possibleCells);
            Harry harry = new Harry(coordinates[0], coordinates[1], scenario);
            possibleCells.remove((Integer) (coordinates[1] * Game.SIZE + coordinates[0]));

            coordinates = getCoordinates(actors[1], possibleCells);
            Filch filch = new Filch(coordinates[0], coordinates[1]);
            possibleCells.remove((Integer) (coordinates[1] * Game.SIZE + coordinates[0]));

            coordinates = getCoordinates(actors[2], possibleCells);
            Cat cat = new Cat(coordinates[0], coordinates[1]);
            possibleCells.remove((Integer) (coordinates[1] * Game.SIZE + coordinates[0]));
            for (BadAgent.VisibilityZone zone : filch.getPerception())
                possibleCells.remove((Object) (zone.y * Game.SIZE + zone.x));
            for (BadAgent.VisibilityZone zone : cat.getPerception())
                possibleCells.remove((Object) (zone.y * Game.SIZE + zone.x));

            coordinates = getCoordinates(actors[3], possibleCells);
            Book book = new Book(coordinates[0], coordinates[1]);
            possibleCells.remove((Integer) (coordinates[1] * Game.SIZE + coordinates[0]));

            coordinates = getCoordinates(actors[4], possibleCells);
            Cloak cloak = new Cloak(coordinates[0], coordinates[1]);
            possibleCells.remove((Integer) (coordinates[1] * Game.SIZE + coordinates[0]));

            coordinates = getCoordinates(actors[5], possibleCells);
            Exit exit = new Exit(coordinates[0], coordinates[1]);
            possibleCells.remove((Integer) (coordinates[1] * Game.SIZE + coordinates[0]));

            return new Game(harry, filch, cat, book, cloak, exit);
        } catch (IOException e) {
            throw new IncorrectDataException("Error while reading an input");
        }
    }

    /**
     * Supporting method for validation of input data.
     * @param input String in the format: [1,2] which describes the position of the agent.
     * @param possibleCells List with coordinates of the possible position for the input agent.
     *                      If the input coordinates not in this list then throws IncorrectDataException.
     * @return coordinates of the agent in array: [x,y].
     * @throws IncorrectDataException when user enters incorrect data.
     */
    private static int[] getCoordinates(String input, ArrayList<Integer> possibleCells) throws IncorrectDataException {
        if (!input.contains("[") || !input.contains("]") || !input.contains(","))
            throw new IncorrectDataException("The format of input coordinates is incorrect. Given " + input);
        String[] actor = input.substring(1, input.length()-1).split(",");
        if (actor.length != 2)
            throw new IncorrectDataException("Expected 2 input coordinates: [x,y]. But given: " + input);
        int x, y;
        try {
            x = Integer.parseInt(actor[0]);
            y = Integer.parseInt(actor[1]);
        } catch (NumberFormatException e) {
            throw new IncorrectDataException("Input coordinates should be integers of the following format: [1,2]." +
                    " Given: " + input);
        }
        if (x < 0 || y < 0 || x >= Game.SIZE || y >= Game.SIZE)
            throw new IncorrectDataException("Input coordinates must be greater or equal to 0" +
                    " and less than game field size: " + Game.SIZE);
        if (!possibleCells.contains(y * Game.SIZE + x))
            throw new IncorrectDataException("Agents cannot be placed on each other. Given: [" + x + "," + y + "]");
        return new int[]{x, y};
    }

    /**
     * Returns Game object with random parameters.
     * @return Game object containing the random placed agents.
     * @throws IncorrectDataException when something went wrong. See message in exception.
     */
    private static Game getRandomGame(int scenario) throws IncorrectDataException {
        Random rand = new Random();
        ArrayList<Integer> possibleCells = new ArrayList<>();
        for (int i = 0; i < Game.SIZE * Game.SIZE; i++) {
            possibleCells.add(i);
        }

        Harry harry = new Harry(0, 0, scenario);
        possibleCells.remove(0);

        int cell = possibleCells.get(rand.nextInt(possibleCells.size()));
        Filch filch = new Filch(cell % Game.SIZE, cell / Game.SIZE);
        possibleCells.remove((Object) cell);

        cell = possibleCells.get(rand.nextInt(possibleCells.size()));
        Cat cat = new Cat(cell % Game.SIZE, cell / Game.SIZE);
        for (BadAgent.VisibilityZone zone : filch.getPerception()) {
            possibleCells.remove((Object) (zone.y * Game.SIZE + zone.x));
        }
        for (BadAgent.VisibilityZone zone : cat.getPerception()) {
            possibleCells.remove((Object) (zone.y * Game.SIZE + zone.x));
        }

        cell = possibleCells.get(rand.nextInt(possibleCells.size()));
        Book book = new Book(cell % Game.SIZE, cell / Game.SIZE);
        possibleCells.remove((Integer) cell);

        cell = possibleCells.get(rand.nextInt(possibleCells.size()));
        Cloak cloak = new Cloak(cell % Game.SIZE, cell / Game.SIZE);
        possibleCells.remove((Integer) cell);

        cell = possibleCells.get(rand.nextInt(possibleCells.size()));
        Exit exit = new Exit(cell % Game.SIZE, cell / Game.SIZE);
        possibleCells.remove((Integer) cell);

        System.out.println("Scenario: " + harry.getScenario());
        return new Game(harry, filch, cat, book, cloak, exit);
    }

    /**
     * Returns standard scenario for the Game specified in the task.
     * @param scenario Scenario for Harry.
     * @return Game object containing the agents.
     * @throws IncorrectDataException when scenario is incorrect.
     */
    private static Game getExampleGame(int scenario) throws IncorrectDataException {
        Harry harry = new Harry(0, 0, scenario);
        Filch filch = new Filch(4, 2);
        Cloak cloak = new Cloak(0, 8);
        Book book = new Book(7, 4);
        Exit exit = new Exit(1, 4);
        Cat cat = new Cat(2, 7);

        return new Game(harry, filch, cat, book, cloak, exit);
    }

    /**
     * Returns test field which represents weakness of the second algorithm (A Star) for the first scenario.
     * @param scenario Scenario for Harry.
     * @return Game object containing the agents.
     * @throws IncorrectDataException when scenario is wrong.
     */
    private static Game getInterestingField1(int scenario) throws IncorrectDataException {
        Harry harry = new Harry(0, 0, scenario);
        Filch filch = new Filch(5, 8);
        Cloak cloak = new Cloak(5, 3);
        Book book = new Book(2, 8);
        Exit exit = new Exit(8, 7);
        Cat cat = new Cat(4, 0);

        return new Game(harry, filch, cat, book, cloak, exit);
    }

    /**
     * Returns test field which represents weakness of the firth algorithm (Backtracking) for the second scenario.
     * @param scenario Scenario for Harry.
     * @return Game object containing the agents.
     * @throws IncorrectDataException when scenario is wrong.
     */
    private static Game getInterestingField2(int scenario) throws IncorrectDataException {
        Harry harry = new Harry(0, 0, scenario);
        Filch filch = new Filch(6, 0);
        Cloak cloak = new Cloak(1, 7);
        Book book = new Book(0, 1);
        Exit exit = new Exit(4, 3);
        Cat cat = new Cat(4, 8);

        return new Game(harry, filch, cat, book, cloak, exit);
    }
}
