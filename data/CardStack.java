package data;

import java.util.ArrayList;
import java.util.List;

/**
 * A stack of one or more cards
 *  
 * @author mahogny
 *
 */
public class CardStack
	{

	/**
	 * We have to think of representing games like MTG - a recursive definition might be better
	 * 
	 * 
	 */
	public List<Card> cards=new ArrayList<Card>();
	
	
	
	public void addCard(Card c)
		{
		cards.add(c);
		}
	
	}
