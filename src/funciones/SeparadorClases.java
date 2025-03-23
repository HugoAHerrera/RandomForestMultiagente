package src.funciones;

import java.util.*;

public class SeparadorClases {
    public static Map<String, List<String>> obtenerPosiblesDivisiones(List<String[]> datos, String[] cabecera, List<String> clasificacionesColumnas) {
        Map<String, List<String>> divisionesPotenciales = new HashMap<>();

        int numColumnas = cabecera.length - 1; // Excluir la última columna, que es la que precide

        for (int col = 0; col < numColumnas; col++) {
            String tipoColumna = clasificacionesColumnas.get(col);
            Set<String> valoresUnicos = new TreeSet<>();

            for (String[] fila : datos) {
                valoresUnicos.add(fila[col]);
            }

            List<String> divisiones = new ArrayList<>(valoresUnicos);

            if(tipoColumna.equals("Continua") && (valoresUnicos.size()>1)) {
                List<Double> valoresOrdenados = new ArrayList<>();
                for (String valor : valoresUnicos) {
                    valoresOrdenados.add(Double.parseDouble(valor));
                }
                Collections.sort(valoresOrdenados);

                List<String> divisionesContinuas = new ArrayList<>();
                for (int i = 1; i < valoresOrdenados.size(); i++) {
                    double division = (valoresOrdenados.get(i - 1) + valoresOrdenados.get(i)) / 2;
                    divisionesContinuas.add(String.valueOf(division));
                }

                divisionesPotenciales.put(cabecera[col], divisionesContinuas);
            }else if(valoresUnicos.size()>1){
                divisionesPotenciales.put(cabecera[col], divisiones);
            }
        }

        return divisionesPotenciales;
    }
}
