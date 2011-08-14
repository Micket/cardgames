package action;

import clientData.GameSession;

/**
 * Update of a game session. Also sent when a user joins a game session
 * @author mahogny
 *
 */
public class UserActionGameSessionUpdate
	{
	public int sessionID;
	public GameSession session;
	}
