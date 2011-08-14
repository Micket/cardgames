package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

import action.Message;

import server.ConnectionToClient;

/**
 * Player - Either a remote connection or an AI
 * @author mahogny
 */
public class ConnectionToClientRemote extends ConnectionToClient
	{
	public int clientID;

	private ObjectInputStream is;
	private ObjectOutputStream os;
	private ServerThread thread;
	
	public ConnectionToClientRemote(ServerThread thread, Socket socket) throws IOException
		{
		this.thread=thread;
		is=new ObjectInputStream(socket.getInputStream());
		os=new ObjectOutputStream(socket.getOutputStream());
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
	
	private LinkedList<Message> sendQueue=new LinkedList<Message>();
	

	public void addToSendQueue(Message msg)
		{
		synchronized (sendQueue)
			{
			sendQueue.addLast(msg);
			sendQueue.notifyAll();
			}
		}
	
	private class SendThread extends Thread
		{
		@Override
		public void run()
			{
			for(;;)
				{
				try
					{
					synchronized (sendQueue)
						{
						if(sendQueue.isEmpty())
							sendQueue.wait();
						else
							{
							Message msg=sendQueue.poll();
							os.writeObject(msg);
							System.out.println("writing to client "+msg);
							}
						}
					}
				catch (Exception e)
					{
					e.printStackTrace();
					}
				}
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

			//Now this connection is ready for normal communication. Allow sending
			//SendThread sendThread=
			SendThread sendThread=new SendThread();
			sendThread.start();

			//Update list of connections
			thread.broadcastUserlistToClients();
			addToSendQueue(thread.createMessageGameTypesToClients());
			addToSendQueue(thread.createMessageGameSessionsToClients());
			
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
