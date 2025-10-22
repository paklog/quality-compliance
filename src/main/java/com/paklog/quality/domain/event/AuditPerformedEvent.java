package com.paklog.quality.domain.event;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuditPerformedEvent extends DomainEvent {
    private final String auditId;
    private final String auditType;
    private final int findingsCount;

    @Builder
    public AuditPerformedEvent(String auditId, String auditType, int findingsCount) {
        super();
        this.auditId = auditId;
        this.auditType = auditType;
        this.findingsCount = findingsCount;
    }

    @Override
    public String getEventType() {
        return "AuditPerformed";
    }
}
