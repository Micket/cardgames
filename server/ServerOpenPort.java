package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerOpenPort
	{
	
	
	
	public ServerOpenPort(ServerThread thread, int port)
		{
	
		try
			{
			ServerSocket listener = new ServerSocket(port);
			Socket newClientSocket = listener.accept();
	
			ConnectionToRemote connClient=new ConnectionToRemote();
			connClient.socket=newClientSocket;
	
			connClient.is=new ObjectInputStream(newClientSocket.getInputStream());
			
			//TODO
			int id=(int)(Math.random()*10000); 
			thread.connections.put(id, connClient);
			
			connClient.start();

			}
		catch (IOException ioe) 
			{
			System.out.println("IOException on socket listen: " + ioe);
			ioe.printStackTrace();
			}
		}
	
	}
