package src.main;

import java.util.*;
import src.funciones.DatoPuro;
import src.funciones.Clasificador;
import src.funciones.SeparadorClases;
import src.funciones.SeparadorEntropia;

public class ArbolDecision {

    private final int tamañoMuestra;
    private final Set<Integer> filasEntrenamiento;
    private final List<String[]> datosEntrenamiento;
    private final List<String[]> datosTest;

    public ArbolDecision(int numeroFilasCSV) {
        tamañoMuestra = numeroFilasCSV;
        filasEntrenamiento = new HashSet<>();
        datosEntrenamiento = new ArrayList<>();
        datosTest = new ArrayList<>();
    }

    public void seleccionarFilasAleatorias() {
        int cantidadFilas = (int) (tamañoMuestra * 0.2); // 20% del total
        Random random = new Random();

        while (filasEntrenamiento.size() < cantidadFilas) {
            int filaAleatoria = random.nextInt(tamañoMuestra);
            filasEntrenamiento.add(filaAleatoria);
        }
    }

    public void dividirDataset(List<String[]> datos) {

        for (int i = 0; i < datos.size(); i++) {
            if (filasEntrenamiento.contains(i)) {
                datosEntrenamiento.add(datos.get(i));
            } else {
                datosTest.add(datos.get(i));
            }
        }
    }

    //Borrar, ahora es prueba, usar luego dividirDataset
    public void cargarDatosTrain(List<String[]> datos) {

        for (String[] dato : datos) {
            datosEntrenamiento.add(dato);
        }
    }

    //Borrar, ahora es prueba, usar luego dividirDataset
    public void cargarDatosTest(List<String[]> datos) {

        for (String[] dato : datos) {
            datosTest.add(dato);
        }
    }

    public Object crearArbolDecision(List<String[]> datos, String[] cabecera, int contador, LectorFicheros lector, Map<String, List<String>> divisiones, int min_num_muestras, int max_profundidad, List<String> clasificacionesColumnas, String funcionModelo) {

        if (DatoPuro.comprobarPureza(datos) || (datos.size() < min_num_muestras) || (max_profundidad == contador)) {
            //Devolver el más probable
            Map<String, Double> resultado = Clasificador.contarClases(datos, funcionModelo);
            String clase = null;
            double maxValor = Double.NEGATIVE_INFINITY;

            for (Map.Entry<String, Double> entry : resultado.entrySet()) {
                if (entry.getValue() > maxValor) {
                    maxValor = entry.getValue();
                    clase = entry.getKey();
                }
            }
            return clase;
        }

        contador++;

        Object[] mejorSeparacion = SeparadorEntropia.getMejorSeparacion(datos, divisiones, cabecera, clasificacionesColumnas, funcionModelo);
        String mejorColumna = (String) mejorSeparacion[0];

        int indice_col = Arrays.asList(cabecera).indexOf(mejorColumna);
        String tipoColumna = clasificacionesColumnas.get(indice_col);

        boolean esContinua = tipoColumna.equals("Continua");

        Object mejorValorSplitObj = mejorSeparacion[1];
        String mejorValorSplit;

        if (mejorValorSplitObj instanceof Float || mejorValorSplitObj instanceof Double) {
            mejorValorSplit = mejorValorSplitObj.toString();
        } else {
            mejorValorSplit = (String) mejorValorSplitObj;
        }

        //Cambiar cosas desde aqui
        List<List<String[]>> splitData = SeparadorEntropia.separarDatos(
                datos,
                mejorColumna,
                esContinua ? Float.parseFloat(mejorValorSplit) : mejorValorSplit,
                cabecera,
                esContinua
        );

        List<String[]> datosInferiores = splitData.get(0);
        List<String[]> datosSuperiores = splitData.get(1);

        int indice = Arrays.asList(cabecera).indexOf(mejorColumna);
        String tipoColumnaFinal = clasificacionesColumnas.get(indice);

        String pregunta;
        if (tipoColumnaFinal.equals("Continua")){
            pregunta = mejorColumna + " <= " + mejorValorSplit;
        }else{
            pregunta = mejorColumna + " == " + mejorValorSplit;
        }

        Map<String, Object> subTree = new HashMap<>();

        Object rama1 = null;
        Object rama2 = null;

        try {
            if (!datosInferiores.isEmpty()) {
                rama1 = crearArbolDecision(datosInferiores, cabecera, contador, lector, divisiones, min_num_muestras, max_profundidad, clasificacionesColumnas, funcionModelo);
            } else {
                rama1 = Clasificador.contarClases(datosInferiores, funcionModelo).keySet().iterator().next();
            }

            if (!datosSuperiores.isEmpty()) {
                rama2 = crearArbolDecision(datosSuperiores, cabecera, contador, lector, divisiones, min_num_muestras, max_profundidad, clasificacionesColumnas, funcionModelo);
            } else {
                rama2 = Clasificador.contarClases(datosSuperiores, funcionModelo).keySet().iterator().next();
            }
        } catch (Exception e) {
            System.out.println("Error al procesar el árbol de decisión: " + e.getMessage());
            return null;
        }

        if (rama1.equals(rama2)) {
            return rama1;
        } else {
            subTree.put(pregunta, new Object[]{rama1, rama2});
        }

        return subTree;
    }

    public List<String[]> getDatosEntrenamiento() {
        return datosEntrenamiento;
    }

    public List<String[]> getDatosTest() {
        return datosTest;
    }
}