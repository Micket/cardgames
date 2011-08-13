package clientData;

import java.util.Set;

/**
 * Information presented to lobby users.
 * @author micket
 */
public class GameMetaData
	{
	public String name;
	public String type;
	
	public int maxusers;
	public int minusers;
	public Set<Integer> joinedUsers;
	}
