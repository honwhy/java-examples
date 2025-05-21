package com.honwhy.examples.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

public class CsvExportUtil {

    public static <T> void writeCsv(List<T> dataList, OutputStream outputStream) throws Exception {
        if (dataList == null || dataList.isEmpty()) {
            return;
        }

        Class<?> clazz = dataList.get(0).getClass();
        List<Field> fields = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        List<String> patterns = new ArrayList<>();

        // 提取字段和注解信息
        for (Field field : clazz.getDeclaredFields()) {
            TkHeader header = field.getAnnotation(TkHeader.class);
            if (header != null) {
                field.setAccessible(true);
                fields.add(field);
                headers.add(header.name());
                patterns.add(header.pattern());
            }
        }

        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        try (CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers.toArray(new String[0])))) {
            for (T item : dataList) {
                List<String> row = new ArrayList<>();
                for (int i = 0; i < fields.size(); i++) {
                    Object value = fields.get(i).get(item);
                    String pattern = patterns.get(i);
                    if (value instanceof Date && !pattern.isEmpty()) {
                        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                        row.add(sdf.format((Date) value));
                    } else {
                        row.add(value != null ? value.toString() : "");
                    }
                }
                printer.printRecord(row);
            }
        }
    }
}

