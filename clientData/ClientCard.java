package clientData;

import java.io.Serializable;

/**
 * Card, as seen from the client
 * 
 *
 */
public class ClientCard implements Serializable
	{
	private static final long serialVersionUID = 1L;
	
	public String front;
	public String back;

	public boolean showsFront=false;

	
	public double rotation;
	
	public double rotationY;
	
	public int cardPlayer;
	public String stackName;
	public int stackPos;
	
	
	}
