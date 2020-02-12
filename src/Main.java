import agent.Agent;
import environment.Environment;

import java.util.Scanner;

public class Main {


    // TODO: The agent currently don't learn... :'(
    public static void main(String[] args) {
        boolean redo = true;
        while (redo) {
            System.out.println("Agent intelligent aspirateur");
            System.out.println("Veuillez choisir un type d'exploration\n\t1) Exploration BFS\n\t2) Exploration A*");
            System.out.println("1 ou 2 : ");
            Scanner sc = new Scanner(System.in);
            String choice = sc.nextLine();
            int choice_number = Integer.parseInt(choice.trim());

            //System.out.println(":"+choice_number+":");
            if (choice_number != 1 && choice_number != 2) {
                System.out.println("Veuillez saisir un nombre correct.");
                continue;
            }
            redo = false;

            Agent.Exploration exploration = Agent.Exploration.BFS;
            if(choice_number == 2)
                exploration = Agent.Exploration.A_STAR;


            Environment environment = new Environment();

            Thread environmentThread = new Thread(environment);
            Thread agentThread = new Thread(new Agent(environment, exploration));

            environmentThread.start();
            try {
                Thread.sleep(100); // le temps que l'environnement s'initialise.
            } catch (InterruptedException e) {
            }
            agentThread.start();
        }
    }
}
