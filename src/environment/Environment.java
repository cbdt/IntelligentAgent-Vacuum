package environment;

import agent.Agent;

import java.util.Random;

public class Environment implements Runnable {
    //taille grille
    public int minX = 0;
    public int maxX = 4;
    public int minY = 0;
    public int maxY = 4;
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
            //generate(DUST)
        }
    }

    public void generateJewel() {
        if (rand.nextInt(100) < jewelSpawnProb) {
            //generate(JEWEL)
        }
    }

    public void display(Position position) {
        display(position.)
    }

    public void generate(Cell.State state) {

    }

    // Fonction pour utiliser un thread, il faut que tu mette la boucle infinie dans cette fonction. Seulement cette fonction est executé (equivalent du main)
    @Override
    public void run() {
        while (true) {

        }
    }

    // FONCTIONS QUE J'UTILISE, si tu changes le nom, change aussi dans les classes pour l'agent
    public Cell[][] getGrid() {
        return new Cell[0][0];
    }

    // Lorsque l'agent bouge, je donne le delta du déplacement. (ex: il a effectué l'action de monté en haut, alors je te donne Position(0, 1), à gauche Position(-1, 0)...
    public void agentMovedSignal(Position position) {

    }

    // Pour que le robot sache où il est au départ.
    public Position getInitialPosition() {
        return new Position(0, 0);
    }

    // Lorsque l'agent fait l'action de ramasser.
    public void agentPickupSignal() {

    }

    // Lorsque l'agent aspire.
    public void agentVacuumSignal() {
    }

    public Cell getCell(Position position) {
        return grid[position.x][position.y];
    }

    public Cell.State getState(Position position) {
        return getCell(position).getState();
    }

    /*public Cell[][] getGrid(){
        return grid;
    }*/

    public void setState(Cell.State state, Position position) {

        Cell.State currentState = getState(position);
        switch (state) {
            case DUST:
                if (currentState == Cell.State.JEWEL) {
                    getCell(position).setM_state(Cell.State.DUST_JEWEL);
                    break;
                } else {
                    getCell(position).setM_state(state);
                    break;
                }
            case JEWEL:
                if (currentState == Cell.State.DUST) {
                    getCell(position).setM_state(Cell.State.DUST_JEWEL);
                    break;
                } else {
                    getCell(position).setM_state(state);
                    break;
                }
            case EMPTY:
                getCell(position).setM_state(state);
                break;
            case DUST_JEWEL:
                getCell(position).setM_state(state);
                break;
            default:
                System.out.println("Error in the environment setState");
        }
    }

    public int getPerformance() {
        return performance;
    }

    public void updatePerformance(Agent.Action action, Position position) {
        switch (getState(position)) {
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
    }


}
