package games;

import java.lang.Math;
import java.util.List;

import serverData.CardStack;
import serverData.PlayingCard;
import serverData.ServerCard;

import clientData.ClientCard;
import action.UserAction;
import action.UserActionClickedButton;


// Common things for playing cards here?
//import data.PlayingCardUtil;

/**
 * Logic for the simple card game BlackJack with betting.
 * Real BlackJack rules are a bit more complicated than this, but I'm leaving that out for now.
 * @author Micket
 */
public class BlackJack extends DefaultGameLogic
	{

	enum GameState
		{

		Betting, Playing, Over
		}

	class PlayerState
		{

		public CardStack<PlayingCard> hand = new CardStack<PlayingCard>();
		public boolean done = false;
		public int bet = 0;
		public int cash = 1000;
		}
	/// Value at which the dealer stops.
	private int dealerStop = 17;
	/// Value at which the player (should) stop.
	private int playerStop = 21;
	/// Number of decks used.
	private int decks = 1;
	/// Complete deck of cards (for convenience)
	private CardStack<PlayingCard> newDeck;
	private CardStack<PlayingCard> deck;
	private CardStack<PlayingCard> dealerHand;
	private GameState gs;
	private List<PlayerState> players;

/* How should we handle the start?
	BlackJack(List<Integer> connections)
		{
		// Do some initialization..
		}
*/

	public boolean userActionClickedButton(int fromUser, UserActionClickedButton action)
		{
		PlayerState p = players.get(fromUser);
		if (action.buttonID == 0) // Another card.
			{
			return playerDraw(p);
			}
		else if (action.buttonID == 1) // Stay
			{
			p.done = true;
			}
		else if (action.buttonID == 2) // Bet 1
			{
			return playerBetting(p, action.buttonValue);
			}
		else
			{
			return false;
			}
		if (allBets())
			{
			startTurn();
			}
		if (allDone())
			{
			dealersTurn();
			}
		return true;
		}

	/**
	 * Checks if all players are done playing the turn (in one way or another).
	 */
	public boolean allDone()
		{
		if (gs != GameState.Playing)
			{
			return false;
			}
		boolean done = true;
		for (PlayerState p : players)
			{
			done &= p.done;
			}
		return done;
		}
	
	/**
	 * Checks if all players have bet (or have lost).
	 */
	public boolean allBets()
		{
		if (gs != GameState.Betting)
			{
			return false;
			}
		boolean done = true;
		for (PlayerState p : players)
			{
			done &= p.cash == 0 || p.bet > 0;
			}
		return done;
		}
	
    /**
	 * Checks if a player or the bank has won.
	 */
	public boolean isGameOver()
		{
		boolean moneyLeft = false;
		for (PlayerState p : players)
			{
			if (p.cash > 0)
				{
				if (moneyLeft)
					return false; // At least two players left.
				else
					moneyLeft = true;
				}
			}
		return true; // Either 0 or 1 player left.
		}
	
	/**
	 * Finds the winner (-1 means the dealer won).
	 */
	public int findWinner()
		{
		for (int i = 0; i < players.size(); i++)
			if (players.get(i).cash > 0)
				return i;
		return -1;
		}

	/**
	 * Request to draw new card for specified player.
	 */
	private boolean playerBetting(PlayerState p, int bet)
		{
		if (gs != GameState.Betting || bet > p.cash || bet <= 0)
			{
			// Tell user he can't.
			return false;
			}
		else
			{
			p.bet = bet;
			// Tell all users what he betted.
			return true;
			}
		}

	/**
	 * Request to draw new card for specified player.
	 */
	private boolean playerDraw(PlayerState p)
		{
		if (gs != GameState.Playing || p.done)
			{
			// Tell user he can't.
			return false;
			}
		else
			{
			PlayingCard c = deck.drawCard();
			p.hand.addCard(c);
			// Tell user what he drew card c.
			if (sumCards(p.hand) > playerStop)
				{
				p.done = true;
				// Tell everyone he is fat
				}
			}
		return true;
		}

	/**
	 * Plays the dealers turn (at the end off turn). Ends the current game and goes to the betting state.
	 * @return Dealers points.
	 */
	private void dealersTurn()
		{
		PlayingCard c;
		int playerPoints;
		int points = sumCards(dealerHand);
		// Tell all user what cards the dealer has
		while (points < dealerStop)
			{
			c = deck.drawCard();
			dealerHand.addCard(c);
			points = sumCards(dealerHand);
			// Tell all user what he drew card c.
			}

		for (int i = 0; i < players.size(); i++)
			{
			PlayerState ps = players.get(i);
			playerPoints = sumCards(ps.hand);
			if (playerPoints <= playerStop && (points > playerStop || points < playerPoints))
				{
				ps.cash += ps.bet;
				ps.bet = 0;
				// Tell all users we have a winner.
				}
			else
				{
				ps.cash -= ps.bet;
				ps.bet = 0;
				// Tell all users we have a loser.
				}
			}
		gs = GameState.Betting;
		}

	/**
	 * Starts a new turn (after betting is done), shuffling the deck and deals 2 cards to all players.
	 * @return Dealers points.
	 */
	private void startTurn()
		{
		CardStack<PlayingCard> cs;

		dealerHand.clear();
		for (int i = 0; i < players.size(); i++)
			{
			players.get(i).hand.clear();
			}

		deck.clear();
		for (int i = 0; i < decks; i++)
			{
			deck.addCards(newDeck);
			}
		deck.shuffle();
		cs = deck.drawCards(2);
		dealerHand.addCards(cs);
		for (int i = 0; i < players.size(); i++)
			{
			if (players.get(i).cash > 0)
				{
				cs = deck.drawCards(2);
				players.get(i).hand.addCards(cs);
				players.get(i).done = false;
				}
			}
		gs = GameState.Playing;
		}

	/**
	 * Counts the optimal points of a given hand.
	 */
	private int sumCards(CardStack<PlayingCard> hand)
		{
		int aces = 0;
		int points = 0;
		for (int i = 0; i < hand.size(); i++)
			{
			if (hand.getCard(i).isAce())
				{
				aces++;
				points += 1; // Minimum value for aces
				}
			else
				{
				points += Math.min(hand.getCard(i).getValue(), 10);
				}
			}
		for (int i = 0; i < aces; ++i)
			{
			if (points <= playerStop - 10)
				{
				points += 10;
				}
			}
		return points;
		}

	public String getName()
		{
		return "BlackJack";
		}

	public String getDescription()
		{
		return "BlackJack tournament edition. Bet money until only one remain.";
		}

	/////////////////////////////////////////////////
	// 
	public ClientCard convertCard(ServerCard sc)
		{


		return null;

		}
	/*
	{
	CardStack<PlayingCard> cards=null;
	CardStack<?> cards2=cards;
	CardStack<ServerCard> cards3=cards2;
	
	
	ServerCard c;
	((PlayingCard)c).isAce();
	}
	 */
	}
