package ru.rgs.csvparser.model;

import lombok.Data;

@Data
public class Response {
    private String status;
    private float scoringValue;
}
