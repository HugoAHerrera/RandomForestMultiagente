package src.main;

import src.funciones.DatoPuro;
import src.funciones.Clasificador;
import src.funciones.SeparadorClases;
import src.funciones.SeparadorEntropia;

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

        SeparadorEntropia separadorEntropia = new SeparadorEntropia();

        List<List<String[]>> resultado = separadorEntropia.separarDatos(lector.getContenido(), "fixed acidity", 7.0f, lector.getCabecera());
        List<String[]> datosInferiores = resultado.get(0);
        List<String[]> datosSuperiores = resultado.get(1);

        separadorEntropia.getMejorSeparacion(lector, divisiones);
        /*
        System.out.println("\n\nDatos inferiores:");
        for(String[] fila: datosInferiores){
            System.out.println(String.join(", ", fila));
        }

        System.out.println("\n\nDatos superiores:");
        for(String[] fila: datosSuperiores){
            System.out.println(String.join(", ", fila));
        }


        for(String[] fila: lector.getContenido()){
            System.out.println(String.join(", ", fila));
        }

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
