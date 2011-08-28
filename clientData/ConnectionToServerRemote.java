package clientData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import action.Message;

/**
 * Connection to a server over TCP/IP
 * 
 * @author mahogny
 *
 */
public class ConnectionToServerRemote extends Thread implements ConnectionToServer 
	{
	public Client client;
	
	private Socket socket; 	
	private ObjectInputStream is;
	private ObjectOutputStream os;

	private int thisClientID;
	
	public int protocolVersionMajor;
	public int protocolVersionMinor;
	

	
	public ConnectionToServerRemote(Client client, InetAddress address, int port) throws IOException
		{
		this.client=client;
		
		socket=new Socket(address, port);
		os=new ObjectOutputStream(socket.getOutputStream());
		is=new ObjectInputStream(socket.getInputStream());
		
		//Verify that this is the right protocol
		char[] checkString="Cardgame\n".toCharArray();
		for(int i=0;i<checkString.length;i++)
			{
			char c=is.readChar();
			if(c!=checkString[i])
				throw new IOException("This is not a cardgame port");
			}
		
		//Get the version of the protocol
		protocolVersionMajor=is.readInt();
		protocolVersionMinor=is.readInt();
		
		//Get the client ID. It stays fixed over a session
		thisClientID=is.readInt();

		//Tell server our preferred nick. We will get the actual nick later from the user list
		os.writeObject(client.tryToGetNick);

		//TODO handle timeouts
		
		System.out.println("Connected");

		
		
		
		}
	
	public void send(Message msg)
		{
		try
			{
			os.writeObject(msg);
			}
		catch (IOException e)
			{
			e.printStackTrace();
			connectionFailed();
			}
		}
	
	private void connectionFailed()
		{
		if(!stopThread)
			{
			stopThread=true;
			// TODO Auto-generated method stub
			
			
			}
		}

	@Override
	public int getCliendID()
		{
		return thisClientID;
		}


	private boolean stopThread=false;
	

	@Override
	public void run()
		{
		try
			{
			while(!stopThread)
				{
				try
					{
					Message msg=(Message)is.readObject();
					System.out.println("Got message");
					client.gotMessageFromServer(msg);
					}
				catch (ClassNotFoundException e)
					{
					client.gotBadMessageFromServer(e);
					}
				}
			}
		catch (IOException e)
			{
			e.printStackTrace();
			connectionFailed();
			}
		}
	
	public void tearDownConnection()
		{
		stopThread=true;
		try
			{
			socket.close();
			}
		catch (IOException e)
			{
			}
		}
	
	}
