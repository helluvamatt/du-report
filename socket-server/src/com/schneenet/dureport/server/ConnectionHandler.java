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
			SocketServer.log("DEBUG", "Got input: [" + input + "]");
			String cmd = String.format("%s %s %s", mCommand, mCommandArgs, input);
			SocketServer.log("DEBUG", "Executing: [" + cmd + "]");
			Process p = Runtime.getRuntime().exec(cmd);
			InputStream errStream = p.getErrorStream();
			BufferedReader errorBr = new BufferedReader(new InputStreamReader(errStream));
			String error;
			while ((error = errorBr.readLine()) != null)
			{
				SocketServer.log("ERROR", error);
			}
			InputStream pis = p.getInputStream();
			byte[] buffer = new byte[1024]; // 1K Buffer
			int read = 0;
			while ((read = pis.read(buffer)) > -1)
			{
				os.write(buffer, 0, read - 1);
				os.flush();
			}
			os.flush();
			mSocket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace(System.err);
		}
		
	}
	
}
