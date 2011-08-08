package data;

/**
 * A normal (French) playing card.
 */
public class PlayingCard extends Card
	{
    public enum Suit { Clubs, Diamonds, Hearts, Spades }
    public enum Rank
        {
        Deuce(2), Three(3), Four(4), Five(5), Six(6), Seven(7), Eight(8), Nine(9), Ten(10), Jack(11), Queen(12), King(13), Ace(1), Joker(0); // Include the joker here?
        private int value;
        private Rank(int n) { value = n; }
        public int giveValue() { return value; }
        }

    private Suit suit;
    private Rank rank;

    PlayingCard(Suit s, Rank r)
        {
        suit = s;
        rank = r;
        }

    public boolean isAce() { return rank == Rank.Ace; }

    public boolean isJoker() { return rank == Rank.Joker; }

    public Rank getRank() { return rank; }

    public Suit getSuit() { return suit; }

    public int getValue() { return rank.giveValue(); }

    public String getID() { return rank+""+suit; }

    public String toString() { return rank + " of " + suit; }
	}
