package environment;

import agent.Agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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

    public Cell() {
        this(0, 0);
    }

    public Cell(int x, int y) {
        this.m_state = State.EMPTY;
        this.position= new Environment.Position(x, y);
    }

    @Override
    public String toString() {
        return "Cell " + position;
    }

    public static boolean equalCells(Cell c1, Cell c2) {
        return c1.position.x == c2.position.x && c1.position.y == c2.position.y;
    }

    public static boolean differentCells(Cell c1, Cell c2) {
        return !equalCells(c1, c2);
    }

    public static Stack<Cell> getCellPath(Cell end, Map<Cell, Cell> parentsChild) {

        Stack<Cell> cellPath = new Stack<>();

        Cell cell = end;
        while (cell != null)
        {

            cellPath.push(cell);
            cell = parentsChild.get(cell);



           /*for( Agent.Tree parent : visited){

               if(cell == parent.getEnfant()){
                   cell = parent.getParent();
                   break;
               }
           }*/
        }

        return cellPath;
    }

    public void setState(State m_state) {
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

    public static List<Cell> getNeighborCells(Cell cell, Cell[][] grid) {
        List list = new ArrayList<Cell>();

        if (cell.position.y > Environment.minY)
            list.add(grid[cell.position.x][ cell.position.y - 1]);

        if (cell.position.x < Environment.maxX)
            list.add(grid[cell.position.x + 1][ cell.position.y]);

        if (cell.position.y < Environment.maxY)
            list.add(grid[cell.position.x][ cell.position.y + 1]);

        if (cell.position.x > Environment.minX)
            list.add(grid[cell.position.x - 1][ cell.position.y]);

        return list;
    }


}
