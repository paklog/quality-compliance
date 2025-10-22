package com.paklog.quality.infrastructure.kafka;

import com.paklog.quality.application.port.out.PublishEventPort;
import com.paklog.quality.domain.event.DomainEvent;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher implements PublishEventPort {

    private final KafkaTemplate<String, CloudEvent> kafkaTemplate;

    @Value("${quality.events.topic}")
    private String topic;

    @Override
    public void publish(DomainEvent event) {
        try {
            CloudEvent cloudEvent = CloudEventBuilder.v1()
                .withId(event.getEventId())
                .withType("com.paklog.quality." + event.getEventType())
                .withSource(URI.create("https://paklog.com/quality"))
                .withTime(event.getOccurredAt().atOffset(java.time.ZoneOffset.UTC))
                .withData("application/json", event.toString().getBytes())
                .build();

            kafkaTemplate.send(topic, event.getEventId(), cloudEvent);
            log.info("Published event: {}", event.getEventType());
        } catch (Exception e) {
            log.error("Failed to publish event: {}", event.getEventType(), e);
        }
    }
}
