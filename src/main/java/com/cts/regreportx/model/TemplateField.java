package com.cts.regreportx.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TemplateField")
public class TemplateField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FieldID")
    private Integer fieldId;

    @Column(name = "TemplateID")
    private Integer templateId;

    @Column(name = "FieldName")
    private String fieldName;

    @Column(name = "DataType")
    private String dataType;

    @Column(name = "MappingExpression")
    private String mappingExpression;

    @Column(name = "RequiredFlag")
    private Boolean requiredFlag;

    public TemplateField() {
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
