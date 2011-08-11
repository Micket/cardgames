package clientData;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import action.Message;

public class ConnectionToServerRemote extends ConnectionToServer
	{
	private Socket socket; 
	
	private ObjectOutputStream os;
	
	public ConnectionToServerRemote(InetAddress address, int port) throws IOException
		{
		socket=new Socket(address, port);
		os=new ObjectOutputStream(socket.getOutputStream());
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
	}
