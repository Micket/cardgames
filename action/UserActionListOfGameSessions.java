package action;

import java.util.HashMap;
import clientData.GameSession;

public class UserActionListOfGameSessions extends UserAction
	{
	private static final long serialVersionUID = 1L;

	// Map between gameID and metadata.
	public HashMap<Integer, GameSession> gameList=new HashMap<Integer, GameSession>();
	}
