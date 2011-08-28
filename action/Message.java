package action;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Message implements Serializable
	{
	private static final long serialVersionUID = 1L;
	
	public Integer messageID; //To be returned when replying
	public Integer replyTo;
	public List<Action> actions=new LinkedList<Action>();
	

	/**
	 * Empty message
	 */
	public Message()
		{
		}
	
	/**
	 * Message with one action added
	 */
	public Message(Action action)
		{
		actions.add(action);
		}
	
	public void add(Action action)
		{
		actions.add(action);
		}
	
	
	@Override
	public String toString()
		{
		return messageID+","+replyTo+","+actions.toString();
		}
	}
