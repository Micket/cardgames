package action;

public class GameActionClickedCard extends GameAction
	{
	private static final long serialVersionUID = 1L;
	
	public Integer playerID; //if null then common area
	public String stackName; //which stack
	public int stackPos; //which card in stack
	
	@Override
	public String toString()
		{
		return "UsedClickedCard{player="+playerID+",stack="+stackName+",pos="+stackPos+"}";
		}
	}
