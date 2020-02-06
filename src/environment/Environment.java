package environment;

public class Environment {
    //probabilité d'apparation de la poussiere et des bijoux
    int dustSpawnProbability = 17;
    int jewelDustProbability = 8;

    public class Position {
        int x;
        int y;

        Position(int x, int y) {
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


    // FONCTIONS QUE J'UTILISE, si tu changes le nom, change aussi dans les classes pour l'agent
    public Cell[][] getGrid() { return new Cell[0][0]; };

}
