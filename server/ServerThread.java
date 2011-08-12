package server;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import action.Message;
import action.UserAction;
import action.UserActionListOfUsers;
import action.UserActionLobbyMessage;
import action.UserActionSetNick;
import action.UserActionStartGame;
import games.GameLogic;

public class ServerThread extends Thread
	{
	public static final int defaultServerPort=4445;
	
	
	
	
	//from ID
	public Map<Integer,ConnectionToClient> connections=new HashMap<Integer, ConnectionToClient>();
	
	
	public Set<GameLogic> sessions=new HashSet<GameLogic>();
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
						GameLogic game = GameLogic.GameFactory(((UserActionStartGame)action).gameID);
						if (game != null)
							{
							sessions.add(game);
							System.out.println("Starting game.");
							}
						else
							{
							System.out.println("Couldn't find game.");
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
					else // Pass on message to game.
						{
						//TODO
						/*
						GameLogic game = null;
						game.userAction(action.fromClientID, action);
						System.out.println("Should send actions to game. (But to which)?");
						*/
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
	 * Send a new list of all users
	 */
	void broadcastUserlistToClients()
		{
		UserActionListOfUsers action=new UserActionListOfUsers();
		for(Map.Entry<Integer,ConnectionToClient> c:connections.entrySet())
			action.nickMap.put(c.getKey(), c.getValue().nick);
		broadcastToClients(new Message(action));
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
