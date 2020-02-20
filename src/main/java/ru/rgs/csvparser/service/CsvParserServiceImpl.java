package ru.rgs.csvparser.service;

import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import lombok.Builder;
import lombok.SneakyThrows;
import ru.rgs.csvparser.client.ExchangeCommand;
import ru.rgs.csvparser.model.ClientData;
import ru.rgs.csvparser.model.EnrichedClientData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.lang.String.format;

public class CsvParserServiceImpl implements CsvParserService {

    private static final String FILE_EXTENSION = ".csv";
    private static final String FILE_HEADER = "CLIENT_NAME,CONTRACT_DATE,SCORING";
    private static final String NEWLINE = System.lineSeparator();
    private static final String FILE_DELIMITER = ",";
    private static final String CLIENT_NAME_TEMPLATE = "%s %s %s";
    private static final String NOT_FOUND_WORDING = "не найден";
    private static final String FAILED_WORDING = "ошибка обработки";
    private final GsonEncoder gsonEncoder;
    private final GsonDecoder gsonDecoder;

    @Builder
    public CsvParserServiceImpl(GsonEncoder gsonEncoder, GsonDecoder gsonDecoder) {
        this.gsonEncoder = gsonEncoder;
        this.gsonDecoder = gsonDecoder;
    }

    @SneakyThrows
    @Override
    public Path processCsv(Path source) {
        List<ClientData> clientDataList = parseInputFile(source);
        List<EnrichedClientData> enrichedClientDataList = requestClientData(clientDataList);
        return writeToFile(enrichedClientDataList).toPath();
    }

    private List<ClientData> parseInputFile(Path source) throws IOException {
        List<ClientData> clientDataList = new ArrayList<>();
        List<String> lines = Files.readAllLines(source);
        boolean isHeader = true;
        for (String line : lines) {
            if (isHeader) {
                isHeader = false;
            } else {
                String[] data = line.split(FILE_DELIMITER);
                clientDataList.add(ClientData.builder()
                        .clientName(format(CLIENT_NAME_TEMPLATE, data[0], data[2], data[1]).toUpperCase())
                        .contractDate(data[3])
                        .build());
            }
        }
        return clientDataList;
    }

    private List<EnrichedClientData> requestClientData(List<ClientData> clientDataList) throws ExecutionException,
            InterruptedException {
        List<EnrichedClientData> enrichedClientDataList = new ArrayList<>();
        List<Future<EnrichedClientData>> enrichedClientDataFutures = new ArrayList<>();
        for (ClientData clientData : clientDataList) {
            enrichedClientDataFutures.add(ExchangeCommand.builder()
                    .clientData(clientData)
                    .gsonEncoder(gsonEncoder)
                    .gsonDecoder(gsonDecoder)
                    .build()
                    .queue());
        }
        for (Future<EnrichedClientData> enrichedClientDataFuture : enrichedClientDataFutures) {
            enrichedClientDataList.add(enrichedClientDataFuture.get());
        }
        return enrichedClientDataList;
    }

    private File writeToFile(List<EnrichedClientData> enrichedClientDataList) throws IOException {
        String filename = UUID.randomUUID().toString() + FILE_EXTENSION;
        File file = new File(filename);
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8)) {
            writer.append(FILE_HEADER).append(NEWLINE);
            for (EnrichedClientData enrichedClientData : enrichedClientDataList) {
                writer.append(prepareRecord(enrichedClientData)).append(NEWLINE);
            }
        }
        return file;
    }

    private String prepareRecord(EnrichedClientData enrichedClientData) {
        StringJoiner stringJoiner = new StringJoiner(FILE_DELIMITER);
        stringJoiner.add(enrichedClientData.getClientName())
                .add(enrichedClientData.getContractDate());
        switch (enrichedClientData.getStatus()) {
            case COMPLETED:
                stringJoiner.add(Float.toString(enrichedClientData.getScoringValue()));
                break;

            case NOT_FOUND:
                stringJoiner.add(NOT_FOUND_WORDING);
                break;

            case FAILED:
                stringJoiner.add(FAILED_WORDING);
                break;

            default:
                break;
        }
        return stringJoiner.toString();
    }

}
