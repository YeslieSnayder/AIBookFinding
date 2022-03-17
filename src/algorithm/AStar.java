package algorithm;

import agents.*;

import java.util.*;

/**
 * Improved A* algorithm.
 */
public class AStar extends Algorithm {

    /**
     * Copy of the game field.
     */
    private final Agent[][] table;

    public AStar(Agent[][] table) {
        this.table = Arrays.copyOf(table, table.length);
        for (int i = 0; i < table.length; i++) {
            this.table[i] = Arrays.copyOf(table[i], table[0].length);
        }
    }

    /**
     * Finding optimal (minimal) path using A* algorithm.
     * @param harry Starting point.
     * @param exit End point. It is necessary to find a book first.
     * @return Array with a path to end point.
     * @throws NoPathException If there is no path or algorithm cannot find it.
     * @throws IncorrectDataException If something went wrong.
     */
    @Override
    public Path[] getMinimalPath(Harry harry, Exit exit) throws NoPathException, IncorrectDataException {
        ArrayList<Path> res = new ArrayList<>();

        PriorityQueue<PathObj> open = new PriorityQueue<>();
        ArrayList<PathObj> closed = new ArrayList<>();
        ArrayList<PathObj> blocked = new ArrayList<>();

        open.add(new PathObj(harry, 0, false, null));
        PathObj[][] map = new PathObj[table.length][table[0].length];
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                map[y][x] = new PathObj(new Harry(x, y, harry.getScenario()), Integer.MAX_VALUE,
                        table[y][x] instanceof Cloak, null);
            }
        }
        map[harry.y][harry.x] = open.peek();

        Book book = null;
        Cloak cloak = null;
        boolean needBook = true;

        while (!open.isEmpty()) {
            PathObj curPath = open.poll();

            // Check current position
            if (needBook && table[curPath.harry.y][curPath.harry.x] instanceof Book) {
                if (book == null)
                    book = (Book) table[curPath.harry.y][curPath.harry.x];
                res.addAll(getPathToTarget(curPath.prev));
                for (int x = 0; x < map.length; x++) {
                    for (int y = 0; y < map.length; y++) {
                        if (curPath.harry.y != y || curPath.harry.x != x) {
                            map[y][x].gCost = Integer.MAX_VALUE;
                            map[y][x].prev = null;
                            map[y][x].hasCloak = table[y][x] instanceof Cloak;
                            map[y][x].hCost = getHeuristic(x, y, exit);
                        }
                    }
                }
                for (PathObj o : blocked)
                    map[o.harry.y][o.harry.x].gCost = Integer.MIN_VALUE;
                map[curPath.harry.y][curPath.harry.x].gCost = 0;
                map[curPath.harry.y][curPath.harry.x].prev = null;
                map[curPath.harry.y][curPath.harry.x].hCost = getHeuristic(curPath.harry.x, curPath.harry.y, exit);
                open.clear();
                closed.clear();
                needBook = false;
            } else if (!needBook && table[curPath.harry.y][curPath.harry.x] instanceof Exit) {
                res.addAll(getPathToTarget(curPath));
                return res.toArray(new Path[0]);
            } else if (table[curPath.harry.y][curPath.harry.x] instanceof Cloak) {
                if (cloak == null)
                    cloak = (Cloak) table[curPath.harry.y][curPath.harry.x];
                curPath.hasCloak = true;
            }

            closed.add(curPath);

            // Fill map according to what curPath can see
            for (Harry zone : curPath.harry.getVisibilityZone()) {
                if (!open.contains(map[zone.y][zone.x]) && !closed.contains(map[zone.y][zone.x])
                        && (table[zone.y][zone.x] instanceof BadAgent
                        || !curPath.hasCloak && table[zone.y][zone.x] instanceof BadAgent.VisibilityZone)) {
                    map[zone.y][zone.x].gCost = Integer.MIN_VALUE;
                    map[zone.y][zone.x].prev = null;
                    map[zone.y][zone.x].hasCloak = false;
                    blocked.add(map[zone.y][zone.x]);
                    continue;
                }
                if (!open.contains(map[zone.y][zone.x]) && !closed.contains(map[zone.y][zone.x])) {
                    map[zone.y][zone.x].gCost = Integer.MAX_VALUE;
                    map[zone.y][zone.x].prev = null;
                    map[zone.y][zone.x].hasCloak = curPath.hasCloak;
                    open.add(map[zone.y][zone.x]);
                }
                if (open.contains(map[zone.y][zone.x]) && !closed.contains(map[zone.y][zone.x])) {
                    for (Harry n : new Harry(zone.x, zone.y, 1).getVisibilityZone()) {
                        if (closed.contains(map[n.y][n.x])
                                && map[n.y][n.x].getCost() + 1 < map[zone.y][zone.x].getCost()) {
                            map[zone.y][zone.x].gCost = map[n.y][n.x].gCost + 1;
                            map[zone.y][zone.x].prev = map[n.y][n.x];
                            map[zone.y][zone.x].hasCloak = map[n.y][n.x].hasCloak;
                            updateQueue(open, map[zone.y][zone.x]); // Update specific value in OPEN
                        }
                    }
                }
                if (book == null && table[zone.y][zone.x] instanceof Book) {
                    book = (Book) table[zone.y][zone.x];
                    for (int x = 0; x < map.length; x++) {
                        for (int y = 0; y < map.length; y++) {
                            map[y][x].hCost = getHeuristic(x, y, book);
                        }
                    }
                    ArrayList<PathObj> tempList = new ArrayList<>(open);
                    open.clear();
                    open.addAll(tempList);
                } else if (cloak == null && table[zone.y][zone.x] instanceof Cloak) {
                    cloak = (Cloak) table[zone.y][zone.x];
                }
            }

            // Get neighboring cells to movement
            for (Harry zone : new Harry(curPath.harry.x, curPath.harry.y, 1).getVisibilityZone()) {
                PathObj x = map[zone.y][zone.x];
                if (!blocked.contains(x) && !closed.contains(x) && open.contains(x)) {
                    if (curPath.getCost() + 1 < x.getCost()) {
                        x.gCost = curPath.gCost + 1;
                        x.hasCloak = curPath.hasCloak;
                        x.prev = curPath;
                        updateQueue(open, x);   // Update specific value in OPEN
                    }
                } else if (blocked.contains(x) && !closed.contains(x) && curPath.hasCloak
                        && table[x.harry.y][x.harry.x] instanceof BadAgent.VisibilityZone) {
                    blocked.remove(x);
                    x.gCost = curPath.gCost + 1;
                    x.hasCloak = true;
                    x.prev = curPath;
                    open.add(x);
                }
            }
            // Blind (Smart) move
            // If the next move is unknown
            if (!open.isEmpty() && open.peek().gCost == Integer.MAX_VALUE) {
                Harry nextMove = blindStep(curPath.harry, map);
                if (nextMove != null) {
                    map[nextMove.y][nextMove.x].gCost = curPath.gCost + 1;
                    map[nextMove.y][nextMove.x].hasCloak = curPath.hasCloak;
                    map[nextMove.y][nextMove.x].prev = curPath;
                    open.add(map[nextMove.y][nextMove.x]);
                }
            }
        }
        throw new NoPathException();
    }

    /**
     * Implementation for Euclid heuristics.
     * @param x x-coordinate of the current position.
     * @param y y-coordinate of the current position.
     * @param target Coordinates of the target position.
     * @return Euclidean distance from current to target position.
     */
    private int getHeuristic(int x, int y, Agent target) {
        return (int) Math.sqrt(Math.pow(x-target.x, 2) + Math.pow(y-target.y, 2));
    }

    /**
     * Updates the particular element of priority queue.
     * @param open Priority queue where to update the given element.
     * @param newObj Element of the PQ to update.
     */
    private void updateQueue(PriorityQueue<PathObj> open, PathObj newObj) {
        List<PathObj> newQueue = new ArrayList<>();
        PathObj temp;
        while (!open.isEmpty()) {
            temp = open.poll();
            newQueue.add(temp);
            if (temp.harry == newObj.harry)
                break;
        }
        open.addAll(newQueue);
    }

    @Override
    public String toString() {
        return "A Star";
    }

    /**
     * Represents the object where Harry can move on.
     */
    private static class PathObj extends MapObj implements Comparable<PathObj> {
        int hCost;          // Heuristics cost.
        Harry harry;        // Object to collect coordinates and see neighbors.
        boolean hasCloak;   // Current state of the existence of invisibility cloak.

        public PathObj(Harry harry, int cost, boolean hasCloak, PathObj prev) {
            super(cost, prev);
            this.harry = harry;
            this.hCost = 0;
            this.hasCloak = hasCloak;
        }

        /**
         * Returns the cost for this cell.
         * @return the cost for this cell.
         */
        public int getCost() {
            long val = (long) gCost + (long) hCost;
            if (val >= Integer.MAX_VALUE) return Integer.MAX_VALUE;
            if (val <= Integer.MIN_VALUE) return Integer.MIN_VALUE;
            return (int) val;
        }

        /**
         * Returns x-coordinate of this cell.
         * @return x-coordinate of this cell.
         */
        @Override
        protected int getX() {
            return this.harry.x;
        }

        /**
         * Returns y-coordinate of this cell.
         * @return y-coordinate of this cell.
         */
        @Override
        protected int getY() {
            return this.harry.y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PathObj pathObj = (PathObj) o;
            return harry.x == pathObj.harry.x && harry.y == pathObj.harry.y;
        }

        /**
         * Compare 2 objects to distinguish them in priority queue.
         * @param pathObj Object to compare with.
         * @return -1 if the current object more costly (in terms of priority queue / min heap),
         *          1 if the current object less costly,
         *          0 if object are equally costly.
         */
        @Override
        public int compareTo(PathObj pathObj) {
            if (this.gCost == pathObj.gCost)
                return this.hCost - pathObj.hCost;
            return Double.compare(this.getCost(), pathObj.getCost());
        }
    }
}
