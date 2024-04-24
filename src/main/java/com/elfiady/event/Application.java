package com.elfiady.event;

import com.elfiady.event.infrastructure.listener.EventListenerListener;
import com.elfiady.event.web.SendEventAPI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Application {

    public static final String ROOT_PATH = "server-sent-events";
    public static Logger LOGGER = Logger.getLogger(Application.class.getName());
	final static Scanner keyboard = new Scanner(System.in);
	final static int portForServer=9998;
	final static int portForHello=9998;
	private static final URI BASE_URI = URI.create("http://localhost:"+portForServer+"/");

	public static void main(final String[] args) {
		try {
			LOGGER.info("Starting event Sent Events for AMD TP ISIDIS ....");

			final ResourceConfig resourceConfig = new ResourceConfig(SendEventAPI.class, SseFeature.class);
			LOGGER.info("Enter the port number for the associated replicator server (ex: 9995) : ");
			final int portForServer=keyboard.nextInt();
			final URI BASE_URI = URI.create("http://localhost:"+portForServer+"/");
			final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig, false);
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

				@Override
				public void run() {
					server.shutdownNow();
				}
			}));
			server.start();

			LOGGER.info("The server was successfully started .\n Try out "+BASE_URI+ROOT_PATH+"\nStop the server using CTRL+C");

			LOGGER.info("Enter the port number for the remote replicator server (ex: 9998) : ");
			final int portForHello=keyboard.nextInt();
			Executors.newSingleThreadExecutor().submit(new EventListenerListener(portForHello,portForServer));

			Thread.currentThread().join();
		} catch (final IOException ex) {
			LOGGER.info("Exception during starting event server"+ex);
		} catch (final InterruptedException e) {
			LOGGER.info("Exception during starting event server"+e);
		}
	}
}
