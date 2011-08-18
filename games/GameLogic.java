package games;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import clientData.GameDesign;

import server.ServerThread;
import util.ClassHandling;

import action.GameAction;
import action.GameActionLeave;
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
	
	public ServerThread thread;
	public int sessionID;
	
	public boolean userAction(int fromUser, GameAction s)
		{
		if (s instanceof UserActionClickedCard)
			return userActionClickedCard(fromUser, (UserActionClickedCard) s);
		else if (s instanceof UserActionClickedButton)
			return userActionClickedButton(fromUser, (UserActionClickedButton) s);
		else if (s instanceof GameActionLeave)
			{
			GameActionLeave a=(GameActionLeave)s;
			boolean b=userLeft(a.fromClientID);
			players.remove(a.fromClientID);
			
			if(players.isEmpty())
				thread.gameSessions.remove(sessionID);
			//TODO only update this game session
			thread.broadcastToClients(thread.createMessageGameSessionsToClients());
			
			return b;
			}
		return false;
		}

	abstract public boolean userActionClickedCard(int fromUser, UserActionClickedCard s);

	abstract public boolean userActionClickedButton(int fromUser, UserActionClickedButton s);
	
	/**
	 * User wants to participate in the game
	 */
	abstract public boolean userJoined(int userID);
	
	/**
	 * User does not want to participate anymore
	 */
	abstract public boolean userLeft(int userID);

	/**
	 * Get how many players are actually participating (because max does not put a limit on spectators)
	 */
	abstract public int getNumParticipatingPlayers();
	
	abstract public int getMaxPlayers();
	abstract public int getMinPlayers();
	
	abstract public GameDesign createGameDesign();
	
	/**
	 * Detect available game types
	 */
	@SuppressWarnings("unchecked")
	public static Map<Class<? extends GameLogic>, GameType> availableGames()
		{
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
