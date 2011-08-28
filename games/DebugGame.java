package games;

import java.util.HashMap;
import java.util.Map;

import serverData.CardStack;
import serverData.CardStack.StackStyle;
import serverData.PlayingCard;
import clientData.ClientCard;
import clientData.GameDesign;

import action.Message;
import action.GameActionClickedCard;
import action.GameActionDragCard;
import action.GameActionUpdateGameState;
import action.GameActionUpdateGameState.PlayerState;

/**
 * Empty game for testing. One player, and some decks of cards.
 * @author Micket
 */
@GameTypePlugin(
		description = "Debug game. Deals a hand, lets user pick from deck, sort his hand and throw cards in a discard heap, etc.",
		maxplayers = 1,
		minplayers = 1,
		name = "DebugGame",
		category = "None",
		instructions = "Play to win etc." // Good idea to have some rich text player instructions here?
		)
public class DebugGame extends DefaultGameLogic
	{

	/// Complete deck of cards (for convenience)
//	private CardStack<PlayingCard> deckA = PlayingCardUtil.getDeck52();

	private Map<Integer, LogicPlayerState> pstate=new HashMap<Integer, LogicPlayerState>();
	
	private class LogicPlayerState
		{
		public CardStack<PlayingCard> hand = new CardStack<PlayingCard>();
		public CardStack<PlayingCard> deck= new CardStack<PlayingCard>();
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
		
		s.hand.cards.add(new PlayingCard(PlayingCard.Suit.Spades, PlayingCard.Rank.Deuce));
		s.hand.cards.add(new PlayingCard(PlayingCard.Suit.Spades, PlayingCard.Rank.Three));
		s.hand.cards.add(new PlayingCard(PlayingCard.Suit.Spades, PlayingCard.Rank.Five));
		s.hand.cards.add(new PlayingCard(PlayingCard.Suit.Spades, PlayingCard.Rank.Four));
		s.hand.stackStyle=StackStyle.Hand;

		
		s.deck.cards.add(new PlayingCard(PlayingCard.Suit.Spades, PlayingCard.Rank.Five));
		s.deck.cards.add(new PlayingCard(PlayingCard.Suit.Spades, PlayingCard.Rank.Six));
		s.deck.stackStyle=StackStyle.Deck;
		
		for(PlayingCard c:s.hand.cards)
			c.showsFront=true;
		for(PlayingCard c:s.deck.cards)
			c.showsFront=true;
		
		return true;
		}
	
	public boolean userActionClickedCard(int fromUser, GameActionClickedCard s)
		{
		
		if (s.stackName.equals("hand") && s.playerID==fromUser)
			{
			LogicPlayerState ps=pstate.get(fromUser);
			
			GameActionDragCard action=new GameActionDragCard();
			action.gameID=sessionID;
			
			action.fromPlayer=s.playerID;
			action.fromPos=s.stackPos;
			action.fromStackName="hand";

			action.toPlayer=s.playerID;
			action.toPos=ps.deck.size();
			action.toStackName="deck";
	
			executeMove(action);
			thread.send(fromUser, new Message(action));
			return true;
			}
		else
			return false;
		}
	
	
	public boolean userActionDragCard(int fromUser, GameActionDragCard s)
		{
		LogicPlayerState ps=pstate.get(fromUser);

		executeMove(s);
		thread.send(fromUser, new Message(s));
		
		return true;
		}
	
	public CardStack<PlayingCard> getStack(int player, String stackName)
		{
		if(stackName.equals("hand"))
			return pstate.get(player).hand;
		else if(stackName.equals("deck"))
			return pstate.get(player).deck;
		else
			throw new RuntimeException("no such stack: "+stackName);
		}
	
	public void executeMove(GameActionDragCard action)
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
		GameDesign.StackDef defHand=d.playerField.createStack("hand");
		defHand.stack=new CardStack<Object>();
		
		GameDesign.StackDef defDeck=d.playerField.createStack("deck");
		defDeck.stack=new CardStack<Object>();
		defDeck.stack.stackStyle=StackStyle.Deck;
		defDeck.y=-300;

		
		return d;
		}
	
	public void getGameState(GameActionUpdateGameState action)
		{
		for(int p:players)
			{
			LogicPlayerState ds=pstate.get(p);
			PlayerState ps=action.createPlayer(p);
			
			CardStack<ClientCard> stackHand=CardStack.toClientCardStack(ds.hand);
			ps.stacks.put("hand", stackHand);

			CardStack<ClientCard> stackDeck=CardStack.toClientCardStack(ds.deck);
			ps.stacks.put("deck", stackDeck);
			}
		}


	}
