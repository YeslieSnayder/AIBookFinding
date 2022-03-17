package algorithm;

import agents.*;

import java.util.*;

/**
 * Backtracking algorithm.
 */
public class Backtracking extends Algorithm {
    private final Agent[][] table;  // copy of the game field.
    private Book book;              // found book object.
    private Cloak cloak;            // found invisibility cloak object.
    private boolean hadCloak;       // did Harry use invisibility cloak on previous part of the algorithm.

    public Backtracking(Agent[][] table) {
        // Copy table to avoid changes in the initial map.
        this.table = Arrays.copyOf(table, table.length);
        for (int i = 0; i < table.length; i++) {
            this.table[i] = Arrays.copyOf(table[i], table[i].length);
        }
    }

    /**
     * Finding optimal (minimal) path using Backtracking algorithm.
     * @param mainHarry Starting point.
     * @param exit End point. It is necessary to find a book first.
     * @return Array with an optimal path to end point.
     * @throws NoPathException If there is no path or algorithm cannot find it.
     * @throws IncorrectDataException If something went wrong.
     */
    @Override
    public Path[] getMinimalPath(Harry mainHarry, Exit exit) throws NoPathException, IncorrectDataException {
        // To omit changes of the main object
        Harry harry = new Harry(mainHarry.x, mainHarry.y, mainHarry.getScenario());

        // Find a path to book
        PathObj[][] mapToBook = new PathObj[table.length][table[0].length];
        PathObj[][] mapToExit = new PathObj[table.length][table[0].length];
        PathObj[][] mapToBookWithCloak = new PathObj[table.length][table[0].length];
        PathObj[][] mapToExitWithCloak = new PathObj[table.length][table[0].length];
        PathObj[][] mapToExitWithCloakInitially = new PathObj[table.length][table[0].length];
        mapToBook[harry.y][harry.x] = new PathObj(harry.x, harry.y, 0, null);

        if (harry.getScenario() == 1)
            backtracking(harry, mapToBook, false, true);
        else if (harry.getScenario() == 2)
            backtrackingFor2Scenario(harry, mapToBook, false, true);
        boolean isCloakUsed = hadCloak;

        // Find the solution using invisibility cloak
        if (cloak != null && !isCloakUsed && mapToBook[cloak.y][cloak.x] != null) {
            copyPathToCloak(mapToBook, mapToBookWithCloak);
            if (harry.getScenario() == 1)
                backtracking(new Harry(cloak.x, cloak.y, harry.getScenario()), mapToBookWithCloak, true, true);
            else if (harry.getScenario() == 2)
                backtrackingFor2Scenario(new Harry(cloak.x, cloak.y, harry.getScenario()), mapToBookWithCloak, true, true);
        }
        if (book == null)
            throw new NoPathException();

        // Find a path to exit
        harry.x = book.x;
        harry.y = book.y;
        hadCloak = false;
        mapToExit[harry.y][harry.x] = new PathObj(harry.x, harry.y, 0, null);
        if (harry.getScenario() == 1)
            backtracking(harry, mapToExit, isCloakUsed, false);
        else if (harry.getScenario() == 2) {
            copyBadCell(mapToBook, mapToExit);
            backtrackingFor2Scenario(harry, mapToExit, isCloakUsed, false);
        }

        if (cloak != null && !isCloakUsed && !hadCloak && mapToExit[cloak.y][cloak.x] != null) {
            copyPathToCloak(mapToExit, mapToExitWithCloak);
            if (harry.getScenario() == 1)
                backtracking(new Harry(cloak.x, cloak.y, harry.getScenario()), mapToExitWithCloak, true, false);
            else if (harry.getScenario() == 2)
                backtrackingFor2Scenario(new Harry(cloak.x, cloak.y, harry.getScenario()), mapToExitWithCloak, true, false);
        }
        if (mapToExit[exit.y][exit.x] == null && mapToExitWithCloak[exit.y][exit.x] == null) {
            throw new NoPathException();
        }

        // Use cloak while looking for the book
        if (isCloakUsed) {
            ArrayList<Path> path = getPathToTarget(mapToBook[book.y][book.x].prev);
            path.addAll(getPathToTarget(mapToExit[exit.y][exit.x]));
            return path.toArray(new Path[0]);
        }

        mapToExitWithCloakInitially[harry.y][harry.x] = new PathObj(harry.x, harry.y, 0, null);
        if (harry.getScenario() == 1)
            backtracking(harry, mapToExitWithCloakInitially, true, false);
        else if (harry.getScenario() == 2) {
            copyBadCell(mapToExit, mapToExitWithCloakInitially);
            backtrackingFor2Scenario(harry, mapToExitWithCloakInitially, true, false);
        }

        // Use cloak while looking for the book
        int costPP = mapToBookWithCloak[book.y][book.x] == null || mapToExitWithCloakInitially[exit.y][exit.x] == null
                ? Integer.MAX_VALUE
                : mapToBookWithCloak[book.y][book.x].gCost + mapToExitWithCloakInitially[exit.y][exit.x].gCost;

        // Use cloak while looking for the exit
        int costNP = mapToExitWithCloak[exit.y][exit.x] == null || mapToBook[book.y][book.x] == null
                ? Integer.MAX_VALUE
                : mapToExitWithCloak[exit.y][exit.x].gCost + mapToBook[book.y][book.x].gCost;

        // Do not use cloak at all
        int costNN = mapToExit[exit.y][exit.x] == null || mapToBook[book.y][book.x] == null
                ? Integer.MAX_VALUE
                : mapToBook[book.y][book.x].gCost + mapToExit[exit.y][exit.x].gCost;

        if (costPP < costNP && costPP < costNN) {
            ArrayList<Path> path = new ArrayList<>(getPathToTarget(mapToBookWithCloak[book.y][book.x].prev));
            path.addAll(getPathToTarget(mapToExitWithCloakInitially[exit.y][exit.x]));
            return path.toArray(new Path[0]);
        } else if (costNP < costPP && costNP < costNN) {
            ArrayList<Path> path = new ArrayList<>(getPathToTarget(mapToBook[book.y][book.x].prev));
            path.addAll(getPathToTarget(mapToExitWithCloak[exit.y][exit.x]));
            return path.toArray(new Path[0]);
        }
        ArrayList<Path> path = new ArrayList<>(getPathToTarget(mapToBook[book.y][book.x].prev));
        path.addAll(getPathToTarget(mapToExit[exit.y][exit.x]));
        return path.toArray(new Path[0]);
    }

