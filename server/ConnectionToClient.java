package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import action.Message;

import clientQT.Client;


/**
 * Player - Either a remote connection or an AI
 * @author mahogny
 *
 */
public class ConnectionToClient extends Thread
	{

	public Socket socket;
	public Client localClient;
	
	public String nick;
	public ObjectInputStream is;
	
	/**
	 * Random unique ID obtained upon connection. It never changes during a session.
	 */
	//public int connectionID;
	
	
	public void send(Message msg)
		{
		if(localClient!=null)
			localClient.localSend(msg);
		else
			;//TODO

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
		
		//socket.
		
		
		}
	
	}
