package ru.rgs.csvparser;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import ru.rgs.csvparser.client.Client;
import ru.rgs.csvparser.model.Request;
import ru.rgs.csvparser.model.Response;

import static java.lang.String.format;

public class Command extends HystrixCommand<String> {

    private static final String URL = "http://localhost:8081/score";
    private static final String NOT_FOUND_STATUS = "NOT_FOUND";
    private static final String COMPLETED_TEMPLATE = "%s,%s,%s%s";
    private static final String NOT_FOUND_TEMPLATE = "%s,%s,не найден%s";
    private static final String FAILED_TEMPLATE = "%s,%s,ошибка обработки%s";
    private static final String NEW_LINE = System.lineSeparator();
    private final Request request;
    private final GsonEncoder gsonEncoder;
    private final GsonDecoder gsonDecoder;

    public Command(Request request, GsonEncoder gsonEncoder, GsonDecoder gsonDecoder) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.request = request;
        this.gsonEncoder = gsonEncoder;
        this.gsonDecoder = gsonDecoder;
    }

    @Override
    protected String run() {
        Client client = Feign.builder()
                .encoder(gsonEncoder)
                .decoder(gsonDecoder)
                .target(Client.class, URL);
        String data;
        String clientName = request.getClientName();
        String contractDate = request.getContractDate();
        try {
            Response response = client.getScore(request);
            if (response.getStatus().equalsIgnoreCase(NOT_FOUND_STATUS)) {
                data = format(NOT_FOUND_TEMPLATE, clientName, contractDate, NEW_LINE);
            } else {
                data = format(COMPLETED_TEMPLATE, clientName, contractDate, response.getScoringValue(), NEW_LINE);
            }
        } catch (Exception e) {
            data = format(FAILED_TEMPLATE, clientName, contractDate, NEW_LINE);
        }
        return data;
    }

}
