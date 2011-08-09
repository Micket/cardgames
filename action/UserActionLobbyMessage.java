package action;

public class UserActionLobbyMessage extends UserAction
	{
	public String message;
	public int fromUser; //Filled in by server, not client

	public UserActionLobbyMessage(String text)
		{
		message=text;
		}
	}
