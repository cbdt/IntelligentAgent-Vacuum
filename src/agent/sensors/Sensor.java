package agent.sensors;

import environment.Environment;

public abstract class Sensor {
    private Environment m_environment;

    protected Sensor(Environment environment) {
        this.m_environment = environment;
    }

    abstract void analyze();
}
