package action;

import java.util.ArrayList;
import java.util.List;
import clientData.GameMetaData;
import games.GameType;

public class UserActionListOfAvailableGames extends UserAction
	{
	private static final long serialVersionUID = 1L;

	// Map between gameID and metadata.
	public List<GameType> gameList=new ArrayList<GameType>();
	}
