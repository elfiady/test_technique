package com.elfiady.event.web.event;

import com.elfiady.event.core.model.event.Event;
import com.elfiady.event.infrastructure.listener.SseCustomBroadcaster;
import com.elfiady.event.infrastructure.listener.command.CanProceedCommandResponse;
import com.elfiady.event.infrastructure.listener.command.IMapCommand;
import com.elfiady.event.web.SendEventAPI;
import com.elfiady.event.web.throttle.Throttler;
import org.glassfish.jersey.media.sse.OutboundEvent;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class SendEventBusMultiThread implements Throttler {

    private final SseCustomBroadcaster broadcaster = new SseCustomBroadcaster();
    public Logger LOGGER = Logger.getLogger(SendEventAPI.class.getName());
    //In order to manage well the pool of thread i choose to use executor
    ExecutorService executor = Executors.newFixedThreadPool(10);
    List<Future<?>> futures = new ArrayList<Future<?>>();
    @Override
    public void publish(Object iMapMessage) {
        final OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
		LOGGER.info("The command = " + iMapMessage + " was broadcasted to all listening client");
		final OutboundEvent event = eventBuilder.name("byteArray").mediaType(MediaType.TEXT_PLAIN_TYPE)
				.data(String.class, iMapMessage).build();
        Runnable runnableTask = () -> {
        broadcaster.broadcast(event);
        };
        futures.add(executor.submit(runnableTask));
    }

    @Override
    public void addSubscriber(Class<?> clazz, Object eventOutputListener) {
        Runnable runnableTask = () -> {
        this.broadcaster.add(clazz.cast(eventOutputListener));
        };
        futures.add(executor.submit(runnableTask));

    }

    @Override
    public void addSubscriber(Class<?> clazz, Object eventOutputListener, Event filter) {
        Runnable runnableTask = () -> {
       this.broadcaster.addFiltredEvent(eventOutputListener, filter);
        };
        futures.add(executor.submit(runnableTask));

    }

    @Override
    public void close() {
        this.broadcaster.closeAll();
        executor.shutdownNow();
    }

    @Override
    public ThrottlerResult shouldProceed() {
        if(futures.size() == 9) {
            // B) Check if all runnables are done (non-blocking)
            boolean allDone = true;
            for (Future<?> future : futures) {
                allDone &= future.isDone(); // check if future is done
            }
            return allDone ? ThrottlerResult.PROCEED : ThrottlerResult.DO_NOT_PROCEED;
        }

        return ThrottlerResult.PROCEED;
    }

    @Override
    public void notifyWhenCanProceed(Class<?> clazz, Object eventOutputListener, Event filter) throws ExecutionException, InterruptedException {
      this.addSubscriber(clazz, eventOutputListener, filter);
        // A) Await all runnables to be done (blocking)
        for(Future<?> future : futures)
            future.get(); // get will block until the future is done
        Map<Long, Map<Long, Event>> localContext = new HashMap<>();
        Map<Long, Event> canProceedCommandResponses = new HashMap<>();
        Event canProceedCommandResponse = new Event();
        canProceedCommandResponse.setName("canProceedCommandResponse");
        canProceedCommandResponse.setDescription("message to tell you that you can porceed");
        canProceedCommandResponse.setId(1);
        canProceedCommandResponses.put(1l, canProceedCommandResponse);
        canProceedCommandResponses.put(1L, canProceedCommandResponse);
        localContext.put(1l, canProceedCommandResponses);
        IMapCommand mapCommand = new CanProceedCommandResponse(localContext);
        this.publish(mapCommand);
        }

}
