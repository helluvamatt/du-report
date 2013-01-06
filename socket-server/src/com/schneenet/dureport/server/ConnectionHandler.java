package com.schneenet.dureport.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

public class ConnectionHandler implements Runnable
{
	private static final String COMMAND = "socketserver.run.command";
	private static final String COMMAND_ARGS = "socketserver.run.commandargs";
	
	private Socket mSocket;
	private String mCommand;
	private String mCommandArgs;
	
	public ConnectionHandler(Socket sock, Properties props)
	{
		mSocket = sock;
		mCommand = props.getProperty(COMMAND);
		mCommandArgs = props.getProperty(COMMAND_ARGS);
	}

	@Override
	public void run()
	{
		try
		{
			InputStream is = mSocket.getInputStream();
			OutputStream os = mSocket.getOutputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String input = br.readLine();
			Process p = Runtime.getRuntime().exec(String.format("%s %s %s", mCommand, mCommandArgs, input));
			InputStream pis = p.getInputStream();
			byte[] buffer = new byte[1024]; // 1K Buffer
			int read = 0;
			while ((read = pis.read(buffer)) > -1)
			{
				os.write(buffer, 0, read - 1);
			}
			mSocket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace(System.err);
		}
		
	}
	
}
