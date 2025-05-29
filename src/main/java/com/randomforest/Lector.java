package com.randomforest;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.csv.*;

import java.io.FileReader;
import java.io.Reader;
import java.util.*;

@Getter
@Setter
public class Lector {

    private String[] cabecera = {};
    private String ultimoFicheroLeido = "";
    private Clasificador clasificador = new Clasificador();

    public void leerCSV(String nombreArchivo) {
        leerCSV(nombreArchivo, Collections.emptySet());
    }

    public void leerCSV(String nombreArchivo, Set<String> columnasIgnorar) {
        if (nombreArchivo.equals(ultimoFicheroLeido)) {
            System.out.println("El archivo ya fue leído previamente.");
            return;
        }

        try (Reader in = new FileReader(nombreArchivo)) {
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();

            CSVParser parser = new CSVParser(in, format);
            Map<String, Integer> headerMap = parser.getHeaderMap();

            List<String> cabeceraFiltrada = new ArrayList<>();
            for (String col : headerMap.keySet()) {
                if (columnasIgnorar == null || !columnasIgnorar.contains(col.trim())) {
                    cabeceraFiltrada.add(col.trim());
                }
            }

            cabecera = cabeceraFiltrada.toArray(new String[0]);
            clasificador.inicializarColumnas(cabecera);

            for (CSVRecord record : parser) {
                String[] filaFiltrada = new String[cabecera.length];
                for (int i = 0; i < cabecera.length; i++) {
                    filaFiltrada[i] = record.get(cabecera[i]).trim();
                }
                clasificador.analizarFila(filaFiltrada, cabecera);
            }

            clasificador.finalizarClasificacion();
            ultimoFicheroLeido = nombreArchivo;
            System.out.println("Archivo leído correctamente.");

        } catch (Exception e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    public Map<String, Clasificador.Tipo> getTiposColumnas() {
        return clasificador.getTiposColumnas();
    }
}

