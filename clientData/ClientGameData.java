package clientData;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import clientQT.QtGraphicsData;

public class ClientGameData
	{

	/**
	 * For the name of a card, what image does it map to?
	 */
	public Map<String,String> imageMap=new HashMap<String, String>();
	
	
	
	/**
	 * Heaps for each player. Common heap is -1. This ID is reserved, all user IDs are positive
	 */
	public Map<Integer, ClientPlayerData> playerMap=new HashMap<Integer, ClientPlayerData>();

	
	
	public ClientGameData()
		{
		//This is the default deck of cards. It can be extended with custom cards
		for(int i=1;i<=12;i++)
			{
			imageMap.put("poker Spades "+i, "spades"+i+".svg");
			imageMap.put("poker Diamonds "+i, "diamonds"+i+".svg");
			imageMap.put("poker Hearts "+i, "hearts"+i+".svg");
			imageMap.put("poker Clubs "+i, "clubs"+i+".svg");
			}

		imageMap.put("poker back","pokerbackside.svg");
		imageMap.put("emptypos","empty.svg");

		//System.out.println(imageMap);
		}
	

	/**
	 * Images should be stored on the client for bandwidth. But it would make sense to allow the server to provide missing cards,
	 * in case a game need a very specialized card - this allows for more stupid clients.
	 * 
	 * Interface problem: Now this depends on QT
	 */
	public QtGraphicsData getImageForCard(String card)
		{
		String cardFile=imageMap.get(card);
		if(cardFile==null)
			{
			throw new RuntimeException("No card image map for "+card); //Later: Request image from server
			}
			
		String file="cards/"+cardFile;
		if(new File("cards",cardFile).exists())
			return new QtGraphicsData(new File("cards",cardFile));
		else
			throw new RuntimeException("No such file, "+file); //Later: Request image from server
		}
	
	}
