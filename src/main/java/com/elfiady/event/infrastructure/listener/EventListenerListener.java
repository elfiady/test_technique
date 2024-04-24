package com.elfiady.event.infrastructure.listener;

import com.elfiady.event.infrastructure.listener.command.HelloCommand;
import com.elfiady.event.infrastructure.listener.command.HelloCommandResponse;
import com.elfiady.event.infrastructure.listener.command.IMapCommand;
import com.google.gson.Gson;


import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.xml.stream.EventFilter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

public class EventListenerListener implements Runnable, EventBusListener {

	private EventInput eventInput;
	private final Client client;
	private final WebTarget targetToExecuteAndBroadcast;
	private final Gson gson;
	WebTarget targetForRemoteReplicatorServer;
	WebTarget targetToExecute;
	public static Logger LOGGER = Logger.getLogger(EventListenerListener.class.getName());
	private HelloCommandResponse contextReplication;
	private final int portForHello;
	private final int portForServer;

	public EventListenerListener(final int portForHello, final int portForServer) {
		this.init();
		this.portForHello = portForHello;
		this.portForServer = portForServer;
		this.client = ClientBuilder.newBuilder().register(SseFeature.class).build();
		this.targetToExecuteAndBroadcast = client.target("http://localhost:" + portForHello + "/server-sent-events");
		targetForRemoteReplicatorServer = client
				.target("http://localhost:" + portForHello + "/server-sent-events/helloResponse");
		this.targetToExecute = client.target("http://localhost:" + portForServer + "/server-sent-events/execute");
		this.gson = new Gson();
		this.senHelloMessage(portForHello, portForServer);
	}

	public void init() {
		try {
			if (portForHello != portForServer) {
				LOGGER.info("Getting an eventInput from the remote replicator server (port number = " + portForHello
						+ ")....");
				eventInput = targetToExecuteAndBroadcast.request().get(EventInput.class);
			}
		} catch (final Exception e) {
			LOGGER.info("Errors during getting an eventInput from the remote replicator server port number = "
					+ portForHello + e.getMessage());
		}
	}

	/**
	 * This method is used to ask the current context from the remote replicator
	 * server
	 *
	 * @param portForHello
	 * @param portForServer
	 */
	public void senHelloMessage(final int portForHello, final int portForServer) {
		if (portForHello != portForServer) {
			final IMapCommand helloCommande = new HelloCommand();
			final byte[] byteArray = toByte(helloCommande);
			final String iMapCommandByteToString = gson.toJson(byteArray);

			this.publish(iMapCommandByteToString);
		}

	}

	private byte[] toByte(final IMapCommand imCommand) {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(imCommand);
		} catch (final IOException e) {
			LOGGER.info(e.getMessage());
		}
		new OutboundEvent.Builder();
		final byte[] byteArray = bos.toByteArray();
		return byteArray;
	}

	@Override
	public void run() {
		if (eventInput == null) {
			init();
		}
		while (true) {

			while (!eventInput.isClosed()) {
				final InboundEvent inboundEvent = eventInput.read();
				if (inboundEvent == null) {
					break;
				}
				final String iMapCommandByteToString = inboundEvent.readData(String.class);
				LOGGER.info("New update Command received from remote replicator server , centent = "
						+ iMapCommandByteToString
						+ " we are invoking the associated replicator server with this command");
				targetToExecute.request().post(Entity.text(iMapCommandByteToString));
			}
		}

	}

	@Override
	public void publish(Object event) {
		final Response response = targetForRemoteReplicatorServer.request()
					.post(Entity.text(event));
		final String contextInJson = response.readEntity(String.class);
			contextReplication = gson.fromJson(contextInJson, HelloCommandResponse.class);
			LOGGER.info("Hello response is " + contextReplication.toString()
					+ " we are invoking the associated replicator server with this context");
			final byte[] byteArrayForResponse = toByte(contextReplication);
			targetToExecute.request().post(Entity.text(gson.toJson(byteArrayForResponse)));
	}

	@Override
	public void addSubscriber(Class<?> clazz, EventListenerListener listener) {

	}

	@Override
	public void addSubscriber(Class<?> clazz, EventListenerListener listener, EventFilter filter) {

	}
}