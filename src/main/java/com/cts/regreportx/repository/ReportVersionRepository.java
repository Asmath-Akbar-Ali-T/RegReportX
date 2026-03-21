package com.cts.regreportx.repository;

import com.cts.regreportx.model.ReportVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportVersionRepository extends JpaRepository<ReportVersion, Integer> {
    Optional<ReportVersion> findTopByReportIdOrderByVersionNumberDesc(Integer reportId);
}
