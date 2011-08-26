package games;


import java.util.Map;
import java.util.HashMap;

import serverData.CardStack;
import serverData.PlayingCard;
import serverData.PlayingCardUtil;
import serverData.ServerCard;
import serverData.CardStack.StackStyle;

import clientData.ClientCard;
import clientData.GameDesign;

import action.UserActionClickedButton;
import action.UserActionGameStateUpdate;
import action.UserActionGameStateUpdate.PlayerState;

/**
 * Logic for the simple card game BlackJack with betting.
 * Real BlackJack rules are a bit more complicated than this, but I'm leaving that out for now.
 * @author Micket
 */
@GameTypePlugin(
		description = "Compete in classical Blackjack against other players to until only one remain.",
		maxplayers = 8,
		minplayers = 0,
		name = "Blackjack",
		category = "Banking",
		instructions = "Play to win etc." // Good idea to have some rich text player instructions here?
		)
public class Blackjack extends DefaultGameLogic
	{
	private enum GameState { Betting, Playing, Over }

	private class LogicPlayerState
		{
		public CardStack<PlayingCard> hand = new CardStack<PlayingCard>();
		public boolean done = false;
		public int bet = 0;
		public int cash = 1000;
		public boolean quit = false;
		}
	
	/// Value at which the dealer stops.
	private int dealerStop = 17;
	/// Value at which the player (should) stop.
	private int playerStop = 21;
	/// Number of decks used.
	private int decks = 1;
	
	private CardStack<PlayingCard> deckWithNewCards=new CardStack<PlayingCard>();
	private CardStack<PlayingCard> dealerHand=new CardStack<PlayingCard>();
	private GameState gs;
	private Map<Integer,LogicPlayerState> players = new HashMap<Integer,LogicPlayerState>();

	// TODO: Is it possible to access max and min-players from the gametype? But then, these could be changed (at least when creating the game).
	public int getMaxPlayers() { return 8; }
	public int getMinPlayers() { return 0; }
	
	public void startGame()
		{
		gameOn = true;
		gs = GameState.Betting;
		// Send some message to clients?
		}
	
	public boolean userJoined(int userID)
		{
		if (!super.userJoined(userID))
			return false;
		players.put(userID, new LogicPlayerState());
		return true;
		}
	
	public boolean userLeft(int userID)
		{
		if (!super.userLeft(userID))
			return false;

		if (gameOn)
			players.get(userID).quit = true;
		else
			players.remove(userID);
		return true;
		}
	
	public int getNumParticipatingPlayers()
		{
		return players.size();
		}
	
	public boolean userActionClickedButton(int fromUser, UserActionClickedButton action)
		{
		LogicPlayerState p = players.get(fromUser);
		if (action.buttonID == 0) // Bet
			{
			return playerBetting(p, action.buttonValue);
			}
		if (action.buttonID == 1) // Hit
			{
			return playerDraw(p);
			}
		else if (action.buttonID == 2) // Stand
			{
			p.done = true;
			}
		/*else if (action.buttonID == 3) // Split
			{
			...
			}*/
		/*else if (action.buttonID == 4) // Double
			{
			...
			}*/
		/*else if (action.buttonID == 5) // Surrender
			{
			...
			}*/
		/*else if (action.buttonID == 6) // Insurance
			{
			...
			}*/
		else // split, double, 
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
			return false;
		for (LogicPlayerState p : players.values())
			if(!p.done)
				return false;
		return true;
		}
	
	/**
	 * Checks if all players have bet (or have lost).
	 */
	public boolean allBets()
		{
		if (gs != GameState.Betting)
			return false;
		boolean done = true;
		for (LogicPlayerState p : players.values())
			done &= p.cash == 0 || p.bet > 0;
		return done;
		}
	
    /**
	 * Checks if a player or the bank has won.
	 * 
	 * TODO this function is obviously coded wrong
	 */
	public boolean isGameOver()
		{
		boolean moneyLeft = false;
		for (LogicPlayerState p : players.values())
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
	private boolean playerBetting(LogicPlayerState p, int bet)
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
	private boolean playerDraw(LogicPlayerState p)
		{
		if (gs != GameState.Playing || p.done)
			{
			// Tell user he can't.
			return false;
			}
		else
			{
			PlayingCard c = deckWithNewCards.drawCard();
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
			c = deckWithNewCards.drawCard();
			dealerHand.addCard(c);
			points = sumCards(dealerHand);
			// Tell all user what he drew card c.
			}

		for (int i = 0; i < players.size(); i++)
			{
			LogicPlayerState ps = players.get(i);
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
		dealerHand.clear();
		for (int i = 0; i < players.size(); i++)
			players.get(i).hand.clear();
		
		deckWithNewCards.clear();
		for (int i = 0; i < decks; i++)
			deckWithNewCards.addCards(PlayingCardUtil.getDeck52());
		deckWithNewCards.shuffle();
		
		dealerHand.addCards(deckWithNewCards.drawCards(2)); //TODO If it is laid out already like this, there will be no animation
		for (int i = 0; i < players.size(); i++)
			{
			if (players.get(i).cash > 0)
				{
				players.get(i).hand.addCards(deckWithNewCards.drawCards(2)); //TODO If it is laid out already like this, there will be no animation
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


	public GameDesign createGameDesign()
		{
		GameDesign d=new GameDesign();
		GameDesign.StackDef defPlayerHand=d.playerField.createStack("hand");
		defPlayerHand.stack=new CardStack<Object>();
		
		GameDesign.StackDef defDealerHand=d.commonField.createStack("hand");
		defDealerHand.stack=new CardStack<Object>();

		GameDesign.StackDef defNewCards=d.commonField.createStack("newcards");
		defNewCards.stack=new CardStack<Object>();
		defNewCards.stack.stackStyle=StackStyle.Deck;
		defNewCards.x=200;

		return d;
		}
	
	public void getGameState(UserActionGameStateUpdate action)
		{
		
		for(int p:players.keySet())
			{
			LogicPlayerState ds=players.get(p);
			PlayerState ps=action.createPlayer(p);
			
			CardStack<ClientCard> stackHand=CardStack.toClientCardStack(ds.hand);
			ps.stacks.put("hand", stackHand);
			}

		PlayerState ps=action.createPlayer(-1); //TODO create a const
		CardStack<ClientCard> stackNewCards=CardStack.toClientCardStack(deckWithNewCards);
		ps.stacks.put("newcards", stackNewCards);
		CardStack<ClientCard> stackDealer=CardStack.toClientCardStack(dealerHand);
		ps.stacks.put("hand", stackDealer);
		
		}

	/////////////////////////////////////////////////
	// 
	public ClientCard convertCard(ServerCard sc)
		{


		return null;

		}

	}
