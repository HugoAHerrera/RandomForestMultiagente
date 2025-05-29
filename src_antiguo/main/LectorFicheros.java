package src_antiguo.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LectorFicheros {

    private final List<String[]> contenido;
    private String[] cabecera;

    public LectorFicheros() {
        contenido = new ArrayList<>();
    }

    public void leerCSV(String nombreArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {
                String[] lineaArray = linea.split(",");

                if (primeraLinea) {
                    cabecera = lineaArray;
                    primeraLinea = false;
                } else {
                    contenido.add(lineaArray);
                }
            }
            System.out.println("El archivo ha sido leído correctamente.");
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    public List<String[]> getContenido() {
        return contenido;
    }

    public String[] getCabecera() {
        return cabecera;
    }
}