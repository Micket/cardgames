package action;

public class UserActionClickedCard extends GameAction
	{
	private static final long serialVersionUID = 1L;
	
	public Integer player; //if null then common area
	public String stack; //which stack
	public int stackPos; //which card in stack
	
	@Override
	public String toString()
		{
		return "UsedClickedCard{player="+player+",stack="+stack+",pos="+stackPos+"}";
		}
	}
