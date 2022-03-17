package algorithm;

import agents.Exit;
import agents.Harry;
import agents.Path;
import game.Game;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Base class for algorithms.
 */
public abstract class Algorithm {
    /**
     * Returns a path to exit with book.
     * @param harry Starting point.
     * @param exit End point. It is necessary to find a book first.
     * @return Array with a path to end point.
     * @throws NoPathException If there is no path from start to the end.
     * @throws IncorrectDataException If something went wrong.
     */
    public abstract Path[] getMinimalPath(Harry harry, Exit exit) throws NoPathException, IncorrectDataException;

    /**
     * Represents a blind step using priority.
     * @param curPath Current position of the Harry.
     * @param map Known objects on the field.
     * @return The next movement for the Harry.
     */
    protected Harry blindStep(Harry curPath, MapObj[][] map) {
        // Calculating priority for next movement
        int up = 0, ur = 0, r = 0, dr = 0, down = 0, dl = 0, l = 0, ul = 0;
        for (int x = curPath.x-1; x <= curPath.x+1; x++) {
            if (x < 0 || x >= Game.SIZE)
                continue;
            // Up
            if (curPath.y+2 < Game.SIZE && map[curPath.y+2][x] != null
                    && map[curPath.y+2][x].gCost < 0) {
                if (x == curPath.x-1) { up++; ul++; }
                else if (x == curPath.x) { up++; ul++; ur++; }
                else { up++; ur++; }
            }
            // Down
            if (curPath.y-2 >= 0 && map[curPath.y-2][x] != null
                    && map[curPath.y-2][x].gCost < 0) {
                if (x == curPath.x-1) { down++; dl++; }
                else if (x == curPath.x) { down++; dl++; dr++; }
                else { down++; dr++; }
            }
        }
        for (int y = curPath.y-1; y <= curPath.y+1; y++) {
            if (y < 0 || y >= Game.SIZE)
                continue;
            // Right
            if (curPath.x+2 < Game.SIZE && map[y][curPath.x+2] != null
                    && map[y][curPath.x+2].gCost < 0) {
                if (y == curPath.y-1) { r++; dr++; }
                else if (y == curPath.y) { r++; ur++; dr++; }
                else { r++; ur++; }
            }
            // Left
            if (curPath.x-2 >= 0 && map[y][curPath.x-2] != null
                    && map[y][curPath.x-2].gCost < 0) {
                if (y == curPath.y-1) { l++; dl++; }
                else if (y == curPath.y) { l++; ul++; dl++; }
                else { l++; ul++; }
            }
        }
        // Remove unable steps
        if (curPath.x-1 < 0) {
            l = Integer.MAX_VALUE;
            ul = Integer.MAX_VALUE;
            dl = Integer.MAX_VALUE;
        }
        if (curPath.x+1 >= Game.SIZE) {
            r = Integer.MAX_VALUE;
            ur = Integer.MAX_VALUE;
            dr = Integer.MAX_VALUE;
        }
        if (curPath.y-1 < 0) {
            down = Integer.MAX_VALUE;
            dl = Integer.MAX_VALUE;
            dr = Integer.MAX_VALUE;
        }
        if (curPath.y+1 >= Game.SIZE) {
            up = Integer.MAX_VALUE;
            ul = Integer.MAX_VALUE;
            ur = Integer.MAX_VALUE;
        }
        // Make a movement
        try {
            if (checkMin(up, up, l, r, down, dl, dr, ul, ur) && curPath.y + 1 < Game.SIZE) {
                if (map[curPath.y + 1][curPath.x] != null
                        && map[curPath.y][curPath.x].gCost + 1 > map[curPath.y + 1][curPath.x].gCost)
                    return null;
                return new Harry(curPath.x, curPath.y + 1, curPath.getScenario());
            } else if (checkMin(ur, up, l, r, down, dl, dr, ul, ur)
                    && curPath.y + 1 < Game.SIZE && curPath.x + 1 < Game.SIZE) {
                if (map[curPath.y + 1][curPath.x + 1] != null
                        && map[curPath.y][curPath.x].gCost + 1 > map[curPath.y + 1][curPath.x + 1].gCost)
                    return null;
                return new Harry(curPath.x + 1, curPath.y + 1, curPath.getScenario());
            } else if (checkMin(r, up, l, r, down, dl, dr, ul, ur) && curPath.x + 1 < Game.SIZE) {
                if (map[curPath.y][curPath.x + 1] != null
                        && map[curPath.y][curPath.x].gCost + 1 > map[curPath.y][curPath.x + 1].gCost)
                    return null;
                return new Harry(curPath.x + 1, curPath.y, curPath.getScenario());
            } else if (checkMin(dr, up, l, r, down, dl, dr, ul, ur)
                    && curPath.y - 1 >= 0 && curPath.x + 1 < Game.SIZE) {
                if (map[curPath.y - 1][curPath.x + 1] != null
                        && map[curPath.y][curPath.x].gCost + 1 > map[curPath.y - 1][curPath.x + 1].gCost)
                    return null;
                return new Harry(curPath.x + 1, curPath.y - 1, curPath.getScenario());
            } else if (checkMin(down, up, l, r, down, dl, dr, ul, ur) && curPath.y - 1 >= 0) {
                if (map[curPath.y - 1][curPath.x] != null
                        && map[curPath.y][curPath.x].gCost + 1 > map[curPath.y - 1][curPath.x].gCost)
                    return null;
                return new Harry(curPath.x, curPath.y - 1, curPath.getScenario());
            } else if (checkMin(dl, up, l, r, down, dl, dr, ul, ur)
                    && curPath.y - 1 >= 0 && curPath.x - 1 >= 0) {
                if (map[curPath.y - 1][curPath.x - 1] != null
                        && map[curPath.y][curPath.x].gCost + 1 > map[curPath.y - 1][curPath.x - 1].gCost)
                    return null;
                return new Harry(curPath.x - 1, curPath.y - 1, curPath.getScenario());
            } else if (checkMin(l, up, l, r, down, dl, dr, ul, ur) && curPath.x - 1 >= 0) {
                if (map[curPath.y][curPath.x - 1] != null
                        && map[curPath.y][curPath.x].gCost + 1 > map[curPath.y][curPath.x - 1].gCost)
                    return null;
                return new Harry(curPath.x - 1, curPath.y, curPath.getScenario());
            } else if (checkMin(ul, up, l, r, down, dl, dr, ul, ur)
                    && curPath.y + 1 < Game.SIZE && curPath.x - 1 >= 0) {
                if (map[curPath.y + 1][curPath.x - 1] != null
                        && map[curPath.y][curPath.x].gCost + 1 > map[curPath.y + 1][curPath.x - 1].gCost)
                    return null;
                return new Harry(curPath.x - 1, curPath.y + 1, curPath.getScenario());
            }
        } catch (IncorrectDataException ignored) {}
        return null;
    }

