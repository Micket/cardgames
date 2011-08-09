package client;

import action.Message;
import server.ServerThread;

public class ServerConnectionLocal extends ServerConnection
	{
	public ServerThread thread=new ServerThread();
	
	public ServerConnectionLocal()
		{
		thread.start();
		}
	
	public void send(Message msg)
		{
		thread.localSend(thisClientID, msg);
		}
	}
