package games;

import java.util.HashMap;
import java.util.Map;

import serverData.CardStack;
import serverData.CardStack.StackStyle;
import serverData.PlayingCard;
import serverData.PlayingCardUtil; // Common things for playing cards here?

import clientData.ClientCard;
import clientData.GameDesign;

import action.UserActionClickedCard;
import action.UserActionGameStateUpdate;
import action.UserActionGameStateUpdate.PlayerState;

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
//	private CardStack<PlayingCard> deckB = new CardStack<PlayingCard>();
//	private CardStack<PlayingCard> player = new CardStack<PlayingCard>();

	Map<Integer, DebugPlayerState> pstate=new HashMap<Integer, DebugPlayerState>();
	
	class DebugPlayerState
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
		DebugPlayerState s=new DebugPlayerState();
		pstate.put(userID, s);
		
		s.hand.cards.add(new PlayingCard(PlayingCard.Suit.Spades, PlayingCard.Rank.Deuce));
		s.hand.cards.add(new PlayingCard(PlayingCard.Suit.Spades, PlayingCard.Rank.Three));
		s.hand.cards.add(new PlayingCard(PlayingCard.Suit.Spades, PlayingCard.Rank.Five));
		s.hand.cards.add(new PlayingCard(PlayingCard.Suit.Spades, PlayingCard.Rank.Four));
		s.hand.stackStyle=StackStyle.Hand;

		
		s.deck.cards.add(new PlayingCard(PlayingCard.Suit.Spades, PlayingCard.Rank.Five));
		s.deck.cards.add(new PlayingCard(PlayingCard.Suit.Spades, PlayingCard.Rank.Six));
		s.deck.stackStyle=StackStyle.Deck;
		

//////////////		
		if (!super.userJoined(userID))
			return false;
		return true;
		}
	
	public boolean userActionClickedCard(int fromUser, UserActionClickedCard s)
		{
		if (s.stack.equals("hand"))
			{
			/*
			System.out.println("Taking a card from deck A");
			PlayingCard c = deckA.drawCard();
			player.addCard(c);
			// Send card to user..
			*/
			}
		else
			return false;
		return true;
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
	
	public void getGameState(UserActionGameStateUpdate action)
		{
		for(int p:players)
			{
			DebugPlayerState ds=pstate.get(p);
			PlayerState ps=action.createPlayer(p);
			
			CardStack<ClientCard> stackHand=CardStack.toClientCardStack(ds.hand);
			ps.stacks.put("hand", stackHand);

			CardStack<ClientCard> stackDeck=CardStack.toClientCardStack(ds.deck);
			ps.stacks.put("deck", stackDeck);
			}
		}


	}
