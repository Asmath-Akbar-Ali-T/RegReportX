package com.cts.regreportx.repository;

import com.cts.regreportx.model.ExposureRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExposureRecordRepository extends JpaRepository<ExposureRecord, Integer> {
}
