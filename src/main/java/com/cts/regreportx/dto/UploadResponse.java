package com.cts.regreportx.dto;

public class UploadResponse {
    
    private String message;
    private String fileName;
    private String datasetType;
    private int recordsInserted;

    public UploadResponse() {
    }

    public UploadResponse(String message, String fileName, String datasetType, int recordsInserted) {
        this.message = message;
        this.fileName = fileName;
        this.datasetType = datasetType;
        this.recordsInserted = recordsInserted;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDatasetType() {
        return datasetType;
    }

    public void setDatasetType(String datasetType) {
        this.datasetType = datasetType;
    }

    public int getRecordsInserted() {
        return recordsInserted;
    }

    public void setRecordsInserted(int recordsInserted) {
        this.recordsInserted = recordsInserted;
    }
}
