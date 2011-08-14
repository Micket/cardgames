package server;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import action.GameAction;
import action.Message;
import action.UserAction;
import action.UserActionGameSessionUpdate;
import action.UserActionListOfGameTypes;
import action.UserActionListOfUsers;
import action.UserActionListOfGameSessions;
import action.UserActionLobbyMessage;
import action.UserActionSetNick;
import action.UserActionStartGame;
import games.GameLogic;
import games.GameType;

import clientData.GameSession;

/**
 * Main server thread - handles all existing connections
 * 
 * @author mahogny
 *
 */
public class ServerThread extends Thread
	{
	private Map<Class<? extends GameLogic>, GameType> availableGames = GameLogic.availableGames();
	
	/** from clientID */
	public Map<Integer,ConnectionToClient> connections=new HashMap<Integer, ConnectionToClient>();
	
	/** from sessionID */
	public Map<Integer,GameLogic> gameSessions=new HashMap<Integer,GameLogic>();
	
	private LinkedList<Message> incomingQueue=new LinkedList<Message>();
	private Set<ServerOpenPort> openPorts=new HashSet<ServerOpenPort>();
	
	/**
	 * Add an incoming message to the message queue
	 */
	public void addIncomingMessage(int fromClientID, Message msg)
		{
		synchronized (incomingQueue)
			{
			//Make sure the from-flag is properly set (clients does not set, and should not)
			for(UserAction action:msg.actions)
				action.fromClientID=fromClientID;
			
			//Add to queue, wake up thread
			incomingQueue.addLast(msg);
			incomingQueue.notifyAll();
			}
		
		}
	
	@Override
	public void run()
		{
		for(;;)
			{
			Message msg;
			synchronized (incomingQueue)
				{
				if(incomingQueue.isEmpty())
					try
						{
						incomingQueue.wait();
						}
					catch (InterruptedException e)
						{
						e.printStackTrace();
						}
				
				msg=incomingQueue.poll();
				System.out.println("server got: "+msg);
				}
			if(msg!=null)
				{
				Message outMsg=new Message();
				
				for(UserAction action:msg.actions)
					{
					if(action instanceof UserActionLobbyMessage)
						{
						UserActionLobbyMessage lm=(UserActionLobbyMessage)action;
						lm.fromClientID=action.fromClientID;
						outMsg.add(lm);
						broadcastToClients(outMsg);
						
						System.out.println("got message "+lm.message);
						}
					else if (action instanceof UserActionStartGame)
						{
						try
							{
							GameLogic game = ((UserActionStartGame)action).game.newInstance();
							game.players.add(action.fromClientID); //This really should be here and not in gamelogic
							game.userJoined(action.fromClientID);
							int sessionID=getFreeGameSessionID();
							gameSessions.put(sessionID, game);
							
							broadcastToClients(new Message(new UserActionGameSessionUpdate(sessionID, createGameSessionUpdate(sessionID))));
							
							System.out.println("Starting game.");
							}
						catch (Exception e)
							{
							System.out.println("Can't instanciate game!");
							}
						}
					else if (action instanceof UserActionSetNick)
						{
						UserActionSetNick a=(UserActionSetNick)action;
						if(!getNickSet().contains(a.nick))
							{
							connections.get(a.fromClientID).nick=a.nick;
							broadcastUserlistToClients();
							}
						}
					else if(action instanceof GameAction)// Pass on message to game session
						{
						GameAction ga=(GameAction)action;
						
						GameLogic game = gameSessions.get(ga.gameID);
						if(game!=null)
							game.userAction(action.fromClientID, ga);
						else
							System.out.println("Error: Trying to pass message to non-existing game session "+ga.gameID);
						}
					}
				}
			
			
			}
		
		
		
		}
	
	/**
	 * Pass message on to all clients
	 */
	private void broadcastToClients(Message msg)
		{
		for(ConnectionToClient conn:connections.values())
			conn.send(msg);
		}

	/**
	 * Send a new user list of all users
	 */
	public void broadcastUserlistToClients()
		{
		UserActionListOfUsers action=new UserActionListOfUsers();
		for(Map.Entry<Integer,ConnectionToClient> c:connections.entrySet())
			action.nickMap.put(c.getKey(), c.getValue().nick);
		broadcastToClients(new Message(action));
		}

	
	/**
	 * Get the type of a game, given ID
	 */
	public Class<? extends GameLogic> getGameType(int sessionID)
		{
		return gameSessions.get(sessionID).getClass();
		}
	
	
	/**
	 * Create message: list of all game types
	 */
	public Message createMessageGameTypesToClients()
		{
		System.out.println("Sending game type list");
		UserActionListOfGameTypes action=new UserActionListOfGameTypes();
		action.availableGames=new HashMap<Class<? extends GameLogic>, GameType>(availableGames);
		return new Message(action);
		}
	
	
	
	private GameSession createGameSessionUpdate(int sessionID)
		{
		GameLogic logic=gameSessions.get(sessionID);
		GameSession gmd = new GameSession();
		gmd.maxusers=logic.getMaxPlayers();
		gmd.minusers=logic.getMinPlayers();
		gmd.type=logic.getClass();
		gmd.joinedUsers = logic.players;
		return gmd;
		}
	
	/**
	 * Create message: list of all game sessions
	 */
	public Message createMessageGameSessionsToClients()
		{
		System.out.println("Sending game list");
		UserActionListOfGameSessions action=new UserActionListOfGameSessions();
		for(Map.Entry<Integer,GameLogic> s:gameSessions.entrySet())
			{
			GameSession gmd = createGameSessionUpdate(s.getKey());
			action.gameList.put(s.getKey(), gmd);
			}
		return new Message(action);
		}

	public boolean openPort(int port)
		{
		try
			{
			ServerOpenPort p=new ServerOpenPort(this, port);
			openPorts.add(p);
			p.start();
			return true;
			}
		catch (IOException e)
			{
			e.printStackTrace();
			return false;
			}
		}
	
	public int getFreeClientID()
		{
		int id=1;
		while(connections.containsKey(id))
			id++;
		return id;
		}

	
	public Set<String> getNickSet()
		{
		Set<String> nicks=new HashSet<String>();
		for(ConnectionToClient c:connections.values())
			nicks.add(c.nick);
		return nicks;
		}
	
	public String getFreeNick()
		{
		Set<String> nicks=getNickSet();
		int id=1;
		while(nicks.contains("Guest"+id))
			id++;
		return "Guest"+id;
		}

	public int getFreeGameSessionID()
		{
		int i=1;
		while(gameSessions.containsKey(i))
			i++;
		return i;
		}
	
	}
