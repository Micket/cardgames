package games;

import java.util.Set;

import action.UserAction;
import action.UserActionClickedButton;
import action.UserActionClickedCard;

/**
 * www.pagat.com for many many games.
 * @author mahogny
 * @author Micket
 */
abstract public class GameLogic
	{
	public boolean gameOn = false;
	public Set<Integer> players;
	
	abstract void startGame();
	
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
	
	abstract public boolean userJoined(int userID);
	abstract public boolean userLeft(int userID);
	//abstract public boolean joinAI(); // False if full, or if AI can't join?

	// General metadata displayed to connected users.
	abstract public String getName();
	abstract public String getDescription();
	abstract public int getMaxPlayers();
	abstract public int getMinPlayers();
	
	public static GameLogic GameFactory(String game)
		{
		return new BlackJack();
		}
	}
