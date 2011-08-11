package server;

import action.Message;

/**
 * Player - Either a remote connection or an AI
 * @author mahogny
 *
 */
abstract public class ConnectionToClient extends Thread
	{

	abstract public void send(Message msg);
	
	@Override
	abstract public void run();
	
	}
