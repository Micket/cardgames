package games;

import action.UserAction;
import action.UserActionClickedButton;
import action.UserActionClickedCard;

/**
 * Note: Aisleriot has all games as descriptions. Could be possible to import all of them with a reader
 * www.pagat.com for many many games.
 * @author mahogny
 *
 */
abstract public class GameLogic
	{

	public boolean userAction(int fromUser, UserAction s)
		{
		if (s instanceof UserActionClickedCard)
			return userActionClickedCard(fromUser, (UserActionClickedCard) s);
		else if (s instanceof UserActionClickedButton)
			return userActionClickedButton(fromUser, (UserActionClickedButton) s);
		return false;
		}

	abstract public boolean userActionClickedCard(int fromUser, UserActionClickedCard s);

	abstract public boolean userActionClickedButton(int fromUser, UserActionClickedButton s);

	// General metadata displayed to connected users.
	abstract public String getName();
	abstract public String getDescription();
	abstract public int getMaxPlayers();
	abstract public int getMinPlayers();
	}
