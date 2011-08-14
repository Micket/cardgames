package clientData;

import games.GameLogic;
import games.GameType;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import server.ConnectionToClientLocal;
import server.ServerThread;

import action.Message;
import action.UserAction;
import action.UserActionListOfGameSessions;
import action.UserActionListOfGameTypes;
import action.UserActionListOfUsers;


/**
 * Central client data
 * 
 * @author mahogny
 *
 */
public class Client
	{
	private ConnectionToServer serverConn;
	public Map<Integer,GameLogic> sessions=new HashMap<Integer, GameLogic>();
	public List<ServerListener> serverListeners=new LinkedList<ServerListener>();
	public Map<Integer, String> mapClientIDtoNick=new HashMap<Integer, String>();
	public Map<Integer, GameSession>  gameSessions=new HashMap<Integer, GameSession>();
	public Map<Class<? extends GameLogic>, GameType> gameTypes=new HashMap<Class<? extends GameLogic>, GameType>();

	/**
	 * Add a message from the server to the incoming queue
	 */
	public void gotMessageFromServer(Message msg)
		{
		System.out.println(msg);
		
		//Handle special messages
		for(UserAction action:msg.actions)
			{
			if(action instanceof UserActionListOfUsers)
				gotListOfUsers((UserActionListOfUsers)action);
			else if(action instanceof UserActionListOfGameTypes)
				gotListOfGameTypes((UserActionListOfGameTypes)action);
			else if(action instanceof UserActionListOfGameSessions)
				gotListOfGameSessions((UserActionListOfGameSessions)action);
			}
		
		//Send raw copies of messages
		for(ServerListener listener:serverListeners)
			listener.eventServerMessage(msg);
		
		}

	/**
	 * Handle incoming list of game types
	 */
	private void gotListOfGameTypes(UserActionListOfGameTypes action)
		{
		System.out.println("------------ game type list----------");
		gameTypes=action.availableGames;
		System.out.println(gameTypes);
		for(ServerListener listener:serverListeners)
			listener.eventNewGameSessions();
		}

	/**
	 * Handle incoming list of users
	 */
	private void gotListOfUsers(UserActionListOfUsers action)
		{
		System.out.println("---------- userlist ------------");
		
		mapClientIDtoNick=action.nickMap;
		for(ServerListener listener:serverListeners)
			listener.eventNewUserList();
		}

	/**
	 * Handle incoming list of games
	 */
	private void gotListOfGameSessions(UserActionListOfGameSessions action)
		{
		System.out.println("---------- gamelist ------------");
		
		gameSessions=action.gameList;
		for(ServerListener listener:serverListeners)
			listener.eventNewGameSessions();
		}
	
	/**
	 * Get the nick for a given client ID
	 */
	public String getNickFor(int id)
		{
		String nick=mapClientIDtoNick.get(id);
		if(nick==null)
			nick="<unnamed>";
		return nick;
		}


	/**
	 * Create a local server
	 */
	public void createServer()
		{
		ConnectionToServerLocal serverConn=new ConnectionToServerLocal();
		this.serverConn=serverConn;
		ConnectionToClientLocal connToClient=new ConnectionToClientLocal();
		connToClient.localClient=this;
		serverConn.thread.connections.put(0,connToClient);
		
		//TODO temp
		serverConn.thread.openPort(ServerThread.defaultServerPort);
		
		}
	
	/**
	 * Connect to a remote server
	 */
	public void connectToServer(InetAddress address, int port) throws IOException
		{
		//TODO cleanly close connection to the previous server first
		ConnectionToServerRemote sc=new ConnectionToServerRemote(this, address, port);
		serverConn=sc;
		sc.start();
		
		//TODO is there a need to update the GUI here?
		}
	


	public int getClientID()
		{
		return serverConn.getCliendID();
		}

	public String getNick()
		{
		//TODO if there is no connection, show the preferred one?
		String nick=getNickFor(getClientID());
		return nick;
		}

	public void send(Message msg)
		{
		serverConn.send(msg);
		}
	
	
	
	
	}
