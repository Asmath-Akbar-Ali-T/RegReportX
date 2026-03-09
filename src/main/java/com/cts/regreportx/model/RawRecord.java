package com.cts.regreportx.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "RawRecord")
public class RawRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RawRecordID")
    private Integer rawRecordId;

    @Column(name = "BatchID")
    private Integer batchId;

    @Column(name = "PayloadJSON", columnDefinition = "TEXT")
    private String payloadJson;

    @Column(name = "RecordDate")
    private LocalDateTime recordDate;

    public RawRecord() {
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
