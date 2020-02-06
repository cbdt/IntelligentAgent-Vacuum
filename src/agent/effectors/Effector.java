package agent.effectors;

import environment.Environment;
import agent.Agent;

public abstract class Effector {
    Environment m_environment;

    protected Effector(Environment environment) {
        this.m_environment = environment;
    }

    public Environment getEnvionment() {
        return m_environment;
    }

    public abstract void makeAction(Agent.Action action);
}
