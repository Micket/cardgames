package clientData;

import action.Message;
import server.ServerThread;

public class ConnectionToServerLocal implements ConnectionToServer
	{
	public ServerThread thread=new ServerThread();
	public int thisClientID=0;
	
	public ConnectionToServerLocal()
		{
		thread.start();
		}
	
	public void send(Message msg)
		{
		thread.addIncomingMessage(thisClientID, msg);
		}

	@Override
	public int getCliendID()
		{
		return thisClientID;
		}
	

	}
