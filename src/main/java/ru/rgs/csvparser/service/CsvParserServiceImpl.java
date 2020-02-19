package ru.rgs.csvparser.service;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.FileWriteMode;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import lombok.SneakyThrows;
import ru.rgs.csvparser.Command;
import ru.rgs.csvparser.model.Request;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import static java.lang.String.format;

public class CsvParserServiceImpl implements CsvParserService {

    private static final String CSV_FILE_EXTENSION = ".csv";
    private static final String HEADER = "CLIENT_NAME,CONTRACT_DATE,SCORING";
    private static final String NEW_LINE = System.lineSeparator();
    private static final String CSV_FILE_DELIMITER = ",";
    private static final String CLIENT_NAME_TEMPLATE = "%s %s %s";
    private final GsonEncoder gsonEncoder;
    private final GsonDecoder gsonDecoder;

    public CsvParserServiceImpl(GsonEncoder gsonEncoder, GsonDecoder gsonDecoder) {
        this.gsonEncoder = gsonEncoder;
        this.gsonDecoder = gsonDecoder;
    }

    @SneakyThrows
    @Override
    public Path processCsv(Path source) {
        File file = new File(UUID.randomUUID().toString() + CSV_FILE_EXTENSION);
        CharSink charSink = com.google.common.io.Files.asCharSink(file, Charsets.UTF_8, FileWriteMode.APPEND);
        charSink.write(HEADER + NEW_LINE);
        List<String> lines = Files.readAllLines(source);
        List<Future<String>> futures = new ArrayList<>();
        boolean isHeader = true;
        for (String line : lines) {
            if (isHeader) {
                isHeader = false;
            } else {
                String[] data = line.split(CSV_FILE_DELIMITER);
                String clientName = format(CLIENT_NAME_TEMPLATE, data[0], data[2], data[1]).toUpperCase();
                String contractDate = data[3];
                Request request = new Request(clientName, contractDate);
                futures.add(new Command(request, gsonEncoder, gsonDecoder).queue());
            }
        }
        for (Future<String> future : futures) {
            String row = future.get();
            charSink.write(row);
        }
        return file.toPath();
    }

}
