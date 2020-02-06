package environment;

public class Cell {
    public enum State {
        EMPTY,
        // Mettre les autres états
    }

    private State m_state;

    public State getState() {
        return m_state;
    }
}
