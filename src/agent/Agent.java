package agent;

import agent.sensors.GridSensor;
import agent.sensors.Sensor;
import environment.Environment;

public class Agent {

    private Environment m_environment;

    private boolean isAlive = true;

    private Sensor gridSensor;

    public Agent(Environment environment) {
        this.m_environment = environment;

        this.gridSensor = new GridSensor(m_environment);
    }

    public void evolve() {
        while(isAlive) {
            observeEnvironement();
            updateState();
            chooseAction();
            processAction();
        }
    }

    private void observeEnvironement() {

    }

    private void updateState() {

    }

    private void chooseAction() {

    }

    private void processAction() {

    }
}
