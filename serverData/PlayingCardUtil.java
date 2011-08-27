package serverData;

import java.util.EnumSet;

import serverData.CardStack;

/**
 * Helpers for normal playing cards.
 */
public class PlayingCardUtil
	{
	public static CardStack<PlayingCard> getDeck52()
		{
		CardStack<PlayingCard> deck52 = new CardStack<PlayingCard>();
		for(PlayingCard.Suit s : EnumSet.range(PlayingCard.Suit.Clubs, PlayingCard.Suit.Spades))
			for(PlayingCard.Rank r : EnumSet.range(PlayingCard.Rank.Deuce, PlayingCard.Rank.Ace))
				deck52.addCard(new PlayingCard(s,r));
		return deck52;
		}
	
	public static CardStack<PlayingCard> getJokers()
		{
		CardStack<PlayingCard> jokers = new CardStack<PlayingCard>();
		for(PlayingCard.Suit s : EnumSet.range(PlayingCard.Suit.RedJoker, PlayingCard.Suit.BlackJoker))
			jokers.addCard(new PlayingCard(s,PlayingCard.Rank.Joker));
		return jokers;
		}

	public static void sortSuit(CardStack<PlayingCard> c)
		{
		// TODO
		}

	public static void sortRank(CardStack<PlayingCard> c)
		{
		// TODO
		}

	}
