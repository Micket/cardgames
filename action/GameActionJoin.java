package action;

/**
 *
 * @author micket
 */
public class GameActionJoin extends GameAction
	{
	private static final long serialVersionUID = 1L;
	boolean asSpectator = false;
	
	public GameActionJoin(int gameID)
		{
		this.gameID=gameID;
		}
	}
