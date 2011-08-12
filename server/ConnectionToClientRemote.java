package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import action.Message;

import server.ConnectionToClient;

/**
 * Player - Either a remote connection or an AI
 * @author mahogny
 */
public class ConnectionToClientRemote extends ConnectionToClient
	{
	public Socket socket;
	public String nick;
	public ObjectInputStream is;
	public ObjectOutputStream os;
	public int clientID;
	private ServerThread thread;
	
	public ConnectionToClientRemote(ServerThread thread)
		{
		this.thread=thread;
		}
	
	/**
	 * Random unique ID obtained upon connection. It never changes during a session.
	 */
	//public int connectionID;
	
	public void send(Message msg)
		{
		try
			{
			os.writeObject(msg);
			System.out.println("Sending message over network!!!");
			}
		catch (IOException e)
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		
		}
	
	
	@Override
	public void run()
		{


		try
			{
			os.writeChars("Cardgame\n");
			os.writeInt(1);        //Protocol version
			os.writeInt(0);
			os.writeInt(clientID);
			os.flush();

			System.out.println("----------");

			//Get preferred nick, set it if it is available
			String preferredNick=(String)is.readObject();
			if(!thread.getNickSet().contains(preferredNick))
				nick=preferredNick;

			for(;;)
				{

				Message msg=(Message)is.readObject();
				System.out.println("Got message");

				thread.addIncomingMessage(clientID, msg);
				}

			}
		catch (IOException e)
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		catch (ClassNotFoundException e)
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}

		//TODO

		//socket.


		}

	}
