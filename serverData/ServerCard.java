package serverData;

import clientData.ClientCard;

/**
 * Card, as seen from the server
 * 
 *
 */
public abstract class ServerCard
	{
//	public boolean showsFront=false;
//	public int rotation=0; //Can be 0,1,2,3, going clockwise

    // There replaces the strings;
    //public String getID() { return ""; }
    //public int getDeckNumber() { return 0; }

  abstract public ClientCard toClientCard();

	}
