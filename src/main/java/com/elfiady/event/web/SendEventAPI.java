package com.elfiady.event.web;

import com.elfiady.event.core.model.event.Event;
import com.elfiady.event.infrastructure.listener.command.HelloCommandResponse;
import com.elfiady.event.infrastructure.listener.command.IMapCommand;
import com.elfiady.event.web.event.EventBus;
import com.elfiady.event.web.event.SendEventBus;
import com.elfiady.event.web.event.SendEventBusMultiThread;
import com.elfiady.event.web.throttle.Throttler;
import com.google.gson.Gson;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.glassfish.jersey.media.sse.SseFeature;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;


@Path("server-sent-events")
@Singleton
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class SendEventAPI {

    private final SseBroadcaster broadcaster = new SseBroadcaster();
	public Map<Long, Map<Long, Event>> calendarContext = new HashMap<>();
    public Logger LOGGER = Logger.getLogger(SendEventAPI.class.getName());
	final Gson gson = new Gson();
	private static EventOutput eventOutput = new EventOutput();
	public EventBus eventBus = new SendEventBus();
	public Throttler eventBusMultithread = new SendEventBusMultiThread();

	public SendEventAPI() {
	}



	@POST
	@Path("/helloResponse")
	public Response helloResponse(final String iMapMessage) {
		final String calendarContextTojson = executeCommande(iMapMessage);
		return Response.ok(calendarContextTojson).build();

	}

	@POST
	public Response executeAndBroadcast(final String commandInJson) throws IOException {
		LOGGER.info("Execute and broadcast method is called with command = " + commandInJson);
		final String calendarContextTojson = executeCommande(commandInJson);
		this.eventBus.publish(commandInJson);
		this.eventBusMultithread.publish(commandInJson);
		return Response.ok(calendarContextTojson).build();
	}

	@POST
	@Path("/execute")
	public Response execute(final String commandInJson) throws IOException {
		LOGGER.info("Execute method is called with command = " + commandInJson);
		final String calendarContextTojson = executeCommande(commandInJson);
		return Response.ok(calendarContextTojson).build();
	}

	/**
	 * this method is used to execute all the send command and update the
	 * current context
	 *
	 * @param commandInJson
	 * @return
	 */
	private synchronized String executeCommande(final String commandInJson) {
		final Gson json = new Gson();
		final byte[] readDataToByte = json.fromJson(commandInJson, byte[].class);
		final ByteArrayInputStream bis = new ByteArrayInputStream(readDataToByte);
		Object o = null;
		ObjectInput in = null;
		String calendarContextTojson = null;
		try {
			in = new ObjectInputStream(bis);
			o = in.readObject();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		}
		final IMapCommand command = (IMapCommand) o;
		final Map<Long, Map<Long, Event>> calendarContextRetuned = command.execute(calendarContext);
		if (calendarContextRetuned != null) {
			calendarContext = calendarContextRetuned;
			LOGGER.info(
					"The current context was updated by a remote command and the current value is =" + calendarContext);
			final Map<Long, Event> calendar = calendarContext.get(command.getIdCalendar());
			calendarContextTojson = json.toJson(calendar);
		} else {
			LOGGER.info("The server recieved a Hello command and we are preparing for reponse ....");
			final HelloCommandResponse reqHello = new HelloCommandResponse(calendarContext);
			calendarContextTojson = gson.toJson(reqHello);
		}
		return calendarContextTojson;
	}

	@DELETE
	public void close() throws IOException {
		eventOutput.close();
		SendEventAPI.setEventOutput(new EventOutput());

	}


	private static void setEventOutput(final EventOutput eventOutput) {
		SendEventAPI.eventOutput = eventOutput;
	}

	@POST
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	public EventOutput listenToBroadcast() {
		LOGGER.info("A new client ask for listening the server notification an EventOutPut will sent");
		final EventOutput eventOutput = new EventOutput();
		this.eventBus.addSubscriber(EventOutput.class, eventOutput);
		this.eventBusMultithread.addSubscriber(EventOutput.class, eventOutput);
		return eventOutput;
	}

	@POST
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	public EventOutput listenToBroadcastForaSpecificEvent(Event event) {
		LOGGER.info("A new client ask for listening the server notification an EventOutPut will sent");
		final EventOutput eventOutput = new EventOutput();
		this.eventBus.addSubscriber(EventOutput.class, eventOutput, event);
		this.eventBusMultithread.addSubscriber(EventOutput.class, eventOutput, event);
		return eventOutput;
	}

	@GET
	@Path("/shouldProceed")
	public Throttler.ThrottlerResult shouldProceed() {
		return this.eventBusMultithread.shouldProceed();
	}

	@POST
	@Path("/notifyWhenCanProceed")
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	public EventOutput notifyWhenCanProceed(Event event) throws ExecutionException, InterruptedException {
		final EventOutput eventOutput = new EventOutput();
		 this.eventBusMultithread.notifyWhenCanProceed(EventOutput.class, eventOutput, event);
		 return eventOutput;
	}



	public Map<Long, Map<Long, Event>> getContexts() {
		return calendarContext;
	}

}
