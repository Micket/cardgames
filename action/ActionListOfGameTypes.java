package action;

import games.GameLogic;
import games.GameType;

import java.util.Map;

public class ActionListOfGameTypes extends Action
	{
	private static final long serialVersionUID = 1L;

	public Map<Class<? extends GameLogic>, GameType> availableGames;
	}
