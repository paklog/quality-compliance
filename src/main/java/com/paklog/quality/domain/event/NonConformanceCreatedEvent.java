package com.paklog.quality.domain.event;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class NonConformanceCreatedEvent extends DomainEvent {
    private final String inspectionId;
    private final String description;
    private final int defectCount;

    @Builder
    public NonConformanceCreatedEvent(String inspectionId, String description, int defectCount) {
        super();
        this.inspectionId = inspectionId;
        this.description = description;
        this.defectCount = defectCount;
    }

    @Override
    public String getEventType() {
        return "NonConformanceCreated";
    }
}
