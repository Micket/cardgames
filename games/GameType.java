/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package games;

/**
 *
 * @author micket
 */
public class GameType
	{
	String name;
	String description;
	int maxplayers;
	int minplayers;
	
	Class<? extends GameLogic> game;
	}