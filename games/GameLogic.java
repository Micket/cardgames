package games;

import action.UserActionClickedButton;
import action.UserActionClickedCard;
import games.*;

/**
 * Note: Aisleriot has all games as descriptions. Could be possible to import all of them with a reader
 * www.pagat.com for many many games.
 * @author mahogny
 *
 */
abstract public class GameLogic
	{

    abstract public boolean userActionClickedCard(int fromUser, UserActionClickedCard s);
    abstract public boolean userActionButton(int fromUser, UserActionClickedButton s);

    abstract public String getName();

	}
