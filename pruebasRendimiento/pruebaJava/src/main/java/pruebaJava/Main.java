package pruebaJava;

import java.io.*;
import java.util.*;
import org.apache.commons.csv.*;
import java.time.*;

public class Main {
    public static void main(String[] args) {
        String rutaArchivo = "/C:/Users/Hugo/Downloads/OneDrive_1_6-20-2025/winequality-synthetic-part3.csv";
        String rutaResultados = "resultados.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaResultados))) {

            // === MÉTODO 1: BufferedReader ===
            writer.write("=== BufferedReader ===\n");
            List<String[]> contenido = new ArrayList<>();
            String[] cabecera = null;

            Instant inicio = Instant.now();
            try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
                String linea;
                boolean primera = true;
                while ((linea = br.readLine()) != null) {
                    String[] campos = linea.split(",");
                    if (primera) {
                        cabecera = campos;
                        primera = false;
                    } else {
                        contenido.add(campos);
                    }
                }
            }
            Instant fin = Instant.now();
            writer.write("Tiempo de carga: " + Duration.between(inicio, fin).toMillis() + " ms\n");

            // Fila X millones
            inicio = Instant.now();
            if (contenido.size() > 14_999_999) {
                String[] fila = contenido.get(14_999_999);
                writer.write("Fila 15 millones: " + Arrays.toString(fila) + "\n");
            } else {
                writer.write("No hay 15 millones de filas\n");
            }
            fin = Instant.now();
            writer.write("Tiempo acceso fila 15M: " + Duration.between(inicio, fin).toMillis() + " ms\n");

            // Valores únicos primera columna
            inicio = Instant.now();
            Set<String> valoresUnicos = new HashSet<>();
            for (String[] fila : contenido) {
                if (fila.length > 0) {
                    valoresUnicos.add(fila[0].trim());
                }
            }
            fin = Instant.now();
            writer.write("Valores únicos primera columna: " + valoresUnicos.size() + "\n");
            writer.write("Tiempo valores únicos: " + Duration.between(inicio, fin).toMillis() + " ms\n\n");


            // === MÉTODO 2: Apache Commons CSV ===
            writer.write("=== Apache Commons CSV ===\n");

            List<String[]> contenidoCSV = new ArrayList<>();
            String[] cabeceraCSV;

            inicio = Instant.now();
            try (FileReader fr = new FileReader(rutaArchivo)) {
                CSVFormat format = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .build();
                CSVParser parser = new CSVParser(fr, format);
                Map<String, Integer> headerMap = parser.getHeaderMap();
                List<String> columnas = new ArrayList<>(headerMap.keySet());
                cabeceraCSV = columnas.toArray(new String[0]);

                for (CSVRecord record : parser) {
                    String[] fila = new String[cabeceraCSV.length];
                    for (int i = 0; i < cabeceraCSV.length; i++) {
                        fila[i] = record.get(cabeceraCSV[i]).trim();
                    }
                    contenidoCSV.add(fila);
                }
            }
            fin = Instant.now();
            writer.write("Tiempo de carga: " + Duration.between(inicio, fin).toMillis() + " ms\n");

            inicio = Instant.now();
            if (contenidoCSV.size() > 14_999_999) {
                String[] fila = contenidoCSV.get(14_999_999);
                writer.write("Fila 15 millones: " + Arrays.toString(fila) + "\n");
            }
            fin = Instant.now();
            writer.write("Tiempo acceso fila 15M: " + Duration.between(inicio, fin).toMillis() + " ms\n");

            // Valores únicos primera columna
            inicio = Instant.now();
            Set<String> valoresUnicosCSV = new HashSet<>();
            for (String[] fila : contenidoCSV) {
                if (fila.length > 0) {
                    valoresUnicosCSV.add(fila[0].trim());
                }
            }
            fin = Instant.now();
            writer.write("Valores únicos primera columna: " + valoresUnicosCSV.size() + "\n");
            writer.write("Tiempo valores únicos: " + Duration.between(inicio, fin).toMillis() + " ms\n");

            System.out.println("Resultados guardados en " + rutaResultados);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
