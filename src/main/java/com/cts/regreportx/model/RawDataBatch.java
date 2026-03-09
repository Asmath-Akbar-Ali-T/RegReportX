package com.cts.regreportx.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "RawDataBatch")
public class RawDataBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BatchID")
    private Integer batchId;

    @Column(name = "SourceID")
    private Integer sourceId;

    @Column(name = "IngestedDate")
    private LocalDateTime ingestedDate;

    @Column(name = "RowCount")
    private Integer rowCount;

    @Column(name = "Status")
    private String status;

    public RawDataBatch() {
    }

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public LocalDateTime getIngestedDate() {
        return ingestedDate;
    }

    public void setIngestedDate(LocalDateTime ingestedDate) {
        this.ingestedDate = ingestedDate;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
