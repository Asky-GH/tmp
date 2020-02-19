package ru.rgs.csvparser.client;

import feign.Headers;
import feign.RequestLine;
import ru.rgs.csvparser.model.Response;

import java.util.Map;

public interface ScoreClient {

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    Response getScore(Map<String, String> payload);
}
