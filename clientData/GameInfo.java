package clientData;

import games.GameLogic;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Information presented to lobby users.
 * @author micket
 */
public class GameInfo implements Serializable
	{
	private static final long serialVersionUID = 1L;

	public Class<? extends GameLogic> type; //Maybe better to keep this as a string?
//	public String name;
//	public String category;
	
	public int maxusers;
	public int minusers;
	public Set<Integer> joinedUsers=new HashSet<Integer>();
	public String sessionName;
	
	
	@Override
	public String toString()
		{
		return "GameInfo{"+sessionName+"  "+joinedUsers+"}";
		}
	}
