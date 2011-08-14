package clientData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import action.Message;

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
		System.out.println("This is a cardgame server!");
		
		//Get the version of the protocol
		protocolVersionMajor=is.readInt();
		protocolVersionMinor=is.readInt();
		
		//Get the client ID. It stays fixed over a session
		thisClientID=is.readInt();

		//Tell server our preferred nick. We will get the actual nick later from the user list
		os.writeObject("Iwannabeme");
		
		
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}
	
	@Override
	public int getCliendID()
		{
		return thisClientID;
		}


	

	@Override
	public void run()
		{
		
		for(;;)
			{
			
			try
				{
				Message msg=(Message)is.readObject();
				System.out.println("Got message");

				client.gotMessageFromServer(msg);
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
			}
		
		//socket.
		
		
		}
	
	
	
	}
