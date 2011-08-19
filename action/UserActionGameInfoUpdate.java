package action;

import clientData.GameInfo;

/**
 * Update of game info
 * 
 * @author mahogny
 *
 */
public class UserActionGameInfoUpdate extends GameAction
	{
	private static final long serialVersionUID = 1L;
	public GameInfo session;
	
	
	public UserActionGameInfoUpdate(int sessionID, GameInfo session)
		{
		this.gameID=sessionID;
		this.session=session;
		}
	}
