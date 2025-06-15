package prueba;
import jade.core.Agent;

public class MiClaseAgente extends Agent {
    protected void setup() {
        System.out.println("Agente "+getLocalName()+" iniciado correctamente.");
    }
}
