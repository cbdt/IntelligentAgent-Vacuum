package agent.sensors;

import environment.Environment;

public abstract class Sensor<T> {
    private Environment m_environment;

    protected Sensor(Environment environment) {
        this.m_environment = environment;
    }

    public abstract T analyze();

    protected Environment getEnvironment() { return this.m_environment; }
}
