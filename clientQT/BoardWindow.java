package clientQT;

import action.GameActionLeave;
import action.Message;
import action.GameActionSendMessage;
import action.GameActionClickedButton;
import action.GameActionDragCard;
import action.GameActionUpdateCard;
import action.GameActionUpdateGameDesign;
import action.GameActionUpdateGameState;
import clientData.GameDesign;
import clientData.ServerListener;

import com.trolltech.qt.gui.*;

/**
 * Window showing one ongoing game
 * 
 */
public class BoardWindow extends QWidget implements ServerListener
	{
	private final ClientQT client;
	private final BoardView view;
	
	private QTextEdit chatHistory;
	private QLineEdit tfEditLine;
	private QHBoxLayout buttonsLayout;
	
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

		buttonsLayout=new QHBoxLayout();
		
		
		QVBoxLayout lobbyLayout=new QVBoxLayout();
		setLayout(lobbyLayout);
		lobbyLayout.addWidget(view);
		lobbyLayout.addLayout(buttonsLayout);
		lobbyLayout.addWidget(chatHistory);
		lobbyLayout.addWidget(tfEditLine);
		
		setMinimumSize(400, 480);
		setWindowTitle(tr("Card game"));
		}

	
		
	public void setButtons(GameDesign design)
		{
		for(final GameDesign.Button b:design.buttons)
			{
			QPushButton qtb=new QPushButton(b.title);
			buttonsLayout.addWidget(qtb);
			
			qtb.clicked.connect(new Runnable()
				{
				public void run()
					{
					client.send(new Message(new GameActionClickedButton(view.gameID, b.id)));
					}
				}, "run()");
			
			}
		
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
	public void eventGameDesign(final GameActionUpdateGameDesign msg)
		{
		if(msg.gameID==view.gameID)
			{
			view.setGameDesign(msg);
			
			QApplication.invokeLater(new Runnable() {
			public void run() {
				setButtons(msg.design); 
			}
			});
			
			}
		}

	public void eventGameStateUpdate(GameActionUpdateGameState msg)
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
	public void eventDragCard(GameActionDragCard action)
		{
		view.dragCard(action);
		}

	@Override
	public void eventGameCardUpdate(GameActionUpdateCard action)
		{
		view.gameCardUpdate(action);
		}
	
	
	
	
	
	}
