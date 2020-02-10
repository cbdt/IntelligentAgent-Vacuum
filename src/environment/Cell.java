package environment;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    public enum State {
        EMPTY,
        DUST,
        JEWEL,
        DUST_JEWEL
    }

    private State m_state;

    public Environment.Position position;

    public State getState() {
        return m_state;
    }

    public Cell(int x, int y) {
        x = 0;
        y = 0;
        this.m_state = State.EMPTY;
        Environment.Position position = new Environment.Position(x, y);
    }

    public Cell(Environment.Position position, State state) {
        state = State.EMPTY;
        this.position = position;

    }

    public static boolean equalCells(Cell c1, Cell c2) {
        return c1.position.x == c2.position.x && c1.position.y == c2.position.y;
    }

    public static boolean differentCells(Cell c1, Cell c2) {
        return !equalCells(c1, c2);
    }

    public static Cell[] getCellPath(Cell start, Cell end) {

    }

    public void setM_state(State m_state) {
        this.m_state = m_state;
    }

    public Environment.Position getPosition() {
        return new Environment.Position(0, 0);
    }

    public boolean isAbove(Cell cell) {
        return position.y < cell.position.y;
    }

    public boolean isRightFrom(Cell cell) {
        return position.x > cell.position.x;
    }

    public boolean isBelow(Cell cell) {
        return position.y > cell.position.y;
    }

    public boolean isLeftFrom(Cell cell) {
        return position.x < cell.position.x;
    }

    public <Cell> List<Cell> getNeighborCells(Cell cell, Cell[][] grid) {
        List list = new ArrayList<Cell>();

        if ()

    }


}
