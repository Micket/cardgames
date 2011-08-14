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
import util.CardGameInfo;

import action.Message;
import action.UserAction;
import action.UserActionGameSessionUpdate;
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
	public Map<Integer, GameSession> gameSessions=new HashMap<Integer, GameSession>();
	public Map<Class<? extends GameLogic>, GameType> gameTypes=new HashMap<Class<? extends GameLogic>, GameType>();

	public String tryToGetNick="Igotnick";
	
	/**
	 * Add a message from the server to the incoming queue
	 */
	public void gotMessageFromServer(Message msg)
		{
		System.out.println(msg);
		
		//Handle special messages
		for(UserAction action:msg.actions)
			{
			System.out.println("Getting action ---- "+action.getClass());
			if(action instanceof UserActionListOfUsers)
				gotListOfUsers((UserActionListOfUsers)action);
			else if(action instanceof UserActionListOfGameTypes)
				gotListOfGameTypes((UserActionListOfGameTypes)action);
			else if(action instanceof UserActionListOfGameSessions)
				gotListOfGameSessions((UserActionListOfGameSessions)action);
			else if(action instanceof UserActionGameSessionUpdate)
				gotGameSessionUpdate((UserActionGameSessionUpdate)action);
			
			//TODO in so many ways it would be nice with a general "else"
			
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
		mapClientIDtoNick=action.nickMap;
		for(ServerListener listener:serverListeners)
			listener.eventNewUserList();
		}

	/**
	 * Handle incoming list of games
	 */
	private void gotListOfGameSessions(UserActionListOfGameSessions action)
		{
		gameSessions=action.gameList;
		for(ServerListener listener:new LinkedList<ServerListener>(serverListeners))
			listener.eventNewGameSessions();
		}

	/**
	 * One updated game session
	 */
	private void gotGameSessionUpdate(UserActionGameSessionUpdate action)
		{
		if(action.session==null)
			gameSessions.remove(action.gameID);
		else
			gameSessions.put(action.gameID,action.session);
		
		for(ServerListener listener:new LinkedList<ServerListener>(serverListeners))
			listener.eventNewGameSessions();

		
		//TODO here detect if there is a need to join the channel? or leave it to somewhere else? quitting?
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
		
		ConnectionToClientLocal connToClient=new ConnectionToClientLocal(serverConn.thread);
		connToClient.nick=tryToGetNick;
		connToClient.localClient=this;
		
		int userID=0;
		serverConn.thread.connections.put(userID,connToClient);
		
		connToClient.doFinalHandshake(serverConn.thread);
		
		//TODO temp
		serverConn.thread.openPort(CardGameInfo.defaultServerPort);		
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
		if(serverConn!=null)
			return serverConn.getCliendID();
		else
			return 0;
		}

	public String getNick()
		{
		//If there is no connection, show the nick it will try to get upon connecting
		if(serverConn!=null)
			return getNickFor(getClientID());
		else
			return tryToGetNick;
		}

	public void send(Message msg)
		{
		if(serverConn!=null)
			serverConn.send(msg);
		else
			System.out.println("Error: Trying to send message but there is no connection");
		}
	
	
	
	
	}