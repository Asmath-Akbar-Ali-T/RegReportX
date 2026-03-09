package com.cts.regreportx.repository;

import com.cts.regreportx.model.RawRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawRecordRepository extends JpaRepository<RawRecord, Integer> {
}
