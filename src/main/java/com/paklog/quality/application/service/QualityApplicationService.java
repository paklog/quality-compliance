package com.paklog.quality.application.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paklog.quality.application.command.PerformInspectionCommand;
import com.paklog.quality.application.port.in.QualityControlUseCase;
import com.paklog.quality.application.port.out.PublishEventPort;
import com.paklog.quality.domain.aggregate.*;
import com.paklog.quality.domain.repository.*;
import com.paklog.quality.domain.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;

@Service
public class QualityApplicationService implements QualityControlUseCase {
    private static final Logger log = LoggerFactory.getLogger(QualityApplicationService.class);


    private final InspectionRecordRepository inspectionRepository;
    private final ComplianceRuleRepository ruleRepository;
    private final RuleEvaluationService ruleEvaluationService;
    private final PublishEventPort publishEventPort;
    public QualityApplicationService(InspectionRecordRepository inspectionRepository, ComplianceRuleRepository ruleRepository, RuleEvaluationService ruleEvaluationService, PublishEventPort publishEventPort) {
        this.inspectionRepository = inspectionRepository;
        this.ruleRepository = ruleRepository;
        this.ruleEvaluationService = ruleEvaluationService;
        this.publishEventPort = publishEventPort;
    }


    @Override
    @Transactional
    public String performInspection(PerformInspectionCommand command) {
        log.info("Performing {} inspection", command.type());

        InspectionRecord inspection = InspectionRecord.builder()
            .id(UUID.randomUUID().toString())
            .inspectionNumber("INS-" + Instant.now().getEpochSecond())
            .type(command.type())
            .itemId(command.itemId())
            .inspectorId(command.inspectorId())
            .samplingStrategy(command.samplingStrategy())
            .sampleSize(command.sampleSize())
            .orderId(command.orderId())
            .shipmentId(command.shipmentId())
            .build();

        inspection.perform();
        inspection = inspectionRepository.save(inspection);

        log.info("Inspection created: {}", inspection.getId());
        return inspection.getId();
    }

    @Override
    @Transactional
    public void addDefect(String inspectionId, Defect defect) {
        InspectionRecord inspection = inspectionRepository.findById(inspectionId)
            .orElseThrow(() -> new IllegalArgumentException("Inspection not found"));

        inspection.addDefect(defect);
        inspectionRepository.save(inspection);

        inspection.domainEvents().forEach(publishEventPort::publish);
        inspection.clearDomainEvents();
    }

    @Override
    @Transactional
    public void completeInspection(String inspectionId) {
        InspectionRecord inspection = inspectionRepository.findById(inspectionId)
            .orElseThrow(() -> new IllegalArgumentException("Inspection not found"));

        // Evaluate compliance rules
        List<ComplianceRule> rules = ruleRepository.findByType(inspection.getType());
        RuleEvaluationService.RuleEvaluationResult ruleResult =
            ruleEvaluationService.evaluateRules(inspection, rules);

        if (!ruleResult.isOverallPassed()) {
            inspection.createNonConformance("Compliance rule violations detected");
        }

        inspection.complete();
        inspectionRepository.save(inspection);

        inspection.domainEvents().forEach(publishEventPort::publish);
        inspection.clearDomainEvents();
    }

    @Override
    public InspectionRecord getInspection(String inspectionId) {
        return inspectionRepository.findById(inspectionId)
            .orElseThrow(() -> new IllegalArgumentException("Inspection not found"));
    }
}
