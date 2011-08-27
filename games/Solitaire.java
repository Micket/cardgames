package games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import serverData.CardStack;
import serverData.CardStack.StackStyle;
import serverData.PlayingCard;
import serverData.PlayingCardUtil; // Common things for playing cards here?

import clientData.ClientCard;
import clientData.ClientPlayerData;
import clientData.GameDesign;

import action.Message;
import action.UserActionClickedCard;
import action.UserActionDragCard;
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
	private int numSolitaireHeap=8;
	
	private class LogicPlayerState
		{
		public ArrayList<CardStack<PlayingCard>> stacksForHand = new ArrayList<CardStack<PlayingCard>>();
		public ArrayList<CardStack<PlayingCard>> stacksForSorted = new ArrayList<CardStack<PlayingCard>>();
		public CardStack<PlayingCard> deckNew = new CardStack<PlayingCard>();

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
		
		s.deckNew.addCards(PlayingCardUtil.getDeck52());
		s.deckNew.shuffle();
		s.deckNew.stackStyle=StackStyle.Deck;

		for(CardStack<PlayingCard> stack:s.stacksForHand)
			{
			stack.stackStyle=StackStyle.Solitaire;
			stack.addCard(new PlayingCard(PlayingCard.Suit.Diamonds, PlayingCard.Rank.Eight));
			}

		for(CardStack<PlayingCard> stack:s.stacksForSorted)
			{
			stack.stackStyle=StackStyle.Deck;
			stack.addCard(new PlayingCard(PlayingCard.Suit.Diamonds, PlayingCard.Rank.Eight));
			}

		for(PlayingCard c:s.deckNew.cards)
			c.showsFront=false;
		s.deckNew.cards.get(s.deckNew.cards.size()-1).showsFront=true;
		
//////////////		
		if (!super.userJoined(userID))
			return false;
		return true;
		}
	
	public boolean userActionClickedCard(int fromUser, UserActionClickedCard s)
		{
		/*
		if (s.stack.equals("hand") && s.player==fromUser)
			{
			LogicPlayerState ps=pstate.get(fromUser);
			
			UserActionDragCard action=new UserActionDragCard();
			action.gameID=sessionID;
			
			action.fromPlayer=s.player;
			action.fromPos=s.stackPos;
			action.fromStackName="hand";

			action.toPlayer=s.player;
			action.toPos=ps.deck.size();
			action.toStackName="deck";
	
			executeMove(action);
			thread.send(fromUser, new Message(action));
			return true;
			
			}
		else
			*/
			return false;
		}
	
	
	public boolean userActionDragCard(int fromUser, UserActionDragCard s)
		{
		//TODO check if it makes sense
		
		LogicPlayerState ps=pstate.get(fromUser);

		executeMove(s);
		thread.send(fromUser, new Message(s));
		
		return true;
		}
	
	public CardStack<PlayingCard> getStack(int player, String stackName)
		{
		if(stackName.equals("decknew"))
			return pstate.get(player).deckNew;
		else if(stackName.startsWith("solitaire"))
			return pstate.get(player).stacksForHand.get(Integer.parseInt(stackName.substring("solitaire".length())));
		else if(stackName.startsWith("sorted"))
			return pstate.get(player).stacksForSorted.get(Integer.parseInt(stackName.substring("sorted".length())));
		else
			throw new RuntimeException("no such stack: "+stackName);
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
		GameDesign d=new GameDesign();
		GameDesign.StackDef defDeckNew=d.playerField.createStack("decknew");
		defDeckNew.stack=new CardStack<Object>();
		defDeckNew.y=300;
		defDeckNew.x=0;
		
		for(int i=0;i<numSolitaireHeap;i++)
			{
			GameDesign.StackDef defDeck=d.playerField.createStack("solitaire"+i);
			defDeck.stack=new CardStack<Object>();
			defDeck.stack.stackStyle=StackStyle.Solitaire;
			defDeck.y=-300;
			defDeck.x=-700+i*200;
			}

		for(int i=0;i<4;i++)
			{
			GameDesign.StackDef defDeck=d.playerField.createStack("sorted"+i);
			defDeck.stack=new CardStack<Object>();
			defDeck.stack.stackStyle=StackStyle.Solitaire;
			defDeck.y=-0;
			defDeck.x=-700+i*200;
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
