package action;

import java.io.Serializable;

public abstract class UserAction implements Serializable
	{
	private static final long serialVersionUID = 1L;
	
	public int fromClientID;
	
	public int gameID; // TODO: Perhaps move to subclass of non-lobby actions?
	}
