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

import action.GameActionSendMessage;
import action.Message;
import action.Action;
import action.GameActionDragCard;
import action.GameActionUpdateCard;
import action.GameActionUpdateGameDesign;
import action.GameActionUpdateGameInfo;
import action.GameActionUpdateGameState;
import action.ActionListOfGameSessions;
import action.ActionListOfGameTypes;
import action.ActionListOfUsers;
import action.ActionDisconnect;


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
	public Map<Integer, GameInfo> gameSessions=new HashMap<Integer, GameInfo>();
	public Map<Class<? extends GameLogic>, GameType> gameTypes=new HashMap<Class<? extends GameLogic>, GameType>();

	public String tryToGetNick=System.getProperty("user.name");
	
	/**
	 * Add a message from the server to the incoming queue
	 */
	public void gotMessageFromServer(Message msg)
		{
		System.out.println(msg);
		
		//Handle special messages
		for(Action action:msg.actions)
			if(action!=null)
				{
				System.out.println("Getting action ---- "+action.getClass());
				if(action instanceof ActionListOfUsers)
					gotListOfUsers((ActionListOfUsers)action);
				else if(action instanceof ActionListOfGameTypes)
					gotListOfGameTypes((ActionListOfGameTypes)action);
				else if(action instanceof ActionListOfGameSessions)
					gotListOfGameSessions((ActionListOfGameSessions)action);
				else if(action instanceof GameActionUpdateGameDesign)
					gotGameDesign((GameActionUpdateGameDesign)action);
				else if(action instanceof GameActionUpdateGameInfo)
					gotGameSessionUpdate((GameActionUpdateGameInfo)action);
				else if(action instanceof GameActionSendMessage)
					gotGameMessage((GameActionSendMessage)action);
				else if(action instanceof GameActionUpdateGameState)
					gotGameStateUpdate((GameActionUpdateGameState)action);
				else if(action instanceof GameActionUpdateCard)
					gotGameCardUpdate((GameActionUpdateCard)action);
				else if(action instanceof GameActionDragCard)
					gotDragCard((GameActionDragCard)action);
				
				//TODO in so many ways it would be nice with a general "else"
				
				}
		
		//Send raw copies of messages
		for(ServerListener listener:serverListeners)
			listener.eventServerMessage(msg);
		
		}


	private void gotGameCardUpdate(GameActionUpdateCard action)
		{
		for(ServerListener listener:serverListeners)
			listener.eventGameCardUpdate(action);
		}


	private void gotDragCard(GameActionDragCard action)
		{
		for(ServerListener listener:serverListeners)
			listener.eventDragCard(action);
		}


	private void gotGameStateUpdate(GameActionUpdateGameState action)
		{
		for(ServerListener listener:serverListeners)
			listener.eventGameStateUpdate(action);
		}


	private void gotGameMessage(GameActionSendMessage action)
		{
		for(ServerListener listener:serverListeners)
			listener.eventGameMessage(action);
		}


	private void gotGameDesign(GameActionUpdateGameDesign action)
		{
		for(ServerListener listener:serverListeners)
			listener.eventGameDesign(action);
		}


	/**
	 * Handle incoming list of game types
	 */
	private void gotListOfGameTypes(ActionListOfGameTypes action)
		{
		gameTypes=action.availableGames;
		System.out.println(gameTypes);
		for(ServerListener listener:serverListeners)
			listener.eventNewGameSessions();
		}

	/**
	 * Handle incoming list of users
	 */
	private void gotListOfUsers(ActionListOfUsers action)
		{
		mapClientIDtoNick=action.nickMap;
		for(ServerListener listener:serverListeners)
			listener.eventNewUserList();
		}

	/**
	 * Handle incoming list of games
	 */
	private void gotListOfGameSessions(ActionListOfGameSessions action)
		{
		gameSessions=action.gameList;
		for(ServerListener listener:new LinkedList<ServerListener>(serverListeners))
			listener.eventNewGameSessions();
		}

	/**
	 * One updated game session
	 */
	private void gotGameSessionUpdate(GameActionUpdateGameInfo action)
		{
		if(action.gameInfo==null)
			gameSessions.remove(action.gameID);
		else
			gameSessions.put(action.gameID,action.gameInfo);
		
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
		
		connToClient.doFinalHandshake(serverConn.thread); //TODO bug: nick not the one sent!!!
		
		//TODO temp
		serverConn.thread.openPort(CardGameInfo.defaultServerPort);		
		}
	
	/**
	 * Connect to a remote server
	 */
	public void connectToServer(InetAddress address, int port) throws IOException
		{
		disconnectFromServer();
		ConnectionToServerRemote sc=new ConnectionToServerRemote(this, address, port);
		serverConn=sc;
		sc.start();
		}
	
	/**
	 * Connect to a remote server
	 */
	public void disconnectFromServer()
		{
		if(serverConn != null)
			{
			serverConn.send(new Message(new ActionDisconnect()));
			serverConn.tearDownConnection(); //TODO This is not optimal! the action might not even be sent here. so should one even send one?
			}
		serverConn = null;
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


	public void gotBadMessageFromServer(Exception e)
		{
		System.out.println("Got a message from server that could not be resolved "+e);
		}
	
	
	
	}
