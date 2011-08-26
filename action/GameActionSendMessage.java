package action;

public class GameActionSendMessage extends GameAction
	{
	private static final long serialVersionUID = 1L;
	public String message;
	

	public GameActionSendMessage(int gameID, String text)
		{
		this.gameID=gameID;
		message=text;
		}
	}
