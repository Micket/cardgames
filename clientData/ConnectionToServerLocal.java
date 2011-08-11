package clientData;

import action.Message;
import server.ServerThread;

public class ConnectionToServerLocal extends ConnectionToServer
	{
	public ServerThread thread=new ServerThread();
	
	public ConnectionToServerLocal()
		{
		thread.start();
		}
	
	public void send(Message msg)
		{
		thread.localSend(thisClientID, msg);
		}
	}
