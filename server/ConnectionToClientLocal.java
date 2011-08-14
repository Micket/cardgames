package server;

import action.Message;

import clientData.Client;

import server.ConnectionToClient;

/**
 * Connection to a local player.
 * @author mahogny
 * @author Micket
 */
public class ConnectionToClientLocal extends ConnectionToClient
	{
	public Client localClient;
	public String nick;
	//public ObjectInputStream is;
	
	/**
	 * Random unique ID obtained upon connection. It never changes during a session.
	 */
	//public int connectionID;
	
	
	public void send(Message msg)
		{
		localClient.gotMessageFromServer(msg);
		}
	
	@Override
	public void run()
		{
		}
	
	}
