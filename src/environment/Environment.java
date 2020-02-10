package environment;
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
            //generate(DUST)
        }
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
