package com.example.monitoring.repository;

import com.example.monitoring.model.CheckResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckResultRepository extends JpaRepository<CheckResult, Long> {

    Page<CheckResult> findByTargetIdOrderByCreatedAtDesc(String targetId, Pageable pageable);

    Page<CheckResult> findByTargetIdAndOkFalseOrderByCreatedAtDesc(String targetId, Pageable pageable);
}
