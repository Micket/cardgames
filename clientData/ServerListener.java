package clientData;

import action.Message;

public interface ServerListener
	{

	public void eventServerMessage(Message msg);
	
	public void eventNewUserList();
	
	public void eventNewGameSessions();
	}
