package ru.rgs.csvparser.client;

import feign.Headers;
import feign.RequestLine;
import ru.rgs.csvparser.model.Request;
import ru.rgs.csvparser.model.Response;

public interface Client {

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    Response getScore(Request request);

}