    /**
     * Copies found bad agents and cloak from source to destination tables.
     * Note: Bad agents are Mrs. Norris (Cat), Argus Filch (Filch), and their perception zone.
     * @param src Source table that already contains information about found bad agents.
     * @param dst Destination table where to copy information about found bad agents.
     */
    private void copyBadCell(PathObj[][] src, PathObj[][] dst) {
        for (int x = 0; x < src.length; x++) {
            for (int y = 0; y < src[0].length; y++) {
                if (src[y][x] != null && src[y][x].gCost < 0
                        || cloak != null && x == cloak.x && y == cloak.y)
                    dst[y][x] = src[y][x];
            }
        }
    }

    /**
     * Copies path to invisibility cloak from source to destination tables.
     * After it copies found bad agents.
     * Note: Bad agents are Mrs. Norris (Cat), Argus Filch (Filch), and their perception zone.
     * @param src Source table from which to copy the path to the cloak and bad agents.
     * @param dst Destination table to which to copy the path to the cloak and bad agents.
     */
    private void copyPathToCloak(PathObj[][] src, PathObj[][] dst) {
        PathObj temp = src[cloak.y][cloak.x];
        while (temp != null) {
            dst[temp.y][temp.x] = temp;
            temp = (PathObj) temp.prev;
        }
        copyBadCell(src, dst);
    }

    /**
     * Represents current state of the algorithm.
     * Used to avoid recursive calls.
     */
    private static class State {
        Harry harry;        // Coordinates of the current state
        boolean hasCloak;   // Existence of invisibility cloak on current state

        public State(Harry harry, boolean hasCloak) {
            this.harry = harry;
            this.hasCloak = hasCloak;
        }
    }

