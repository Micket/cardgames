package clientQT;

import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;

public class ConnectToServerDialog extends QDialog
	{
	QLabel labelAddress;
	QLabel labelPort;
  
	public ConnectToServerDialog()
		{
		labelAddress = new QLabel(tr("Address:"), this);
		labelPort = new QLabel(tr("Port:"), this);
		QLineEdit editAddress = new QLineEdit(this);
		QLineEdit editPort = new QLineEdit(this);

    QPushButton bConnect=new QPushButton(tr("Connect"));
    QPushButton bCancel=new QPushButton(tr("Cancel"));
    bConnect.clicked.connect(this, "actionConnect()");
    bCancel.clicked.connect(this, "actionCancel()");
    
    
    QHBoxLayout buttonsLayout = new QHBoxLayout();
    buttonsLayout.addStretch();
    buttonsLayout.addWidget(bConnect);
    buttonsLayout.addWidget(bCancel);

		
	   QGridLayout mainLayout = new QGridLayout();
	   mainLayout.addWidget(labelAddress, 0, 0);
	   mainLayout.addWidget(editAddress, 0, 1);
	   mainLayout.addWidget(labelPort, 1, 0);
	   mainLayout.addWidget(editPort, 1, 1);
	   mainLayout.addLayout(buttonsLayout, 2, 0, 1,2);
	   setLayout(mainLayout);
	   

	   setWindowTitle(tr("Connect to server"));
//	   resize(300, 100);

     
}

	public void actionConnect()
		{
		System.out.println("Connect!!!");
		
		String addr=labelAddress.text();
		String portS=labelPort.text();
		
		}
	
	public void actionCancel()
		{
		close();
		}
	
	}
