package clientQT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Vector2d;

import action.UserActionDragCard;
import action.UserActionGameCardUpdate;
import action.UserActionGameStateUpdate;

import serverData.CardStack;
import serverData.CardStack.StackStyle;
import util.Matrix2d;

import clientData.Client;
import clientData.ClientCard;
import clientData.ClientGameData;
import clientData.ClientPlayerData;
import clientData.GameDesign;
import clientData.GameDesign.StackDef;


/**
 * Control of the position of cards
 *
 */
public class BoardLayout
	{
	private Map<ClientCard,AnimatedCard> mapCC_AC=new HashMap<ClientCard, AnimatedCard>();
	private boolean needRedraw;
	
	private BoardView view;
	
	private GameDesign design=new GameDesign(); 
	private Client client;
	
	public BoardLayout(BoardView view, Client client)
		{
		this.view=view;
		this.client=client;
		}
	
	/**
	 * Update the card placement
	 * 
	 * @return True if scene has to be redrawn
	 */
	public boolean doLayout()
		{
		needRedraw=false;
		mapCC_AC.clear();
		
		ClientGameData gamedata=view.gameData;

		ArrayList<Integer> playersList=new ArrayList<Integer>(gamedata.playerMap.keySet());
		int indexOfMe=playersList.indexOf(client.getClientID());
		//TODO what if this user not in list?
		if(indexOfMe==-1)
			indexOfMe=0;
		
		//For each player
		for(int playerID:gamedata.playerMap.keySet())
			{
			//Here, create a transform for this players coordinate system
			Matrix2d transformRot=new Matrix2d();
			
			int posOfThisPlayer=playersList.indexOf(playerID)-indexOfMe;
			
			double baseRotAngle=posOfThisPlayer*(Math.PI*2/playersList.size());
			transformRot.setRot(baseRotAngle);
			Vector2d transformMove=new Vector2d(0,100/view.zoom);
			
			ClientPlayerData pdata=gamedata.playerMap.get(playerID);

			Vector2d midPos=new Vector2d(400.0/2/view.zoom,400.0/2/view.zoom);

			layoutForOnePlayer(view, client, playerID, baseRotAngle, transformRot, transformMove, midPos, pdata);
			}

		//Common area
		ClientPlayerData pdata=gamedata.playerMap.get(-1);
		if(pdata!=null)
			{
			Matrix2d transformRot=new Matrix2d();
			double baseRotAngle=0;
			transformRot.setRot(baseRotAngle);
			Vector2d transformMove=new Vector2d(0,100/view.zoom);
			Vector2d midPos=new Vector2d(400.0/2/view.zoom,400.0/2/view.zoom);
			layoutForOnePlayer(view, client, -1, baseRotAngle, transformRot, transformMove, midPos, pdata);
			}
		
		return needRedraw;
		}
	
	
	private void layoutForOnePlayer(BoardView view, Client client, int playerID, 
			double baseRotAngle, Matrix2d transformRot, Vector2d transformMoveOrig, Vector2d midPos, 
			ClientPlayerData pdata)
		{

		//Create a reversible map of the cards
		for(AnimatedCard ac:view.cards)
			mapCC_AC.put(ac.cardData, ac);

		//For each stack
		for(String stackName:pdata.stackMap.keySet())
			{
			CardStack<ClientCard> onestack=pdata.stackMap.get(stackName);

			Vector2d transformMove=new Vector2d(transformMoveOrig);
			StackDef stackDef;
			
			if(playerID>=0)
				stackDef=design.playerField.stacks.get(stackName);
			else
				stackDef=design.commonField.stacks.get(stackName);
			if(stackDef!=null)
				{
				transformMove.x+=stackDef.x;
				transformMove.y+=stackDef.y;
				}
			else
				throw new RuntimeException("Error: There is no stackdef for "+stackName+
						", there is "+design.playerField.stacks.keySet()+" and "+design.commonField.stacks.keySet());

			//Layout a normal stack
			if(onestack.stackStyle==StackStyle.Deck)
				{
				//Place position beneath
				Vector2d ePos=new Vector2d(transformMove);
				transformRot.transform(ePos);
				ePos.add(midPos); //To center rotation around midpos
//				view.emptyPosList.add(new EmptyPos(ePos.x, ePos.y, baseRotAngle));


				view.setEmptyPos(playerID, stackName, ePos, baseRotAngle);

				
				
				/*double cardDistance=10.0/Math.log(onestack.size()+2);
				if(cardDistance>10)
					cardDistance=10;*/
				double cardDistance=2;
				
				//Place cards
				for(int i=0;i<onestack.size();i++)
					{
					ClientCard cc=onestack.getCard(i);

					//Vector2d cardPos=new Vector2d(-i*10, -i*10);
					//cardPos.add(transformMove);
					Vector2d cardPos=new Vector2d(transformMove);
					transformRot.transform(cardPos);
					cardPos.add(new Vector2d(-i*cardDistance, -i*cardDistance));
					cardPos.add(midPos); //To center rotation around midpos
					
					AnimatedCard ac=new AnimatedCard(cc);
					ac.posX=cardPos.x;
					ac.posY=cardPos.y;
					ac.posZ=i;
					ac.rotation=baseRotAngle;

					placeAnimateCards(ac, cc);
					}
				}
			else if(onestack.stackStyle==StackStyle.Hand)
				{
				
				//Place position beneath
				/*
				Vector2d ePos=new Vector2d(transformMove);
				transformRot.transform(ePos);
				ePos.add(midPos); //To center rotation around midpos
				view.emptyPosList.add(new EmptyPos(ePos.x, ePos.y, baseRotAngle));
				 */
				//Place cards
				for(int i=0;i<onestack.size();i++)
					{
					ClientCard cc=onestack.getCard(i);

					//Vector2d cardPos=new Vector2d(-i*10, -i*10);
					//cardPos.add(transformMove);
					Vector2d cardPos=new Vector2d(transformMove);
					cardPos.add(new Vector2d(i*80, 0));
					transformRot.transform(cardPos);
					cardPos.add(midPos); //To center rotation around midpos
					
					AnimatedCard ac=new AnimatedCard(cc);
					ac.posX=cardPos.x;
					ac.posY=cardPos.y;
					ac.posZ=i;
					ac.rotation=baseRotAngle;

					placeAnimateCards(ac, cc);
					}
				
				}
			else if(onestack.stackStyle==StackStyle.Solitaire)
				{
				
				//Place position beneath
				Vector2d ePos=new Vector2d(transformMove);
				transformRot.transform(ePos);
				ePos.add(midPos); //To center rotation around midpos
				view.setEmptyPos(playerID, stackName, ePos, baseRotAngle);
				 
				//Place cards
				for(int i=0;i<onestack.size();i++)
					{
					ClientCard cc=onestack.getCard(i);

					Vector2d cardPos=new Vector2d(transformMove);
					cardPos.add(new Vector2d(0, i*30));
					transformRot.transform(cardPos);
					cardPos.add(midPos); //To center rotation around midpos
					
					AnimatedCard ac=new AnimatedCard(cc);
					ac.posX=cardPos.x;
					ac.posY=cardPos.y;
					ac.posZ=i;
					ac.rotation=baseRotAngle;

					placeAnimateCards(ac, cc);
					}
				
				}


			}

		
		}
	
	
	
	public void placeAnimateCards(AnimatedCard newAc, ClientCard cc)
		{
		AnimatedCard ac=mapCC_AC.get(cc);
		if(ac==null)
			{
			//If a card does not have an animated card, then just create it in the right location
			view.cards.add(newAc);
			mapCC_AC.put(cc, newAc);
			needRedraw=true;
			}
		else
			{
			//If a card is not in the right location, then animate it moving there
			if(!ac.isBeingDragged && (ac.posX!=newAc.posX || ac.posY!=newAc.posY || ac.posZ!=newAc.posZ || ac.rotation!=newAc.rotation))
				{
				double mvx=ac.posX-newAc.posX;
				double mvy=ac.posY-newAc.posY;

				ac.posX-=mvx*0.2;
				ac.posY-=mvy*0.2;

				//When card is close enough, make sure it is exactly in the right position so updates can stop
				double stopRadius=3;
				if(Math.abs(ac.posX-newAc.posX)<stopRadius && Math.abs(ac.posY-newAc.posY)<stopRadius)
					{
					ac.posX=newAc.posX;
					ac.posY=newAc.posY;
					}

				ac.posZ=newAc.posZ;


				
				needRedraw=true;
				}

			//Rotation around z
			if(ac.rotation!=newAc.rotation)
				{
				//There can be any reference angle,
				//so set current rotation to closest equivalent angle
				while(newAc.rotation-ac.rotation>Math.PI)
					newAc.rotation+=2*Math.PI;
				while(ac.rotation-newAc.rotation>Math.PI)
					newAc.rotation+=2*Math.PI;
				
				//Converge
				double dr=newAc.rotation-ac.rotation;
				if(Math.abs(dr)<Math.PI*0.02)
					ac.rotation=newAc.rotation;
				else
					ac.rotation+=dr*0.2;
				}
			
			
			//Rotation around y
			double shouldBeRotY=ac.shouldHaveRotation();
			if(ac.rotY!=shouldBeRotY)
				{
				//Simple handling since only 0 & PI is considered
				double dr=(shouldBeRotY-ac.rotY)*0.2;
				if(Math.abs(ac.rotY-shouldBeRotY)<Math.PI*0.02)
					ac.rotY=shouldBeRotY;
				else
					ac.rotY+=dr;
				needRedraw=true;
				}

			}

		}



	public void newDesign(GameDesign design)
		{
		this.design=design;
		}

	public void newState(UserActionGameStateUpdate msg)
		{
		ClientGameData gamedata=view.gameData;

		for(int playerID:msg.player.keySet())
			{
			ClientPlayerData pdata=new ClientPlayerData();
			gamedata.playerMap.put(playerID,pdata);

			UserActionGameStateUpdate.PlayerState pstate=msg.player.get(playerID);
			
			for(String stackName:pstate.stacks.keySet())
				{
				/*
				//Get the design of the board area
				GameDesign.StackDef stackDef;
				if(playerID==-1)
					stackDef=design.commonField.stacks.get(stackName);
				else
					stackDef=design.playerField.stacks.get(stackName);
				*/
				
				//Add all the cards

				pdata.stackMap.put(stackName,pstate.stacks.get(stackName));
				}
			
			}
		
		gamedata.updateCardLinksToStacks();
		}

	
	
	
	public void dragCard(UserActionDragCard action)
		{
		ClientGameData gamedata=view.gameData;
		
		ClientPlayerData pdataFrom=gamedata.playerMap.get(action.fromPlayer);
		ClientPlayerData pdataTo=gamedata.playerMap.get(action.toPlayer);
		
		CardStack<ClientCard> stackFrom=pdataFrom.stackMap.get(action.fromStackName);
		CardStack<ClientCard> stackTo=pdataTo.stackMap.get(action.toStackName);
		
		if(stackFrom.stackStyle==StackStyle.Solitaire && stackTo.stackStyle==StackStyle.Solitaire)
			{
			//This code is simplified
			int numCardToMove=stackFrom.cards.size()-action.fromPos;
			for(int i=0;i<numCardToMove;i++)
				{
				ClientCard theCard=stackFrom.cards.remove(action.fromPos);
				stackTo.cards.add(theCard);
				}
			}
		else
			{
			//If it is the same stack then one has to be careful with indexing
			int fromPos=action.fromPos;
			int toPos=action.toPos;
			if(stackFrom==stackTo)
				{
				if(toPos>fromPos)
					toPos--;
				}
			ClientCard theCard=stackFrom.cards.remove(fromPos);
			stackTo.cards.add(toPos, theCard);
			}
		
		
		gamedata.updateCardLinksToStacks();
		}

	public void cardUpdate(UserActionGameCardUpdate action)
		{
		ClientGameData gamedata=view.gameData;
		action.updateStack(gamedata.playerMap.get(action.playerID).stackMap.get(action.stackName));
		}
	
	
	

	
	
	}
