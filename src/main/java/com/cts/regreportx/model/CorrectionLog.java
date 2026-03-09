package com.cts.regreportx.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "CorrectionLog")
public class CorrectionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CorrectionID")
    private Integer correctionId;

    @Column(name = "ExceptionID")
    private Integer exceptionId;

    @Column(name = "OldValue")
    private String oldValue;

    @Column(name = "NewValue")
    private String newValue;

    @Column(name = "CorrectedBy")
    private Integer correctedBy;

    @Column(name = "CorrectedDate")
    private LocalDateTime correctedDate;

    public CorrectionLog() {
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
