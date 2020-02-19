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
        return new CsvParserServiceImpl(gsonEncoder(), gsonDecoder());
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
