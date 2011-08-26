/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package games;

import java.io.Serializable;

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
	
//	public Class<? extends GameLogic> game;

	
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

	@Override
	public int compareTo(GameType o)
		{
		return name.compareTo(o.name);
		}
	}