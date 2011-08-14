package clientQT;

import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;

public class ConnectToServerDialog extends QDialog
	{
	
	public ConnectToServerDialog()
		{
		QLabel labelAddress = new QLabel(tr("Address:"), this);
		QLabel labelPort = new QLabel(tr("Port:"), this);
		QLineEdit editAddress = new QLineEdit(this);
		QLineEdit editPort = new QLineEdit(this);
		
	   QGridLayout mainLayout = new QGridLayout();
	   mainLayout.addWidget(labelAddress, 0, 0);
	   mainLayout.addWidget(editAddress, 0, 1);
	   mainLayout.addWidget(labelPort, 1, 0);
	   mainLayout.addWidget(editPort, 1, 1);
	   setLayout(mainLayout);

	   setWindowTitle(tr("Connect to server"));
//	   resize(700, 300);
		
		}

	}
