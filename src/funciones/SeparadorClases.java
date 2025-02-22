package src.funciones;

import java.util.*;

public class SeparadorClases {
    public static Map<String, List<Double>> obtenerPosiblesDivisiones(List<String[]> datos, String[] cabecera) {
        Map<String, List<Double>> divisionesPotenciales = new HashMap<>();

        int numColumnas = cabecera.length - 1; // Excluir la última columna

        for (int col = 0; col < numColumnas; col++) {
            Set<Double> valoresUnicos = new TreeSet<>();

            for (String[] fila : datos) {
                valoresUnicos.add(Double.parseDouble(fila[col]));
            }

            List<Double> divisiones = new ArrayList<>();
            List<Double> valoresOrdenados = new ArrayList<>(valoresUnicos);

            for (int i = 1; i < valoresOrdenados.size(); i++) {
                double division = (valoresOrdenados.get(i - 1) + valoresOrdenados.get(i)) / 2;
                divisiones.add(division);
            }

            divisionesPotenciales.put(cabecera[col], divisiones);
        }

        return divisionesPotenciales;
    }
}
