package com.paklog.quality.domain.aggregate;

import com.paklog.quality.domain.valueobject.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.*;

@Document(collection = "compliance_rules")
public class ComplianceRule {

    @Id
    private String id;

    private String ruleName;
    private String ruleCode;
    private String description;

    private ComplianceLevel level;
    private InspectionType applicableTo;

    private String condition;  // Rule expression
    private double threshold;

    private boolean active;
    private boolean mandatory;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    /**
     * Evaluate rule against inspection data
     */
    public boolean evaluate(Map<String, Object> inspectionData) {
        if (!active) {
            return true;
        }

        // Simple rule evaluation logic
        // In production, this would use a rules engine like Drools

        String field = condition.split(" ")[0];
        Object value = inspectionData.get(field);

        if (value == null) {
            return !mandatory;
        }

        if (value instanceof Number) {
            double numValue = ((Number) value).doubleValue();

            if (condition.contains(">")) {
                return numValue > threshold;
            } else if (condition.contains("<")) {
                return numValue < threshold;
            } else if (condition.contains(">=")) {
                return numValue >= threshold;
            } else if (condition.contains("<=")) {
                return numValue <= threshold;
            } else if (condition.contains("==")) {
                return Math.abs(numValue - threshold) < 0.001;
            }
        }

        return true;
    }


    // Getters
    public String getId() { return id; }
    public String getRuleName() { return ruleName; }
    public String getRuleCode() { return ruleCode; }
    public String getDescription() { return description; }
    public ComplianceLevel getLevel() { return level; }
    public InspectionType getApplicableTo() { return applicableTo; }
    public String getCondition() { return condition; }
    public double getThreshold() { return threshold; }
    public boolean isActive() { return active; }
    public boolean isMandatory() { return mandatory; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public void setRuleCode(String ruleCode) { this.ruleCode = ruleCode; }
    public void setDescription(String description) { this.description = description; }
    public void setLevel(ComplianceLevel level) { this.level = level; }
    public void setApplicableTo(InspectionType applicableTo) { this.applicableTo = applicableTo; }
    public void setCondition(String condition) { this.condition = condition; }
    public void setThreshold(double threshold) { this.threshold = threshold; }
    public void setActive(boolean active) { this.active = active; }
    public void setMandatory(boolean mandatory) { this.mandatory = mandatory; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
