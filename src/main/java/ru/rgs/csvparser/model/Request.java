package ru.rgs.csvparser.model;

import lombok.Data;

@Data
public class Request {

    private String clientName;
    private String contractDate;

    public Request(String clientName, String contractDate) {
        this.clientName = clientName;
        this.contractDate = contractDate;
    }

}
