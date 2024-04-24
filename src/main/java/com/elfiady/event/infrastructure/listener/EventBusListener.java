package com.elfiady.event.infrastructure.listener;

import javax.xml.stream.EventFilter;

public interface EventBusListener {

    void publish(Object event);

    void addSubscriber(Class<?> clazz, EventListenerListener listener);

    void addSubscriber(Class<?> clazz, EventListenerListener listener, EventFilter filter);
}
