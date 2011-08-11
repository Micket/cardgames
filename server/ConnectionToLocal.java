package server;

import java.io.IOException;
import java.io.ObjectInputStream;

import action.Message;

import clientQT.Client;

import server.ConnectionToClient;

/**
 * Connection to a local player.
 * @author mahogny
 * @author Micket
 */
public class ConnectionToLocal extends ConnectionToClient
	{
	public Client localClient;
	public String nick;
	public ObjectInputStream is;
	
	/**
	 * Random unique ID obtained upon connection. It never changes during a session.
	 */
	//public int connectionID;
	
	
	public void send(Message msg)
		{
		localClient.localSend(msg);
		}
	
	@Override
	public void run()
		{
		
		for(;;)
			{
			
			try
				{
				Message msg=(Message)is.readObject();
				System.out.println("Get message");
				}
			catch (IOException e)
				{
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			catch (ClassNotFoundException e)
				{
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			//TODO
			}
		
		}
	
	}
