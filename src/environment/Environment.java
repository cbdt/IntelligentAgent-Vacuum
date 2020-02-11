package environment;

import agent.Agent;

import java.util.Random;

public class Environment implements Runnable {
    //taille grille
    private static int minX = 0;
    private static int maxX = 4;
    private static int minY = 0;
    private static int maxY = 4;
    //proba d'apparition
    public int dustSpawnProb = 17;
    public int jewelSpawnProb = 6;
    //robot
    public int performance;
    public Position robotPosition = new Position(2, 2);
    Random rand = new Random();
    //Grille
    private Cell[][] grid = new Cell[maxX + 1][maxY + 1];
    private int updateTime = 500;

    public void initGrid() {
        for (int x = 0; x < maxX; x++) {
            for (int y = 0; y < maxY; y++) {
                grid[x][y] = new Cell(x, y);
            }
        }
    }

    public void generateDust() {
        if (rand.nextInt(100) < dustSpawnProb) {
            generate(Cell.State.DUST);
        }
    }

    public void generateJewel() {
        if (rand.nextInt(100) < jewelSpawnProb) {
            generate(Cell.State.JEWEL);
        }
    }


    public void generate(Cell.State state) {
        Position randomPosition = Position.random();
        Cell cell = getCell(randomPosition);
        Cell.State currentState = getCellState(randomPosition);
        switch (currentState) {
            case DUST:
                if(state == Cell.State.JEWEL) {
                    cell.setState(Cell.State.DUST_JEWEL);
                }
                break;
            case JEWEL:
                if(state == Cell.State.DUST) {
                    cell.setState(Cell.State.DUST_JEWEL);
                }
            case EMPTY:
                cell.setState(state);
                break;
        }
        updateUI();
    }

    // Fonction pour utiliser un thread, il faut que tu mette la boucle infinie dans cette fonction. Seulement cette fonction est executé (equivalent du main)
    @Override
    public void run() {
        initGrid();
        while (true) {
            generateDust();
            generateJewel();
            try {
                Thread.sleep(updateTime);
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
        }
    }

    // FONCTIONS QUE J'UTILISE, si tu changes le nom, change aussi dans les classes pour l'agent
    public Cell[][] getGrid() {
        return new Cell[0][0];
    }


    // Lorsque l'agent aspire.
    public void agentVacuumSignal() {
        updatePerformance(Agent.Action.CLEAN, robotPosition);
        setNextCellState(Agent.Action.CLEAN, robotPosition);
        updateUI();
    }

    // Lorsque l'agent bouge, je donne le delta du déplacement. (ex: il a effectué l'action de monté en haut, alors je te donne Position(0, 1), à gauche Position(-1, 0)...
    public void agentMovedSignal(Position position) {
        robotPosition.update(position);
        updateUI();
    }


    // Lorsque l'agent fait l'action de ramasser.
    public void agentPickupSignal() {
        updatePerformance(Agent.Action.PICK_UP, robotPosition);
        setNextCellState(Agent.Action.PICK_UP, robotPosition);
        updateUI();
    }

    // Pour que le robot sache où il est au départ.
    public Position getInitialPosition() {
        return new Position(0, 0);
    }


    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void updateUI() {
        clearScreen();
        for (int y = 0; y < maxY; y++) {
            System.out.print("|");
            for (int x = 0; x < maxX; x++) {
                String symbol = getCellSymbol(new Position(x, y));
                System.out.println(" " + symbol + " ");
                if(x == maxX - 1) {
                    System.out.print("|");
                }
            }
        }
    }

    private String getCellSymbol(Position position) {
        Cell.State state = getCellState(position);

        if(position.equals(robotPosition)) {
            return "X";
        }

        switch (state) {
            case DUST_JEWEL:
                return ";";
            case DUST:
                return ".";
            case JEWEL:
                return ",";
            case EMPTY:
                return " ";
        }
        return " ";
    }


    public Cell getCell(Position position) {
        return grid[position.x][position.y];
    }

    public Cell.State getCellState(Position position) {
        return getCell(position).getState();
    }

    public void setNextCellState(Agent.Action action, Position position) {

        Cell.State currentState = getCellState(position);
        switch (action) {
            case PICK_UP:
                if (currentState == Cell.State.JEWEL) {
                    getCell(position).setState(Cell.State.EMPTY);
                    break;
                }
            case CLEAN:
                if (currentState == Cell.State.DUST || currentState == Cell.State.DUST_JEWEL || currentState == Cell.State.JEWEL) {
                    getCell(position).setState(Cell.State.EMPTY);
                    break;
                }
        }
    }

    public int getPerformance() {
        return performance;
    }

    public void updatePerformance(Agent.Action action, Position position) {
        switch (getCellState(position)) {
            case JEWEL:
                if (action == Agent.Action.CLEAN) {
                    performance = performance - 5;
                    break;
                } else if (action == Agent.Action.PICK_UP) {
                    performance = performance + 10;
                    break;
                }
            case DUST:
                if (action == Agent.Action.CLEAN) {
                    performance = performance + 7;
                    break;
                }
            case DUST_JEWEL:
                if (action == Agent.Action.CLEAN) {
                    performance = performance - 56;
                    break;
                } else if (action == Agent.Action.PICK_UP) {
                    performance = performance + 10;
                    break;
                }
            case EMPTY:
                break;
            default:
                System.out.println("Error in the environment updatePerformance");
        }

    }


    public static class Position {
        int x;
        int y;


        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public void update(Position position) {
            x += position.x;
            y += position.y;
        }

        @Override
        public boolean equals(Object obj) {
            Position pos = (Position) obj;
            return pos.x == this.x && pos.y == this.y;
        }

        public static Position random() {
            Random r = new Random();
            int x = r.nextInt(maxX);
            int y = r.nextInt(maxY);
            return new Position(x, y);
        }
    }


}
