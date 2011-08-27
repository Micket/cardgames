package action;

public class UserActionClickedButton extends GameAction
	{
	private static final long serialVersionUID = 1L;

	public String buttonID;
	
	public UserActionClickedButton(int gameID, String buttonID)
		{
		this.gameID=gameID;
		this.buttonID=buttonID;
		}
	}
