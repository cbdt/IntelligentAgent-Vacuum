package agent;

import agent.effectors.Arms;
import agent.effectors.Effector;
import agent.effectors.Vaccum;
import agent.effectors.Wheels;
import agent.sensors.GridSensor;
import agent.sensors.Sensor;
import environment.Cell;
import environment.Environment;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Agent implements Runnable {

    //TODO: A renommer, je sais pas encore quel type d'exploration on va faire (cc: Clément C)
    enum Exploration { TYPE_1, TYPE_2}

    public enum Action {
        PICK_UP,
        CLEAN,
        MOVE_UP,
        MOVE_DOWN,
        MOVE_LEFT,
        MOVE_RIGHT,
    }

    private Environment m_environment;
    private Exploration m_explorationType;

    private int m_battery = 50;
    private boolean m_isAlive = true;
    private Environment.Position m_currentPosition;

    private Sensor<Cell[][]> m_gridSensor;
    private Effector m_arms, m_vacuum, m_wheels;

    private Stack<Action> m_actions;

    public Agent(Environment environment) {
        this.m_environment = environment;

        this.m_gridSensor = new GridSensor(m_environment);
        this.m_arms = new Arms(m_environment);
        this.m_vacuum = new Vaccum(m_environment);
        this.m_wheels = new Wheels(m_environment);

        this.m_currentPosition = m_environment.getInitialPosition();
        this.m_actions = new Stack<>();
    }

    @Override
    public void run() {
        evolve();
    }

    public void setExploration(Exploration exploration) {
        this.m_explorationType = exploration;
    }

    private void evolve() {
        while(m_isAlive) {
            Cell[][] perceivedGrid = observeEnvironment();
            m_actions = updateState(perceivedGrid);
            Action action = chooseAction(m_actions);
            processAction(action);

            m_isAlive = m_battery > 0;
        }
    }

    /* On essaie d'avoir le moins de side-effect pour les fonctions ci-dessous, plus lisible si fonctions "pures" */
    private Cell[][] observeEnvironment() {
        return this.m_gridSensor.analyze();
    }

    private Stack<Action> updateState(Cell[][] perceivedGrid) {
        List<Cell> beliefs = getBelief(perceivedGrid);
        Cell desiredCell = getDesire(beliefs);
        return getIntention(perceivedGrid, desiredCell);
    }

    private Action chooseAction(Stack<Action> actions) {
        if (actions.isEmpty()) return null;
        return actions.pop();
    }

    private void processAction(Action action) {
        if(action == null) { return; } // Ici null veut dire qu'on fait rien, on reste sur la case.
        switch (action) {
            case MOVE_UP:
            case MOVE_DOWN:
            case MOVE_LEFT:
            case MOVE_RIGHT:
                updatePosition(action);
                m_wheels.makeAction(action);
                break;
            case CLEAN:
                m_vacuum.makeAction(null);
                break;
            case PICK_UP:
                m_arms.makeAction(null);
                break;
            default:
                return;
        }
        m_battery--;
    }

    private void updatePosition(Action action) {
        switch (action) {
            case MOVE_UP:
                m_currentPosition.update(new Environment.Position(0, 1));
                break;
            case MOVE_DOWN:
                m_currentPosition.update(new Environment.Position(0, -1));
                break;
            case MOVE_LEFT:
                m_currentPosition.update(new Environment.Position(-1, 0));
                break;
            case MOVE_RIGHT:
                m_currentPosition.update(new Environment.Position(1, 0));
                break;
        }
    }

    /**
     * @param grid Cell[][]
     * @return List<Cell> Liste des cellules non vides que l'on observe.
     */
    private List<Cell> getBelief(Cell[][] grid) {
        List<Cell> emptyCells = new LinkedList<>();

        for (Cell[] rangeCell: grid) {
            for(Cell cell: rangeCell) {
                if(cell.getState() == Cell.State.EMPTY) {
                    emptyCells.add(cell);
                }
            }
        }
        return emptyCells;
    }

    /**
     * Retourne la cellule la plus adaptée parmis la liste des beliefs (basée sur une métrique de performance).
     * @param beliefs Liste des cellules non vides que l'on observe avec nos capteurs.
     * @return Cell Cellule que l'on doit aller parcourir
     */
    private Cell getDesire(List<Cell> beliefs) {
        double bestPerformance = Integer.MIN_VALUE;
        Cell bestCell = new Cell();

        for(Cell cell: beliefs) {
            double currentPerformance = getPerformance(cell);
            if(currentPerformance > bestPerformance)  {
                bestCell = cell;
                bestPerformance = currentPerformance;
            }
        }

        return bestCell;
    }

    /**
     * Retourne les actions que l'agent doit effectuer pour atteindre la cellule Cell
     * @param grid
     * @param desiredCell
     * @return Stack Liste des actions à effectuer.
     */
    private Stack<Action> getIntention(Cell[][] grid, Cell desiredCell) {
        // TODO: Peut-être ajouter une condition pour savoir si c'est rentable de se déplacer en terme (cf. fixer un seuil minimal ?)
        // Car là il se déplace même si c'est pas rentable.
        // Le seuil peut-être de 0, pas perdant.

        if(getPerformance(desiredCell) < 0) { // on fait rien si pas rentable de se déplacer.
            return new Stack<>();
        }

        return explore(grid, desiredCell);
    }

    public Stack<Action> explore(Cell[][] grid, Cell desiredCell) {
        Stack<Action> actions = new Stack<>();

        switch (this.m_explorationType) {
            case TYPE_1:
                actions = exploration_1(grid, desiredCell);
                break;
            case TYPE_2:
                actions =  exploration_2(grid, desiredCell);
                break;
        }
        return actions;
    }

    // TODO: Algorithmes d'exploration

    public Stack<Action> exploration_1(Cell[][] grid, Cell desiredCell) {
        return new Stack<Action>();
    }

    public Stack<Action> exploration_2(Cell[][] grid, Cell desiredCell) {
        return new Stack<Action>();
    }

    /** UTILITY FUNCTION */

    private double getPerformance(Cell cell) {

        // Récompense
        int recompense = 0;

        // Dépense énérgétique pour effectuer l'action une fois sur la cellule
        int energieAction = 1;
        switch (cell.getState()) {
            case EMPTY:
                break;
            case DUST:
                recompense = 1;
                break;
            case JEWEL:
                recompense = 2;
                break;
            case DUST_JEWEL:
                recompense = 3;
                energieAction = 2;
                break;
            default:
                break;
        }

        // Malus : Energie dépensée (distance + action effectuée)
        double energieDistance = getDistance(m_currentPosition, cell.getPosition());
        double energie = energieAction + energieDistance;

        return recompense - energie;
    }

    public double getDistance(Environment.Position a, Environment.Position b) {
        return Math.sqrt(Math.pow(b.getX() - a.getX(), 2) + Math.pow(b.getY() - a.getY(), 2));
    }
}
