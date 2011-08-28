package action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import serverData.CardStack;
import clientData.ClientCard;

/**
 * Sent to client to update the entire state of a game. Only used when a game is started and when a user joins an on-going game 
 * 
 * @author mahogny
 *
 */
public class GameActionUpdateGameState extends GameAction
	{
	private static final long serialVersionUID = 1L;

	public static class PlayerState implements Serializable
		{
		private static final long serialVersionUID = 1L;
		public Map<String, CardStack<ClientCard>> stacks=new HashMap<String, CardStack<ClientCard>>();
		}
	
	public Map<Integer, PlayerState> player=new HashMap<Integer, PlayerState>();
		
	public GameActionUpdateGameState(int gameID)
		{
		this.gameID=gameID;
		}
	
	public PlayerState createPlayer(int id)
		{
		PlayerState s=new PlayerState();
		player.put(id,s);
		return s;
		}

	}
