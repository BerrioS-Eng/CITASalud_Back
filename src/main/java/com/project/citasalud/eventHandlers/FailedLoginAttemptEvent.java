package com.project.citasalud.eventHandlers;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FailedLoginAttemptEvent extends ApplicationEvent {
    private final String email;
    private final String ipAddress;
    private final int attemptCount;
    private final String attemptTime;

    public FailedLoginAttemptEvent(Object source, String email, String ipAddress, int attemptCount, String attemptTime) {
        super(source);
        this.email = email;
        this.ipAddress = ipAddress;
        this.attemptCount = attemptCount;
        this.attemptTime = attemptTime;
    }
}
