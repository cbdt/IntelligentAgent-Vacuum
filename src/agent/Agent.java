package agent;

import agent.effectors.Arms;
import agent.effectors.Effector;
import agent.effectors.Vaccum;
import agent.effectors.Wheels;
import agent.sensors.GridSensor;
import agent.sensors.Sensor;
import environment.Cell;
import environment.Environment;

import java.util.*;

public class Agent implements Runnable {


    public enum Exploration { BFS, A_STAR}

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

    public Agent(Environment environment, Exploration exploration) {
        this.m_environment = environment;

        this.m_gridSensor = new GridSensor(m_environment);
        this.m_arms = new Arms(m_environment);
        this.m_vacuum = new Vaccum(m_environment);
        this.m_wheels = new Wheels(m_environment);

        this.m_currentPosition = m_environment.getInitialPosition();
        this.m_actions = new Stack<>();

        this.m_explorationType = exploration;
    }

    @Override
    public void run() {
        evolve();
    }

    private void evolve() {
        int nbTour = 0;
        while(m_isAlive) {

            if(nbTour == 6 || m_actions.isEmpty()) {
                Cell[][] perceivedGrid = observeEnvironment();
                m_actions = updateState(perceivedGrid);
                //System.out.println(m_actions);
                nbTour = 0;
            }

            Action action = chooseAction(m_actions);
            processAction(action);
            nbTour++;

            m_isAlive = m_battery > 0;
            try {
                Thread.sleep(750); // ça va trop vite sinn
            }catch (Exception e) {

            }
        }
        System.out.println("I'm dead :(");
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
                m_currentPosition.update(new Environment.Position(0, -1));
                break;
            case MOVE_DOWN:
                m_currentPosition.update(new Environment.Position(0, 1));
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
        List<Cell> nonEmptyCells = new LinkedList<>();

        for (int x = 0; x < grid.length; x++) {
            for(int y = 0; y < grid[x].length; y++) {
                Cell cell = grid[x][y];
                if(cell == null) { continue; } // l'environnement n'a pas encore fait initGrid()
                if(cell.getState() != Cell.State.EMPTY) {
                    nonEmptyCells.add(cell);
                }
            }
        }
        return nonEmptyCells;
    }

    /**
     * Retourne la cellule la plus adaptée parmis la liste des beliefs (basée sur une métrique de performance).
     * @param beliefs Liste des cellules non vides que l'on observe avec nos capteurs.
     * @return Cell Cellule que l'on doit aller parcourir
     */
    private Cell getDesire(List<Cell> beliefs) {
        double bestPerformance = Integer.MIN_VALUE;
        Cell bestCell = null;
        for(Cell cell: beliefs) {
            double currentPerformance = getPerformance(cell);
            if(currentPerformance > bestPerformance)  {
                bestCell = cell;
                bestPerformance = currentPerformance;
            }
        }

        //System.out.println("GOAL : " + bestCell);

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

        if(desiredCell == null){
            return new Stack<>();
        }

        return explore(grid, desiredCell);
    }

    public Stack<Action> explore(Cell[][] grid, Cell desiredCell) {
        Stack<Action> actions = null; // jamais null

        switch (this.m_explorationType) {
            case BFS:
                actions = exploration_BFS(grid, desiredCell);
                break;
            case A_STAR:
                actions =  exploration_AStar(grid, desiredCell);
                break;
        }
        return actions;
    }

    // TODO: Algorithmes d'exploration

    private Cell getRobotCell(Cell[][] grid) //todo Resoudre pour avoir les pos du robot à l'etat initial
    {
        return grid[m_currentPosition.getX()][m_currentPosition.getY()];
    }


    public Stack<Action> exploration_BFS(Cell[][] grid, Cell desiredCell) {

        Map<Cell, Cell> parentMap = new HashMap<>(); // Enfant: Parent
        Cell start = getRobotCell(grid);
        Cell end = desiredCell;

        LinkedList<Cell> frontiere = new LinkedList<Cell>();
        frontiere.add(start);

        // on fait une liste des endroits déjà explorer
        LinkedList<Cell> visited = new LinkedList<>();
        visited.add(start);

        parentMap.put(start, null);

        while (!frontiere.isEmpty()){
            Cell currentCell = frontiere.poll();

            if(currentCell == end){  break;}
            List<Cell> neighbors = Cell.getNeighborCells(currentCell,grid);
            for (Cell cell: neighbors) {
                if(visited.contains(cell)) continue;
                frontiere.add(cell);
                parentMap.put(cell, currentCell);
                visited.add(cell);
            }

        }


        // TODO: duplicate with reconstructPath (not the same structure, start -> nil ; and reconstructPath the start node is not in the keys)
        Stack<Cell> cellsPath = new Stack<>();
        Cell cell = end;
        while (cell != null)
        {

            cellsPath.push(cell);
            cell = parentMap.get(cell);
        }
        //System.out.println("start : " + start  + " end : " + end);
        //System.out.println("CP : " + cellsPath);

        Stack<Action> actions = getActions(cellsPath);
        return  actions;
    }

