package games;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import clientData.GameDesign;

import server.ServerThread;
import util.ClassHandling;

import action.GameAction;
import action.GameActionJoin;
import action.GameActionLeave;
import action.Message;
import action.GameActionClickedButton;
import action.GameActionClickedCard;
import action.GameActionDragCard;
import action.GameActionUpdateGameDesign;
import action.GameActionUpdateGameInfo;
import action.GameActionUpdateGameState;

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
		if (s instanceof GameActionClickedCard)
			return userActionClickedCard(fromUser, (GameActionClickedCard) s);
		else if (s instanceof GameActionClickedButton)
			return userActionClickedButton(fromUser, (GameActionClickedButton) s);
		else if (s instanceof GameActionDragCard)
			return userActionDragCard(fromUser, (GameActionDragCard) s);
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
		else if (s instanceof GameActionJoin)
			{
			GameActionJoin action=(GameActionJoin)s;
			
			//Check that the user is not in the game already
			if(!players.contains(action.fromClientID))
				{
				System.out.println("is joining "+action.fromClientID+" "+action.gameID);
				
				handleClientJoinGameInfo(action.fromClientID);
				
				
				}
			}
		return false;
		}

	public void handleClientJoinGameInfo(int clientID)
		{
		userJoined(clientID);
		thread.broadcastToClients(new Message(new GameActionUpdateGameInfo(sessionID, thread.createGameSessionUpdate(sessionID))));
		
		Message back=new Message();
		back.add(new GameActionUpdateGameDesign(sessionID, createGameDesign()));
		GameActionUpdateGameState upd=new GameActionUpdateGameState(sessionID);
		getGameState(upd);
		back.add(upd);
		
		sendToPlayers(back);
		}

	public void sendToPlayers(Message msg)
		{
		for(int playerID:players)
			thread.send(playerID, msg);
		}
	
	
	abstract public boolean userActionDragCard(int fromUser, GameActionDragCard s);

	abstract public boolean userActionClickedCard(int fromUser, GameActionClickedCard s);

	abstract public boolean userActionClickedButton(int fromUser, GameActionClickedButton s);
	
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
	
	abstract public void getGameState(GameActionUpdateGameState state);
	
	/**
	 * Detect available game types
	 */
	public static Map<Class<? extends GameLogic>, GameType> availableGames()
		{
		Map<Class<? extends GameLogic>, GameType> games=new HashMap<Class<? extends GameLogic>, GameType>();
		
		addGames(games, getClassesFromJar());
		
		try
			{
			addGames(games, ClassHandling.getClasses("games"));
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		
		return games;
		}
	
	
	@SuppressWarnings("unchecked")
	private static void addGames(Map<Class<? extends GameLogic>, GameType> games, Collection<Class<?>> classes)
		{
		for(Class<?> cl:classes)
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
	
	private static List<Class<?>> getClassesFromJar()
		{
		LinkedList<Class<?>> classes=new LinkedList<Class<?>>();
		InputStream is=GameLogic.class.getResourceAsStream("gameslist.txt");
		if(is!=null)
			try
				{
				BufferedReader br=new BufferedReader(new InputStreamReader(is));
				String line;
				while((line=br.readLine())!=null)
					{
					line=line.replace("/", ".");
					line=line.substring(0, line.length()-".java".length());
					try
						{
						classes.add(Class.forName(line));
						}
					catch (ClassNotFoundException e)
						{
						e.printStackTrace();
						}
					}
				}
			catch (IOException e)
				{
				e.printStackTrace();
				}
		return classes;
		}

	
	}
