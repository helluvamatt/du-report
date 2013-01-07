package com.schneenet.dureport.server;


public class SocketServerBootstrap
{
	/**
	 * Server program entry point
	 * 
	 * @param args
	 *            Command line arguments
	 */
	public static void main(String[] args)
	{
		// #0000002 Print something on start up
		System.out.println("[INFO] Starting SocketServer...");
		
		// Create the socket server
		new SocketServer();
	}

}
