package com.elfiady.event.infrastructure.listener;

import com.elfiady.event.core.model.event.Event;
import com.elfiady.event.web.event.CustomEventOutput;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.glassfish.jersey.server.ChunkedOutput;

public class SseCustomBroadcaster extends SseBroadcaster {

    public <OUT extends ChunkedOutput<OutboundEvent>> boolean add(Object chunkedOutput) {
        EventOutput eventOutput = (EventOutput) chunkedOutput;
        return super.add(eventOutput);
    }

     public <OUT extends ChunkedOutput<OutboundEvent>> boolean addFiltredEvent(Object chunkedOutput, Event event) {
        if(event.getName().equals("CUSTOM")){
        CustomEventOutput eventOutput = (CustomEventOutput) chunkedOutput;
        return super.add(eventOutput);
        //with the same way we can add other if or switch cas for customer event in fact of filter event
        }else{
           return  this.add(chunkedOutput);
        }
    }
}
