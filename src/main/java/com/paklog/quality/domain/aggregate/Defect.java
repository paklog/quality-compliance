package com.paklog.quality.domain.aggregate;

import com.paklog.quality.domain.valueobject.*;
import java.time.Instant;

public class Defect {

    private String defectId;
    private DefectType type;
    private SeverityLevel severity;
    private String description;

    private String location;
    private int quantity;

    private String photoUrl;
    private String reportedBy;
    private Instant reportedAt;

    private String rootCause;
    private String correctionAction;

    public boolean isCritical() {
        return severity == SeverityLevel.CRITICAL;
    }

    public boolean requiresImmediateAction() {
        return severity == SeverityLevel.CRITICAL || severity == SeverityLevel.HIGH;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String defectId;
        private DefectType type;
        private SeverityLevel severity;
        private String description;
        private String location;
        private int quantity;
        private String photoUrl;
        private String reportedBy;
        private Instant reportedAt;
        private String rootCause;
        private String correctionAction;

        public Builder defectId(String defectId) {
            this.defectId = defectId;
            return this;
        }

        public Builder type(DefectType type) {
            this.type = type;
            return this;
        }

        public Builder severity(SeverityLevel severity) {
            this.severity = severity;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder photoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
            return this;
        }

        public Builder reportedBy(String reportedBy) {
            this.reportedBy = reportedBy;
            return this;
        }

        public Builder reportedAt(Instant reportedAt) {
            this.reportedAt = reportedAt;
            return this;
        }

        public Builder rootCause(String rootCause) {
            this.rootCause = rootCause;
            return this;
        }

        public Builder correctionAction(String correctionAction) {
            this.correctionAction = correctionAction;
            return this;
        }

        public Defect build() {
            Defect defect = new Defect();
            defect.defectId = this.defectId;
            defect.type = this.type;
            defect.severity = this.severity;
            defect.description = this.description;
            defect.location = this.location;
            defect.quantity = this.quantity;
            defect.photoUrl = this.photoUrl;
            defect.reportedBy = this.reportedBy;
            defect.reportedAt = this.reportedAt;
            defect.rootCause = this.rootCause;
            defect.correctionAction = this.correctionAction;
            return defect;
        }
    }

    // Getters
    public String getDefectId() { return defectId; }
    public DefectType getType() { return type; }
    public SeverityLevel getSeverity() { return severity; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public int getQuantity() { return quantity; }
    public String getPhotoUrl() { return photoUrl; }
    public String getReportedBy() { return reportedBy; }
    public Instant getReportedAt() { return reportedAt; }
    public String getRootCause() { return rootCause; }
    public String getCorrectionAction() { return correctionAction; }

    // Setters
    public void setDefectId(String defectId) { this.defectId = defectId; }
    public void setType(DefectType type) { this.type = type; }
    public void setSeverity(SeverityLevel severity) { this.severity = severity; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public void setReportedBy(String reportedBy) { this.reportedBy = reportedBy; }
    public void setReportedAt(Instant reportedAt) { this.reportedAt = reportedAt; }
    public void setRootCause(String rootCause) { this.rootCause = rootCause; }
    public void setCorrectionAction(String correctionAction) { this.correctionAction = correctionAction; }
}
