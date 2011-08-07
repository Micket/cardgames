package games;

public class UserAction
	{
	public static final int JOIN=0, LEAVE=1, MOVECARD=2;
	
	public int type;
	
	
	
	
	public UserAction moveCard() //from (stack, number), to ()
		{
		UserAction action=new UserAction();
		action.type=MOVECARD;
	
		
		
		
		return action;
		}
	
	}
