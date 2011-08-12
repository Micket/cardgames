package clientData;

import java.util.Map;

import action.Message;

public interface ServerListener
	{

	public void eventServerMessage(Message msg);
	
	public void eventNewUserList();
	}
