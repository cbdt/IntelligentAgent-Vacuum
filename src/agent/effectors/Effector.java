package agent.effectors;

import environment.Environment;

public abstract class Effector {
    Environment m_environment;

    abstract void makeAction();
}
