package clientData;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import serverData.CardStack;

/**
 * The graphical design of the game
 * 
 * @author mahogny
 *
 */
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


	public static class Button
		{
		public String title;
		public String id;
		
		public Button(String title, String id)
			{
			this.title = title;
			this.id = id;
			}
		}
	
	/**
	 * Buttons. TODO should this maybe be dynamic?
	 * 
	 * title -> id
	 */
	public List<Button> buttons=new LinkedList<Button>();

	public void addButton(String title, String id)
		{
		buttons.add(new Button(title, id));
		}

	

	}
