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
        String rutaTest = "src/data/test.csv";
        String rutaTrain = "src/data/train.csv";
        String rutaDataSet = "src/data/dataset.csv";
        String rutaTitanic = "src/data/titanic_podas_t.csv";
        String rutaTitanicTrain = "src/data/titanic_podas_train.csv";

        String data2 = "src/data/train_bike.csv";
        String data3 = "src/data/val_bike.csv";

        String data4 = "src/data/pruebas_train.csv";
        String data5 = "src/data/pruebas_val.csv";


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
        lectorCsv.leerCSV(rutaDataSet);

        LectorFicheros lector2Csv = new LectorFicheros();
        lector2Csv.leerCSV(rutaTest);

        List<String> clasificacionesColumnas = Clasificador.determinarTipoColumna(lectorCsv.getContenido(), lectorCsv.getCabecera());

        ArbolDecision arbolDecision = new ArbolDecision(lectorCsv.getContenido().size());

        arbolDecision.seleccionarFilasAleatorias();
        arbolDecision.dividirDataset(lectorCsv.getContenido());
        //arbolDecision.cargarDatosTrain(lector2Csv.getContenido());
        //arbolDecision.cargarDatosTest(lectorCsv.getContenido());

        String tarea = "clasificacion";

        int numeroColumnasDivisionesPotenciales;
        int totalColumnas = lectorCsv.getCabecera().length-1;
        if (tarea.equals("clasificacion")) {
            numeroColumnasDivisionesPotenciales = (int) Math.max(1, Math.round(Math.sqrt(totalColumnas)));
        } else {
            numeroColumnasDivisionesPotenciales = (int) Math.max(1, Math.round(totalColumnas / 3.0));
        }
        System.out.println("Número columnas aleatorias " + numeroColumnasDivisionesPotenciales + ", de " + totalColumnas);
        Map<String, List<String>> divisiones = SeparadorClases.obtenerPosiblesDivisiones(arbolDecision.getDatosEntrenamiento(), lectorCsv.getCabecera(), clasificacionesColumnas, numeroColumnasDivisionesPotenciales);


        Object arbol = arbolDecision.crearArbolDecision(
                arbolDecision.getDatosEntrenamiento(),
                lectorCsv.getCabecera(),
                0,
                lectorCsv,
                divisiones,
                0,
                10,
                clasificacionesColumnas,
                tarea
        );

        imprimirArbol(arbol, 0);

        int[] resultado = evaluarArbol(arbol, arbolDecision.getDatosTest(), lectorCsv.getCabecera());
        double precision = (double) resultado[0] / resultado[1] * 100;
        System.out.println("Precisión del árbol original: " + precision + "%");

        Object arbolPodado = podaArbol(arbol, arbolDecision.getDatosEntrenamiento(), arbolDecision.getDatosTest(), lectorCsv.getCabecera(), tarea);

        imprimirArbol(arbolPodado, 0);

        int[] resultadoPoda = evaluarArbol(arbolPodado, arbolDecision.getDatosTest(), lectorCsv.getCabecera());
        double precisionPoda = (double) resultadoPoda[0] / resultadoPoda[1] * 100;
        System.out.println("Precisión del árbol podado: " + precisionPoda + "%");

        //Para regresión
        //mejorProfundidadRegresion(arbolDecision, lectorCsv, divisiones, clasificacionesColumnas);

        //System.out.println(calcularRCuadrado(arbolDecision.getDatosTest(), arbol, lectorCsv.getCabecera()));
        //System.out.println(calcularRCuadrado(arbolDecision.getDatosTest(), arbolPodado, lectorCsv.getCabecera()));
    }


    public static Object podaArbol(Object arbol, List<String[]> datosTrain, List<String[]> datosVal, String[] cabecera, String funcionModelo) {
        if (!(arbol instanceof Map)) {
            return arbol;
        }

        Map<String, Object> nodo = (Map<String, Object>) arbol;
        String pregunta = nodo.keySet().iterator().next();
        Object[] ramas = (Object[]) nodo.get(pregunta);

        if (ramas[0] instanceof String && ramas[1] instanceof String) {
            return podaRama(arbol, datosTrain, datosVal, cabecera, funcionModelo);
        }else{
            List<List<String[]>> resultadoTrain = filtrarDatos(datosTrain, pregunta, cabecera);
            List<List<String[]>> resultadoVal = filtrarDatos(datosVal, pregunta, cabecera);

            if (ramas[0] instanceof Map) {
                ramas[0] = podaArbol(ramas[0], resultadoTrain.get(0), resultadoVal.get(0), cabecera, funcionModelo);
            }
            if (ramas[1] instanceof Map) {
                ramas[1] = podaArbol(ramas[1], resultadoTrain.get(1), resultadoVal.get(1), cabecera, funcionModelo);
            }

            return podaRama(arbol, datosTrain, datosVal, cabecera, funcionModelo);
        }
    }

    public static List<List<String[]>> filtrarDatos(List<String[]> datos, String pregunta, String[] cabecera) {
        //System.out.println("Pregunta analizada " + pregunta);

        String[] partes = pregunta.split(" <= ");
        boolean esNumerico = true;

        if (partes.length != 2) {
            partes = pregunta.split(" == ");
            esNumerico = false;
        }

        String columna = partes[0];
        String valorComparacion = partes[1];

        int indiceColumna = Arrays.asList(cabecera).indexOf(columna);

        List<String[]> listaCumpleCondicion = new ArrayList<>();
        List<String[]> listaNoCumpleCondicion = new ArrayList<>();

        for (String[] fila : datos) {
            String valorDato = fila[indiceColumna];

            if (esNumerico) {
                double valor = Double.parseDouble(valorDato);
                double comparador = Double.parseDouble(valorComparacion);

                if (valor <= comparador) {
                    listaCumpleCondicion.add(fila);
                } else {
                    listaNoCumpleCondicion.add(fila);
                }
            } else {
                if (valorDato.equals(valorComparacion)) {
                    listaCumpleCondicion.add(fila);
                } else {
                    listaNoCumpleCondicion.add(fila);
                }
            }
        }

        return Arrays.asList(listaCumpleCondicion, listaNoCumpleCondicion);
    }

    public static Object podaRama(Object arbol, List<String[]> datosTrain, List<String[]> datosVal, String[] cabecera, String funcionModelo) {
        if(funcionModelo.equals("clasificacion")){
            String hojaSustituta = determinarHoja(datosTrain, funcionModelo, cabecera);

            Double erroresHojaSustituta = calcularErroresHoja(hojaSustituta, datosTrain, datosVal, cabecera, funcionModelo, arbol);

            int[] resultado = evaluarArbol(arbol, datosVal, cabecera);
            Double erroresNodo = Double.valueOf(resultado[1] - resultado[0]);

            if (erroresHojaSustituta <= erroresNodo) {
                return hojaSustituta;
            }
            return arbol;
        }else{
            String hojaSustituta = determinarHoja(datosTrain, funcionModelo, cabecera);

            Double erroresHojaSustituta = calcularErroresHoja(hojaSustituta, datosTrain, datosVal, cabecera, funcionModelo, arbol);

            Double erroresNodo = calcularRCuadrado(datosVal, arbol, cabecera);
            //System.out.println("Error hoja " + erroresHojaSustituta + ", arbol:" + erroresNodo);
            if (erroresHojaSustituta <= erroresNodo) {
                return hojaSustituta;
            }
            return arbol;
        }
    }

    public static Double calcularErroresHoja(String hoja, List<String[]> datosTrain, List<String[]> datosVal, String[] cabecera, String funcionModelo, Object arbol){
        if(funcionModelo.equals("clasificacion")){
            Map<String, Double> clasificacionVal = Clasificador.contarClases(datosVal, "clasificacion");
            int erroresHojaSustituta = 0;

            for (Map.Entry<String, Double> entry : clasificacionVal.entrySet()) {
                if (!entry.getKey().equals(hoja)) {
                    erroresHojaSustituta += entry.getValue().intValue();
                }
            }
            return Double.valueOf(erroresHojaSustituta);
        }else{
            double sumaErrores = 0;
            int total = 0;

            for(String[] fila: datosVal){
                String muestra = String.join(",", Arrays.copyOf(fila, fila.length - 1));
                String valorRealStr = fila[fila.length - 1];
                double valorReal = Double.parseDouble(valorRealStr);

                String prediccionStr = predecir(arbol, muestra, cabecera);
                double prediccion = Double.parseDouble(prediccionStr);
                double errorCuadratico = Math.pow(valorReal - prediccion, 2);
                sumaErrores += errorCuadratico;
                total++;
            }

            return Double.valueOf(sumaErrores / total);
        }
    }

    public static String determinarHoja(List<String[]> datosTrain, String funcionModelo, String[] cabecera){
        if(funcionModelo.equals("clasificacion")){
            Map<String, Double> clasificacionCategorias = Clasificador.contarClases(datosTrain, funcionModelo);

            String claseMayoritaria = null;
            double maxValor = Double.NEGATIVE_INFINITY;

            for (Map.Entry<String, Double> entry : clasificacionCategorias.entrySet()) {
                if (entry.getValue() > maxValor) {
                    maxValor = entry.getValue();
                    claseMayoritaria = entry.getKey();
                }
            }
            return claseMayoritaria;
        }else{
            int indiceColumna = Arrays.asList(cabecera).indexOf("Fila_regresion");

            double suma = 0;
            int contador = 0;

            for (String[] fila : datosTrain) {
                String valorDato = fila[indiceColumna];

                double valor = Double.parseDouble(valorDato);
                suma += valor;
                contador++;
            }
            return String.valueOf(suma / contador);
        }
    }

    public static int[] evaluarArbol(Object arbol, List<String[]> datos, String[] cabecera) {
        int total = 0;
        int aciertos = 0;

        for (String[] fila : datos) {
            String muestra = String.join(",", Arrays.copyOf(fila, fila.length - 1));
            String valorReal = fila[fila.length - 1];

            String prediccion = predecir(arbol, muestra, cabecera);

            if (prediccion.equals(valorReal)) {
                aciertos++;
            }
            total++;
        }

        return new int[]{aciertos, total};
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
                //System.out.println("Pregunta " + pregunta);
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

        for (int max_profundidad = 2; max_profundidad <= 20; max_profundidad++) {
            for (int min_num_muestras = 5; min_num_muestras <= 30; min_num_muestras += 5) {
                Object arbol = arbolDecision.crearArbolDecision(
                        datos_train,
                        cabecera,
                        0,
                        lectorCsv,
                        divisiones,
                        0,
                        max_profundidad,
                        clasificacionesColumnas,
                        "regresion"
                );

                double rCuadradoTrain = calcularRCuadrado(datos_train, arbol, cabecera);
                double rCuadradoVal = calcularRCuadrado(datos_evaluacion, arbol, cabecera);

                resultadosBusqueda.get("max_profundidad").add(max_profundidad);
                resultadosBusqueda.get("min_num_muestras").add(min_num_muestras);
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