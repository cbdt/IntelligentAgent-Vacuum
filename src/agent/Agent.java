package agent;

import agent.effectors.Arms;
import agent.effectors.Effector;
import agent.effectors.Vaccum;
import agent.effectors.Wheels;
import agent.sensors.GridSensor;
import agent.sensors.Sensor;
import environment.Cell;
import environment.Environment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Agent implements Runnable {


    enum Exploration { BFS, GREEDY}

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
            case BFS:
                actions = exploration_BFS(grid, desiredCell);
                break;
            case GREEDY:
                actions =  exploration_GREEDY(grid, desiredCell);
                break;
        }
        return actions;
    }

    // TODO: Algorithmes d'exploration

    private Cell getRobotCell(Cell[][] grid) //todo Resoudre pour avoir les pos du robot à l'etat initial
    {
        return grid[position.x][ position.y];
    }

    public class Tree {

        public Cell Parent;
        public Cell Enfant;
        public double Distance;

        public Tree(Cell Parent , Cell Enfant, double Distance){
            this.Parent = Parent;
            this.Enfant = Enfant;
            this.Distance = Distance;
        }

        public void setEnfant(Cell enfant) {
            Enfant = enfant;
        }

        public void setParent(Cell parent) {
            Parent = parent;
        }

        public void setDistance(double distance) {
            Distance = distance;
        }

        public double getDistance() {
            return Distance;
        }

        public Cell getEnfant() {
            return Enfant;
        }

        public Cell getParent() {
            return Parent;
        }
    }

    public Stack<Action> exploration_BFS(Cell[][] grid, Cell desiredCell) {

        Cell Start_Enfant = getRobotCell(grid);
        Cell End = desiredCell;
        List<Cell> frontiere = new ArrayList<Cell>();
        frontiere.add(Start_Enfant);
        // on fait une liste des endroits déjà explorer
        List<Tree> Parent = new ArrayList<Tree>();


        Tree Start = null ;
        Start.setEnfant(Start_Enfant);

        Parent.add(Start);

        while (frontiere != null){

            Cell CurentCell = frontiere.get(0);
            frontiere.remove(0);

            if(CurentCell == End){


                break;}

            for (Cell cell: Cell.getNeighborCells(CurentCell,grid)
                 ) {
                if(Parent.contains(cell)) continue;
                frontiere.add(cell);
                Tree Noeud =null;
                Noeud.setEnfant(cell);
                Noeud.setParent(CurentCell);
                Parent.add(Noeud);
            }
            // TODO retrouver le chemin ( partir de la cellule END puis remonter la liste grace aux parents)
            //avec algo en largeur, la liste parents contient tous les noeud vérifier

        }


        return new Stack<Action>();
    }





    public Stack<Action> exploration_GREEDY(Cell[][] grid, Cell desiredCell) {

        Cell Start = getRobotCell(grid);
        Cell End = desiredCell;
        List<Tree> frontiere = new ArrayList<Tree>();
        Tree Start_d = null;
        Start_d.setEnfant(Start);
        Start_d.setDistance(1000);


        frontiere.add(Start_d);
        // on fait une liste des endroits déjà explorer
        List<Tree> Parent = new ArrayList<Tree>();
        Parent.add(Start_d);

        while (frontiere != null){

            Cell CurentCell = frontiere.get(0).getEnfant();
            frontiere.remove(0);
            if(CurentCell == End) break;

            for (Cell cell: Cell.getNeighborCells(CurentCell,grid) //todo fonction getNeighbour qui renvoie les voisins de la case
            ) {
                if(Parent.contains(cell)) continue;
                double distance = getDistance(cell.getPosition(),End.getPosition()); // on calcule la distance restante

                Tree front = null;
                front.setDistance(distance);
                front.setEnfant(cell);
                front.setParent(CurentCell);

                for(int i=0 ; i <frontiere.size(); i++){
                    if(distance < frontiere.get(i).getDistance()){

                        frontiere.add(i,front);

                    }
                    else {

                        frontiere.add(front);

                    }
                }
                Parent.add(front);
            }


        }
/**
        var cellPath = Cell.getCellPath(startCell, destination, cameFrom);
        var actions = getActions(cellPath);
 */
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
