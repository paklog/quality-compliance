package com.paklog.quality.application.port.out;

import com.paklog.quality.domain.event.DomainEvent;

public interface PublishEventPort {
    void publish(DomainEvent event);
}
