package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A server port, listening for new connections
 */
public class ServerOpenPort extends Thread
	{
	private ServerThread thread;
	private ServerSocket listener;
	
	private boolean stopThread=false;
	
	public ServerOpenPort(ServerThread thread, int port) throws IOException
		{
		this.thread=thread;
		listener = new ServerSocket(port);
		}
	
	
	@Override
	public void run()
		{
		while(!stopThread)
			try
				{
				Socket newClientSocket = listener.accept();
		
				ConnectionToClientRemote connClient=new ConnectionToClientRemote(thread, newClientSocket);
				
				synchronized (thread)
					{
					connClient.clientID=thread.getFreeClientID();
					connClient.nick=thread.getFreeNick();
					thread.connections.put(connClient.clientID, connClient);
					}
				connClient.start();
				
				
	
				}
			catch (IOException ioe) 
				{
				}
		}

	/**
	 * Stop listening to this port
	 */
	public void closePort()
		{
		stopThread=true;
		try
			{
			listener.close();
			}
		catch (IOException e)
			{
			}
		}
	
	}