    /**
     * Backtracking for the second scenario.
     * @param harry Position of the starting point.
     * @param map Map to be filled with paths.
     * @param hasC Has invisibility cloak or not.
     * @param findBook true if needed to find book,
     *                 false if needed to find exit.
     * @throws IncorrectDataException If something went wrong.
     */
    private void backtrackingFor2Scenario(Harry harry, PathObj[][] map, boolean hasC, boolean findBook) throws IncorrectDataException {
        Stack<State> states = new Stack<>();
        states.push(new State(harry, hasC));

        while (!states.empty()) {
            Harry curPath = states.peek().harry;
            boolean hasCloak = states.pop().hasCloak;

            if (map[curPath.y][curPath.x] == null
                    || map[curPath.y][curPath.x].gCost < 0
                    || map[curPath.y][curPath.x].gCost == Integer.MAX_VALUE)
                continue;

            // Check current cell because we can "randomly" step on unknown cell
            if (book == null && table[curPath.y][curPath.x] instanceof Book)
                book = (Book) table[curPath.y][curPath.x];
            else if (cloak == null && table[curPath.y][curPath.x] instanceof Cloak)
                cloak = (Cloak) table[curPath.y][curPath.x];

            // Update map according to visible zone
            for (Harry zone : curPath.getVisibilityZone()) {
                if (table[zone.y][zone.x] instanceof BadAgent
                        || !hasCloak && table[zone.y][zone.x] instanceof BadAgent.VisibilityZone) {
                    map[zone.y][zone.x] = new PathObj(zone.x, zone.y, Integer.MIN_VALUE, null);
                } else if (book == null && table[zone.y][zone.x] instanceof Book) {
                    book = (Book) table[zone.y][zone.x];
                } else if (cloak == null && table[zone.y][zone.x] instanceof Cloak) {
                    cloak = (Cloak) table[zone.y][zone.x];
                }
                if (map[zone.y][zone.x] == null) {
                    map[zone.y][zone.x] = new PathObj(zone.x, zone.y, Integer.MAX_VALUE, null);
                }
            }
            // Move on known cell
            boolean cannotMove = true;
            List<Harry> visibleZones = new Harry(curPath.x, curPath.y, 1).getVisibilityZone();
            Collections.reverse(visibleZones);
            for (Harry zone : visibleZones) {
                if (map[zone.y][zone.x] != null && (map[zone.y][zone.x].gCost > map[curPath.y][curPath.x].gCost + 1
                        && (hasCloak || !(table[zone.y][zone.x] instanceof BadAgent.VisibilityZone))
                        || hasCloak && table[zone.y][zone.x] instanceof BadAgent.VisibilityZone
                        && map[zone.y][zone.x].gCost == Integer.MIN_VALUE)
                ) {
                    cannotMove = false;
                    map[zone.y][zone.x] = new PathObj(zone.x, zone.y, map[curPath.y][curPath.x].gCost + 1, map[curPath.y][curPath.x]);
                    if (findBook && book != null && zone.x == book.x && zone.y == book.y) {
                        hadCloak = hasCloak;
                        continue;
                    } else if (!findBook && table[zone.y][zone.x] instanceof Exit) {
                        continue;
                    }
                    if (!hasCloak && cloak != null && zone.x == cloak.x && zone.y == cloak.y)
                        states.push(new State(new Harry(zone.x, zone.y, curPath.getScenario()), true));
                    else
                        states.push(new State(new Harry(zone.x, zone.y, curPath.getScenario()), hasCloak));
                } else if (map[zone.y][zone.x] == null)
                    states.push(new State(new Harry(zone.x, zone.y, curPath.getScenario()), hasCloak));
            }
            // Move on unknown cell
            if (cannotMove) {
                Harry nextMove = blindStep(curPath, map);
                if (nextMove != null) {
                    map[nextMove.y][nextMove.x] = new PathObj(nextMove.x, nextMove.y,
                            map[curPath.y][curPath.x].gCost + 1, map[curPath.y][curPath.x]);
                    states.push(new State(nextMove, hasCloak));
                }
            }
        }
    }

    /**
     * Backtracking for the first scenario. Standard backtracking algorithm.
     * @param curPath Position of the starting point.
     * @param map Map to be filled with paths.
     * @param hasCloak Has invisibility cloak or not.
     * @param findBook true if needed to find book,
     *                 false if needed to find exit.
     * @throws IncorrectDataException If something went wrong.
     */
    private void backtracking(Harry curPath, PathObj[][] map, boolean hasCloak, boolean findBook)
            throws IncorrectDataException {
        List<Harry> visiblePath = curPath.getVisibilityZone();
        for (Harry path : visiblePath) {
            if (table[path.y][path.x] instanceof BadAgent ||
                    !hasCloak && table[path.y][path.x] instanceof BadAgent.VisibilityZone)
                continue;
            if (map[path.y][path.x] == null || map[path.y][path.x].gCost > map[curPath.y][curPath.x].gCost + 1) {
                map[path.y][path.x] = new PathObj(path.x, path.y, map[curPath.y][curPath.x].gCost + 1, map[curPath.y][curPath.x]);
                if (!hasCloak && table[path.y][path.x] instanceof Cloak) {
                    cloak = (Cloak) table[path.y][path.x];
                    hasCloak = true;
                }
                if (findBook && table[path.y][path.x] instanceof Book) {
                    hadCloak = hasCloak;
                    if (this.book == null)
                        this.book = (Book) table[path.y][path.x];
                    return;
                } else if (!findBook && table[path.y][path.x] instanceof Exit) {
                    return;
                }
                backtracking(path, map, hasCloak, findBook);
            }
        }
    }

    @Override
    public String toString() {
        return "Backtracking";
    }

    /**
     * Represents the object where Harry can move on.
     */
    private static class PathObj extends MapObj {
        int x, y;

        PathObj(int x, int y, int cost, PathObj prev) {
            super(cost, prev);
            this.x = x;
            this.y = y;
        }

        /**
         * Returns x-coordinate of this cell.
         * @return x-coordinate of this cell.
         */
        @Override
        protected int getX() {
            return this.x;
        }

        /**
         * Returns y-coordinate of this cell.
         * @return y-coordinate of this cell.
         */
        @Override
        protected int getY() {
            return this.y;
        }
    }
}
