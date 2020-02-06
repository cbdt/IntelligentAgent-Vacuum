package environment;

public class Environment implements Runnable {
    //probabilité d'apparation de la poussiere et des bijoux
    int dustSpawnProbability = 17;
    int jewelDustProbability = 8;

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

        public void add(Position position) {
            x += position.x;
            y += position.y;
        }
    }

    // Fonction pour utiliser un thread, il faut que tu mette la boucle infinie dans cette fonction. Seulement cette fonction est executé (equivalent du main)
    @Override
    public void run() {

    }

    // FONCTIONS QUE J'UTILISE, si tu changes le nom, change aussi dans les classes pour l'agent
    public Cell[][] getGrid() { return new Cell[0][0]; };
    // Pour que le robot sache où il est au départ.
    public Position getInitialPosition() { return new Position(0, 0); }
    // Lorsque l'agent bouge, je donne le delta du déplacement. (ex: il a effectué l'action de monté en haut, alors je te donne Position(0, 1), à gauche Position(-1, 0)...
    public void agentMovedSignal(Position position) {};
    // Lorsque l'agent fait l'action de ramasser.
    public void agentPickupSignal() {};
    // Lorsque l'agent aspire.
    public void agentVacuumSignal() {};

}
