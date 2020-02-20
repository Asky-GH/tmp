package ru.rgs.csvparser.client;

import feign.Headers;
import feign.RequestLine;
import ru.rgs.csvparser.model.ClientData;
import ru.rgs.csvparser.model.EnrichedClientData;

public interface FeignClient {

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    EnrichedClientData exchangeData(ClientData clientData);

}
