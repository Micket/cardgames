package action;

import java.util.LinkedList;
import java.util.List;

public class Message
	{
	public Integer messageID; //To be returned when replying
	public Integer replyTo;
	public List<UserAction> actions=new LinkedList<UserAction>();
	
	
	public void add(UserAction action)
		{
		actions.add(action);
		}
	
	
	
	}
