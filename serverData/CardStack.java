package serverData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * A stack of zero or more cards.
 * Should this even extend list? I find myself wrapping everything..
 * @author mahogny
 *
 */
public class CardStack<E>
	{

	public static enum StackStyle implements Serializable
		{
		Hand, Solitaire, Deck
		}
	
	public StackStyle stackStyle=StackStyle.Hand;
	
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

    public void addCards(CardStack<E> cs)
        {
        cards.addAll(cs.cards);
        }

    public E drawCard()
        {
        E card = cards.get(0);
        cards.remove(0);
        return card;
        }

    public CardStack<E> drawCards(int n)
        {
        CardStack<E> cs = new CardStack<E>();
        List<E> cl = cards.subList(0,n);
        cs.cards.addAll(cl);
        cl.clear();
        return cs;
        }

    public E getCard(int i) { return cards.get(i); }
    public int size() { return cards.size(); }
    public void shuffle() { Collections.shuffle(cards); }
    public void clear() { cards.clear(); }
    public void moveCard(int from, int to)
        {
        List<E> new_order;
        E card = cards.get(from);
        if (to < from) // Not pretty, or fast...
            {
            new_order = cards.subList(0,to);
            new_order.add(card);
            new_order.addAll(cards.subList(to,from));
            new_order.addAll(cards.subList(from+1,cards.size()));
            }
        else
            {
            new_order = cards.subList(0,from);
            new_order.addAll(cards.subList(from+1,to+1));
            new_order.add(card);
            new_order.addAll(cards.subList(to+1,cards.size()));
            }
        cards = new_order;
        }
	}
