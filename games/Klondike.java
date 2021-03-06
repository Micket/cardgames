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
import action.GameActionClickedCard;
import action.GameActionDragCard;
import action.GameActionUpdateCard;
import action.GameActionUpdateGameState;
import action.GameActionUpdateGameState.PlayerState;

/**
 * Classic solitaire game
 * @author mahogny
 */
@GameTypePlugin(
		description = "Classic solitaire game",
		maxplayers = 1,
		minplayers = 1,
		name = "Klondike",
		category = "Solitaire",
		instructions = "Play to win etc." // Good idea to have some rich text player instructions here?
		)
public class Klondike extends DefaultGameLogic
	{
	private Map<Integer, LogicPlayerState> pstate=new HashMap<Integer, LogicPlayerState>();
	private int numWorkingHeap=7;
	private int numCardsRevealed=3; ///< Number of cards to reveal at once.
	private int maxPasses=3; ///< Maximum number of times the deck can be turned.

	final private String REVEALEDDECK="deckcurrent";
	final private String DECK="deck";

	private class LogicPlayerState
		{
		public ArrayList<CardStack<PlayingCard>> workingStacks = new ArrayList<CardStack<PlayingCard>>();
		public ArrayList<CardStack<PlayingCard>> sortedStacks = new ArrayList<CardStack<PlayingCard>>();
		public CardStack<PlayingCard> deck = new CardStack<PlayingCard>(); // Top left deck
		public CardStack<PlayingCard> revealedDeck= new CardStack<PlayingCard>(); // Revealed cards
		
		private int numOfPasses=1;

		
		public CardStack<PlayingCard> getStack(String stackName)
			{
			if(stackName.equals(DECK))
				return deck;
			else if(stackName.equals(REVEALEDDECK))
				return revealedDeck;
			else if(isWorkingStack(stackName))
				return workingStacks.get(Integer.parseInt(stackName.substring("working".length())));
			else if(isSortedStack(stackName))
				return sortedStacks.get(Integer.parseInt(stackName.substring("sorted".length())));
			else
				throw new RuntimeException("no such stack: "+stackName);
			}

		public LogicPlayerState()
			{
			for(int i=0;i<numWorkingHeap;i++)
				workingStacks.add(new CardStack<PlayingCard>());
			for(int i=0;i<4;i++)
				sortedStacks.add(new CardStack<PlayingCard>());
			}
		
		}
	
	
	public void startGame()
		{
		gameOn = true;
		// Send information on layout and cardstacks.
		}
	
	public boolean userJoined(int userID)
		{
		if (!super.userJoined(userID))
			return false;

		LogicPlayerState s=new LogicPlayerState();
		pstate.put(userID, s);

		//Set style of decks
		s.deck.stackStyle=StackStyle.Deck;
		for(CardStack<PlayingCard> stack:s.workingStacks)
			stack.stackStyle=StackStyle.Stair;
		for(CardStack<PlayingCard> stack:s.sortedStacks)
			stack.stackStyle=StackStyle.Deck;
			
		//Distribute cards
		s.deck.addCards(PlayingCardUtil.getDeck52());
		for(PlayingCard c:s.deck.cards)
			c.showsFront=false;
		s.deck.shuffle();
		for(int i=0;i<7;i++)
			{
			CardStack<PlayingCard> stack=s.workingStacks.get(i);
			for(int j=0;j<i+1;j++)
				stack.addCard(s.deck.drawCard());
			stack.getCard(stack.size()-1).showsFront=true;
			}

		return true;
		}
	
	/**
	 * Handle user clicking on a card
	 */
	public boolean userActionClickedCard(int fromUser, GameActionClickedCard s)
		{		
		LogicPlayerState ps=pstate.get(fromUser);
		CardStack<PlayingCard> fromStack=ps.getStack(s.stackName);
		if(s.stackName.equals(DECK) && s.playerID==fromUser)
			{
			//Cycle current cards
			if(ps.deck.size()!=0)
				{
				Message msg=new Message();
				
				if(ps.revealedDeck.size()!=0)
					{
					//Make old card face downward
					
					//TODO This causes a crash after a while
					getStack(fromUser, REVEALEDDECK).getCard(0).showsFront=false;
					msg.add(getUpdateCardForClient(fromUser, REVEALEDDECK, 0));
					
					
					//Move away the old current card
					GameActionDragCard actionMoveOld=new GameActionDragCard();
					actionMoveOld.gameID=sessionID;
					
					actionMoveOld.fromPlayer=s.playerID;
					actionMoveOld.fromPos=0;
					actionMoveOld.fromStackName=REVEALEDDECK;
					
					actionMoveOld.toPlayer=s.playerID;
					actionMoveOld.toPos=0;
					actionMoveOld.toStackName=DECK;
			
					executeMove(actionMoveOld);
					msg.add(actionMoveOld);
					}
				
				//Make next card face upward
				fromStack.getTopCard().showsFront=true;
				msg.add(getUpdateCardForClient(fromUser, s.stackName, fromStack.size()-1));

				//Move in the next card
				GameActionDragCard actionMoveNew=new GameActionDragCard();
				actionMoveNew.gameID=sessionID;
				
				actionMoveNew.fromPlayer=s.playerID;
				actionMoveNew.fromPos=ps.deck.size()-1;
				actionMoveNew.fromStackName=DECK;

				actionMoveNew.toPlayer=s.playerID;
				actionMoveNew.toPos=ps.revealedDeck.size();
				actionMoveNew.toStackName=REVEALEDDECK;
		
				executeMove(actionMoveNew);
				msg.add(actionMoveNew);
				
				sendToPlayers(msg);
				return true;
				}
			
			}
		else if(!fromStack.getCard(s.stackPos).showsFront)
			{
			//Make card face upward if facing down and on top
			if(s.stackPos==fromStack.size()-1)
				{
				fromStack.getCard(s.stackPos).showsFront=true;
				sendToPlayers(new Message(getUpdateCardForClient(fromUser, s.stackName, s.stackPos)));
				return true;
				}
			}
		else if(!s.stackName.startsWith("sorted"))
			{
			//Try to automatically put card on any sorted heap
			if(s.stackPos==fromStack.size()-1)
				for(int i=0;i<4;i++)
					if(canPutOnSorted(ps.sortedStacks.get(i), fromStack.getCard(s.stackPos)))
						{
						GameActionDragCard action=new GameActionDragCard();
						
						action.fromPlayer=fromUser;
						action.fromStackName=s.stackName;
						action.fromPos=s.stackPos;
						
						action.toPlayer=fromUser;
						action.toStackName="sorted"+i;
						action.toPos=ps.sortedStacks.get(i).size();
						
						executeMove(action);
						sendToPlayers(new Message(action));
						return true;
						}
			}
		return false;
		}
	

	/**
	 * Generate an update of the view of a card
	 */
	private GameActionUpdateCard getUpdateCardForClient(int playerID, String stackName, int stackPos)
		{
		System.out.println("updating card ----- "+playerID+"  "+stackName+"  "+stackPos);
		
		LogicPlayerState ps=pstate.get(playerID);
		return new GameActionUpdateCard(
				sessionID, 
				playerID, stackName, stackPos,
				ps.getStack(stackName).getCard(stackPos).toClientCard());
		}

	/**
	 * Check if card can be put on a stair stack
	 */
	private boolean canPutOnStair(CardStack<PlayingCard> stack, PlayingCard newc)
		{
		PlayingCard topCard=stack.getTopCard();
		if(topCard==null)
			return true;
		else
			return (topCard.getValue() == newc.getValue()+1 && topCard.isRed() != newc.isRed());
		}

	/**
	 * Check if card can be put on a sorted stack
	 */
	private boolean canPutOnSorted(CardStack<PlayingCard> stack, PlayingCard newc)
		{
		PlayingCard topCard=stack.getTopCard();
		if(topCard==null)
			return newc.isAce();
		else
			return topCard.getValue() == newc.getValue()-1 && topCard.getSuit() == newc.getSuit();
		}

	private boolean isWorkingStack(String name)
		{
		return name.startsWith("working");
		}
	private boolean isSortedStack(String name)
		{
		return name.startsWith("sorted");
		}
	private boolean isNewDeck(String name)
		{
		return name.equals(DECK);
		}
	private boolean isCurrentDeck(String name)
		{
		return name.equals(REVEALEDDECK);
		}
	

	/**
	 * Handle user dragging a card
	 */
	public boolean userActionDragCard(int fromUser, GameActionDragCard s)
		{
		LogicPlayerState ps=pstate.get(fromUser);
		boolean isOk=false;
		
		if(isWorkingStack(s.toStackName))
			{
			if(isWorkingStack(s.fromStackName))
				{
				//Between working stacks one can move a card and all cards below
				CardStack<PlayingCard> fromStack=ps.getStack(s.fromStackName);
				if(fromStack.getCard(s.fromPos).showsFront && canPutOnStair(ps.getStack(s.toStackName), fromStack.getCard(s.fromPos)))
					isOk=true;
				}
			else if(isSortedStack(s.fromStackName) || isCurrentDeck(s.fromStackName))
				{
				//From these stacks one can move the top-most card
				CardStack<PlayingCard> fromStack=ps.getStack(s.fromStackName);
				if(s.fromPos==fromStack.size()-1 && canPutOnStair(ps.getStack(s.toStackName), fromStack.getCard(s.fromPos)))
					isOk=true;
				}
			}
		else if(isSortedStack(s.toStackName) && !isNewDeck(s.fromStackName))
			{
			//To the sorted stack any top
			CardStack<PlayingCard> fromStack=ps.getStack(s.fromStackName);
			if(s.fromPos==fromStack.size()-1 && canPutOnSorted(ps.getStack(s.toStackName), fromStack.getCard(s.fromPos)))
				isOk=true;
			}
		
		
		if(isOk)
			{
			executeMove(s);
			sendToPlayers(new Message(s));
			return true;
			}
		else
			return false;
		}
	

	/**
	 * Get a stack
	 */
	public CardStack<PlayingCard> getStack(int player, String stackName)
		{
		return pstate.get(player).getStack(stackName);
		}

	/**
	 * Execute an action server-side
	 */
	public void executeMove(GameActionDragCard action)
		{
		CardStack<PlayingCard> stackFrom=getStack(action.fromPlayer, action.fromStackName);
		CardStack<PlayingCard> stackTo=getStack(action.toPlayer, action.toStackName);

		if(stackFrom.stackStyle==StackStyle.Stair && stackTo.stackStyle==StackStyle.Stair)
			{
			//This code is simplified
			int numCardToMove=stackFrom.cards.size()-action.fromPos;
			for(int i=0;i<numCardToMove;i++)
				{
				PlayingCard theCard=stackFrom.cards.remove(action.fromPos);
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
			PlayingCard theCard=stackFrom.cards.remove(fromPos);
			stackTo.cards.add(toPos, theCard);
			}
		}
	
	
	/**
	 * Handle user leaving game
	 */
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
	
	/**
	 * Get the graphical layout of the game
	 */
	public GameDesign createGameDesign()
		{
		double cardDistX=200;
		
		GameDesign d=new GameDesign();
		GameDesign.StackDef defDeck=d.playerField.createStack(DECK);
		defDeck.stack=new CardStack<Object>();
		defDeck.y=-300;
		defDeck.x=-400+0*cardDistX;

		
		GameDesign.StackDef defDeckCurrent=d.playerField.createStack(REVEALEDDECK);
		defDeckCurrent.stack=new CardStack<Object>();
		defDeckCurrent.y=-300;
		defDeckCurrent.x=-400+1*cardDistX;

		
		
		for(int i=0;i<numWorkingHeap;i++)
			{
			GameDesign.StackDef defWorking=d.playerField.createStack("working"+i);
			defWorking.stack=new CardStack<Object>();
			defWorking.stack.stackStyle=StackStyle.Stair;
			defWorking.y=-0;
			defWorking.x=-400+i*cardDistX;
			}

		for(int i=0;i<4;i++)
			{
			GameDesign.StackDef deckSorted=d.playerField.createStack("sorted"+i);
			deckSorted.stack=new CardStack<Object>();
			deckSorted.stack.stackStyle=StackStyle.Stair;
			deckSorted.y=-300;
			deckSorted.x=-400+(i+3)*cardDistX;
			}

		
		return d;
		}

	/**
	 * Get the current state of the game
	 */
	public void getGameState(GameActionUpdateGameState action)
		{
		for(int p:players)
			{
			LogicPlayerState ds=pstate.get(p);
			PlayerState ps=action.createPlayer(p);
			
			CardStack<ClientCard> deck=CardStack.toClientCardStack(ds.deck);
			ps.stacks.put(DECK, deck);

			CardStack<ClientCard> deckCurrent=CardStack.toClientCardStack(ds.revealedDeck);
			ps.stacks.put(REVEALEDDECK, deckCurrent);

			for(int i=0;i<ds.workingStacks.size();i++)
				{
				CardStack<ClientCard> stack=CardStack.toClientCardStack(ds.workingStacks.get(i));
				ps.stacks.put("working"+i, stack);
				}
			
			for(int i=0;i<4;i++)
				{
				CardStack<ClientCard> stack=CardStack.toClientCardStack(ds.sortedStacks.get(i));
				ps.stacks.put("sorted"+i, stack);
				}

			}
		}


	}
