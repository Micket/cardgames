package clientQT;

import games.GameType;
import games.GameLogic;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.TreeMap;

import util.CardGameInfo;

import action.GameActionJoin;
import action.GameActionSendMessage;
import action.Message;
import action.Action;
import action.GameActionDragCard;
import action.GameActionUpdateCard;
import action.GameActionUpdateGameDesign;
import action.GameActionUpdateGameState;
import action.ActionLobbyMessage;
import action.ActionSetNick;
import action.ActionStartGame;

import clientData.Client;
import clientData.ServerListener;
import clientData.GameInfo;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.core.Qt.ItemFlag;
import com.trolltech.qt.gui.*; // Fuck it..
import com.trolltech.qt.gui.QAbstractItemView.SelectionBehavior;

public class LobbyWindow extends QWidget implements ServerListener
	{
	private QPushButton bNick;
	private QPushButton bJoin;
	
	private QTreeWidget nickList;
	private Map<Integer,QTreeWidgetItem> nicks = new HashMap<Integer, QTreeWidgetItem>();
	private QTableWidget gameList;
	private QTextEdit chatHistory;
	private QLineEdit tfEditLine;
	
	private QMenuBar menuBar;
	private QMenu miNewGame;
	
	private Client client;
	
	public LobbyWindow(Client client)
		{
		this.client=client;

		QHBoxLayout lobbyLayout=new QHBoxLayout();
		QVBoxLayout chatWindowLayout=new QVBoxLayout();
		QHBoxLayout chatInputLayout=new QHBoxLayout();
		
		QSplitter chatAndSidebar = new QSplitter();
		QSplitter nickAndGame = new QSplitter();
		
		nickAndGame.setOrientation(Qt.Orientation.Vertical);
		chatAndSidebar.setOrientation(Qt.Orientation.Horizontal);

		bNick=new QPushButton(client.getNick()+":", this);
		bNick.clicked.connect(this, "actionChangeNick()");
		bNick.setSizePolicy(QSizePolicy.Policy.Minimum, QSizePolicy.Policy.Minimum);

		bJoin=new QPushButton("Join",this);
		bJoin.clicked.connect(this, "actionJoinGame()");

		chatHistory=new QTextEdit(this);
		chatHistory.setReadOnly(true);
		
		tfEditLine=new QLineEdit(this);
		tfEditLine.returnPressed.connect(this,"actionSendMessage()");

		nickList=new QTreeWidget();
		nickList.header().close();
		
		gameList=new QTableWidget();
		gameList.setColumnCount(3);
		gameList.setColumnWidth(0,30);
		gameList.setShowGrid(false);
		gameList.setHorizontalHeaderLabels(Arrays.asList("#","Type","Session"));
		gameList.verticalHeader().hide();
		gameList.horizontalHeader().setStretchLastSection(true);
		gameList.setAlternatingRowColors(true);
		gameList.setSortingEnabled(true);
		gameList.setSelectionBehavior(SelectionBehavior.SelectRows);
		gameList.doubleClicked.connect(this, "actionJoinGame()");
		
		// Lobby layout
		setLayout(lobbyLayout);
		lobbyLayout.setContentsMargins(5,5,5,5);
		lobbyLayout.addWidget(chatAndSidebar);
		QWidget temp = new QWidget();
		temp.setLayout(chatWindowLayout); // This gives extra padding.
		temp.setContentsMargins(0, 0, 0, 0);
		
		chatWindowLayout.setContentsMargins(0,0,0,0);
		
		chatAndSidebar.addWidget(temp);
		chatAndSidebar.addWidget(nickAndGame);
		chatWindowLayout.addWidget(chatHistory);
		chatWindowLayout.addLayout(chatInputLayout);
		chatWindowLayout.setSpacing(0);
		
		chatInputLayout.addWidget(bNick);
		chatInputLayout.addWidget(tfEditLine);
		chatInputLayout.setSpacing(0);
		
		nickAndGame.addWidget(nickList);
		nickAndGame.addWidget(gameList);
		nickAndGame.addWidget(bJoin);
		
		setWindowTitle("Lobby");
		
		menuBar=new QMenuBar();
		lobbyLayout.setMenuBar(menuBar);
		buildMenuBar();
		}

	public void buildMenuBar()
		{
		QAction miExit = new QAction(tr("E&xit"), this);
		//miExit.setShortcuts(QKeySequence.Quit);
		miExit.setStatusTip(tr("Exit the application"));
		miExit.triggered.connect(this, "actionExit()");

		QAction miConnect = new QAction(tr("Connect"), this);
		miConnect.setStatusTip(tr("Connect to server"));
		miConnect.triggered.connect(this, "actionConnect()");

		QAction miDisconnect = new QAction(tr("Disconnect"), this);
		miDisconnect.setStatusTip(tr("Disconnect to server"));
		miDisconnect.triggered.connect(this, "actionDisconnect()");

		QAction miAbout = new QAction(tr("About"), this);
		miAbout.setStatusTip(tr("About this program"));
		miAbout.triggered.connect(this, "actionAbout()");

		QMenu mClient = menuBar.addMenu("&Client");
		mClient.addAction(miConnect);
		mClient.addAction(miDisconnect);
		mClient.addAction(miExit);
		miNewGame = menuBar.addMenu("&New");


		QMenu helpMenu = menuBar.addMenu(tr("&Help"));
		helpMenu.addAction(miAbout);
		
		}

	public void eventServerMessage(Message msg)
		{
		for(Action action:msg.actions)
			if(action instanceof ActionLobbyMessage)
				showLobbyMessage((ActionLobbyMessage)action);
		
		}

	
	public void actionExit()
		{
		System.out.println("Exiting...");
		client.disconnectFromServer();
		System.exit(0);
		}

	public void actionAbout()
		{
		QMessageBox.about(this, tr("About "+CardGameInfo.programName),
        tr("<b>"+CardGameInfo.programName+" QT client"+"</b> version "+CardGameInfo.version+"<br/>"+
           "by Johan Henriksson & Mikael Öhman<br/>"
           ));
		}
	
	public void actionSendMessage()
		{
		String text=tfEditLine.text();
		if(!text.equals(""))
			{
			tfEditLine.setText("");
			client.send(new Message(new ActionLobbyMessage(text)));
			}
		}
	
	public void actionChangeNick()
		{
		String text = QInputDialog.getText(this, "New nick", "Nick:", QLineEdit.EchoMode.Normal, client.getNick());
		if (text != null)
			client.send(new Message(new ActionSetNick(text)));
		}
	
	
	public void actionConnect()
		{
		new ConnectToServerDialog(client).show();
		}
	
	public void actionDisconnect()
		{
		client.disconnectFromServer();
		}
	
	public void actionJoinGame()
		{
		for(QTableWidgetItem i:gameList.selectedItems())
			{
			Integer sessionID=mapGameList2sessionID.get(i);
			if(sessionID!=null)
				{
				client.send(new Message(new GameActionJoin(sessionID)));
				return;
				}
			}
		}
	
	
	public void closeEvent(QCloseEvent e)
		{
		actionExit();
		}
	

	public void showStatusMessage(final String str)
		{
		QApplication.invokeLater(new Runnable() {
			public void run() {
				chatHistory.append("## "+str);
			}
			});
		}
	
	public void showLobbyMessage(final ActionLobbyMessage lm)
		{
		QApplication.invokeLater(new Runnable() {
			public void run() {
				chatHistory.append(client.getNickFor(lm.fromClientID)+": "+lm.message);
			}
			});
		}
	
	public void setNickList()
		{
		nickList.clear();
		nicks.clear();
		for(Map.Entry<Integer,String> u:client.mapClientIDtoNick.entrySet())
			{
			List<String> usertexts = new ArrayList<String>(1); // Just nick (perhaps nick + comment?)
			usertexts.add(u.getValue());
			QTreeWidgetItem user = new QTreeWidgetItem(usertexts);
			nickList.insertTopLevelItem(0, user);
			nicks.put(u.getKey(), user);
			}
		setGameSessions();
		
		}

	private Map<QTableWidgetItem, Integer> mapGameList2sessionID=new HashMap<QTableWidgetItem, Integer>();
	
	public void setGameSessions()
		{
		gameList.clear();
		gameList.setRowCount(client.gameSessions.size());
		gameList.setHorizontalHeaderLabels(Arrays.asList("#","Type","Session"));
		System.out.println("game sessions: "+client.gameSessions);
		
		// Delete all old entries first. (Why is there no clear()?
		for(QTreeWidgetItem w:nicks.values())
			for(QTreeWidgetItem s:w.takeChildren())
				w.removeChild(s);
		mapGameList2sessionID.clear();
		
		int currentRow = 0;
		for(int sessionID:client.gameSessions.keySet())
			{
			GameInfo g=client.gameSessions.get(sessionID);
			GameType gt=client.gameTypes.get(g.type);
			if(gt==null)
				System.out.println("Error: gametype does not exist "+gt); //This is a problem with the local connection
			
			// TODO: Store metadata in some way to allow sorting and such.. (perhaps use a tree view)
			QTableWidgetItem newGameType=new QTableWidgetItem(gt.name);
			System.out.println("minusers = "+g.minusers+", maxusers = "+g.maxusers);
			QTableWidgetItem newGameUsersItem=new PlayersTableWidgetItem(g.minusers, g.maxusers, g.joinedUsers.size());
			//QTableWidgetItem newGameUsersItem=new QTableWidgetItem( g.maxusers < 0 ? ""+g.joinedUsers.size() : ""+g.joinedUsers.size()+"/"+g.maxusers);
			QTableWidgetItem newGameName=new QTableWidgetItem( g.sessionName );
			mapGameList2sessionID.put(newGameName,sessionID);
			
			newGameUsersItem.setFlags(ItemFlag.ItemIsSelectable, ItemFlag.ItemIsEnabled);
			newGameType.setFlags(ItemFlag.ItemIsSelectable, ItemFlag.ItemIsEnabled);
			newGameName.setFlags(ItemFlag.ItemIsSelectable, ItemFlag.ItemIsEnabled);

			gameList.setItem(currentRow,0,newGameUsersItem);
			gameList.setItem(currentRow,1,newGameType);
			gameList.setItem(currentRow,2,newGameName);
			currentRow++;
			


			
			// Maybe something like this?
			List<String> temptext = new ArrayList<String>(1);
			temptext.add(g.sessionName + " ("+gt.name+")"); // temptext.add(gt.name); and possible other data..
			QTreeWidgetItem temp=new QTreeWidgetItem(temptext);
			for(Integer u:g.joinedUsers)
				nicks.get(u).addChild(temp);
			}

		}
	
	public void setGameTypes()
		{
		System.out.println("Filling in the list of available games. ("+client.gameTypes.size()+" in total).");
		miNewGame.clear();
		
		//Sort games by name
		Map<GameType, Class<? extends GameLogic>> gameTypes=new TreeMap<GameType, Class<? extends GameLogic>>();
		for(Map.Entry<Class<? extends GameLogic>, GameType> g:client.gameTypes.entrySet())
			gameTypes.put(g.getValue(),g.getKey());
		
		//Create menu entries
		//TODO submenu for each category
		for(Map.Entry<GameType, Class<? extends GameLogic>> g:gameTypes.entrySet())
			{
			final Class<? extends GameLogic> cl=g.getValue();
			final GameType gt=g.getKey();
			QAction menuaction = new QAction(gt.name, miNewGame);
			miNewGame.addAction(menuaction);
			menuaction.triggered.connect(new Runnable()
				{
					public void run()
						{
						System.out.println("new game "+cl);
						// TODO Auto-generated method stub
						
						ActionStartGame a=new ActionStartGame();
						a.sessionName = QInputDialog.getText(null, "Session name", gt.description+"\nSession name:", QLineEdit.EchoMode.Normal, "My game");
						if (a.sessionName == null)
							return; // Then no new game!
						a.game=cl;
						client.send(new Message(a));
						
						
						}
				}, "run()");
			menuaction.setIconVisibleInMenu(true); // TODO: Make some icons perhaps?
			menuaction.setToolTip(gt.description); // TODO: Doesn't seem to show up?
			menuaction.setIcon(QPixmap.fromImage(new QImage("./cards/playingcard_icon.png")));
			}
		}

	
	public void eventNewUserList()
		{
		System.out.println("list of nicks: "+client.mapClientIDtoNick);
		QApplication.invokeLater(new Runnable() {
			public void run() {
				setNickList();
			}
		});

		//Update our nick
		final String ourNick=client.getNickFor(client.getClientID());
		QApplication.invokeLater(new Runnable() {
			public void run() {
				bNick.setText(ourNick+":");
			}
		});

		}

	public void eventNewGameSessions()
		{
		System.out.println("New game list...");
		QApplication.invokeLater(new Runnable() {
			public void run() {
				setGameTypes(); // TODO: This shouldn't be here.
				setGameSessions();
			}
		});
		}

	public void eventGameDesign(GameActionUpdateGameDesign msg)
		{
		}

	public void eventGameMessage(GameActionSendMessage action)
		{
		}

	public void eventGameStateUpdate(GameActionUpdateGameState action)
		{
		}

	public void eventDragCard(GameActionDragCard action)
		{
		}

	public void eventGameCardUpdate(GameActionUpdateCard action)
		{
		}

	
	//TODO on exit, stop the thread and properly shut down the game
	}
