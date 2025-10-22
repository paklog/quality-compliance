package com.paklog.quality.infrastructure.persistence.repository;

import com.paklog.quality.domain.aggregate.InspectionRecord;
import com.paklog.quality.domain.repository.InspectionRecordRepository;
import com.paklog.quality.domain.valueobject.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class MongoInspectionRecordRepository implements InspectionRecordRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public InspectionRecord save(InspectionRecord record) {
        return mongoTemplate.save(record);
    }

    @Override
    public Optional<InspectionRecord> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, InspectionRecord.class));
    }

    @Override
    public List<InspectionRecord> findByType(InspectionType type) {
        Query query = new Query(Criteria.where("type").is(type));
        return mongoTemplate.find(query, InspectionRecord.class);
    }

    @Override
    public List<InspectionRecord> findByResult(InspectionResult result) {
        Query query = new Query(Criteria.where("result").is(result));
        return mongoTemplate.find(query, InspectionRecord.class);
    }

    @Override
    public List<InspectionRecord> findFailedInspections() {
        Query query = new Query(Criteria.where("result")
            .in(InspectionResult.FAILED, InspectionResult.QUARANTINE));
        return mongoTemplate.find(query, InspectionRecord.class);
    }

    @Override
    public void deleteById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, InspectionRecord.class);
    }
}
