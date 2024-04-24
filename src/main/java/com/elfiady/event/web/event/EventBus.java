package com.elfiady.event.web.event;

import com.elfiady.event.core.model.event.Event;
import com.elfiady.event.infrastructure.listener.EventListenerListener;
import org.glassfish.jersey.media.sse.EventOutput;

import javax.xml.stream.EventFilter;

public interface EventBus {
     void publish(Object event);

    void addSubscriber(Class<?> clazz, Object listener);

    void addSubscriber(Class<?> clazz, Object eventOutputListener, Event filter);

    //this method is used to shutdown the executor
    void close();
}
