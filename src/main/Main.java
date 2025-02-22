package src.main;

public class Main {
    public static void main(String[] args) {
        String rutaArchivo = "src/data/winequality-red-categorical.csv";

        LectorFicheros lector = new LectorFicheros();
        lector.leerCSV(rutaArchivo);

        ArbolDecision arbolDecision = new ArbolDecision(lector.getContenido().size());
        arbolDecision.seleccionarFilasAleatorias();
        arbolDecision.dividirDataset(lector.getContenido());

        for (String[] fila : arbolDecision.getDatosEntrenamiento()) {
            for (String valor : fila) {
                System.out.print(valor + " ");
            }
            System.out.println();
        }
        /*
        for (String[] fila : lector.getContenido()) {
            for (String valor : fila) {
                System.out.print(valor + " ");
            }
            System.out.println();
        }*/
    }
}
