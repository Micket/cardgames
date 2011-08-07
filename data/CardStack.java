package data;

import java.util.ArrayList;
import java.util.List;

/**
 * A stack of zero or more cards
 *
 * @author mahogny
 *
 */
public interface CardStack<E>
	{

	/**
	 * We have to think of representing games like MTG - a recursive definition might be better
	 *
	 *
	 */
	public List<E> cards=new ArrayList<E>();

	public void addCard(E c)
		{
		cards.add(c);
		}

    public E drawCard()
        {
        E card = cards.get(0);
        cards.remove(0);
        return card;
        }

    public CardStack<E> drawCards(int n)
        {
        CardStack<E> cards = new CardStack<E>();
        List<E> cl = cards.subList(0,n);
        cards.cards.addAll(cl);
        cl.clear();
        return cards;
        }


	}
