package games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import serverData.CardStack;
import serverData.CardStack.StackStyle;
import serverData.PlayingCard;
import serverData.PlayingCardUtil; // Common things for playing cards here?

import clientData.ClientCard;
import clientData.GameDesign;

import action.Message;
import action.UserActionClickedCard;
import action.UserActionDragCard;
import action.UserActionGameCardUpdate;
import action.UserActionGameStateUpdate;
import action.UserActionGameStateUpdate.PlayerState;

/**
 * Classic solitaire
 * @author mahogny
 */
@GameTypePlugin(
		description = "Classic solitaire, a game for one person",
		maxplayers = 1,
		minplayers = 1,
		name = "Solitaire",
		category = "None",
		instructions = "Play to win etc." // Good idea to have some rich text player instructions here?
		)
public class Solitaire extends DefaultGameLogic
	{
	private Map<Integer, LogicPlayerState> pstate=new HashMap<Integer, LogicPlayerState>();
	private int numSolitaireHeap=7;

	private PlayingCard.Rank[] ordering=new PlayingCard.Rank[]{
			PlayingCard.Rank.Ace, 
			PlayingCard.Rank.Deuce,
			PlayingCard.Rank.Three,
			PlayingCard.Rank.Four,
			PlayingCard.Rank.Five,
			PlayingCard.Rank.Six,
			PlayingCard.Rank.Seven,
			PlayingCard.Rank.Eight,
			PlayingCard.Rank.Nine,
			PlayingCard.Rank.Ten,
			PlayingCard.Rank.Jack,
			PlayingCard.Rank.Queen,
			PlayingCard.Rank.King,
	};

	private class LogicPlayerState
		{
		public ArrayList<CardStack<PlayingCard>> stacksForHand = new ArrayList<CardStack<PlayingCard>>();
		public ArrayList<CardStack<PlayingCard>> stacksForSorted = new ArrayList<CardStack<PlayingCard>>();
		public CardStack<PlayingCard> deckNew = new CardStack<PlayingCard>();
		public CardStack<PlayingCard> deckCurrent= new CardStack<PlayingCard>();

		
		public CardStack<PlayingCard> getStack(String stackName)
			{
			if(stackName.equals("decknew"))
				return deckNew;
			else if(stackName.equals("deckcurrent"))
				return deckCurrent;
			else if(stackName.startsWith("solitaire"))
				return stacksForHand.get(Integer.parseInt(stackName.substring("solitaire".length())));
			else if(stackName.startsWith("sorted"))
				return stacksForSorted.get(Integer.parseInt(stackName.substring("sorted".length())));
			else
				throw new RuntimeException("no such stack: "+stackName);
			}

		/*
		public CardStack<PlayingCard> getStackHand(String name)
			{
			if(name.startsWith(""))
			return stacksForHand.get(Integer.parseInt(name.substring("solitaire".length())));
			}
		public CardStack<PlayingCard> getStackSorted(String name)
			{
			return stacksForSorted.get(Integer.parseInt(name.substring("sorted".length())));
			}*/
		
		public LogicPlayerState()
			{
			for(int i=0;i<numSolitaireHeap;i++)
				stacksForHand.add(new CardStack<PlayingCard>());
			for(int i=0;i<4;i++)
				stacksForSorted.add(new CardStack<PlayingCard>());
			}
		
		}
	
	
	public void startGame()
		{
		
		
		
		gameOn = true;
		// Send information on layout and cardstacks.
		}
	
	public boolean userJoined(int userID)
		{
		LogicPlayerState s=new LogicPlayerState();
		pstate.put(userID, s);

		//Set style of decks
		s.deckNew.stackStyle=StackStyle.Deck;
		for(CardStack<PlayingCard> stack:s.stacksForHand)
			stack.stackStyle=StackStyle.Solitaire;
		for(CardStack<PlayingCard> stack:s.stacksForSorted)
			stack.stackStyle=StackStyle.Deck;
			
		//Distribute cards
		s.deckNew.addCards(PlayingCardUtil.getDeck52());
		for(PlayingCard c:s.deckNew.cards)
			c.showsFront=false;
		s.deckNew.shuffle();
		for(int i=0;i<7;i++)
			{
			CardStack<PlayingCard> stack=s.stacksForHand.get(i);
			for(int j=0;j<i+1;j++)
				stack.addCard(s.deckNew.drawCard());
			stack.getCard(stack.size()-1).showsFront=true;
			}

		s.deckNew.cards.get(s.deckNew.cards.size()-1).showsFront=true;
		
//////////////		
		if (!super.userJoined(userID))
			return false;
		return true;
		}
	
	public boolean userActionClickedCard(int fromUser, UserActionClickedCard s)
		{		
		LogicPlayerState ps=pstate.get(fromUser);
		CardStack<PlayingCard> fromStack=ps.getStack(s.stack);
		if(s.stack.equals("decknew") && s.player==fromUser)
			{
			//Cycle current cards
			if(ps.deckNew.size()!=0)
				{
				Message msg=new Message();
				
				//Move away the old current card
				if(ps.deckCurrent.size()!=0)
					{
					UserActionDragCard action=new UserActionDragCard();
					action.gameID=sessionID;
					
					action.fromPlayer=s.player;
					action.fromPos=0;
					action.fromStackName="deckcurrent";

					action.toPlayer=s.player;
					action.toPos=0;
					action.toStackName="decknew";
			
					executeMove(action);
					msg.add(action);
					}
				
				UserActionDragCard action=new UserActionDragCard();
				action.gameID=sessionID;
				
				action.fromPlayer=s.player;
				action.fromPos=ps.deckNew.size()-1;
				action.fromStackName="decknew";

				action.toPlayer=s.player;
				action.toPos=ps.deckCurrent.size();
				action.toStackName="deckcurrent";
		
				executeMove(action);
				msg.add(action);
				thread.send(fromUser, msg);
				return true;
				}
			
			}
		else if(!fromStack.getCard(s.stackPos).showsFront)
			{
			//Turn up card if facing down
			
			if(s.stackPos==fromStack.size()-1)
				{
				fromStack.getCard(s.stackPos).showsFront=true;
				thread.send(fromUser, new Message(getUpdateCardForClient(fromUser, s.stack, s.stackPos)));
				
				System.out.println("----here");
				}
			}
		else
			//if(s.stack.startsWith("solitaire"))
			{
			//Try to automatically put card on any sorted heap
			if(s.stackPos==fromStack.size()-1)
				for(int i=0;i<4;i++)
					if(canPutOnSorted(ps.stacksForSorted.get(i), fromStack.getCard(s.stackPos)))
						{
						UserActionDragCard action=new UserActionDragCard();
						
						action.fromPlayer=fromUser;
						action.fromStackName=s.stack;
						action.fromPos=s.stackPos;
						
						action.toPlayer=fromUser;
						action.toStackName="sorted"+i;
						action.toPos=ps.stacksForSorted.get(i).size();
						
						executeMove(action);
						thread.send(fromUser, new Message(action));
						return true;
						}
			}
		return false;
		}
	
	
	private UserActionGameCardUpdate getUpdateCardForClient(int playerID, String stackName, int stackPos)
		{
		LogicPlayerState ps=pstate.get(playerID);
		return new UserActionGameCardUpdate(
				sessionID, 
				playerID, stackName, stackPos,
				ps.getStack(stackName).getCard(stackPos).toClientCard());
		}

	private boolean canPutOnHand(CardStack<PlayingCard> stack, PlayingCard newc)
		{
		PlayingCard topCard=stack.getTopCard();
		if(topCard==null)
			return true;
		else
			for(int i=0;i<ordering.length-1;i++)
				if(newc.getRank()==ordering[i])
					return topCard.getRank()==ordering[i+1];
		throw new RuntimeException("Should never reach this line");
		}


	private boolean canPutOnSorted(CardStack<PlayingCard> stack, PlayingCard newc)
		{
		PlayingCard topCard=stack.getTopCard();
		if(topCard==null)
			return newc.getRank()==PlayingCard.Rank.Ace;
		else if(topCard.getSuit()==newc.getSuit())
			{
			for(int i=0;i<ordering.length-1;i++)
				if(topCard.getRank()==ordering[i])
					return newc.getRank()==ordering[i+1];
			throw new RuntimeException("Should never reach this line");
			}
		else
			return false;
		}

	
	
	public boolean userActionDragCard(int fromUser, UserActionDragCard s)
		{
		LogicPlayerState ps=pstate.get(fromUser);
		
		if(s.toStackName.startsWith("solitaire"))
			{
			CardStack<PlayingCard> fromStack=ps.getStack(s.fromStackName);
			
			if(s.fromPos==fromStack.size()-1 && canPutOnHand(ps.getStack(s.toStackName), fromStack.getCard(s.fromPos)))
				{
				executeMove(s);
				thread.send(fromUser, new Message(s));
				return true;
				}
			
			}
		else if(s.toStackName.startsWith("sorted"))
			{
			CardStack<PlayingCard> fromStack=ps.getStack(s.fromStackName);
			
			if(s.fromPos==fromStack.size()-1 && canPutOnSorted(ps.getStack(s.toStackName), fromStack.getCard(s.fromPos)))
				{
				executeMove(s);
				thread.send(fromUser, new Message(s));
				return true;
				}
			
			}
		return false;
		}
	
	public CardStack<PlayingCard> getStack(int player, String stackName)
		{
		return pstate.get(player).getStack(stackName);
		}
	
	public void executeMove(UserActionDragCard action)
		{
		CardStack<PlayingCard> stackFrom=getStack(action.fromPlayer, action.fromStackName);
		CardStack<PlayingCard> stackTo=getStack(action.toPlayer, action.toStackName);
		
		//If it is the same stack then one has to be careful with indexing
		int fromPos=action.fromPos;
		int toPos=action.toPos;
		if(stackFrom==stackTo)
			{
			if(toPos>fromPos)
				toPos--;
			}
		
		PlayingCard theCard=stackFrom.cards.remove(fromPos);
		stackTo.cards.add(toPos, theCard);
		}
	
	
	
	public boolean userLeft(int userID)
		{
		if (!super.userLeft(userID))
			return false;
		return true;
		}
	
	public int getNumParticipatingPlayers()
		{
		return players.size();
		}
	
	public GameDesign createGameDesign()
		{
		double cardDistX=200;
		
		GameDesign d=new GameDesign();
		GameDesign.StackDef defDeckNew=d.playerField.createStack("decknew");
		defDeckNew.stack=new CardStack<Object>();
		defDeckNew.y=-300;
		defDeckNew.x=-400+0*cardDistX;

		
		GameDesign.StackDef defDeckCurrent=d.playerField.createStack("deckcurrent");
		defDeckCurrent.stack=new CardStack<Object>();
		defDeckCurrent.y=-300;
		defDeckCurrent.x=-400+1*cardDistX;

		
		
		for(int i=0;i<numSolitaireHeap;i++)
			{
			GameDesign.StackDef defDeck=d.playerField.createStack("solitaire"+i);
			defDeck.stack=new CardStack<Object>();
			defDeck.stack.stackStyle=StackStyle.Solitaire;
			defDeck.y=-0;
			defDeck.x=-400+i*cardDistX;
			}

		for(int i=0;i<4;i++)
			{
			GameDesign.StackDef defDeck=d.playerField.createStack("sorted"+i);
			defDeck.stack=new CardStack<Object>();
			defDeck.stack.stackStyle=StackStyle.Solitaire;
			defDeck.y=-300;
			defDeck.x=-400+(i+3)*cardDistX;
			}

		
		return d;
		}
	
	public void getGameState(UserActionGameStateUpdate action)
		{
		for(int p:players)
			{
			LogicPlayerState ds=pstate.get(p);
			PlayerState ps=action.createPlayer(p);
			
			CardStack<ClientCard> deckNew=CardStack.toClientCardStack(ds.deckNew);
			ps.stacks.put("decknew", deckNew);

			CardStack<ClientCard> deckCurrent=CardStack.toClientCardStack(ds.deckCurrent);
			ps.stacks.put("deckcurrent", deckCurrent);

			for(int i=0;i<ds.stacksForHand.size();i++)
				{
				CardStack<ClientCard> stack=CardStack.toClientCardStack(ds.stacksForHand.get(i));
				ps.stacks.put("solitaire"+i, stack);
				}
			
			for(int i=0;i<4;i++)
				{
				CardStack<ClientCard> stack=CardStack.toClientCardStack(ds.stacksForSorted.get(i));
				ps.stacks.put("sorted"+i, stack);
				}

			}
		}


	}
