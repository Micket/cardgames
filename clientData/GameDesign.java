package clientData;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import serverData.CardStack;

public class GameDesign implements Serializable
	{
	private static final long serialVersionUID = 1L;
	
	public static class FieldDef implements Serializable
		{
		private static final long serialVersionUID = 1L;
		
		public Map<String,StackDef> stacks=new HashMap<String, StackDef>();
		
		public StackDef createStack(String name)
			{
			StackDef def=new StackDef();
			stacks.put(name, def);
			return def;
			}
		}

	public static class StackDef implements Serializable
		{
		private static final long serialVersionUID = 1L;
		
		public CardStack<?> stack; //This cardstack has no cards. it is just to pull out options. but should they even be there?
		public double x;
		public double y;
		
		}
	

	public FieldDef playerField=new FieldDef();
	public FieldDef commonField=new FieldDef();



	}
