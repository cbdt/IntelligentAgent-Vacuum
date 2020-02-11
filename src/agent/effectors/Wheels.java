package agent.effectors;

import agent.Agent;
import environment.Environment;

public class Wheels extends Effector{

    public Wheels(Environment environment) {
        super(environment);
    }

    @Override
    public void makeAction(Agent.Action action) {
        Environment.Position pos = new Environment.Position(0, 0);
        switch (action) {
            case MOVE_UP:
                pos.setY(-1);
                break;
            case MOVE_DOWN:
                pos.setY(1);
                break;
            case MOVE_LEFT:
                pos.setX(-1);
                break;
            case MOVE_RIGHT:
                pos.setX(1);
                break;
        }

        getEnvionment().agentMovedSignal(pos);
    }
}
