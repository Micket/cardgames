package clientData;


import action.Message;

/**
 * Connection to one server
 * 
 * @author mahogny
 *
 */
public interface ConnectionToServer
	{

	/**
	 * Send a message to the server
	 */
	public void send(Message msg);
	
	/**
	 * Get our clientID for this connection
	 */
	public int getCliendID();

	/**
	 * Close the socket to a server and stop any threads
	 */
	public void tearDownConnection();
	}
