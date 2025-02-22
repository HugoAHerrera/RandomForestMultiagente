package src.main;

public class Main {
    public static void main(String[] args) {
        String rutaArchivo = "src/data/winequality-red-categorical.csv";

        LectorFicheros lector = new LectorFicheros();
        lector.leerCSV(rutaArchivo);

        for (String[] fila : lector.getContenido()) {
            for (String valor : fila) {
                System.out.print(valor + " ");
            }
            System.out.println();
        }
    }
}
