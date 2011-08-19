package action;

import java.util.HashMap;
import clientData.GameInfo;

public class UserActionListOfGameSessions extends UserAction
	{
	private static final long serialVersionUID = 1L;

	// Map between gameID and metadata.
	public HashMap<Integer, GameInfo> gameList=new HashMap<Integer, GameInfo>();
	}
