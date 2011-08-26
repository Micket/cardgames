package action;

public class UserActionDragCard extends GameAction
	{
	private static final long serialVersionUID = 1L;
	
	public Integer fromPlayer; //if null then common area
	public String fromStackName; //which stack
	public int fromPos; //which card in stack
	
	public Integer toPlayer; //if null then common area
	public String toStackName; //which stack
	public int toPos; //which card in stack
	
	
	@Override
	public String toString()
		{
		return "DragCard: "+fromPlayer+"/"+fromStackName+"/"+fromPos
		+" to "+
		toPlayer+"/"+toStackName+"/"+toPos;
		}
	}
