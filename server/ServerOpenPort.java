package server;

import java.io.IOException;
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
	
			ConnectionToClient connClient=new ConnectionToClient();
			connClient.socket=newClientSocket;
	
			
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
