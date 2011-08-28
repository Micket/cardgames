package server;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import action.GameAction;
import action.GameActionSendMessage;
import action.Message;
import action.Action;
import action.ActionListOfGameTypes;
import action.ActionListOfUsers;
import action.ActionListOfGameSessions;
import action.ActionListOfAvailableGames;
import action.ActionLobbyMessage;
import action.ActionSetNick;
import action.ActionStartGame;
import action.ActionDisconnect;
import games.GameLogic;
import games.GameType;

import clientData.GameInfo;

/**
 * Main server thread - handles all existing connections
 * 
 * @author mahogny
 *
 */
public class ServerThread extends Thread
	{
	private Map<Class<? extends GameLogic>, GameType> availableGames = GameType.availableGames();
	
	/** from clientID */
	public Map<Integer,ConnectionToClient> connections=new HashMap<Integer, ConnectionToClient>();
	
	/** from sessionID */
	public Map<Integer,GameLogic> gameSessions=new HashMap<Integer,GameLogic>();
	
	private LinkedList<Message> incomingQueue=new LinkedList<Message>();
	private Set<ServerOpenPort> openPorts=new HashSet<ServerOpenPort>();
	
	private boolean stopThread=false;
	
	/**
	 * Add an incoming message to the message queue
	 */
	public void addIncomingMessage(int fromClientID, Message msg)
		{
		synchronized (incomingQueue)
			{
			//Make sure the from-flag is properly set (clients does not set, and should not)
			for(Action action:msg.actions)
				action.fromClientID=fromClientID;
			
			//Add to queue, wake up thread
			incomingQueue.addLast(msg);
			incomingQueue.notifyAll();
			}
		
		}
	
	@Override
	public void run()
		{
		while(!stopThread)
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
				for(Action action:msg.actions)
					{
					if(action instanceof GameAction)// Pass on message to game session
						{
						GameAction ga=(GameAction)action;
						
						GameLogic game = gameSessions.get(ga.gameID);
						if(game!=null)
							{
							if(ga instanceof GameActionSendMessage)
								{
								GameActionSendMessage lm=(GameActionSendMessage)action;
								lm.fromClientID=action.fromClientID;
								broadcastToGameClients(new Message(lm), lm.gameID);
								}
							else
								game.userAction(action.fromClientID, ga);
							}
						else
							System.out.println("Error: Trying to pass message to non-existing game session "+ga.gameID);
						}
					else if(action instanceof ActionLobbyMessage)
						{
						ActionLobbyMessage lm=(ActionLobbyMessage)action;
						lm.fromClientID=action.fromClientID;
						broadcastToClients(new Message(lm));
						
						System.out.println("got message "+lm.message);
						}
					else if (action instanceof ActionStartGame)
						{
						try
							{
							//Create the game session
							GameLogic game = ((ActionStartGame)action).game.newInstance();
							game.thread=this;
							int sessionID=getFreeSessionID();
							game.sessionID=sessionID;
							gameSessions.put(sessionID, game);
							game.sessionName = ((ActionStartGame)action).sessionName;
							
							//Join the player
							game.handleClientJoinGameInfo(action.fromClientID);
							
							System.out.println("Starting game.");
							}
						catch (Exception e)
							{
							System.out.println("Can't instanciate game!");
							e.printStackTrace();
							}
						}
					else if (action instanceof ActionSetNick)
						{
						ActionSetNick a=(ActionSetNick)action;
						if(!getNickSet().contains(a.nick))
							{
							connections.get(a.fromClientID).nick=a.nick;
							broadcastUserlistToClients();
							}
						}
					else if (action instanceof ActionDisconnect)
						{
						disconnectClient(action.fromClientID);
						}
					else 
						System.out.println("Unrecognized action");
					}
				}
			}
		}
	
	
	
	/**
	 * Pass message on to all clients
	 */
	public void broadcastToClients(Message msg)
		{
		for(ConnectionToClient conn:connections.values())
			conn.send(msg);
		}

	/**
	 * Pass message on to all clients in a game
	 */
	public void broadcastToGameClients(Message msg, int gameID)
		{
		GameLogic logic=gameSessions.get(gameID);
		if(logic!=null)
			for(int clientID:logic.players)
				connections.get(clientID).send(msg);
		}
	
	
	/**
	 * Send a new user list of all users
	 */
	public void broadcastUserlistToClients()
		{
		ActionListOfUsers action=new ActionListOfUsers();
		for(Map.Entry<Integer,ConnectionToClient> c:connections.entrySet())
			action.nickMap.put(c.getKey(), c.getValue().nick);
		System.out.println("----- "+action.nickMap);
		broadcastToClients(new Message(action));
		}

	/**
	 * Send data to one client
	 */
	public void send(int clientID, Message msg)
		{
		connections.get(clientID).send(msg);
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
		ActionListOfGameTypes action=new ActionListOfGameTypes();
		action.availableGames=new HashMap<Class<? extends GameLogic>, GameType>(availableGames);
		return new Message(action);
		}
	
	
	
	public GameInfo createGameSessionUpdate(int sessionID)
		{
		GameLogic logic=gameSessions.get(sessionID);
		GameInfo gmd = new GameInfo();
		gmd.maxusers=logic.getMaxPlayers();
		gmd.minusers=logic.getMinPlayers();
		gmd.type=logic.getClass();
		gmd.joinedUsers.addAll(logic.players);
		gmd.sessionName = logic.sessionName;
		return gmd;
		}
	
	/**
	 * Create message: list of all game sessions
	 */
	public Message createMessageGameSessionsToClients()
		{
		System.out.println("Sending game list");
		ActionListOfGameSessions action=new ActionListOfGameSessions();
		for(Map.Entry<Integer,GameLogic> s:gameSessions.entrySet())
			{
			GameInfo gmd = createGameSessionUpdate(s.getKey());
			action.gameList.put(s.getKey(), gmd);
			}
		return new Message(action);
		}

	
	/**
	 * Send a list of available games.
	 */
	public void sendGamelistToClients(int userID)
		{
		System.out.println("Sending game list");
		ActionListOfAvailableGames action=new ActionListOfAvailableGames();
		action.gameList = new LinkedList<GameType>(availableGames.values());
		broadcastToClients(new Message(action));
		}
	
	/**
	 * Listen to one additional port
	 */
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
	
	
	/**
	 * Disconnect a client
	 */
	public void disconnectClient(int clientID)
		{
		if(connections.containsKey(clientID))
			{
			connections.remove(clientID);
			for(Map.Entry<Integer,GameLogic> g:gameSessions.entrySet())
				{
				if(g.getValue().players.contains(clientID))
					{
					g.getValue().userLeft(clientID);
					//Remove empty games automatically
					if (g.getValue().players.isEmpty()) 
						gameSessions.remove(g.getKey());
					}
				}
			// TODO: Broadcast game session list as well.
			broadcastUserlistToClients();
			}
		else
			System.out.println("Can't disconnect nonconnected user");
		}

	
	/**
	 * Get an unused clientID
	 */
	public int getFreeClientID()
		{
		int id=1;
		while(connections.containsKey(id))
			id++;
		return id;
		}

	/**
	 * Get all nicks in use
	 */
	public Set<String> getNickSet()
		{
		Set<String> nicks=new HashSet<String>();
		for(ConnectionToClient c:connections.values())
			nicks.add(c.nick);
		return nicks;
		}

	/**
	 * Generate one nick which is guaranteed to be free
	 */
	public String getFreeNick()
		{
		Set<String> nicks=getNickSet();
		int id=1;
		while(nicks.contains("Guest"+id))
			id++;
		return "Guest"+id;
		}

	/**
	 * Get one unused sessionID
	 */
	public int getFreeSessionID()
		{
		int i=1;
		while(gameSessions.containsKey(i))
			i++;
		return i;
		}

	/**
	 * Shut down the server
	 */
	public void shutDown()
		{
		//Close all listening ports
		for(ServerOpenPort p:openPorts)
			p.closePort();
		openPorts.clear();
		
		//Stop the server
		stopThread=true;
		synchronized (incomingQueue)
			{
			incomingQueue.notifyAll();
			}
		}
	
	}
