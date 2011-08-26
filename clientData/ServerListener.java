package clientData;

import action.GameActionSendMessage;
import action.Message;
import action.UserActionGameDesign;
import action.UserActionGameStateUpdate;

public interface ServerListener
	{

	public void eventServerMessage(Message msg);
	
	public void eventNewUserList();
	
	public void eventNewGameSessions();

	public void eventGameDesign(UserActionGameDesign msg);

	public void eventGameMessage(GameActionSendMessage action);

	public void eventGameStateUpdate(UserActionGameStateUpdate action);
	}
