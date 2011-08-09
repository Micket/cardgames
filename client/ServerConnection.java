package client;

import action.Message;

public abstract class ServerConnection
	{

	public int thisClientID=0;
	public abstract void send(Message msg);

	
	}
