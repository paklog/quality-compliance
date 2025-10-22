package com.paklog.quality.domain.repository;

import com.paklog.quality.domain.aggregate.InspectionRecord;
import com.paklog.quality.domain.valueobject.*;
import java.util.*;

public interface InspectionRecordRepository {
    InspectionRecord save(InspectionRecord record);
    Optional<InspectionRecord> findById(String id);
    List<InspectionRecord> findByType(InspectionType type);
    List<InspectionRecord> findByResult(InspectionResult result);
    List<InspectionRecord> findFailedInspections();
    void deleteById(String id);
}
