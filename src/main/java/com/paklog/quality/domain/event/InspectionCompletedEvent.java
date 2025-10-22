package com.paklog.quality.domain.event;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionCompletedEvent extends DomainEvent {
    private final String inspectionId;
    private final String inspectionType;
    private final String result;
    private final int defectsFound;
    private final int itemsInspected;

    @Builder
    public InspectionCompletedEvent(String inspectionId, String inspectionType, String result,
                                   int defectsFound, int itemsInspected) {
        super();
        this.inspectionId = inspectionId;
        this.inspectionType = inspectionType;
        this.result = result;
        this.defectsFound = defectsFound;
        this.itemsInspected = itemsInspected;
    }

    @Override
    public String getEventType() {
        return "InspectionCompleted";
    }
}
