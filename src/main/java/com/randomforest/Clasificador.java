package com.randomforest;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Clasificador {

    public enum Tipo {
        CONTINUA,
        CATEGORICA
    }

    private final Map<String, Tipo> tiposColumnas = new HashMap<>();
    private final Map<String, Set<String>> valoresPorColumna = new HashMap<>();
    private static final int LIMITE_DISTINTOS = 15;
    private final Set<String> columnasContinuas = new HashSet<>();

    public void inicializarColumnas(String[] cabecera) {
        tiposColumnas.clear();
        valoresPorColumna.clear();
        columnasContinuas.clear();

        for (String col : cabecera) {
            valoresPorColumna.put(col, new HashSet<>());
        }
    }

    public void analizarFila(String[] fila, String[] cabecera) {
        for (int i = 0; i < fila.length; i++) {
            String col = cabecera[i];
            if (columnasContinuas.contains(col)) continue;

            Set<String> valores = valoresPorColumna.get(col);
            valores.add(fila[i]);

            if (valores.size() > LIMITE_DISTINTOS) {
                tiposColumnas.put(col, Tipo.CONTINUA);
                columnasContinuas.add(col);
                valoresPorColumna.remove(col);
            }
        }
    }

    public void finalizarClasificacion() {
        for (String col : valoresPorColumna.keySet()) {
            if (!tiposColumnas.containsKey(col)) {
                tiposColumnas.put(col, Tipo.CATEGORICA);
            }
        }
        valoresPorColumna.clear();
    }
}
