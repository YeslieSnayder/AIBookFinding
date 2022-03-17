package agents;

import game.Game;

import java.util.ArrayList;

/**
 * Represents bad agents (Filch and Cat) on a field.
 */
public abstract class BadAgent extends Agent {
    /**
     * Diameter of the perception zone = 2 * Radius + 1.
     */
    public final int perception;

    public BadAgent(int x, int y, char symbol, int perception) {
        super(x, y, symbol);
        this.perception = perception;
    }

    /**
     * Calculates and returns perception zone of the bad agent.
     * @return Array with cell representing perception zone of the bad agent.
     */
    public VisibilityZone[] getPerception() {
        ArrayList<VisibilityZone> answer = new ArrayList<>();
        int step = perception / 2;
        for (int y = this.y-step; y <= this.y+step; y++) {
            if (y < 0 || y >= Game.SIZE) continue;
            for (int x = this.x-step; x <= this.x+step; x++) {
                if (x < 0 || x >= Game.SIZE) continue;
                answer.add(new VisibilityZone(x, y));
            }
        }
        return answer.toArray(new VisibilityZone[0]);
    }

    /**
     * Represents perception zone of the bad agent.
     */
    public static class VisibilityZone extends Agent {
        public VisibilityZone(int x, int y) {
            super(x, y, '*');
        }
    }
}
