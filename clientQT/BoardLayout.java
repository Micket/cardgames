package clientQT;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import serverData.CardStack;

import clientData.ClientCard;
import clientData.ClientGameData;
import clientData.ClientPlayerData;

public class BoardLayout
	{
	boolean first=true;
	
	
	
	
	public void doLayout(BoardView view, Client client)
		{
		ClientGameData gamedata=view.gameData;
	
		if(first)
			{
			ClientPlayerData pdata=new ClientPlayerData();
			gamedata.playerMap.put(client.getClientID(),pdata);

			CardStack<ClientCard> onestack=new CardStack<ClientCard>();
			pdata.stackMap.put("os", onestack);

			for(int i=1;i<=10;i++)
				{
				ClientCard cdata=new ClientCard();
				cdata.front="poker Spades "+i;
				cdata.back="poker back";
				pdata.stackMap.get("os").addCard(cdata);
				}
			first=false;
			
			System.out.println("---------here!");
			}

		
		//TODO also for common area!
		
		//Check if this player has data. Otherwise it is a spectator
		ClientPlayerData pdata=gamedata.playerMap.get(client.getClientID());
		if(pdata!=null)
			{

			System.out.println("---------has player data");
			Map<ClientCard,AnimatedCard> mapCC_AC=new HashMap<ClientCard, AnimatedCard>();
			for(AnimatedCard ac:view.cards)
				mapCC_AC.put(ac.cardData, ac);
			
			
			//Step 1: if a card does not have an animated card, then just create it in the right location
			for(String stackName:pdata.stackMap.keySet())
				{
				CardStack<ClientCard> onestack=pdata.stackMap.get(stackName);
				
				for(int i=0;i<onestack.size();i++)
					{
					ClientCard cc=onestack.getCard(i);
					AnimatedCard ac=mapCC_AC.get(cc);
					if(ac==null)
						{
						System.out.println("card !! "+i);
						
						
						AnimatedCard c=new AnimatedCard(cc);
						//c.loadImageFront("cards/spades"+i+".png");
						//c.loadImage("cards/spades"+i+".svg");
						c.posX=40+i*20;
						c.posY=40+i*20;
						c.posZ=10-i;
						
						view.cards.add(c);
						mapCC_AC.put(cc, ac);
						
						}
					}
				
				
				}
			
			//Step 2: if a card is not in the right location, then animate it moving there
			
			}
	
		/*
		view.cards.clear();
		for(int i=1;i<=10;i++)
			{
			ClientCard cdata=new ClientCard();
			cdata.front="poker Spades "+i;
			cdata.back="poker back";
			
			AnimatedCard c=new AnimatedCard(cdata);
			//c.loadImageFront("cards/spades"+i+".png");
			//c.loadImage("cards/spades"+i+".svg");
			c.posX=40+i*20;
			c.posY=40+i*20;
			c.posZ=10-i;
			view.cards.add(c);
			}
*/
		
		}
	
	
	}
