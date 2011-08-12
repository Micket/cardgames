package clientQT;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import server.ServerThread;

import action.Message;
import action.UserAction;
import action.UserActionLobbyMessage;

import clientData.ServerListener;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMenuBar;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QTextEdit;
import com.trolltech.qt.gui.QSplitter;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QInputDialog;
import com.trolltech.qt.gui.QSizePolicy;
import com.trolltech.qt.gui.QTableWidget;
import com.trolltech.qt.gui.QTableWidgetItem;

import com.trolltech.qt.gui.QTreeWidget;
import com.trolltech.qt.gui.QTreeWidgetItem;

import com.trolltech.qt.gui.QWidget;

public class LobbyWindow extends QWidget implements ServerListener
	{
	private QAction miExit;
	private QAction miConnect;

	private QPushButton bNick;
	
	private QTreeWidget userList;
	private QTableWidget gameList;
	
	private QTextEdit chatHistory;
	
	private QLineEdit tfEditLine;
	
	private QMenuBar menuBar;
	
	private String nick; // TODO: Get from server and so on.
	
	
	private Client client;
	
	public LobbyWindow(Client client)
		{
		this.client=client;

		QHBoxLayout lobbyLayout=new QHBoxLayout();
		//QSplitter lobbyLayout=new QSplitter();
		//lobbyLayout.setOrientation(Qt.Orientation.Horizontal);
		QVBoxLayout chatWindowLayout=new QVBoxLayout();
		QHBoxLayout chatInputLayout=new QHBoxLayout();
		QSplitter userAndGameLayout = new QSplitter();
		userAndGameLayout.setOrientation(Qt.Orientation.Vertical);


		nick = "Mahogny";
		bNick=new QPushButton(nick+":", this);
		bNick.clicked.connect(this, "actionChangeNick()");
		bNick.setSizePolicy(QSizePolicy.Policy.Minimum, QSizePolicy.Policy.Minimum);
		chatHistory=new QTextEdit(this);
		chatHistory.setReadOnly(true);
		
		tfEditLine=new QLineEdit(this);
		tfEditLine.returnPressed.connect(this,"actionSendMessage()");

		userList=new QTreeWidget();
		
		//userList.setColumnCount(2);
		//userList.setColumnWidth(0,30);

		gameList=new QTableWidget();
		gameList.setColumnCount(2);
		gameList.setColumnWidth(0,30);
		gameList.setShowGrid(false);
		
		// TODO: Real user list
		List<String> testuserentry = new ArrayList<String>();
		testuserentry.add(nick);
		testuserentry.add("Come at me!");
		QTreeWidgetItem testUser = new QTreeWidgetItem(testuserentry);
		userList.insertTopLevelItem(0, testUser);
		userList.setHeader(null);
		//userList.setRowCount(1);
		//userList.setItem(0,1,newUserItem);
		// TODO: Real game list
		QTableWidgetItem newGameItem=new QTableWidgetItem("BlackJack");
		QTableWidgetItem newGameUsersItem=new QTableWidgetItem("1/6");
		gameList.setRowCount(1);
		gameList.setItem(0,0,newGameUsersItem);
		gameList.setItem(0,1,newGameItem);

		// Lobby layout
		setLayout(lobbyLayout);
		lobbyLayout.addLayout(chatWindowLayout);
		lobbyLayout.addWidget(userAndGameLayout);
		chatWindowLayout.addWidget(chatHistory);
		chatWindowLayout.addLayout(chatInputLayout);
		chatInputLayout.addWidget(bNick);
		chatInputLayout.addWidget(tfEditLine);
		userAndGameLayout.addWidget(userList);
		userAndGameLayout.addWidget(gameList);
		
		
		setWindowTitle("Lobby");

		menuBar=new QMenuBar();
		lobbyLayout.setMenuBar(menuBar);
		
		miExit = new QAction(tr("E&xit"), this);
    //miExit.setShortcuts(QKeySequence.Quit);
    miExit.setStatusTip(tr("Exit the application"));
    miExit.triggered.connect(this,"actionExit()");

		miConnect = new QAction(tr("Connect"), this);
    //miExit.setShortcuts(QKeySequence.Quit);
    miConnect.setStatusTip(tr("Connect to server"));
    miConnect.triggered.connect(this,"actionConnect()");
		
		
		
		QMenu fileMenu = menuBar.addMenu(tr("&File"));
    fileMenu.addAction(miExit);
    fileMenu.addAction(miConnect);
    //helpMenu = menuBar()->addMenu(tr("&Help"));
		/*
		

		setFixedSize(400, 400);
		//setCentralWidget(view);
		
		//view.ensureVisible(0, 0, view.width(), view.height());
*/
		
		
		}
	

	public void eventServerMessage(Message msg)
		{
		for(UserAction action:msg.actions)
			if(action instanceof UserActionLobbyMessage)
				showLobbyMessage((UserActionLobbyMessage)action);
		
		}

	
	public void actionExit()
		{
		System.out.println("hello");
		System.exit(0);
		//TODO handle connections
		}
	
	
	public void actionSendMessage()
		{
		String text=tfEditLine.text();
		if(!text.equals(""))
			{
			tfEditLine.setText("");
			
			UserActionLobbyMessage lm=new UserActionLobbyMessage(text);
			
			Message msg=new Message();
			msg.add(lm);
			client.serverConn.send(msg);
			}
		}
	
	public void actionChangeNick()
		{
		String text = QInputDialog.getText(this, "New nick", "Nick:", QLineEdit.EchoMode.Normal, nick);
		if (text != null)
			{
			// TODO: Instead of sending this, should send a request to change the nick to the server.
			nick = text;
			bNick.setText(nick+":");
			}
		}
	
	
	public void actionConnect()
		{
		//TODO dialog
		
		String hostName="localhost";
		int port=ServerThread.defaultServerPort;
		
		try
			{
			InetAddress addr=InetAddress.getByName(hostName);
			client.connectToServer(addr, port);
			showStatusMessage("Connected to  "+hostName+":"+port);
			}
		catch (UnknownHostException e)
			{
			showStatusMessage("Unknown host: "+hostName);
			}
		catch (IOException e)
			{
			showStatusMessage("Could not connect to server");
			e.printStackTrace();
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
	
	
	public void showLobbyMessage(final UserActionLobbyMessage lm)
		{
		QApplication.invokeLater(new Runnable() {
			public void run() {
				chatHistory.append(client.getNickFor(lm.fromClientID)+": "+lm.message);
			}
			});
		}


	@Override
	public void eventNewUserList()
		{
		System.out.println("list of nicks: "+client.mapClientIDtoNick);

		//Update our nick
		final String ourNick=client.getNickFor(client.getClientID());
		QApplication.invokeLater(new Runnable() {
			public void run() {
				bNick.setText(ourNick+":");
			}
		});

		}
	
	
	//TODO on exit, stop the thread and properly shut down the game
	}
