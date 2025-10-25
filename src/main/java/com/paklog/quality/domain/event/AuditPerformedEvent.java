package com.paklog.quality.domain.event;


public class AuditPerformedEvent extends DomainEvent {
    private final String auditId;
    private final String auditType;
    private final int findingsCount;

    private AuditPerformedEvent(final String auditId, final String auditType, final int findingsCount) {
        super();
        this.auditId = auditId;
        this.auditType = auditType;
        this.findingsCount = findingsCount;
    }

    @Override
    public String getEventType() {
        return "AuditPerformed";
    }

    public final String getAuditId() { return auditId; }
    public final String getAuditType() { return auditType; }
    public final int getFindingsCount() { return findingsCount; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String auditId;
        private String auditType;
        private int findingsCount;

        public Builder auditId(final String auditId) { this.auditId = auditId; return this; }
        public Builder auditType(final String auditType) { this.auditType = auditType; return this; }
        public Builder findingsCount(final int findingsCount) { this.findingsCount = findingsCount; return this; }

        public AuditPerformedEvent build() {
            return new AuditPerformedEvent(auditId, auditType, findingsCount);
        
}
}
}
