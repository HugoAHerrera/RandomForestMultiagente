package src.funciones;

import java.util.*;

public class Clasificador {
    public static Map<String, Integer> contarClases(List<String[]> datos) {
        Map<String, Integer> recuentoClases = new HashMap<>();

        for (String[] fila : datos) {
            if (fila.length > 0) {
                String clase = fila[fila.length - 1];
                recuentoClases.put(clase, recuentoClases.getOrDefault(clase, 0) + 1);
            }
        }

        return recuentoClases;
    }
}