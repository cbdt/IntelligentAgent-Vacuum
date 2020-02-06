package agent;

import agent.sensors.GridSensor;
import agent.sensors.Sensor;
import environment.Cell;
import environment.Environment;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Agent {

    private Environment m_environment;

    private boolean m_isAlive = true;

    private Sensor<Cell[][]> m_gridSensor;

    private Cell[][] m_perceivedGrid;

    public Agent(Environment environment) {
        this.m_environment = environment;

        this.m_gridSensor = new GridSensor(m_environment);
    }

    public void evolve() {
        while(m_isAlive) {
            this.m_perceivedGrid = observeEnvironment();
            updateState(this.m_perceivedGrid);
            chooseAction();
            processAction();
        }
    }

    /* On essaie d'avoir le moins de side-effect pour les fonctions ci-dessous, plus lisible si fonctions "pures" */
    private Cell[][] observeEnvironment() {
        return this.m_gridSensor.analyze();
    }

    private void updateState(Cell[][] perceivedGrid) {
        List<Cell> beliefs = getBelief(perceivedGrid);
        Cell desiredCell = getDesire(beliefs);
        Stack actions = getIntention(perceivedGrid, desiredCell);
    }

    private void chooseAction() {

    }

    private void processAction() {

    }

    /**
     * @param grid Cell[][]
     * @return List<Cell> Liste des cellules non vides que l'on observe.
     */
    private List<Cell> getBelief(Cell[][] grid) {
        return new LinkedList<Cell>();
    }

    /**
     * Retourne la cellule la plus adaptée parmis la liste des beliefs (basée sur une métrique de performance).
     * @param beliefs Liste des cellules non vides que l'on observe avec nos capteurs.
     * @return Cell Cellule que l'on doit aller parcourir
     */
    private Cell getDesire(List<Cell> beliefs) {
        return new Cell();
    }

    /**
     * Retourne les actions que l'agent doit effectuer pour atteindre la cellule Cell
     * @param grid
     * @param desiredCell
     * @return Stack Liste des actions à effectuer.
     */
    private Stack getIntention(Cell[][] grid, Cell desiredCell) {
        return new Stack();
    }
}
