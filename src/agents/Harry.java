package agents;

import algorithm.IncorrectDataException;
import game.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the main character Harry Potter.
 */
public class Harry extends Agent {
    /**
     * Scenario for the algorithm. Must be set to 1 or 2.
     */
    private final int scenario;

    public Harry(int x, int y, int scenario) throws IncorrectDataException {
        super(x, y, 'H');
        if (scenario != 1 && scenario != 2)
            throw new IncorrectDataException("Expected scenario for Harry 1 or 2. Given: " + scenario);
        this.scenario = scenario;
    }

    /**
     * Calculates and returns visibility zone of the Harry.
     * If scenario == 1, then visibility zone of Harry:
     * + + +
     * + H +
     * + + +
     * If scenario == 2, then visibility zone of Harry:
     *   + + +
     * +       +
     * +   H   +
     * +       +
     *   + + +
     * Note: pluses (+) - visibility zone of Harry, H - Harry (not in visibility zone).
     * @return Array of objects Harry that are represented as visibility zone.
     * @throws IncorrectDataException
     */
    public List<Harry> getVisibilityZone() throws IncorrectDataException {
        ArrayList<Harry> list = new ArrayList<>();
        if (scenario == 1) {
            if (this.y + 1 < Game.SIZE) list.add(new Harry(this.x, this.y+1, scenario));
            if (this.y + 1 < Game.SIZE && this.x + 1 < Game.SIZE) list.add(new Harry(this.x+1, this.y+1, scenario));
            if (this.x + 1 < Game.SIZE) list.add(new Harry(this.x+1, this.y, scenario));
            if (this.x + 1 < Game.SIZE && this.y - 1 >= 0) list.add(new Harry(this.x+1, this.y-1, scenario));
            if (this.y - 1 >= 0) list.add(new Harry(this.x, this.y-1, scenario));
            if (this.x - 1 >= 0 && this.y - 1 >= 0) list.add(new Harry(this.x-1, this.y-1, scenario));
            if (this.x - 1 >= 0) list.add(new Harry(this.x-1, this.y, scenario));
            if (this.y + 1 < Game.SIZE && this.x - 1 >= 0) list.add(new Harry(this.x-1, this.y+1, scenario));
        } else if (scenario == 2) {
            if (x-1 >= 0 && y+2 < Game.SIZE) list.add(new Harry(x-1, y+2, scenario));
            if (y+2 < Game.SIZE) list.add(new Harry(x, y+2, scenario));
            if (x+1 < Game.SIZE && y+2 < Game.SIZE) list.add(new Harry(x+1, y+2, scenario));
            if (x+2 < Game.SIZE && y+1 < Game.SIZE) list.add(new Harry(x+2, y+1, scenario));
            if (x+2 < Game.SIZE) list.add(new Harry(x+2, y, scenario));
            if (x+2 < Game.SIZE && y-1 >= 0) list.add(new Harry(x+2, y-1, scenario));
            if (x+1 < Game.SIZE && y-2 >= 0) list.add(new Harry(x+1, y-2, scenario));
            if (y-2 >= 0) list.add(new Harry(x, y-2, scenario));
            if (x-1 >= 0 && y-2 >= 0) list.add(new Harry(x-1, y-2, scenario));
            if (x-2 >= 0 && y-1 >= 0) list.add(new Harry(x-2, y-1, scenario));
            if (x-2 >= 0) list.add(new Harry(x-2, y, scenario));
            if (x-2 >= 0 && y+1 < Game.SIZE) list.add(new Harry(x-2, y+1, scenario));
        }
        return list;
    }

    /**
     * Returns scenario of the algorithm.
     * @return scenario of the algorithm.
     */
    public int getScenario() {
        return scenario;
    }

    /**
     * Compare 2 objects of type Harry.
     * @param o object of the class Harry (Agent).
     * @return true if objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
