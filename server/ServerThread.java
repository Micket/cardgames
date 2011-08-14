package server;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import action.Message;
import action.UserAction;
import action.UserActionListOfGameTypes;
import action.UserActionListOfUsers;
import action.UserActionListOfGameSessions;
import action.UserActionLobbyMessage;
import action.UserActionSetNick;
import action.UserActionStartGame;
import games.GameLogic;
import games.GameType;

import clientData.GameSession;

public class ServerThread extends Thread
	{
	public static final int defaultServerPort=4445;
	
	
	private Map<Class<? extends GameLogic>, GameType> availableGames = GameLogic.availableGames();
	
	//from ID
	public Map<Integer,ConnectionToClient> connections=new HashMap<Integer, ConnectionToClient>();
	public Map<Integer,GameLogic> sessions=new HashMap<Integer,GameLogic>();
	private LinkedList<Message> messages=new LinkedList<Message>();

	private Set<ServerOpenPort> openPorts=new HashSet<ServerOpenPort>();
	
	/**
	 * Add an incoming message to the message queue
	 */
	public void addIncomingMessage(int fromClientID, Message msg)
		{
		synchronized (messages)
			{
			for(UserAction action:msg.actions)
				action.fromClientID=fromClientID;

			messages.addLast(msg);
			messages.notifyAll();
			}
		
		}
	
	@Override
	public void run()
		{
		for(;;)
			{
			//TODO there might be more messages. don't wait if there is more

			Message msg;
			synchronized (messages)
				{
				try
					{
					messages.wait();
					}
				catch (InterruptedException e)
					{
					e.printStackTrace();
					}
				
				msg=messages.poll();
				System.out.println(msg);
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
							game.userJoined(action.fromClientID);
							sessions.put(0, game); // TODO: Generate game ID's.
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
//							broadcastGamelistToClients(); // TODO: Remove this. Just for debugging.
							}
						}
					else // Pass on message to game.
						{
						GameLogic game = sessions.get(action.gameID);
						game.userAction(action.fromClientID, action);
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
		return sessions.get(sessionID).getClass();
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
	
	
	/**
	 * Create message: list of all game sessions
	 */
	public Message createMessageGameSessionsToClients()
		{
		System.out.println("Sending game list");
		UserActionListOfGameSessions action=new UserActionListOfGameSessions();
		for(Map.Entry<Integer,GameLogic> s:sessions.entrySet())
			{
			GameSession gmd = new GameSession();
			gmd.maxusers=s.getValue().getMaxPlayers();
			gmd.minusers=s.getValue().getMinPlayers();
			gmd.type=s.getValue().getClass();
			gmd.joinedUsers = s.getValue().players;
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

	}
