package client;

import games.GameLogic;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import server.ConnectionToClient;

import action.Message;

import com.trolltech.qt.gui.QApplication;

public class Client
	{
	public ServerConnection serverConn;
	public Map<Integer,GameLogic> sessions=new HashMap<Integer, GameLogic>();
	public List<ServerListener> serverListeners=new LinkedList<ServerListener>();
	
	
	public void localSend(Message msg)
		{
		for(ServerListener listener:serverListeners)
			listener.eventServerMessage(msg);
		}
	
	
	private void createServer()
		{
		ServerConnectionLocal serverConn=new ServerConnectionLocal();
		this.serverConn=serverConn;
		ConnectionToClient connToClient=new ConnectionToClient();
		connToClient.localClient=this;
		serverConn.thread.connections.put(0,connToClient);
		}
	
	
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
	
	
	
	
	}
