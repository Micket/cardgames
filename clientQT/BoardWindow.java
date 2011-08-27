package clientQT;

import action.GameActionLeave;
import action.Message;
import action.GameActionSendMessage;
import action.UserActionDragCard;
import action.UserActionGameCardUpdate;
import action.UserActionGameDesign;
import action.UserActionGameStateUpdate;
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
	
	private QTextEdit chatHistory;
	private QLineEdit tfEditLine;

	private final int gameID;
	
	public BoardWindow(ClientQT client, int gameID)
		{
		this.client=client;
		this.gameID=gameID;
		
		view=new BoardView(client, this, gameID);

		
		chatHistory=new QTextEdit(this);
		chatHistory.setReadOnly(true);
		chatHistory.setMinimumHeight(50);
		chatHistory.setMaximumHeight(50);
		chatHistory.setSizePolicy(QSizePolicy.Policy.Ignored,QSizePolicy.Policy.Fixed);
		
		tfEditLine=new QLineEdit(this);
		tfEditLine.returnPressed.connect(this,"actionSendMessage()");

		QVBoxLayout lobbyLayout=new QVBoxLayout();
		setLayout(lobbyLayout);
		lobbyLayout.addWidget(view);
		lobbyLayout.addWidget(chatHistory);
		lobbyLayout.addWidget(tfEditLine);
		
		setMinimumSize(400, 480);
		setWindowTitle(tr("Card game"));
		}

	public void showStatusMessage(final String str)
		{
		QApplication.invokeLater(new Runnable() {
			public void run() {
				chatHistory.append("## "+str);
			}
			});
		}

	public void actionSendMessage()
		{
		String text=tfEditLine.text();
		if(!text.equals(""))
			{
			tfEditLine.setText("");
			client.send(new Message(new GameActionSendMessage(gameID, text))); 
			}
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

	@Override
	public void eventGameDesign(UserActionGameDesign msg)
		{
		if(msg.gameID==view.gameID)
			view.setGameDesign(msg);
		}

	public void eventGameStateUpdate(UserActionGameStateUpdate msg)
		{
		if(msg.gameID==view.gameID)
			view.setGameState(msg);
		}

	@Override
	public void eventGameMessage(final GameActionSendMessage action)
		{
		if(action.gameID==gameID)
			QApplication.invokeLater(new Runnable() {
			public void run() {
				chatHistory.append(client.getNickFor(action.fromClientID)+": "+action.message);
			}
			});
		}

	@Override
	public void eventDragCard(UserActionDragCard action)
		{
		view.dragCard(action);
		}

	@Override
	public void eventGameCardUpdate(UserActionGameCardUpdate action)
		{
		view.gameCardUpdate(action);
		}
	
	
	
	
	
	}
