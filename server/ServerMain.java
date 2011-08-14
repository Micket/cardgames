package server;

import util.CardGameInfo;

/**
 * Stand-alone server
 * @author mahogny
 *
 */
public class ServerMain
	{
	public static void main(String[] args)
		{
		ServerThread server=new ServerThread();
		
		server.openPort(CardGameInfo.defaultServerPort);
		}
	}
