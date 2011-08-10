package client;


import action.Message;
import action.UserAction;
import action.UserActionLobbyMessage;

import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QMenuBar;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QTextEdit;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;

public class LobbyWindow extends QWidget implements ServerListener
	{
	

	private QAction miExit;

	private QPushButton bNick;
	
	private QTextEdit chatHistory;
	
	private QLineEdit tfEditLine;
	
	private QMenuBar menuBar;
	
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
	
	private Client client;
	
	public LobbyWindow(Client client)
		{
		this.client=client;

		QVBoxLayout layoutV=new QVBoxLayout();
		setLayout(layoutV);
		
		//layoutV.setMenuBar()
		
		bNick=new QPushButton("Mahogny: ", this);
		
		chatHistory=new QTextEdit(this);
		chatHistory.setReadOnly(true);
		
		
		tfEditLine=new QLineEdit(this);
		tfEditLine.returnPressed.connect(this,"actionSendMessage()");

		layoutV.addWidget(chatHistory);
		layoutV.addWidget(bNick);
		layoutV.addWidget(tfEditLine);
		
		
		setWindowTitle(tr("Lobby"));

		/*
		menuBar=new QMenuBar();
		layoutV.setMenuBar(menuBar);
		
		miExit = new QAction(tr("E&xit"), this);
    //miExit.setShortcuts(QKeySequence.Quit);
    miExit.setStatusTip(tr("Exit the application"));
    miExit.triggered.connect(this,"actionExit()");

		
		
		
		QMenu fileMenu = menuBar().addMenu(tr("&File"));
    fileMenu.addAction(miExit);
    //helpMenu = menuBar()->addMenu(tr("&Help"));
		

		setFixedSize(400, 400);
		//setCentralWidget(view);
		
		//view.ensureVisible(0, 0, view.width(), view.height());
*/
		
		
		}
	

	public void eventServerMessage(Message msg)
		{
		System.out.println("foo");
		for(UserAction action:msg.actions)
			if(action instanceof UserActionLobbyMessage)
				{
				final UserActionLobbyMessage lm=(UserActionLobbyMessage)action;
				
				 QApplication.invokeLater(new Runnable() {
	         public void run() {
	         chatHistory.append(lm.fromClientID+": "+lm.message+"\n");
	         }
				 });

				System.out.println("hello!");
				}
		
		}

	}
