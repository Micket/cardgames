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

	public int getMaxPlayers() { return -1; } // -1 means no limit.
	public int getMinPlayers() { return -1; } // -1 means no limit.
	}
