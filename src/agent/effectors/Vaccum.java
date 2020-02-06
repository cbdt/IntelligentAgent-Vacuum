package agent.effectors;

import agent.Agent;
import environment.Environment;

public class Vaccum extends Effector{

    public Vaccum(Environment environment) {
        super(environment);
    }

    @Override
    public void makeAction(Agent.Action action) {
        getEnvionment().agentVacuumSignal();
    }
}
