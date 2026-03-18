package com.cts.regreportx.dto;


public class TemplateFieldDto {

    private Integer fieldId;

    private Integer templateId;

    private String fieldName;

    private String dataType;

    private String mappingExpression;

    private Boolean requiredFlag;

    public TemplateFieldDto() {
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getMappingExpression() {
        return mappingExpression;
    }

    public void setMappingExpression(String mappingExpression) {
        this.mappingExpression = mappingExpression;
    }

    public Boolean getRequiredFlag() {
        return requiredFlag;
    }

    public void setRequiredFlag(Boolean requiredFlag) {
        this.requiredFlag = requiredFlag;
    }
}
