package com.paklog.quality.domain.event;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class ComplianceViolationEvent extends DomainEvent {
    private final String inspectionId;
    private final String violationType;
    private final String description;

    @Builder
    public ComplianceViolationEvent(String inspectionId, String violationType, String description) {
        super();
        this.inspectionId = inspectionId;
        this.violationType = violationType;
        this.description = description;
    }

    @Override
    public String getEventType() {
        return "ComplianceViolation";
    }
}
