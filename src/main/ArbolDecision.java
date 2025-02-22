package src.main;

import java.util.*;

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

    public List<String[]> getDatosEntrenamiento() {
        return datosEntrenamiento;
    }

    public List<String[]> getDatosTest() {
        return datosTest;
    }
}