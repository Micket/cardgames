package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	
			ConnectionToClientRemote connClient=new ConnectionToClientRemote(thread);
			connClient.socket=newClientSocket;
			connClient.is=new ObjectInputStream(newClientSocket.getInputStream());
			connClient.os=new ObjectOutputStream(newClientSocket.getOutputStream());
			
			synchronized (thread)
				{
				connClient.clientID=thread.getFreeClientID();
				connClient.nick=thread.getFreeNick();
				thread.connections.put(connClient.clientID, connClient);
				}
			connClient.start();
			
			thread.broadcastUserlistToClients();
			

			}
		catch (IOException ioe) 
			{
			System.out.println("IOException on socket listen: " + ioe);
			ioe.printStackTrace();
			}
		}
	
	}
