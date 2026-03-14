package com.cts.regreportx.repository;

import com.cts.regreportx.model.RiskMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RiskMetricRepository extends JpaRepository<RiskMetric, Integer> {
    
    @Transactional
    @Modifying
    @Query("DELETE FROM RiskMetric r WHERE r.reportId = :reportId")
    void deleteByReportId(Integer reportId);
}
