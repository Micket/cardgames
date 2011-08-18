package games;

import serverData.CardStack;
import serverData.PlayingCard;
import serverData.PlayingCardUtil; // Common things for playing cards here?

import clientData.GameDesign;

import action.UserActionClickedCard;

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
	private CardStack<PlayingCard> deckA = PlayingCardUtil.getDeck52();
	private CardStack<PlayingCard> deckB = new CardStack<PlayingCard>();
	private CardStack<PlayingCard> player = new CardStack<PlayingCard>();

	public void startGame()
		{
		gameOn = true;
		// Send information on layout and cardstacks.
		}
	
	public boolean userJoined(int userID)
		{
		if (!super.userJoined(userID))
			return false;
		return true;
		}
	
	public boolean userActionClickedCard(int fromUser, UserActionClickedCard s)
		{
		if (s.stackName.compareTo("deck_A") == 0)
			{
			System.out.println("Taking a card from deck A");
			PlayingCard c = deckA.drawCard();
			player.addCard(c);
			// Send card to user..
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
		GameDesign.StackDef def=d.playerField.createStack("hand");
		def.stack=new CardStack<Object>();
		return d;
		}

	}
