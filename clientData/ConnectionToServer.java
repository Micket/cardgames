package clientData;

import action.Message;

public abstract class ConnectionToServer
	{

	public int thisClientID=0;
	public abstract void send(Message msg);

	
	}
