import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class Receiver extends Agent {
    protected void setup() {
        System.out.println("Receiver iniciado");
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println("Número recibido: " + msg.getContent());
                } else {
                    block();
                }
            }
        });
    }
}
