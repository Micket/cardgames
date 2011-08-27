package clientData;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import serverData.CardStack;

import clientQT.QtGraphicsData;

public class ClientGameData
	{
	/**
	 * Heaps for each player. Common heap is -1. This ID is reserved, all user IDs are positive
	 */
	public Map<Integer, ClientPlayerData> playerMap=new HashMap<Integer, ClientPlayerData>();

	
	
	

	/**
	 * Images should be stored on the client for bandwidth. But it would make sense to allow the server to provide missing cards,
	 * in case a game need a very specialized card - this allows for more stupid clients.
	 * 
	 * Interface problem: Now this depends on QT
	 */
	public QtGraphicsData getImage(String image)
		{
		File fileSVG=new File("images",image+".svg");
		if(fileSVG.exists())
			return new QtGraphicsData(fileSVG);
		else
			{
			File filePNG=new File("images",image+".png");
			if(filePNG.exists())
				return new QtGraphicsData(filePNG);
			}
		throw new RuntimeException("No such image, "+image); //Later: Request image from server
		}
	
	

	/**
	 * Update the position of cards within stacks
	 */
	public void updateCardLinksToStacks()
		{
		for(int playerID:playerMap.keySet())
			{
			ClientPlayerData pdata=playerMap.get(playerID);
			
			for(String stackName:pdata.stackMap.keySet())
				{
				CardStack<ClientCard> s=pdata.stackMap.get(stackName);
				for(int i=0;i<s.cards.size();i++)
					{
					ClientCard cc=s.cards.get(i);
					cc.stackName=stackName;
					cc.stackPos=i;
					cc.cardPlayer=playerID;
					}
				}
			}
		}
	
	
	}
