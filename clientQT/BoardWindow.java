package clientQT;

import action.GameActionLeave;
import action.Message;

import clientData.ServerListener;

import com.trolltech.qt.gui.*;

/**
 * Window showing one ongoing game
 * 
 */
public class BoardWindow extends QWidget implements ServerListener
	{
	
	private ClientQT client;
	private BoardView view;
	
	public BoardWindow(ClientQT client, int gameID)
		{
		this.client=client;
		
		view=new BoardView(client, this, gameID);

		QHBoxLayout lobbyLayout=new QHBoxLayout();
		setLayout(lobbyLayout);
		lobbyLayout.addWidget(view);

		setMinimumSize(400, 400);
		setWindowTitle(tr("Card game"));
		}

	public void eventServerMessage(Message msg)
		{
		
		}

	@Override
	public void eventNewUserList()
		{
		}

	@Override
	public void eventNewGameSessions()
		{
		}

	@Override
	protected void closeEvent(QCloseEvent arg)
		{
		GameActionLeave a=new GameActionLeave();
		a.gameID=view.gameID;
		client.send(new Message(a));
		
		client.serverListeners.remove(this);
		client.boardViewsExistFor.remove(view.gameID);
		}
	
	}
