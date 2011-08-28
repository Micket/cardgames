package action;

/**
 *
 * @author micket
 */
public class GameActionJoinGame extends GameAction
	{
	private static final long serialVersionUID = 1L;
	boolean asSpectator = false;
	
	public GameActionJoinGame(int gameID)
		{
		this.gameID=gameID;
		}
	}
