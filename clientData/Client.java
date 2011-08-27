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
import action.UserAction;
import action.UserActionDragCard;
import action.UserActionGameCardUpdate;
import action.UserActionGameDesign;
import action.UserActionGameInfoUpdate;
import action.UserActionGameStateUpdate;
import action.UserActionListOfGameSessions;
import action.UserActionListOfGameTypes;
import action.UserActionListOfUsers;
import action.UserActionDisconnect;


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
		for(UserAction action:msg.actions)
			if(action!=null)
				{
				System.out.println("Getting action ---- "+action.getClass());
				if(action instanceof UserActionListOfUsers)
					gotListOfUsers((UserActionListOfUsers)action);
				else if(action instanceof UserActionListOfGameTypes)
					gotListOfGameTypes((UserActionListOfGameTypes)action);
				else if(action instanceof UserActionListOfGameSessions)
					gotListOfGameSessions((UserActionListOfGameSessions)action);
				else if(action instanceof UserActionGameDesign)
					gotGameDesign((UserActionGameDesign)action);
				else if(action instanceof UserActionGameInfoUpdate)
					gotGameSessionUpdate((UserActionGameInfoUpdate)action);
				else if(action instanceof GameActionSendMessage)
					gotGameMessage((GameActionSendMessage)action);
				else if(action instanceof UserActionGameStateUpdate)
					gotGameStateUpdate((UserActionGameStateUpdate)action);
				else if(action instanceof UserActionGameCardUpdate)
					gotGameCardUpdate((UserActionGameCardUpdate)action);
				else if(action instanceof UserActionDragCard)
					gotDragCard((UserActionDragCard)action);
				
				//TODO in so many ways it would be nice with a general "else"
				
				}
		
		//Send raw copies of messages
		for(ServerListener listener:serverListeners)
			listener.eventServerMessage(msg);
		
		}


	private void gotGameCardUpdate(UserActionGameCardUpdate action)
		{
		for(ServerListener listener:serverListeners)
			listener.eventGameCardUpdate(action);
		}


	private void gotDragCard(UserActionDragCard action)
		{
		for(ServerListener listener:serverListeners)
			listener.eventDragCard(action);
		}


	private void gotGameStateUpdate(UserActionGameStateUpdate action)
		{
		for(ServerListener listener:serverListeners)
			listener.eventGameStateUpdate(action);
		}


	private void gotGameMessage(GameActionSendMessage action)
		{
		for(ServerListener listener:serverListeners)
			listener.eventGameMessage(action);
		}


	private void gotGameDesign(UserActionGameDesign action)
		{
		for(ServerListener listener:serverListeners)
			listener.eventGameDesign(action);
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
	private void gotGameSessionUpdate(UserActionGameInfoUpdate action)
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
		if (serverConn != null)
			{
			serverConn.send(new Message(new UserActionDisconnect()));
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
	
	
	
	
	}
