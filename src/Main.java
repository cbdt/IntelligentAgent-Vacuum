import agent.Agent;
import environment.Environment;

public class Main {

    public static void main(String[] args) {

        Environment environment = new Environment();

        Thread environmentThread = new Thread(environment);
        Thread agentThread = new Thread(new Agent(environment));

        environmentThread.start();
        try {
            Thread.sleep(100); // le temps que l'environnement s'initialise.
        } catch (InterruptedException e) {
        }
        agentThread.start();
    }
}
