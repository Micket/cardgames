package serverData;

import java.util.EnumSet;

import serverData.CardStack;

/**
 * Helpers for normal playing cards.
 */
public class PlayingCardUtil
	{
    private static final CardStack<PlayingCard> deck52 = new CardStack<PlayingCard>();
    private static final CardStack<PlayingCard> jokers = new CardStack<PlayingCard>();
    static
        {
        for(PlayingCard.Suit s : EnumSet.range(PlayingCard.Suit.Clubs, PlayingCard.Suit.Spades))
            for(PlayingCard.Rank r : EnumSet.range(PlayingCard.Rank.Deuce, PlayingCard.Rank.Ace))
                deck52.addCard(new PlayingCard(s,r));

        for(PlayingCard.Suit s : EnumSet.range(PlayingCard.Suit.RedJoker, PlayingCard.Suit.BlackJoker))
            deck52.addCard(new PlayingCard(s,PlayingCard.Rank.Joker));

        }

    public static CardStack<PlayingCard> getDeck52() { return deck52; }
    public static CardStack<PlayingCard> getJokers() { return jokers; }
	}
