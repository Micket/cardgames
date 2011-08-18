package serverData;

import clientData.ClientCard;

/**
 * A normal (French) playing card.
 */
public class PlayingCard extends ServerCard
	{

	public boolean showsFront=false;
	public double rotation=0; //Can be 0,1,2,3, going clockwise


    public enum Suit { Clubs, Diamonds, Hearts, Spades, RedJoker, BlackJoker } // Jokers can often be only red or black
    public enum Rank
        {
        Deuce(2), Three(3), Four(4), Five(5), Six(6), Seven(7), Eight(8), Nine(9), Ten(10), Jack(11), Queen(12), King(13), Ace(1), Joker(0); // Include the joker here?
        private int value;
        private Rank(int n) { value = n; }
        public int getValue() { return value; }
        }

    private Suit suit;
    private Rank rank;

    PlayingCard(Suit s, Rank r)
        {
        suit = s;
        rank = r;
        }

    /// Convenience checks;
    public boolean isAce() { return rank == Rank.Ace; }
		public boolean isFace() { return rank.getValue() > 10; }
    public boolean isJoker() { return rank == Rank.Joker; }
    public boolean isRed() { return suit == Suit.Diamonds || suit == Suit.Hearts || suit == Suit.RedJoker; }
    public boolean isBlack() { return suit == Suit.Clubs || suit == Suit.Spades || suit == Suit.BlackJoker; }

    public Rank getRank() { return rank; }
    public Suit getSuit() { return suit; }
    public int getValue() { return rank.getValue(); }

    //public String getID() { return rank+""+suit; }

    public String toString() { return rank + " of " + suit; }


    public ClientCard toClientCard()
    	{
    	ClientCard c=new ClientCard();
    	c.showsFront=showsFront;
    	c.rotation=rotation;
    	c.front="poker "+suit+" "+rank.value;
    	c.back="poker back";
    	return c;
    	}
	}
