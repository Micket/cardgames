package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

import action.Message;

import server.ConnectionToClient;

/**
 * Connection to a client over TCP/IP
 * 
 * @author mahogny
 */
public class ConnectionToClientRemote extends ConnectionToClient
	{
	public int clientID;
	
	private ObjectInputStream is;
	private ObjectOutputStream os;
	private ServerThread thread;
	private LinkedList<Message> sendQueue=new LinkedList<Message>();
	
	private boolean stopThread=false;
	private boolean getStopThread()
		{
		return stopThread;
		}
	
	public ConnectionToClientRemote(ServerThread thread, Socket socket) throws IOException
		{
		this.thread=thread;
		is=new ObjectInputStream(socket.getInputStream());
		os=new ObjectOutputStream(socket.getOutputStream());
		}
	
	
	public void send(Message msg)
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
			try
				{
				while(!getStopThread())
					{
					synchronized (sendQueue)
						{
						if(sendQueue.isEmpty())
							{
							try
								{
								sendQueue.wait();
								}
							catch (InterruptedException e)
								{
								e.printStackTrace();
								}
							}
						else
							{
							Message msg=sendQueue.poll();
							os.writeObject(msg);
							System.out.println("writing to client "+msg);
							}
						}
					}
				}
			catch (IOException e)
				{
				e.printStackTrace();
				connectionDied();
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

			//Get preferred nick, set it if it is available
			String preferredNick=(String)is.readObject();
			if(!thread.getNickSet().contains(preferredNick))
				nick=preferredNick;

			//Now this connection is ready for normal communication. Allow sending
			SendThread sendThread=new SendThread();
			sendThread.start();

			doFinalHandshake(thread);

			while(!getStopThread())
				{
				try
					{
					Message msg = (Message)is.readObject();
					System.out.println("Got message");
					thread.addIncomingMessage(clientID, msg);
					}
				catch (ClassNotFoundException e)
					{
					//One should maybe tell client that the version is too old
					e.printStackTrace();
					}
				}

			}
		catch (IOException e)
			{
			e.printStackTrace();
			connectionDied();
			}
		catch (ClassNotFoundException e)
			{
			//This one is impossible to recover from
			e.printStackTrace();
			connectionDied();
			}
		}

	/**
	 * Handle broken connections
	 */
	private void connectionDied()
		{
		if(!stopThread)
			{
			stopThread=true;
			thread.disconnectClient(clientID);
			}
		}
	
	
	}
