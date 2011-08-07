package games;

import java.util.List;
import java.util.ArrayList;

import data.CardStack;
import data.PlayingCard;

/**
 * Logic for the simple card game BlackJack.
 *
 * @author Micket
 */
public class BlackJack extends GameLogic
	{
    /// Complete deck of cards (for convenience)
    private CardStack<PlayingCard> newDeck;
	private CardStack<PlayingCard> deck;
    private CardStack<PlayingCard> dealerHand;
    /// Value at which the dealer stops.
    private dealerStop = 17;
    /// Value at which the player (should) stop.
    private playerStop = 21;
    /// Number of decks used.
    private decks = 1;

    /// Player state
    private List<CardStack<PlayingCard>> playerHand;
    private List<boolean> playerDone;
    private List<int> playerBet;
    private List<int> playerCash;

	public void userAction(UserAction action)
        {
        if (allDone())
            {
            dealersTurn();
            }
        }

    /**
     * Plays the dealers turn (at the end off turn)
     * @return Dealers points.
     */
    public int dealersTurn()
        {
        CardStack<PlayingCard> cs;
        int points = sumCards(dealerHand);
        while (points < dealerStop)
            {
            cs = deck.drawCards(1);
            dealerHand.addCards(c);
            points = sumCards(dealerHand);
            }
        return points;
        }

    /**
     * Starts a new game, shuffling the deck and deals 2 cards to all players.
     * @return Dealers points.
     */
    public void startGame()
        {
        CardStack<PlayingCard> cs;

        dealerHand.empty();
        for (int i = 0; i < playerHand.size(); ++i)
            playerHand[i].empty();

        deck.empty();
        for (int i = 0; i < decks; ++i)
            deck.addCards(newDeck);
        deck.shuffle();
        cs = deck.drawCards(2);
        dealerHand.addCards(c);
        for (int i = 0; i < playerHand.size(); ++i)
            {
            cs = deck.drawCards(2);
            playerHand[i].addCards(c);
            }
        }

    public int sumCards(CardStack<PlayingCard> hand)
        {
        int aces = 0;
        int points = 0;
        for (int i = 0, i < CardStack.size(); ++i)
            {
            if (hand[i].isAce())
                {
                aces++;
                points += 1; // Minimum value for aces
                }
            else
                {
                points += Math.min(hand[i].giveValue(),10);
                }
            }
        }
        for (int i = 0; i < aces; ++i)
            {
            if (points <= playerStop - 9)
                {
                points += 9;
                }
            }
        return points;
	}
