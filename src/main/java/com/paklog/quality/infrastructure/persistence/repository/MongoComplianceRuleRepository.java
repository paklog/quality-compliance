package com.paklog.quality.infrastructure.persistence.repository;

import com.paklog.quality.domain.aggregate.ComplianceRule;
import com.paklog.quality.domain.repository.ComplianceRuleRepository;
import com.paklog.quality.domain.valueobject.InspectionType;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class MongoComplianceRuleRepository implements ComplianceRuleRepository {

    private final MongoTemplate mongoTemplate;
    public MongoComplianceRuleRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public ComplianceRule save(ComplianceRule rule) {
        return mongoTemplate.save(rule);
    }

    @Override
    public Optional<ComplianceRule> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, ComplianceRule.class));
    }

    @Override
    public List<ComplianceRule> findByType(InspectionType type) {
        Query query = new Query(Criteria.where("applicableTo").is(type).and("active").is(true));
        return mongoTemplate.find(query, ComplianceRule.class);
    }

    @Override
    public List<ComplianceRule> findActiveRules() {
        Query query = new Query(Criteria.where("active").is(true));
        return mongoTemplate.find(query, ComplianceRule.class);
    }

    @Override
    public void deleteById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, ComplianceRule.class);
    }
}
