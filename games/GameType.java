/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package games;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import util.ClassHandling;

/**
 *
 * @author micket
 */
public class GameType implements Serializable, Comparable<GameType>
	{
	private static final long serialVersionUID = 1L;
	
	public String name;
	public String category;
	public String description;
	public int maxplayers;
	public int minplayers;

	
	public GameType()
		{
		}
	
	public GameType(GameTypePlugin p, Class<? extends GameLogic> game)
		{
		name=p.name();
		category=p.category();
		description=p.description();
		maxplayers=p.maxplayers();
		minplayers=p.minplayers();
//		this.game=game;
		}
	
	@Override
	public String toString()
		{
		return category+"."+name;
		}

	public int compareTo(GameType o)
		{
		return name.compareTo(o.name);
		}

	/**
	 * Detect available game types
	 */
	public static Map<Class<? extends GameLogic>, GameType> availableGames()
		{
		Map<Class<? extends GameLogic>, GameType> games=new HashMap<Class<? extends GameLogic>, GameType>();
		
		addGames(games, getClassesFromJar());
		
		try
			{
			addGames(games, ClassHandling.getClasses("games"));
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		
		return games;
		}

	@SuppressWarnings("unchecked")
	private static void addGames(Map<Class<? extends GameLogic>, GameType> games, Collection<Class<?>> classes)
		{
		for(Class<?> cl:classes)
			{
			GameTypePlugin plugin=cl.getAnnotation(GameTypePlugin.class);
			if(plugin!=null)
				{
				System.out.println("Adding game: "+cl);
				GameType gt=new GameType(plugin, (Class<? extends GameLogic>)cl);
				games.put((Class<? extends GameLogic>)cl,gt);
				}
			}
		}

	private static List<Class<?>> getClassesFromJar()
		{
		LinkedList<Class<?>> classes=new LinkedList<Class<?>>();
		InputStream is=GameLogic.class.getResourceAsStream("gameslist.txt");
		if(is!=null)
			try
				{
				BufferedReader br=new BufferedReader(new InputStreamReader(is));
				String line;
				while((line=br.readLine())!=null)
					{
					line=line.replace("/", ".");
					line=line.substring(0, line.length()-".java".length());
					try
						{
						classes.add(Class.forName(line));
						}
					catch (ClassNotFoundException e)
						{
						e.printStackTrace();
						}
					}
				}
			catch (IOException e)
				{
				e.printStackTrace();
				}
		return classes;
		}
	}