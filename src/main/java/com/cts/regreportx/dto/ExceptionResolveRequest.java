package com.cts.regreportx.dto;

public class ExceptionResolveRequest {
    private String correctedValue;
    private String justification;

    public String getCorrectedValue() { return correctedValue; }
    public void setCorrectedValue(String correctedValue) { this.correctedValue = correctedValue; }
    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }
}
