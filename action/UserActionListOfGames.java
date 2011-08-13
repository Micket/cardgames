package action;

import java.util.HashMap;
import clientData.GameMetaData;

public class UserActionListOfGames extends UserAction
	{
	private static final long serialVersionUID = 1L;

	// Map between gameID and metadata.
	public HashMap<Integer, GameMetaData> gameList=new HashMap<Integer, GameMetaData>();
	}
