package com.example.monitoring.api;

import com.example.monitoring.model.CheckResult;
import com.example.monitoring.repository.CheckResultRepository;
import com.example.monitoring.service.TargetRegistry;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/targets")
public class CheckResultController {

    private static final int MAX_LIMIT = 200;

    private final CheckResultRepository repository;
    private final TargetRegistry targetRegistry;

    public CheckResultController(CheckResultRepository repository, TargetRegistry targetRegistry) {
        this.repository = repository;
        this.targetRegistry = targetRegistry;
    }

    @PostMapping
    public TargetRegistry.MonitoringTarget createTarget(@Valid @RequestBody TargetRequest request) {
        String targetId = UUID.randomUUID().toString();
        TargetRegistry.MonitoringTarget target = new TargetRegistry.MonitoringTarget(
                targetId,
                request.getName(),
                request.getChecks()
        );
        return targetRegistry.addTarget(target);
    }

    @GetMapping("/{targetId}/last")
    public List<CheckResult> lastResults(@PathVariable String targetId,
                                         @RequestParam(defaultValue = "10") int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), MAX_LIMIT);
        return repository.findByTargetIdOrderByCreatedAtDesc(targetId, PageRequest.of(0, safeLimit))
                .getContent();
    }

    @GetMapping("/{targetId}/failures")
    public List<CheckResult> lastFailures(@PathVariable String targetId,
                                          @RequestParam(defaultValue = "50") int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), MAX_LIMIT);
        return repository.findByTargetIdAndOkFalseOrderByCreatedAtDesc(targetId, PageRequest.of(0, safeLimit))
                .getContent();
    }
}
