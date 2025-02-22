package src.funciones;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatoPuro {

    public static boolean comprobarPureza(List<String[]> datos) {
        Set<String> clases = new HashSet<>();

        for (String[] fila : datos) {
            if (fila.length > 0) {
                clases.add(fila[fila.length - 1]);
            }
            if (clases.size() > 1) {
                System.out.println(clases);
                return false;
            }
        }
        System.out.println(clases);
        return true;
    }
}