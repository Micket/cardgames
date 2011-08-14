/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package games;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Every supported game should be annotated with this type
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GameTypePlugin 
	{
	public String name();
	public String description();
	public int maxplayers();
	public int minplayers();
	}