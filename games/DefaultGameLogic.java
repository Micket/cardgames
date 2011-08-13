package games;

import action.UserActionClickedButton;
import action.UserActionClickedCard;

/**
 * Default values for some game logic methods.
 */
abstract public class DefaultGameLogic extends GameLogic
	{

    public boolean userActionClickedCard(int fromUser, UserActionClickedCard s)
        {
        System.out.println("Card clicked!");
        return false;
        }
    public boolean userActionClickedButton(int fromUser, UserActionClickedButton s)
        {
        System.out.println("Button pressed");
        return false;
        };

	public boolean userJoined(int userID)
		{
		if (getMaxPlayers() > 0 && players.size() >= getMaxPlayers())
			return false;
		players.add(userID);
		return true;
		}

	public boolean userLeft(int userID)
		{
		if (!players.contains(userID))
			return false;
		players.remove(userID);
		return true;
		}

	public int getMaxPlayers() { return -1; } // -1 means no limit.
	public int getMinPlayers() { return -1; } // -1 means no limit.
	}
