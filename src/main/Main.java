package src.main;

import src.funciones.DatoPuro;
import src.funciones.Clasificador;
import src.funciones.SeparadorClases;

import java.util.Map;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String rutaArchivo = "src/data/winequality-red-categorical.csv";

        LectorFicheros lector = new LectorFicheros();
        lector.leerCSV(rutaArchivo);

        ArbolDecision arbolDecision = new ArbolDecision(lector.getContenido().size());
        arbolDecision.seleccionarFilasAleatorias();
        arbolDecision.dividirDataset(lector.getContenido());

        boolean esPuro = DatoPuro.comprobarPureza(lector.getContenido());
        System.out.println("Resultado: " + esPuro);

        Map<String, Integer> clasificacionCategorias = Clasificador.contarClases(lector.getContenido());
        System.out.println(clasificacionCategorias);

        Map<String, List<Double>> divisiones = SeparadorClases.obtenerPosiblesDivisiones(lector.getContenido(), lector.getCabecera());

        for (Map.Entry<String, List<Double>> entry : divisiones.entrySet()) {
            System.out.println("Columna: " + entry.getKey() + " -> Divisiones: " + entry.getValue());
        }
        /*
        for (String[] fila : arbolDecision.getDatosEntrenamiento()) {
            for (String valor : fila) {
                System.out.print(valor + " ");
            }
            System.out.println();
        }

        for (String[] fila : lector.getContenido()) {
            for (String valor : fila) {
                System.out.print(valor + " ");
            }
            System.out.println();
        }*/
    }
}
