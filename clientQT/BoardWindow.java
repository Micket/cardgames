package clientQT;

import action.Message;

import clientData.ServerListener;

import com.trolltech.qt.gui.*;

/**
 * Window showing one ongoing game
 * 
 */
public class BoardWindow extends QWidget implements ServerListener
	{
	
	private Client client;
	
	public BoardWindow(Client client)
		{
		this.client=client;

		
		/*
		QPushButton quit = new QPushButton(tr("Quit"), this);
		quit.setGeometry(62, 40, 75, 30);
		quit.setFont(new QFont("Times", 18, QFont.Weight.Bold.value()));
		quit.clicked.connect(QApplication.instance(), "quit()");
		*/
		

		
		BoardView view=new BoardView(client, this);
		view.setSizePolicy(QSizePolicy.Policy.Ignored,QSizePolicy.Policy.Ignored);

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


	}
