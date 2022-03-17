package agents;

/**
 * Represents an object on a field.
 */
public abstract class Agent {
    /**
     * Coordinate for x axes.
     */

    public int x;
    /**
     * Coordinate for y axes.
     */
    public int y;

    /**
     * Representation of the agent on the field.
     */
    public final char symbol;

    public Agent(int x, int y, char symbol) {
        this.x = x;
        this.y = y;
        this.symbol = symbol;
    }

    /**
     * Check if the 2 agents are equal.
     * @param o object of the class Agent.
     * @return true if objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agent agent = (Agent) o;
        return x == agent.x && y == agent.y && symbol == agent.symbol;
    }
}
