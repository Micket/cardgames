package action;

import games.GameLogic;

/**
 *
 * @author micket
 */
public class UserActionStartGame extends UserAction
	{
	private static final long serialVersionUID = 1L;
	
	public Class<? extends GameLogic> game;
	public String sessionName;
	}
