package action;

public class UserActionSetNick extends UserAction
	{
	private static final long serialVersionUID = 1L;
	public String nick;
	

	public UserActionSetNick(String text)
		{
		nick=text;
		}
	}
