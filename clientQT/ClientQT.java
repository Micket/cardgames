package clientQT;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import server.ServerThread;

import com.trolltech.qt.gui.QApplication;

import clientData.Client;

/**
 * Client with QT-based GUI
 * 
 * @author mahogny
 *
 */
public class ClientQT 
	{

	

	/**
	 * Entry point
	 */
	public static void main(String args[])
		{
		QApplication.initialize(args);
		
		Client client=new Client();
		client.createServer();
		
		BoardWindow boardWindow = new BoardWindow(client);
		client.serverListeners.add(boardWindow); //TODO on close, unregister
		boardWindow.show();
		
		LobbyWindow lobbyWindow = new LobbyWindow(client);
		client.serverListeners.add(lobbyWindow); //TODO on close, unregister
		lobbyWindow.show();

		int ca=0;
		while(ca<args.length)
			{
			if(args[ca].equals("--connect"))
				{
				//Connect to a server
				ca++;
				String url=args[ca];
				int port=ServerThread.defaultServerPort;
				int colon=url.lastIndexOf(":");
				if(colon!=-1)
					{
					port=Integer.parseInt(url.substring(colon+1));
					url=url.substring(0, colon);
					}
				
				try
					{
					InetAddress addr=InetAddress.getByName(url);
					client.connectToServer(addr, port);
					}
				catch (UnknownHostException e)
					{
					System.out.println("Unknown host "+url);
					System.exit(1);
					}
				catch (IOException e)
					{
					e.printStackTrace();
					System.exit(1);
					}
				}
			ca++;
			}
		
		QApplication.exec();
		}
	}
