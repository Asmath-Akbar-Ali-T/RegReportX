package com.cts.regreportx.dto;

import java.time.LocalDateTime;

public class RawDataBatchDto {

    private Integer batchId;

    private Integer sourceId;

    private LocalDateTime ingestedDate;

    private Integer rowCount;

    private String status;

    public RawDataBatchDto() {
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
