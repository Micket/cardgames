package client;

import action.Message;
import action.UserAction;

import com.trolltech.qt.gui.*;


public class BoardWindow extends QMainWindow implements ServerListener
	{
	
	private Client client;
	
	public BoardWindow(Client client)
		{
		this.client=client;

		
		//setFixedSize(250, 250);


		BoardView view=new BoardView(this);
		
		/*
		QPushButton quit = new QPushButton(tr("Quit"), this);
		quit.setGeometry(62, 40, 75, 30);
		quit.setFont(new QFont("Times", 18, QFont.Weight.Bold.value()));


		quit.clicked.connect(QApplication.instance(), "quit()");
		*/
		
		view.setGeometry(10, 10, 200, 200);
		setFixedSize(400, 400);
		//setCentralWidget(view);
		
		//view.ensureVisible(0, 0, view.width(), view.height());

		

		setWindowTitle(tr("Card game"));
		}

	public void eventServerMessage(Message msg)
		{
		
		}


	}
