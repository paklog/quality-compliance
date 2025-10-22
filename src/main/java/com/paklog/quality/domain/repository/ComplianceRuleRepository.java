package com.paklog.quality.domain.repository;

import com.paklog.quality.domain.aggregate.ComplianceRule;
import com.paklog.quality.domain.valueobject.*;
import java.util.*;

public interface ComplianceRuleRepository {
    ComplianceRule save(ComplianceRule rule);
    Optional<ComplianceRule> findById(String id);
    List<ComplianceRule> findByType(InspectionType type);
    List<ComplianceRule> findActiveRules();
    void deleteById(String id);
}
