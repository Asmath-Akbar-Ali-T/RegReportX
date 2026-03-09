package com.cts.regreportx.repository;

import com.cts.regreportx.model.ValidationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidationRuleRepository extends JpaRepository<ValidationRule, Integer> {
}
