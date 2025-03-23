package src.funciones;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

public class SeparadorEntropia {
    public static List<List<String[]>> separarDatos(List<String[]> dataset, String columnaSeparacion, Object valorSeparacion, String[] cabecera, boolean esContinua) {
        List<String[]> datosInferiores = new ArrayList<>();
        List<String[]> datosSuperiores = new ArrayList<>();

        int indiceColumna = Arrays.asList(cabecera).indexOf(columnaSeparacion);
        if (indiceColumna == -1) {
            throw new IllegalArgumentException("La columna especificada no existe en el dataset.");
        }

        for (String[] fila : dataset) {
            if (esContinua) {
                if (Float.parseFloat(fila[indiceColumna]) <= (Float) valorSeparacion) {
                    datosInferiores.add(fila);
                } else {
                    datosSuperiores.add(fila);
                }
            } else {
                if (fila[indiceColumna].equals(valorSeparacion)) {
                    datosInferiores.add(fila);
                } else {
                    datosSuperiores.add(fila);
                }
            }
        }

        return Arrays.asList(datosInferiores, datosSuperiores);
    }

    //ECM = Error cuadrático medio
    public static float calcularECM(List<String[]> datos, String funcionModelo) {
        List<Double> valoresReales = new ArrayList<>();

        for (String[] fila : datos) {
            if (fila.length > 0) {
                valoresReales.add(Double.parseDouble(fila[fila.length - 1]));
            }
        }

        if (valoresReales.isEmpty()) {
            return 0.0f;
        }

        float prediccion = (float) valoresReales.stream()
                .mapToDouble(valor -> (double) valor)
                .average()
                .orElse(0.0);

        float ecm = (float) valoresReales.stream()
                .mapToDouble(valor -> Math.pow((double) valor - (double) prediccion, 2))
                .average()
                .orElse(0.0);

        return ecm;
    }

    public static float calcularEntropia(List<String[]> conjunto, String funcionModelo) {
        Map<String, Double> clasificacionCategorias = Clasificador.contarClases(conjunto, funcionModelo);
        List<Double> probabilidades = calcularProbabilidades(clasificacionCategorias);

        float entropia = 0.0f;

        for (double p : probabilidades) {
            if (p > 0) {
                entropia += -p * (Math.log(p) / Math.log(2));
            }
        }

        return entropia;
    }

    public static List<Double> calcularProbabilidades(Map<String, Double> clasificacionCategorias) {
        List<Double> probabilidades = new ArrayList<>();

        double total = clasificacionCategorias.values().stream().mapToDouble(Double::doubleValue).sum();

        for (Double cuenta : clasificacionCategorias.values()) {
            probabilidades.add(cuenta / total);
        }

        return probabilidades;
    }

    //Cambiar nombre, ya no es entropia ahora es métrico
    public static float calcularEntropiaGlobal(List<List<String[]>> resultado, String funcionModelo, Function<List<String[]>, Float> metodoUsado) {
        int tamañoInferiores = resultado.get(0).size();
        int tamañoSuperiores = resultado.get(1).size();

        int tamañoGlobal = tamañoInferiores + tamañoSuperiores;

        if (tamañoGlobal == 0) {
            return 0.0f;
        }

        float probabilidadInferiores = (float) tamañoInferiores / tamañoGlobal;
        float probabilidadSuperiores = (float) tamañoSuperiores / tamañoGlobal;

        float valorInferior = metodoUsado.apply(resultado.get(0));
        float valorSuperior = metodoUsado.apply(resultado.get(1));

        return (probabilidadInferiores * valorInferior) + (probabilidadSuperiores * valorSuperior);
    }

    public static Object[] getMejorSeparacion(List<String[]> dataset, Map<String, List<String>> divisiones, String[] cabecera, List<String> clasificacionesColumnas, String funcionModelo) {
        /*for (Map.Entry<String, List<String>> entry : divisiones.entrySet()) {
            String columna = entry.getKey();
            List<String> valores = entry.getValue();
            System.out.println("Columna: " + columna + " -> Valores: " + valores);
        }*/
        //Aqui también dejan de ser entropias
        float mejorEntropia = 999;
        String mejorColumna = "";
        Object mejorValorSplit = null;
        Boolean primeraEjecucion = true;

        for (Map.Entry<String, List<String>> entry : divisiones.entrySet()) {
            String columna = entry.getKey();
            List<String> valoresSplit = entry.getValue();

            int indice = Arrays.asList(cabecera).indexOf(columna);
            String tipoColumna = clasificacionesColumnas.get(indice);

            boolean esContinua = tipoColumna.equals("Continua");

            for (String valorSplit : valoresSplit) {
                Object valor = esContinua ? Float.parseFloat(valorSplit) : valorSplit;
                List<List<String[]>> resultado = separarDatos(dataset, columna, valor, cabecera, esContinua);

                float entropiaActual = 0;
                if (funcionModelo == "regresion"){
                    entropiaActual = calcularEntropiaGlobal(resultado, funcionModelo, (datos) -> calcularECM(datos, funcionModelo));
                }else{
                    entropiaActual = calcularEntropiaGlobal(resultado, funcionModelo, (datos) -> calcularEntropia(datos, funcionModelo));
                }


                if ((entropiaActual < mejorEntropia) || primeraEjecucion) {
                    primeraEjecucion = false;
                    mejorEntropia = entropiaActual;
                    mejorColumna = columna;
                    mejorValorSplit = valor;
                }
            }
        }

        return new Object[]{mejorColumna, mejorValorSplit};
    }
}
