package src.funciones;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class SeparadorEntropia {
    public  List<List<String[]>> separarDatos(List<String[]> dataset, String columnaSeparacion, float valorSeparacion, String[] cabecera) {
        List<String[]> datosInferiores = new ArrayList<>();
        List<String[]> datosSuperiores = new ArrayList<>();

        int indiceColumna = Arrays.asList(cabecera).indexOf(columnaSeparacion);
        if (indiceColumna == -1) {
            throw new IllegalArgumentException("La columna especificada no existe en el dataset.");
        }

        for (String[] fila : dataset) {
            if (Float.parseFloat(fila[indiceColumna]) <= valorSeparacion) {
                datosInferiores.add(fila);
            } else {
                datosSuperiores.add(fila);
            }
        }

        return Arrays.asList(datosInferiores, datosSuperiores);
    }

}
