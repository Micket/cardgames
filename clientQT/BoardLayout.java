package clientQT;

import java.util.HashMap;
import java.util.Map;

import com.trolltech.qt.core.QPoint;

import serverData.CardStack;

import clientData.Client;
import clientData.ClientCard;
import clientData.ClientGameData;
import clientData.ClientPlayerData;


/**
 * Control of the position of cards
 *
 */
public class BoardLayout
	{
	boolean first=true;
	
	
	
	/**
	 * Update the card placement
	 * 
	 * @return True if scene has to be redrawn
	 */
	public boolean doLayout(BoardView view, Client client)
		{
		boolean needRedraw=false;
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
			
			System.out.println("---------layout1!");
			}

		
		//TODO also for common area!
		
		//Check if this player has data. Otherwise it is a spectator
		ClientPlayerData pdata=gamedata.playerMap.get(client.getClientID());
		if(pdata!=null)
			{
			//Create a reversible map of the cards
			Map<ClientCard,AnimatedCard> mapCC_AC=new HashMap<ClientCard, AnimatedCard>();
			for(AnimatedCard ac:view.cards)
				mapCC_AC.put(ac.cardData, ac);
			
			view.emptyPosList.clear();
			
			//For each stack
			for(String stackName:pdata.stackMap.keySet())
				{
				CardStack<ClientCard> onestack=pdata.stackMap.get(stackName);
				
				//Place position beneath
				view.emptyPosList.add(new QPoint(0,0));
				
				//Place cards
				for(int i=0;i<onestack.size();i++)
					{
					ClientCard cc=onestack.getCard(i);
					AnimatedCard ac=mapCC_AC.get(cc);

					double shouldBeX=i*20;
					double shouldBeY=i*20;
//					double shouldBeX=40+i*20;
	//				double shouldBeY=40+i*20;

					//If a card does not have an animated card, then just create it in the right location
					if(ac==null)
						{
						System.out.println("creating card "+i);
						
						ac=new AnimatedCard(cc);
						ac.posX=shouldBeX;
						ac.posY=shouldBeY;
						ac.posZ=10-i;
						
						view.cards.add(ac);
						mapCC_AC.put(cc, ac);
						needRedraw=true;
						}
					else
						{
						//If a card is not in the right location, then animate it moving there

						if(!ac.isBeingDragged && (ac.posX!=shouldBeX || ac.posY!=shouldBeY))
							{
							//System.out.println("out of pos");
							
							double mvx=ac.posX-shouldBeX;
							double mvy=ac.posY-shouldBeY;
							
							//System.out.println(mvx+"  "+mvy);
//							if(mvx>5)
//								mvx=5;
							
							ac.posX-=mvx*0.2;
							ac.posY-=mvy*0.2;

							//When card is close enough, make sure it is exactly in the right position so updates can stop
							double stopRadius=3;
							if(Math.abs(ac.posX-shouldBeX)<stopRadius && Math.abs(ac.posY-shouldBeY)<stopRadius)
								{
								ac.posX=shouldBeX;
								ac.posY=shouldBeY;
								}

							needRedraw=true;
							}
						
						}
					}
				
				
				}
			
			
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
		
		return needRedraw;
		}
	
	
	}