    private Stack<Cell> reconstructPath(Map<Cell, Cell> parentMap, Cell cell) {
        Stack<Cell> cellsPath = new Stack<>();
        Cell current = cell;
        cellsPath.push(current);
        while(parentMap.keySet().contains(current)) {
            current = parentMap.get(current);
            cellsPath.push(current);
        }
        return cellsPath;
    }

    public Stack<Action> exploration_AStar(Cell[][] grid, Cell desiredCell) {
        Cell start = getRobotCell(grid);

        List<Cell> openSet = new LinkedList<>();
        Map<Cell, Cell> parentMap = new HashMap<>();

        Map<Cell, Double> gScore = new HashMap<>();
        Map<Cell, Double> fScore = new HashMap<>();

        gScore.put(start, 0.0);
        fScore.put(start, getDistance(start.getPosition(), desiredCell.getPosition()));

        openSet.add(start);

        while (!openSet.isEmpty()) {
            Cell currentCell = bestScore(openSet, fScore);


            if (currentCell == desiredCell) {
                return getActions(reconstructPath(parentMap, currentCell));
            }
            openSet.remove(currentCell);

            List<Cell> neighbors = Cell.getNeighborCells(currentCell, grid);
            for (Cell cell: neighbors) {
                // distance to neighbord.
                Double score = gScore.getOrDefault(currentCell, Double.MAX_VALUE) + 1;
                if(score < gScore.getOrDefault(cell, Double.MAX_VALUE)) {
                    parentMap.put(cell, currentCell);
                    gScore.put(cell, score);
                    fScore.put(cell, gScore.get(cell) + getDistance(cell.getPosition(), desiredCell.getPosition()));
                    if(!openSet.contains(cell)) {
                        openSet.add(cell);
                    }
                }
            }

        }

        return new Stack<>();
    }

    private Cell bestScore(List<Cell> openSet, Map<Cell, Double> fScore) {
        Cell bestCell = openSet.get(0);
        Double bestPerf = Double.MAX_VALUE;

        for (Cell cell: openSet) {
            Double perf  = fScore.get(cell);
            if(perf < bestPerf) {
                bestCell = cell;
                bestPerf = perf;
            }
        }
        return bestCell;
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
                recompense = 2;
                break;
            case JEWEL:
                recompense = 2;
                break;
            case DUST_JEWEL:
                recompense = 4;
                energieAction = 2;
                break;
            default:
                break;
        }

        // Malus : Energie dépensée (distance + action effectuée)
        double energieDistance = getDistance(m_currentPosition, cell.getPosition());
        double energie = energieAction + energieDistance;
        //System.out.println("ED: " + energieDistance + "; EA:"  + energieAction + "; REC" + recompense + " TOTAL" + (recompense - energie) );
        return recompense - energie;
    }

    public double getDistance(Environment.Position a, Environment.Position b) {
        //System.out.println(a + " --> " + b);
        return Math.sqrt(Math.pow(b.getX() - a.getX(), 2) + Math.pow(b.getY() - a.getY(), 2));
    }

    public Stack<Action> getActions(Stack<Cell> cellpath){
        List<Action> actionPath = new ArrayList<Action>();
        //System.out.println("CP " + cellpath);

        while (!cellpath.isEmpty() && cellpath.size() >= 2)
        {
            Cell cell = cellpath.pop();
            Cell nextCell = cellpath.peek();
            if (cell.isBelow(nextCell)) actionPath.add(0,Action.MOVE_UP);
            if (cell.isLeftFrom(nextCell)) actionPath.add(0,Action.MOVE_RIGHT);
            if (cell.isAbove(nextCell)) actionPath.add(0,Action.MOVE_DOWN);
            if (cell.isRightFrom(nextCell)) actionPath.add(0,Action.MOVE_LEFT);
        }

        Stack<Action> actions = new Stack<Action>();

        if(!actionPath.isEmpty() || cellpath.size() == 1) { // qqlchose à spawné là ou on est
                Cell desiredCell = cellpath.get(cellpath.size() - 1);

                switch (desiredCell.getState()) {
                    case DUST:
                        actions.push(Action.CLEAN);
                        break;
                    case JEWEL:
                        actions.push(Action.PICK_UP);
                    case DUST_JEWEL:
                        actions.push(Action.CLEAN);
                        actions.push(Action.PICK_UP);
                        break;

                }

                while (!actionPath.isEmpty()) {
                    actions.push(actionPath.get(0));
                    actionPath.remove(0);
                }
        }

        return actions;
    }
}
