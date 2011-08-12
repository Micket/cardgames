package clientQT;

import games.GameLogic;

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
import action.UserActionListOfUsers;

import clientData.ConnectionToServer;
import clientData.ConnectionToServerLocal;
import clientData.ConnectionToServerRemote;
import clientData.ServerListener;

import com.trolltech.qt.gui.QApplication;

public class Client
	{
	public ConnectionToServer serverConn;
	public Map<Integer,GameLogic> sessions=new HashMap<Integer, GameLogic>();
	public List<ServerListener> serverListeners=new LinkedList<ServerListener>();
	public Map<Integer, String> mapClientIDtoNick=new HashMap<Integer, String>();

	/**
	 * Add a message from the server to the incoming queue
	 */
	public void gotMessageFromServer(Message msg)
		{
		System.out.println(msg);
		
		//Handle special messages
		for(UserAction action:msg.actions)
			if(action instanceof UserActionListOfUsers)
				gotListOfUsers((UserActionListOfUsers)action);
		
		//Send raw copies of messages
		for(ServerListener listener:serverListeners)
			listener.eventServerMessage(msg);
		
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
	private void createServer()
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
		}
	

	/**
	 * Entry point
	 */
	public static void main(String args[])
		{
		QApplication.initialize(args);
		
		Client client=new Client();
		
		client.createServer();
		
		BoardWindow boardWindow = new BoardWindow(client);
		client.serverListeners.add(boardWindow); //TODO on close, unregister
		boardWindow.show();
		
		LobbyWindow lobbyWindow = new LobbyWindow(client);
		client.serverListeners.add(lobbyWindow); //TODO on close, unregister
		lobbyWindow.show();
		
		QApplication.exec();
		}

	public int getClientID()
		{
		return serverConn.getCliendID();
		}
	
	
	
	
	}
