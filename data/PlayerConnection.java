package data;

import java.net.Socket;


/**
 * Player - Either a remote connection or an AI
 * @author mahogny
 *
 */
public class PlayerConnection
	{

	public Socket socket;
	
	public String nick;
	
	/**
	 * Random unique ID obtained upon connection. It never changes during a session.
	 */
	public int connectionID;
	
	
	}
