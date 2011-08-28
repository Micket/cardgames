package clientData;

import action.GameActionSendMessage;
import action.Message;
import action.GameActionDragCard;
import action.GameActionUpdateCard;
import action.GameActionUpdateGameDesign;
import action.GameActionUpdateGameState;

public interface ServerListener
	{

	public void eventServerMessage(Message msg);
	
	public void eventNewUserList();
	
	public void eventNewGameSessions();

	public void eventGameDesign(GameActionUpdateGameDesign msg);

	public void eventGameMessage(GameActionSendMessage action);

	public void eventGameStateUpdate(GameActionUpdateGameState action);

	public void eventDragCard(GameActionDragCard action);

	public void eventGameCardUpdate(GameActionUpdateCard action);
	}
