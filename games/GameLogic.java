package games;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

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
	public Set<Integer> players = new HashSet<Integer>();
	
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
	
	public static List<GameType> AvailableGames()
		{
		GameType type;
		List<GameType> games = new ArrayList<GameType>(1);
		type = new GameType();
		// TODO: Static methods? Any other convenient way to do this? (Probably not)
		type.name = "TODO";
		type.description = "TODO";
		type.maxplayers = -1;
		type.minplayers = -1;
		type.game = BlackJack.class;
		games.add(type);
		return games;
		}
	
	}
