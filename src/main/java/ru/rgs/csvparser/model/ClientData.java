package ru.rgs.csvparser.model;

import lombok.Builder;
import lombok.Data;

@Data
public class ClientData {

    private String clientName;
    private String contractDate;

    @Builder
    public ClientData(String clientName, String contractDate) {
        this.clientName = clientName;
        this.contractDate = contractDate;
    }

}
