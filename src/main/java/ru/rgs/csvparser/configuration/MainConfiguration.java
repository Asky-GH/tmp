package ru.rgs.csvparser.configuration;

import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.rgs.csvparser.service.CsvParserService;
import ru.rgs.csvparser.service.CsvParserServiceImpl;

@Configuration
public class MainConfiguration {

    @Bean
    public CsvParserService csvParserService() {
        return CsvParserServiceImpl.builder()
                .gsonEncoder(gsonEncoder())
                .gsonDecoder(gsonDecoder())
                .build();
    }

    @Bean
    public GsonEncoder gsonEncoder() {
        return new GsonEncoder();
    }

    @Bean
    public GsonDecoder gsonDecoder() {
        return new GsonDecoder();
    }

}
