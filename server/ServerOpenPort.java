package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerOpenPort extends Thread
	{
	ServerThread thread;
	ServerSocket listener;
	
	public ServerOpenPort(ServerThread thread, int port) throws IOException
		{
		this.thread=thread;
		listener = new ServerSocket(port);
		}
	
	
	@Override
	public void run()
		{
		for(;;)
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
			System.out.println("IOException on socket listen: " + ioe);
			ioe.printStackTrace();
			}
		}
	
	}
