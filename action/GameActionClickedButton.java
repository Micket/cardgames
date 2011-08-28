package action;

public class GameActionClickedButton extends GameAction
	{
	private static final long serialVersionUID = 1L;

	public String buttonID;
	
	public GameActionClickedButton(int gameID, String buttonID)
		{
		this.gameID=gameID;
		this.buttonID=buttonID;
		}
	}
