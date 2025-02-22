package src.main;

import src.funciones.DatoPuro;

public class Main {
    public static void main(String[] args) {
        String rutaArchivo = "src/data/winequality-red-categorical-one-type.csv";

        LectorFicheros lector = new LectorFicheros();
        lector.leerCSV(rutaArchivo);

        ArbolDecision arbolDecision = new ArbolDecision(lector.getContenido().size());
        arbolDecision.seleccionarFilasAleatorias();
        arbolDecision.dividirDataset(lector.getContenido());

        boolean esPuro = DatoPuro.comprobarPureza(lector.getContenido());
        System.out.println("Resultado: " + esPuro);
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
