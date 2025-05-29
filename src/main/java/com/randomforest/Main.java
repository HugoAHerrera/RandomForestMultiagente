package com.randomforest;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Lector lector = new Lector();

        lector.leerCSV("src_antiguo/data/dataset.csv");
/**/
/*
        List<String> columnasAExcluir = List.of("sepal_width", "petal_width");
        lector.leerCSV("src_antiguo/data/dataset.csv", columnasAExcluir);
*/
        Map<String, Clasificador.Tipo> tipos = lector.getTiposColumnas();

        for (Map.Entry<String, Clasificador.Tipo> entry : tipos.entrySet()) {
            System.out.println("Columna: " + entry.getKey());
            System.out.println("Tipo: " + entry.getValue());
            System.out.println();
        }
    }
}
