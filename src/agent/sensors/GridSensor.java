package agent.sensors;

import environment.Cell;
import environment.Environment;

public class GridSensor extends Sensor<Cell[][]> {

    public GridSensor(Environment environment) {
        super(environment);
    }

    @Override
    public Cell[][] analyze() {
        return getEnvironment().getGrid();
    }
}
