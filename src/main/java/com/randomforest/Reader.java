package com.randomforest;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.csv.*;

import java.io.FileReader;
import java.util.*;

@Getter
@Setter
public class Reader {

    private String[] header = {};
    private String lastFileRead = "";
    private Classifier classifier = new Classifier();

    public void readCSV(String fileName) {
        readCSV(fileName, Collections.emptySet());
    }

    public void readCSV(String fileName, Set<String> ignoredColumns) {
        if (fileName.equals(lastFileRead)) {
            System.out.println("File previously read");
            return;
        }

        try (FileReader in = new FileReader(fileName)) {
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();

            CSVParser parser = new CSVParser(in, format);
            Map<String, Integer> headerMap = parser.getHeaderMap();

            List<String> filteredHeader = new ArrayList<>();
            for (String col : headerMap.keySet()) {
                if (ignoredColumns == null || !ignoredColumns.contains(col.trim())) {
                    filteredHeader.add(col.trim());
                }
            }

            header = filteredHeader.toArray(new String[0]);
            classifier.columnsInit(header);

            for (CSVRecord record : parser) {
                String[] filaFiltrada = new String[header.length];
                for (int i = 0; i < header.length; i++) {
                    filaFiltrada[i] = record.get(header[i]).trim();
                }
                classifier.analizeRow(filaFiltrada, header);
            }

            classifier.finishClassification();
            lastFileRead = fileName;
            System.out.println("File successfully read");

        } catch (Exception e) {
            System.out.println("Error while reading the file" + e.getMessage());
        }
    }

    public Map<String, Classifier.Type> getColumnTypes() {
        return classifier.getColumnTypes();
    }
}