    /**
     * Supporting function for the blind step.
     * Calculates condition: is the given number a minimum among other input values.
     * @param num Given number to check the condition.
     * @param up The number of bad cells on the top cells.
     * @param l The number of bad cells on the left cells.
     * @param r The number of bad cells on the right cells.
     * @param down The number of bad cells on the bottom cells.
     * @param dl The number of bad cells on the bottom left cells.
     * @param dr The number of bad cells on the bottom right cells.
     * @param ul The number of bad cells on the upper left cells.
     * @param ur The number of bad cells on the upper right cells.
     * @return The condition: is the given number a minimum among other input values.
     */
    private boolean checkMin(int num, int up, int l, int r, int down, int dl, int dr, int ul, int ur) {
        return num <= up && num <= l && num <= r && num <= down && num <= dl && num <= dr && num <= ul && num <= ur;
    }

    /**
     * Deconstruct a path to the cell using target cell.
     * @param target Cell to which deconstruct a path.
     * @return Path to target cell.
     */
    protected ArrayList<Path> getPathToTarget(MapObj target) {
        ArrayList<Path> res = new ArrayList<>();
        MapObj temp = target;
        while (temp != null) {
            res.add(new Path(temp.getX(), temp.getY()));
            temp = temp.prev;
        }
        Collections.reverse(res);
        return res;
    }

    /**
     * Represents cells which the main character can see and step on.
     */
    protected abstract static class MapObj {
        /**
         * Movement cost to that cell.
         */
        int gCost;

        /**
         * Link to the previous cell from which to move on that cell.
         */
        MapObj prev;

        protected MapObj(int cost, MapObj prev) {
            this.gCost = cost;
            this.prev = prev;
        }

        /**
         * Returns x coordinate of the cell.
         * @return x coordinate of the cell.
         */
        protected abstract int getX();

        /**
         * Returns y coordinate of the cell.
         * @return y coordinate of the cell.
         */
        protected abstract int getY();
    }
}
