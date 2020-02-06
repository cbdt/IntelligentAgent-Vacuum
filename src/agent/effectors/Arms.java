package agent.effectors;

import agent.Agent;
import environment.Environment;

public class Arms extends Effector{

    public Arms(Environment environment) {
        super(environment);
    }

    @Override
    public void makeAction(Agent.Action action) {
        getEnvionment().agentPickupSignal();
    }
}
