package action;

import clientData.GameInfo;

/**
 * Update of game info
 * 
 * @author mahogny
 *
 */
public class GameActionUpdateGameInfo extends GameAction
	{
	private static final long serialVersionUID = 1L;
	public GameInfo gameInfo;
	
	public GameActionUpdateGameInfo(int sessionID, GameInfo gameInfo)
		{
		this.gameID=sessionID;
		this.gameInfo=gameInfo;
		}
	}
