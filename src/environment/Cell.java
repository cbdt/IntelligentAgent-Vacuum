package environment;

public class Cell {
    public enum State {
        EMPTY,
        DUST,
        JEWEL,
        DUST_JEWEL
    }

    private State m_state;

    public State getState() {
        return m_state;
    }

    public Environment.Position getPosition() {
        return new Environment.Position(0, 0);
    }
}
