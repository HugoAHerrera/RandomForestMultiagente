package com.randomforest;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Reader reader = new Reader();

        reader.readCSV("src_antiguo/data/dataset.csv");
/**/
/*
        List<String> columnasAExcluir = List.of("sepal_width", "petal_width");
        lector.leerCSV("src_antiguo/data/dataset.csv", columnasAExcluir);
*/
        Map<String, Classifier.Type> tipos = reader.getColumnTypes();

        for (Map.Entry<String, Classifier.Type> entry : tipos.entrySet()) {
            System.out.println("Columna: " + entry.getKey());
            System.out.println("Tipo: " + entry.getValue());
            System.out.println();
        }
    }
}
