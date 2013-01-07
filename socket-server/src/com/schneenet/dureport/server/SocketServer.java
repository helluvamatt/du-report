package com.schneenet.dureport.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer
{
	
	private Properties mProperties;
	private boolean running = true;

	public static final String PROPERTIES_FILE = "server.properties";

	public static final String SERVER_PORT = "socketserver.port";
	
	public SocketServer()
	{
		try {
			// Read config
			InputStream pfStream = getClass().getResourceAsStream(PROPERTIES_FILE);
			InputStreamReader pfReader = new InputStreamReader(pfStream, Charset.forName("UTF-8"));
			mProperties = new Properties();
			mProperties.load(pfReader);
			
			// Create cached thread pool, creates threads as needed and reuses cached threads.
			ExecutorService execService = Executors.newCachedThreadPool();
			ServerSocket serverSocket = new ServerSocket();
			int port = Integer.parseInt(mProperties.getProperty(SERVER_PORT));
			InetSocketAddress addr = new InetSocketAddress(port);
			serverSocket.bind(addr);
			while (running)
			{
				Socket s = serverSocket.accept();
				log("INFO", String.format("Connection accepted: %s", s.getInetAddress().getHostAddress()));
				execService.execute(new ConnectionHandler(s, copyProps(mProperties)));
			}
		}
		catch (IOException e)
		{
			// TODO Proper logging for socket-server
			log("ERROR", e.getLocalizedMessage());
			e.printStackTrace(System.err);
		}
	}
	
	/**
	 * Create a deep copy of a Properties
	 * @param source Properties to copy from
	 * @return Deep copy of source
	 */
	private static Properties copyProps(Properties source)
	{
		Properties copy = (Properties) source.clone();
		copy.putAll(source);
		return copy;
	}
	
	private static void log(String level, String message)
	{
		System.out.println(String.format("[%s] %s", level, message));
	}

}
