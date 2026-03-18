package com.cts.regreportx.dto;

import java.time.LocalDateTime;

public class CorrectionLogDto {

    private Integer correctionId;

    private Integer exceptionId;

    private String oldValue;

    private String newValue;

    private Integer correctedBy;

    private LocalDateTime correctedDate;

    public CorrectionLogDto() {
    }

    public Integer getCorrectionId() {
        return correctionId;
    }

    public void setCorrectionId(Integer correctionId) {
        this.correctionId = correctionId;
    }

    public Integer getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(Integer exceptionId) {
        this.exceptionId = exceptionId;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Integer getCorrectedBy() {
        return correctedBy;
    }

    public void setCorrectedBy(Integer correctedBy) {
        this.correctedBy = correctedBy;
    }

    public LocalDateTime getCorrectedDate() {
        return correctedDate;
    }

    public void setCorrectedDate(LocalDateTime correctedDate) {
        this.correctedDate = correctedDate;
    }
}
