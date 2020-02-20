package ru.rgs.csvparser.model;

import lombok.Builder;
import lombok.Data;

@Data
public class EnrichedClientData {

    private EnrichedClientDataStatus status;
    private float scoringValue;
    private String clientName;
    private String contractDate;

    @Builder
    public EnrichedClientData(EnrichedClientDataStatus status) {
        this.status = status;
    }
}
