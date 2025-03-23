package src.main;

import src.funciones.DatoPuro;
import src.funciones.Clasificador;
import src.funciones.SeparadorClases;
import src.funciones.SeparadorEntropia;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String rutaArchivo = "src/data/winequality-red-categorical.csv";
        String rutaTest = "src/data/test2.csv";
        String rutaTrain = "src/data/train2.csv";
        String rutaDataSet = "src/data/dataset.csv";
        String rutaTitanic = "src/data/titanic.csv";

        String data2 = "src/data/train_bike.csv";
        String data3 = "src/data/val_bike.csv";


        /*
        LectorFicheros lectorPruebas = new LectorFicheros();
        lectorPruebas.leerCSV(rutaTitanic);

        Clasificador clasif = new Clasificador();
        List<String> clasificacionesColumnas;
        clasificacionesColumnas = clasif.determinarTipoColumna(lectorPruebas.getContenido(), lectorPruebas.getCabecera());

        System.out.println(clasificacionesColumnas);
        */


        /*
        LectorFicheros lector_test = new LectorFicheros();
        lector_test.leerCSV(rutaTest);

        LectorFicheros lector_train = new LectorFicheros();
        lector_train.leerCSV(rutaTrain);

        ArbolDecision arbolDecision = new ArbolDecision(lector_test.getContenido().size());
        arbolDecision.cargarDatosTrain(lector_train.getContenido());
        arbolDecision.cargarDatosTest(lector_test.getContenido());
        */
        LectorFicheros lectorCsv = new LectorFicheros();
        lectorCsv.leerCSV(data2);

        LectorFicheros lector2Csv = new LectorFicheros();
        lector2Csv.leerCSV(data3);

        List<String> clasificacionesColumnas = Clasificador.determinarTipoColumna(lectorCsv.getContenido(), lectorCsv.getCabecera());

        ArbolDecision arbolDecision = new ArbolDecision(lectorCsv.getContenido().size());

        //arbolDecision.seleccionarFilasAleatorias();
        //arbolDecision.dividirDataset(lectorCsv.getContenido());
        arbolDecision.cargarDatosTrain(lectorCsv.getContenido());
        arbolDecision.cargarDatosTest(lector2Csv.getContenido());

        Map<String, List<String>> divisiones = SeparadorClases.obtenerPosiblesDivisiones(arbolDecision.getDatosEntrenamiento(), lectorCsv.getCabecera(), clasificacionesColumnas);

        Object arbol = arbolDecision.crearArbolDecision(
                arbolDecision.getDatosEntrenamiento(),
                lectorCsv.getCabecera(),
                0,
                lectorCsv,
                divisiones,
                0,
                3,
                clasificacionesColumnas,
                "regresion"
        );

        imprimirArbol(arbol, 0);

        //Esto solo en clasificacion
        //evaluarArbol(arbol, arbolDecision.getDatosTest(), lectorCsv.getCabecera());

        //Para regresión
        mejorProfundidadRegresion(arbolDecision, lectorCsv, divisiones, clasificacionesColumnas);
    }

    public static void imprimirArbol(Object nodo, int nivel) {
        if (nodo instanceof String) {
            System.out.println("  ".repeat(nivel) + "→ " + nodo);
        } else if (nodo instanceof Map) {
            Map<String, Object> mapa = (Map<String, Object>) nodo;
            for (Map.Entry<String, Object> entry : mapa.entrySet()) {
                System.out.println("  ".repeat(nivel) + entry.getKey());
                Object[] ramas = (Object[]) entry.getValue();
                imprimirArbol(ramas[0], nivel + 1);
                imprimirArbol(ramas[1], nivel + 1);
            }
        }
    }

    //Clase evaluador??
    public static String predecir(Object arbol, String muestra, String[] cabecera) {
        String[] valores = muestra.split(",");
        Map<String, String> datosMuestra = new HashMap<>();

        for (int i = 0; i < valores.length; i++) {
            datosMuestra.put(cabecera[i], valores[i]);
        }

        return recorrerArbol(arbol, datosMuestra);
    }

    private static String recorrerArbol(Object nodo, Map<String, String> datosMuestra) {
        if (nodo instanceof String) {
            return (String) nodo;
        }

        if (nodo instanceof Map) {
            Map<String, Object> nodoMapa = (Map<String, Object>) nodo;
            String pregunta = nodoMapa.keySet().iterator().next();
            Object[] ramas = (Object[]) nodoMapa.get(pregunta);

            if (pregunta.contains(" <= ")) {
                String[] partes = pregunta.split(" <= ");
                String columna = partes[0];
                double valorComparacion = Double.parseDouble(partes[1]);
                double valorMuestra = Double.parseDouble(datosMuestra.get(columna));

                return recorrerArbol(valorMuestra <= valorComparacion ? ramas[0] : ramas[1], datosMuestra);
            } else if (pregunta.contains(" == ")) {
                String[] partes = pregunta.split(" == ");
                String columna = partes[0];
                String valorComparacion = partes[1];

                return recorrerArbol(datosMuestra.get(columna).equals(valorComparacion) ? ramas[0] : ramas[1], datosMuestra);
            }
        }

        return "Error en la clasificación";
    }


    public static void evaluarArbol(Object arbol, List<String[]> datosTest, String[] cabecera) {
        int total = 0;
        int aciertos = 0;

        for (String[] fila : datosTest) {
            String muestra = String.join(",", Arrays.copyOf(fila, fila.length - 1));
            String valorReal = fila[fila.length - 1];

            String prediccion = predecir(arbol, muestra, cabecera);

            if (prediccion.equals(valorReal)) {
                aciertos++;
            }
            total++;
        }

        double precision = (double) aciertos / total * 100;
        System.out.println("Precisión del árbol: " + precision + "%");
    }

    public static double calcularRCuadrado(List<String[]> datos, Object arbol, String[] cabecera) {
        List<Double> valoresReales = new ArrayList<>();
        List<Double> predicciones = new ArrayList<>();

        for (String[] fila : datos) {
            if (fila.length > 0) {
                String muestra = String.join(",", Arrays.copyOf(fila, fila.length - 1));
                String valorRealStr = fila[fila.length - 1];
                double valorReal = Double.parseDouble(valorRealStr);
                valoresReales.add(valorReal);

                String prediccionStr = predecir(arbol, muestra, cabecera);
                double prediccion = Double.parseDouble(prediccionStr);
                predicciones.add(prediccion);
            }
        }

        double media = valoresReales.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        double ssRes = 0.0;
        for (int i = 0; i < valoresReales.size(); i++) {
            ssRes += Math.pow(valoresReales.get(i) - predicciones.get(i), 2);
        }

        double ssTot = 0.0;
        for (double valorReal : valoresReales) {
            ssTot += Math.pow(valorReal - media, 2);
        }

        return 1 - (ssRes / ssTot);
    }

    public static void mejorProfundidadRegresion(ArbolDecision arbolDecision, LectorFicheros lectorCsv, Map<String, List<String>> divisiones, List<String> clasificacionesColumnas) {
        Map<String, List<Object>> resultadosBusqueda = new HashMap<>();
        resultadosBusqueda.put("max_profundidad", new ArrayList<>());
        resultadosBusqueda.put("min_num_muestras", new ArrayList<>());
        resultadosBusqueda.put("r_cuadrado_train", new ArrayList<>());
        resultadosBusqueda.put("r_cuadrado_val", new ArrayList<>());

        List<String[]> datos_train = arbolDecision.getDatosEntrenamiento();
        List<String[]> datos_evaluacion = arbolDecision.getDatosTest();
        String[] cabecera = lectorCsv.getCabecera();

        for (int maxDepth = 2; maxDepth <= 20; maxDepth++) {
            for (int minSamples = 5; minSamples <= 30; minSamples += 5) {
                Object arbol = arbolDecision.crearArbolDecision(
                        datos_train,
                        cabecera,
                        0,
                        lectorCsv,
                        divisiones,
                        0,
                        maxDepth,
                        clasificacionesColumnas,
                        "regresion"
                );

                double rCuadradoTrain = calcularRCuadrado(datos_train, arbol, cabecera);
                double rCuadradoVal = calcularRCuadrado(datos_evaluacion, arbol, cabecera);

                resultadosBusqueda.get("max_profundidad").add(maxDepth);
                resultadosBusqueda.get("min_num_muestras").add(minSamples);
                resultadosBusqueda.get("r_cuadrado_train").add(rCuadradoTrain);
                resultadosBusqueda.get("r_cuadrado_val").add(rCuadradoVal);
            }
        }

        imprimirResultadosProfundidad(resultadosBusqueda);
    }

    public static void imprimirResultadosProfundidad(Map<String, List<Object>> resultadosBusqueda) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        int n = resultadosBusqueda.get("max_profundidad").size();
        for (int i = 0; i < n; i++) {
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("max_profundidad", resultadosBusqueda.get("max_profundidad").get(i));
            resultado.put("min_num_muestras", resultadosBusqueda.get("min_num_muestras").get(i));
            resultado.put("r_cuadrado_train", resultadosBusqueda.get("r_cuadrado_train").get(i));
            resultado.put("r_cuadrado_val", resultadosBusqueda.get("r_cuadrado_val").get(i));
            resultados.add(resultado);
        }

        resultados.sort((r1, r2) -> {
            int ordenarMayorCuadradoTrain = Double.compare((Double) r2.get("r_cuadrado_train"), (Double) r1.get("r_cuadrado_train"));

            if (ordenarMayorCuadradoTrain != 0) {
                return ordenarMayorCuadradoTrain;
            } else {
                return Double.compare((Double) r2.get("r_cuadrado_val"), (Double) r1.get("r_cuadrado_val"));
            }
        });

        System.out.println("max_profundidad | min_num_muestras | r_cuadrado_train | r_cuadrado_val");
        for (Map<String, Object> res : resultados) {
            System.out.println(res.get("max_profundidad") + " | " + res.get("min_num_muestras") + " | " +
                    res.get("r_cuadrado_train") + " | " + res.get("r_cuadrado_val"));
        }
    }


}