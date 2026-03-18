package com.cts.regreportx.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExposureRecordDto {

    private Integer exposureId;

    private Integer reportId;

    private String counterpartyId;

    private BigDecimal ead;

    private BigDecimal lgd;

    private BigDecimal pd;

    private BigDecimal riskWeight;

    private LocalDate exposureDate;

    public ExposureRecordDto() {
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
