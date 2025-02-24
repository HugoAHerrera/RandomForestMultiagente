package src.funciones;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import src.funciones.Clasificador;

public class SeparadorEntropia {
    public List<List<String[]>> separarDatos(List<String[]> dataset, String columnaSeparacion, float valorSeparacion, String[] cabecera) {
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

    public static float calcularEntropia(List<String[]> conjunto) {
        Map<String, Integer> clasificacionCategorias = Clasificador.contarClases(conjunto);
        List<Double> probabilidades = calcularProbabilidades(clasificacionCategorias);

        float entropia = 0.0f;

        for (double p : probabilidades) {
            if (p > 0) {
                entropia += -p * (Math.log(p) / Math.log(2));
            }
        }

        return entropia;
    }

    public static List<Double> calcularProbabilidades(Map<String, Integer> clasificacionCategorias) {
        List<Double> probabilidades = new ArrayList<>();

        int total = clasificacionCategorias.values().stream().mapToInt(Integer::intValue).sum();

        for (Integer cuenta : clasificacionCategorias.values()) {
            probabilidades.add((double) cuenta / total);
        }

        return probabilidades;
    }

    public static float calcularEntropiaGlobal(List<List<String[]>> resultado) {
        int tamañoInferiores = resultado.get(0).size();
        int tamañoSuperiores = resultado.get(1).size();

        int tamañoGlobal = tamañoInferiores + tamañoSuperiores;

        if (tamañoGlobal == 0) {
            return 0.0f;
        }

        float probabilidadInferiores = (float) tamañoInferiores / tamañoGlobal;
        float probabilidadSuperiores = (float) tamañoSuperiores / tamañoGlobal;

        float entropiaInferiores = calcularEntropia(resultado.get(0));
        float entropiaSuperiores = calcularEntropia(resultado.get(1));

        return (probabilidadInferiores * entropiaInferiores) + (probabilidadSuperiores * entropiaSuperiores);
    }

}
