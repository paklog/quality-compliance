package com.paklog.quality.domain.event;


public class InspectionCompletedEvent extends DomainEvent {
    private final String inspectionId;
    private final String inspectionType;
    private final String result;
    private final int defectsFound;
    private final int itemsInspected;

    private InspectionCompletedEvent(final String inspectionId, final String inspectionType, final String result, final int defectsFound, final int itemsInspected) {
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

    public final String getInspectionId() { return inspectionId; }
    public final String getInspectionType() { return inspectionType; }
    public final String getResult() { return result; }
    public final int getDefectsFound() { return defectsFound; }
    public final int getItemsInspected() { return itemsInspected; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String inspectionId;
        private String inspectionType;
        private String result;
        private int defectsFound;
        private int itemsInspected;

        public Builder inspectionId(final String inspectionId) { this.inspectionId = inspectionId; return this; }
        public Builder inspectionType(final String inspectionType) { this.inspectionType = inspectionType; return this; }
        public Builder result(final String result) { this.result = result; return this; }
        public Builder defectsFound(final int defectsFound) { this.defectsFound = defectsFound; return this; }
        public Builder itemsInspected(final int itemsInspected) { this.itemsInspected = itemsInspected; return this; }

        public InspectionCompletedEvent build() {
            return new InspectionCompletedEvent(inspectionId, inspectionType, result, defectsFound, itemsInspected);
        
}
}
}
