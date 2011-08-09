package server;

import java.net.Socket;

import action.Message;

import client.Client;


/**
 * Player - Either a remote connection or an AI
 * @author mahogny
 *
 */
public class ConnectionToClient
	{

	public Socket socket;
	public Client localClient;
	
	public String nick;
	
	/**
	 * Random unique ID obtained upon connection. It never changes during a session.
	 */
	public int connectionID;
	
	
	public void send(Message msg)
		{
		
		}
	
	}
