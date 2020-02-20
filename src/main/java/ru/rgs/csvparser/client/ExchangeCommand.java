package ru.rgs.csvparser.client;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import lombok.Builder;
import ru.rgs.csvparser.model.ClientData;
import ru.rgs.csvparser.model.EnrichedClientData;
import ru.rgs.csvparser.model.EnrichedClientDataStatus;

public class ExchangeCommand extends HystrixCommand<EnrichedClientData> {

    private static final String URL = "http://localhost:8081/score";
    private GsonEncoder gsonEncoder;
    private GsonDecoder gsonDecoder;
    private ClientData clientData;

    @Builder
    public ExchangeCommand(ClientData clientData, GsonEncoder gsonEncoder, GsonDecoder gsonDecoder) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.clientData = clientData;
        this.gsonEncoder = gsonEncoder;
        this.gsonDecoder = gsonDecoder;
    }

    @Override
    protected EnrichedClientData run() {
        FeignClient feignClient = Feign.builder()
                .encoder(gsonEncoder)
                .decoder(gsonDecoder)
                .target(FeignClient.class, URL);
        EnrichedClientData enrichedClientData;
        try {
            enrichedClientData = feignClient.exchangeData(clientData);
        } catch (Exception e) {
            enrichedClientData = EnrichedClientData.builder()
                    .status(EnrichedClientDataStatus.FAILED)
                    .build();
        }
        enrichedClientData.setClientName(clientData.getClientName());
        enrichedClientData.setContractDate(clientData.getContractDate());
        return enrichedClientData;
    }

}
