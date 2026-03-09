package com.cts.regreportx.service;

import com.cts.regreportx.model.DataSource;
import com.cts.regreportx.model.RawDataBatch;
import com.cts.regreportx.repository.DataSourceRepository;
import com.cts.regreportx.repository.RawDataBatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DataIngestionService {

    private final DataSourceRepository dataSourceRepository;
    private final RawDataBatchRepository rawDataBatchRepository;

    @Autowired
    public DataIngestionService(DataSourceRepository dataSourceRepository,
            RawDataBatchRepository rawDataBatchRepository) {
        this.dataSourceRepository = dataSourceRepository;
        this.rawDataBatchRepository = rawDataBatchRepository;
    }

    public RawDataBatch runIngestion(Integer sourceId) {
        // Find or create a DataSource for this example
        DataSource source = dataSourceRepository.findById(sourceId).orElseGet(() -> {
            DataSource newSource = new DataSource();
            newSource.setName("System Data Source");
            newSource.setSourceType("Internal Database");
            newSource.setStatus("ACTIVE");
            return dataSourceRepository.save(newSource);
        });

        // Create a new batch
        RawDataBatch batch = new RawDataBatch();
        batch.setSourceId(source.getSourceId());
        batch.setIngestedDate(LocalDateTime.now());
        batch.setRowCount(100); // Mock row count
        batch.setStatus("COMPLETED");

        return rawDataBatchRepository.save(batch);
    }

    public List<RawDataBatch> getAllBatches() {
        return rawDataBatchRepository.findAll();
    }
}
