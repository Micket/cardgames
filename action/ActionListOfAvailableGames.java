package action;

import java.util.ArrayList;
import java.util.List;
import games.GameType;

public class ActionListOfAvailableGames extends Action
	{
	private static final long serialVersionUID = 1L;

	// Map between gameID and metadata.
	public List<GameType> gameList=new ArrayList<GameType>();
	}
