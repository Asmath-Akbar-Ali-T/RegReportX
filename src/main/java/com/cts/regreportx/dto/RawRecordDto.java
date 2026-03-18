package com.cts.regreportx.dto;

import java.time.LocalDateTime;

public class RawRecordDto {

    private Integer rawRecordId;

    private Integer batchId;

    private String payloadJson;

    private LocalDateTime recordDate;

    public RawRecordDto() {
    }

    public Integer getRawRecordId() {
        return rawRecordId;
    }

    public void setRawRecordId(Integer rawRecordId) {
        this.rawRecordId = rawRecordId;
    }

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }

    public LocalDateTime getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDateTime recordDate) {
        this.recordDate = recordDate;
    }
}
