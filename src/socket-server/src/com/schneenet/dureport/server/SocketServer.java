package com.schneenet.dureport.server;

import java.io.FileInputStream;
import java.io.IOException;
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
	
	private boolean running = true;
	
	private SocketServer(Properties props)
	{
		try
		{
			ExecutorService execService = Executors.newCachedThreadPool();			
			ServerSocket serverSocket = new ServerSocket();
			InetSocketAddress addr = new InetSocketAddress(7000);
			serverSocket.bind(addr);
			while (running)
			{
				Socket s = serverSocket.accept();
				execService.execute(new ConnectionHandler(s));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Server program entry point
	 * 
	 * @param args
	 *            Command line arguments
	 */
	public static void main(String[] args)
	{
		try
		{
			// Read config
			FileInputStream pfStream = new FileInputStream("server.properties");
			InputStreamReader pfReader = new InputStreamReader(pfStream, Charset.forName("UTF-8"));
			Properties props = new Properties();
			props.load(pfReader);

			// Start server
			//SocketServer ss = 
			new SocketServer(props);
		}
		catch (IOException ex)
		{
			ex.printStackTrace(System.err);
		}

	}

}
