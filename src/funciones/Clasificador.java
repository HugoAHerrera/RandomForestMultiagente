package src.funciones;

import java.util.*;

public class Clasificador {
    // crearHoja
    public static Map<String, Double> contarClases(List<String[]> datos, String funcionModelo) {
        Map<String, Double> resultado = new HashMap<>();

        if (funcionModelo.equals("regresion")) {
            double suma = 0;
            int count = 0;

            for (String[] fila : datos) {
                if (fila.length > 0) {
                    suma += Double.parseDouble(fila[fila.length - 1]);
                    count++;
                }
            }

            double media = count > 0 ? (suma / count) : 0.0;

            for (String[] fila : datos) {
                if (fila.length > 0) {
                    String clase = fila[fila.length - 1];
                    resultado.put(clase, media);
                }
            }
        } else {
            for (String[] fila : datos) {
                if (fila.length > 0) {
                    String clase = fila[fila.length - 1];
                    resultado.put(clase, resultado.getOrDefault(clase, 0.0) + 1);
                }
            }
        }
        //Pasa a ser Hoja
        return resultado;
    }

    public static List<String> determinarTipoColumna(List<String[]> datos, String[] cabecera) {
        List<String> tiposColumnas = new ArrayList<>();
        int limiteNumCategorias = 15;

        int numColumnas = cabecera.length;
        List<Set<String>> valoresUnicos = new ArrayList<>();

        for (int i = 0; i < numColumnas; i++) {
            valoresUnicos.add(new HashSet<>());
        }

        for (String[] fila : datos) {
            for (int i = 0; i < numColumnas; i++) {
                valoresUnicos.get(i).add(fila[i]);
            }
        }

        for (int i = 0; i < numColumnas; i++) {
            Set<String> valores = valoresUnicos.get(i);
            String ejemploValor = valores.iterator().next();

            try {
                Double.parseDouble(ejemploValor);
                if (valores.size() <= limiteNumCategorias) {
                    tiposColumnas.add("Categorica");
                } else {
                    tiposColumnas.add("Continua");
                }
            } catch (NumberFormatException e) {
                tiposColumnas.add("Categorica");
            }
        }

        return tiposColumnas;
    }
}