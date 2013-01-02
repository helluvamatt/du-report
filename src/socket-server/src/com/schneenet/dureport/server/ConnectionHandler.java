package com.schneenet.dureport.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionHandler implements Runnable
{
	
	public static final String COMMAND = "/usr/bin/du";
	public static final String COMMAND_ARGS = "--max-depth=1 --all";

	private Socket mSocket;
	
	public ConnectionHandler(Socket sock)
	{
		mSocket = sock;
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
			Process p = Runtime.getRuntime().exec(String.format("%s %s %s", COMMAND, COMMAND_ARGS, input));
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
