package com.paklog.quality.compliance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Quality Compliance
 *
 * Quality inspections and compliance management
 *
 * @author Paklog Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableKafka
@EnableMongoAuditing
public class QualityComplianceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QualityComplianceApplication.class, args);
    }
}