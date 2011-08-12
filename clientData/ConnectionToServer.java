package clientData;


import action.Message;

public interface ConnectionToServer
	{

	public void send(Message msg);
	public int getCliendID();
//	public Map<Integer, String> getMapClientIDtoNick();

	}
