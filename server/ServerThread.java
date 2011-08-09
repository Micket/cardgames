package server;

import games.GameLogic;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import action.Message;
import action.UserAction;
import action.UserActionLobbyMessage;


public class ServerThread extends Thread
	{
	
	
	public Set<ConnectionToClient> connections=new HashSet<ConnectionToClient>();

	
	public Set<GameLogic> sessions=new HashSet<GameLogic>();
	
	
	private LinkedList<Message> messages=new LinkedList<Message>();
	
	public void localSend(int fromClientID, Message msg)
		{
		synchronized (messages)
			{
			for(UserAction action:msg.actions)
				action.fromClientID=fromClientID;
			/*
			public int fromClientID; //Filled in by server

			//Problem: not all actions might be from the same client

			
			msg.fromClientID=fromClientID;
			*/
			messages.addLast(msg);
			messages.notifyAll();
			}
		
		}
	
	@Override
	public void run()
		{
		for(;;)
			{
			//TODO there might be more messages. don't wait if there is more

			Message msg;
			synchronized (messages)
				{
				try
					{
					messages.wait();
					}
				catch (InterruptedException e)
					{
					e.printStackTrace();
					}
				
				msg=messages.poll();
				System.out.println(msg);
				}
			if(msg!=null)
				{
				Message outMsg=new Message();
				
				for(UserAction action:msg.actions)
					if(action instanceof UserActionLobbyMessage)
						{
						UserActionLobbyMessage lm=(UserActionLobbyMessage)action;
						lm.fromUser=action.fromClientID;
						outMsg.add(lm);
						broadcast(outMsg);
						
						System.out.println("got message "+lm.message);
						
						
						
						}
				
				}
			
			
			}
		
		
		
		}
	
	private void broadcast(Message msg)
		{

		//Pass message on to all clients
		
		for(ConnectionToClient conn:connections)
			{
			if(conn.localClient!=null)
				conn.localClient.localSend(msg);
			else
				;//TODO
			}
		}
	
	}
