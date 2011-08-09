package games;

import action.UserActionClickedButton;
import action.UserActionClickedCard;

/**
 */
public class DefaultGameLogic extends GameLogic
	{

    public boolean userActionClickedCard(int fromUser, UserActionClickedCard s)
        {
        System.out.println("Card clicked!");
        return false;
        }
    public boolean userActionButton(int fromUser, UserActionClickedButton s)
        {
        System.out.println("Button pressed");
        return false;
        };

    public String getName() { return "DefaultGameLogic"; }

	}
