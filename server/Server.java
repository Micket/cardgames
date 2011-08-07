package server;

import games.GameLogic;

import java.util.HashSet;
import java.util.Set;

import data.PlayerConnection;

public class Server
	{
	
	
	public Set<PlayerConnection> connections=new HashSet<PlayerConnection>();

	
	public Set<GameLogic> sessions=new HashSet<GameLogic>();
	
	
	}
