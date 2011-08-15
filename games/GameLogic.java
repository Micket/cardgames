package games;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import util.ClassHandling;

import action.GameAction;
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
	public String sessionName;
	
	abstract void startGame();
	
	public boolean userAction(int fromUser, GameAction s)
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
//	abstract public String getName();
//	abstract public String getDescription();
	abstract public int getMaxPlayers();
	abstract public int getMinPlayers();
	
	@SuppressWarnings("unchecked")
	public static Map<Class<? extends GameLogic>, GameType> availableGames()
		{
//		List<GameType> games = new ArrayList<GameType>();
		Map<Class<? extends GameLogic>, GameType> games=new HashMap<Class<? extends GameLogic>, GameType>();
		
		try
			{
			for(Class<?> cl:ClassHandling.getClasses("games"))
				{
				GameTypePlugin plugin=cl.getAnnotation(GameTypePlugin.class);
				if(plugin!=null)
					{
					System.out.println("Adding game: "+cl);
					GameType gt=new GameType(plugin, (Class<? extends GameLogic>)cl);
					games.put((Class<? extends GameLogic>)cl,gt);
					}
				}
			}
		catch (ClassNotFoundException e)
			{
			e.printStackTrace();
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}
		
		return games;
		}
	
	}
