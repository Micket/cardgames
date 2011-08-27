package action;

import serverData.CardStack;
import clientData.ClientCard;

/**
 * Updates the client view of a card
 * 
 * @author mahogny
 *
 */
public class UserActionGameCardUpdate extends GameAction
	{
	private static final long serialVersionUID = 1L;

	public int playerID;
	public String stackName;
	private int stackPos;
	
	private String front;
	private String back;
	private boolean showsFront=false;
	private double rotation;

	public UserActionGameCardUpdate(int gameID, int playerID, String stackName, int stackPos, ClientCard card)
		{
		this.gameID=gameID;
		
		this.playerID=playerID;
		this.stackName=stackName;
		this.stackPos=stackPos;
		
	//	playerID=card.cardPlayer;
	//	stackName=card.stack;
	//	stackPos=card.stackPos;
		
		front=card.front;
		back=card.back;
		showsFront=card.showsFront;
		rotation=card.rotation;
		}

	public void updateStack(CardStack<ClientCard> stack)
		{
		updateCard(stack.getCard(stackPos));
		}
	
	private void updateCard(ClientCard card)
		{
		card.front=front;
		card.back=back;
		card.showsFront=showsFront;
		card.rotation=rotation;
		}
	}
