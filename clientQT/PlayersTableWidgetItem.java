package clientQT;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QTableWidgetItem;

/**
 * Overloaded 
 * @author micket
 */
public class PlayersTableWidgetItem extends QTableWidgetItem
	{

	private int minplayers, maxplayers, players;

	public PlayersTableWidgetItem(int minplayers, int maxplayers, int players)
		{
		this.minplayers = minplayers;
		this.maxplayers = maxplayers;
		this.players = players;
		setText(players 
				+ (minplayers > 0 ? "("+minplayers+")" : "")
				+ (maxplayers > 0 ? "/"+maxplayers : ""));
		setTextAlignment(Qt.AlignmentFlag.AlignCenter.value());
		}

	public boolean operator_less(QTableWidgetItem x)
		{
		if (x instanceof PlayersTableWidgetItem)
			{
			PlayersTableWidgetItem y = (PlayersTableWidgetItem) x;
			// Whatever game which is closest to beeing playable is "less"

			boolean maxhit = players >= maxplayers;
			boolean y_maxhit = y.players >= y.maxplayers;
			if (maxhit && y_maxhit)
				return players > y.players;
			else if (maxhit != y_maxhit)
				return y_maxhit;
			else
				{
				boolean minactive = minplayers > 0 && players < minplayers;
				boolean y_minactive = y.minplayers > 0 && y.players < y.minplayers;
				if (minactive && y_minactive)
					{
					if (players - minplayers == y.players - y.minplayers)
						{
						return players > y.players;
						}
					else
						{
						return players - minplayers < y.players - y.minplayers;
						}
					}
				else if (minactive != y_minactive)
					{
					return y_minactive;
					}
				}
			// Prioritize the one which has most players.
			return players > y.players;
			}
		return false;
		}
	}
