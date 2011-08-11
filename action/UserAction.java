package action;

import java.io.Serializable;

public abstract class UserAction implements Serializable
	{
	private static final long serialVersionUID = 1L;
	
	public int fromClientID;
	//public int fromUser; //Filled in by server, not client
    //public int fromUser;
	}
