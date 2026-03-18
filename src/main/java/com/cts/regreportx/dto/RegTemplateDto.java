package com.cts.regreportx.dto;


public class RegTemplateDto {

    private Integer templateId;

    private String regulationCode;

    private String description;

    private String frequency;

    private String status;

    public RegTemplateDto() {
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public String getRegulationCode() {
        return regulationCode;
    }

    public void setRegulationCode(String regulationCode) {
        this.regulationCode = regulationCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
