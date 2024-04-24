package com.elfiady.event.web.event;

import com.elfiady.event.core.model.event.Event;
import com.elfiady.event.infrastructure.listener.EventListenerListener;
import com.elfiady.event.infrastructure.listener.SseCustomBroadcaster;
import com.elfiady.event.web.SendEventAPI;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.glassfish.jersey.media.sse.EventOutput;

import javax.ws.rs.core.MediaType;
import javax.xml.stream.EventFilter;
import java.util.logging.Logger;

public class SendEventBus implements EventBus{

    private final SseCustomBroadcaster broadcaster = new SseCustomBroadcaster();
    public Logger LOGGER = Logger.getLogger(SendEventAPI.class.getName());

    @Override
    public void publish(Object iMapMessage) {
        final OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
		LOGGER.info("The command = " + iMapMessage + " was broadcasted to all listening client");
		final OutboundEvent event = eventBuilder.name("byteArray").mediaType(MediaType.TEXT_PLAIN_TYPE)
				.data(String.class, iMapMessage).build();
		broadcaster.broadcast(event);
    }

    @Override
    public void addSubscriber(Class<?> clazz, Object eventOutputListener) {
        this.broadcaster.add(clazz.cast(eventOutputListener));
    }

    @Override
    public void addSubscriber(Class<?> clazz, Object eventOutputListener, Event filter) {
      this.broadcaster.addFiltredEvent(eventOutputListener, filter);
    }

    @Override
    public void close() {
        this.broadcaster.closeAll();
    }
}
