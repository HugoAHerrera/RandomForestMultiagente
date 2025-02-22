package prueba;
import jade.core.Agent;

public class MiAgente extends Agent {
    protected void setup() {
        System.out.println("¡Hola! Soy un agente y me llamo " + getLocalName());
    }
}
