package ru.rgs.csvparser.service;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.FileWriteMode;
import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import lombok.SneakyThrows;
import ru.rgs.csvparser.client.ScoreClient;
import ru.rgs.csvparser.model.Response;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvParserServiceImpl implements CsvParserService {

    @SneakyThrows
    @Override
    public Path processCsv(Path source) {
        File file = new File("output.csv");
        CharSink charSink = com.google.common.io.Files.asCharSink(file, Charsets.UTF_8, FileWriteMode.APPEND);
        charSink.write("CLIENT_NAME,CONTRACT_DATE,SCORING" + System.lineSeparator());
        List<String> lines = Files.readAllLines(source);
        boolean isHeader = true;
        for (String line : lines) {
            if (isHeader) {
                isHeader = false;
            } else {
//                System.out.println(line);
                String[] data = line.split(",");
                String clientName = String.format("%s %s %s", data[0], data[2], data[1]);
                String contractDate = data[3];

                Map<String, String> payload = new HashMap<>();
                payload.put("clientName", clientName.toUpperCase());
                payload.put("contractDate", contractDate);

                ScoreClient scoreClient = Feign.builder()
                        .client(new OkHttpClient())
                        .encoder(new GsonEncoder())
                        .decoder(new GsonDecoder())
                        .target(ScoreClient.class, "http://localhost:8081/score");
//                System.out.println(scoreClient.getScore(payload));

                Response response = scoreClient.getScore(payload);
                if (response.getStatus().equalsIgnoreCase("NOT_FOUND")) {
                    System.out.println(response.getStatus());
                    charSink.write(String.format("%s,%s,не найден", clientName.toUpperCase(), contractDate) + System.lineSeparator());
                } else {
                    charSink.write(String.format("%s,%s,%s", clientName.toUpperCase(), contractDate, response.getScoringValue()) + System.lineSeparator());
                }
            }
        }
        return file.toPath();
    }

}
