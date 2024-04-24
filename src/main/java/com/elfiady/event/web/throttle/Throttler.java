package com.elfiady.event.web.throttle;

import com.elfiady.event.core.model.event.Event;
import com.elfiady.event.web.event.EventBus;

import java.util.concurrent.ExecutionException;

public interface Throttler extends EventBus {

    ThrottlerResult shouldProceed();

    void notifyWhenCanProceed(Class<?> clazz, Object eventOutputListener, Event filter) throws ExecutionException, InterruptedException;

    enum ThrottlerResult {
        PROCEED,
        DO_NOT_PROCEED
    }
}
