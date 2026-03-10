package com.cts.regreportx.service;

import com.cts.regreportx.model.*;
import com.cts.regreportx.repository.RawDataBatchRepository;
import com.cts.regreportx.repository.RawRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RawRecordService {

    private final RawRecordRepository rawRecordRepository;
    private final RawDataBatchRepository batchRepository;
    private final SourceDataService sourceDataService;
    private final ObjectMapper objectMapper;

    @Autowired
    public RawRecordService(RawRecordRepository rawRecordRepository,
            RawDataBatchRepository batchRepository,
            SourceDataService sourceDataService) {
        this.rawRecordRepository = rawRecordRepository;
        this.batchRepository = batchRepository;
        this.sourceDataService = sourceDataService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public int loadRawRecords(Integer batchId) {
        Optional<RawDataBatch> batchOpt = batchRepository.findById(batchId);
        if (!batchOpt.isPresent()) {
            return 0;
        }

        RawDataBatch batch = batchOpt.get();
        Integer sourceId = batch.getSourceId();
        int recordsInserted = 0;

        try {
            // Determine which dataset to load based on source ID
            // Assuming 1=Loan, 2=Deposit, 3=Treasury, 4=GL from initDataSources order
            if (sourceId == 1) {
                List<Loan> loans = sourceDataService.getAllLoans();
                for (Loan loan : loans) {
                    insertRawRecord(batchId, loan);
                    recordsInserted++;
                }
            } else if (sourceId == 2) {
                List<Deposit> deposits = sourceDataService.getAllDeposits();
                for (Deposit deposit : deposits) {
                    insertRawRecord(batchId, deposit);
                    recordsInserted++;
                }
            } else if (sourceId == 3) {
                List<TreasuryTrade> trades = sourceDataService.getAllTreasuryTrades();
                for (TreasuryTrade trade : trades) {
                    insertRawRecord(batchId, trade);
                    recordsInserted++;
                }
            } else if (sourceId == 4) {
                List<GeneralLedger> gls = sourceDataService.getAllGeneralLedgers();
                for (GeneralLedger gl : gls) {
                    insertRawRecord(batchId, gl);
                    recordsInserted++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordsInserted;
    }

    private void insertRawRecord(Integer batchId, Object sourceEntity) throws Exception {
        RawRecord record = new RawRecord();
        record.setBatchId(batchId);
        record.setRecordDate(LocalDateTime.now());
        record.setPayloadJson(objectMapper.writeValueAsString(sourceEntity));
        rawRecordRepository.save(record);
    }

    public List<RawRecord> getRecordsByBatch(Integer batchId) {
        // Find all records matching this batch. We need a custom query method in repo.
        // Assuming we will add findByBatchId to repository.
        return rawRecordRepository.findByBatchId(batchId);
    }
}
