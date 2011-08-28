package clientData;

import java.util.HashMap;
import java.util.Map;

import serverData.CardStack;


public class ClientGameData
	{
	/**
	 * Heaps for each player. Common heap is -1. This ID is reserved, all user IDs are positive
	 */
	public Map<Integer, ClientPlayerData> playerMap=new HashMap<Integer, ClientPlayerData>();

	
	

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
