package action;



import clientData.GameDesign;


/**
 * Definition of a game
 *
 * @author mahogny
 */
public class GameActionUpdateGameDesign extends GameAction
	{
	private static final long serialVersionUID = 1L;

	public GameDesign design=new GameDesign();

	
	public GameActionUpdateGameDesign(int gameID, GameDesign design)
		{
		this.gameID=gameID;
		this.design=design;
		}
	
	}
