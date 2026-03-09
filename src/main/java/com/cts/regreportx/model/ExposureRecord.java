package com.cts.regreportx.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ExposureRecord")
public class ExposureRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ExposureID")
    private Integer exposureId;

    @Column(name = "ReportID")
    private Integer reportId;

    @Column(name = "CounterpartyID")
    private String counterpartyId;

    @Column(name = "EAD")
    private BigDecimal ead;

    @Column(name = "LGD")
    private BigDecimal lgd;

    @Column(name = "PD")
    private BigDecimal pd;

    @Column(name = "RiskWeight")
    private BigDecimal riskWeight;

    @Column(name = "ExposureDate")
    private LocalDate exposureDate;

    public ExposureRecord() {
    }

    public Integer getExposureId() {
        return exposureId;
    }

    public void setExposureId(Integer exposureId) {
        this.exposureId = exposureId;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getCounterpartyId() {
        return counterpartyId;
    }

    public void setCounterpartyId(String counterpartyId) {
        this.counterpartyId = counterpartyId;
    }

    public BigDecimal getEad() {
        return ead;
    }

    public void setEad(BigDecimal ead) {
        this.ead = ead;
    }

    public BigDecimal getLgd() {
        return lgd;
    }

    public void setLgd(BigDecimal lgd) {
        this.lgd = lgd;
    }

    public BigDecimal getPd() {
        return pd;
    }

    public void setPd(BigDecimal pd) {
        this.pd = pd;
    }

    public BigDecimal getRiskWeight() {
        return riskWeight;
    }

    public void setRiskWeight(BigDecimal riskWeight) {
        this.riskWeight = riskWeight;
    }

    public LocalDate getExposureDate() {
        return exposureDate;
    }

    public void setExposureDate(LocalDate exposureDate) {
        this.exposureDate = exposureDate;
    }
}
