package clientQT;

import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;

public class ConnectToServerDialog extends QDialog
	{
	
	public ConnectToServerDialog()
		{
		QLabel labelAddress = new QLabel(tr("Address:"), this);
		QLabel labelPort = new QLabel(tr("Port:"), this);
		QLineEdit editAddress = new QLineEdit(this);
		QLineEdit editPort = new QLineEdit(this);
/*
    QPushButton bConnect=new QPushButton(tr("Connect"), this);
    bConnect.clicked.connect(this, "actionConnect()");

    QPushButton bCancel=new QPushButton(tr("Cancel"), this);
    bCancel.clicked.connect(this, "actionCancel()");
    
    
    QHBoxLayout buttonsLayout = new QHBoxLayout(this);
    buttonsLayout.addStretch();
    buttonsLayout.addWidget(bConnect);
    buttonsLayout.addWidget(bCancel);*/

		
	   QGridLayout mainLayout = new QGridLayout();
	   mainLayout.addWidget(labelAddress, 0, 0);
	   mainLayout.addWidget(editAddress, 0, 1);
	   mainLayout.addWidget(labelPort, 1, 0);
	   mainLayout.addWidget(editPort, 1, 1);
//	   mainLayout.addLayout(buttonsLayout, 2, 0);
	   setLayout(mainLayout);
	   

	   setWindowTitle(tr("Connect to server"));
//	   resize(700, 300);

     
}

	public void actionConnect()
		{
		System.out.println("Connect!!!");
		}
	
	public void actionCancel()
		{
		close();
		}
	
	}
