package action;

import clientData.GameSession;

/**
 * Update of a game session. Also sent when a user joins a game session
 * @author mahogny
 *
 */
public class UserActionGameSessionUpdate extends GameAction
	{
	private static final long serialVersionUID = 1L;
	public GameSession session;
	
	public UserActionGameSessionUpdate(int sessionID, GameSession session)
		{
		this.gameID=sessionID;
		this.session=session;
		}
	}
