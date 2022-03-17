package agents;

/**
 * Represents a path of Harry.
 */
public class Path extends Agent {
    public Path(int x, int y) {
        super(x, y, '$');
    }

    /**
     * Represent the Path as a string.
     * @return Representation of the Path.
     */
    @Override
    public String toString() {
        return "[" + this.x + "," + this.y + "]";
    }
}
