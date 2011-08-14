package clientQT;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import server.ServerThread;

import clientData.Client;

import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;

/**
 * Dialog allowing user to connect to a server
 * 
 * @author mahogny
 *
 */
public class ConnectToServerDialog extends QDialog
	{
	private QLineEdit editAddress;
	private QLineEdit editPort; 
	private Client client;
	
	public ConnectToServerDialog(Client client)
		{
		this.client=client;
		
		QLabel labelAddress = new QLabel(tr("Address:"), this);
		QLabel labelPort = new QLabel(tr("Port:"), this);
		editAddress = new QLineEdit(this);
		editPort = new QLineEdit(this);

		editAddress.setText("localhost");
		editPort.setText(""+ServerThread.defaultServerPort);

		QPushButton bConnect=new QPushButton(tr("Connect"));
		QPushButton bCancel=new QPushButton(tr("Cancel"));

		bConnect.clicked.connect(this, "actionConnect()");
		editAddress.returnPressed.connect(this,"actionConnect()");
		editPort.returnPressed.connect(this,"actionConnect()");
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

		editAddress.focusWidget();
		
		setWindowTitle(tr("Connect to server"));
		}

	public void actionConnect()
		{
		String address=editAddress.text();
		String portS=editPort.text();
		
		int port=Integer.parseInt(portS);

		try
			{
			client.connectToServer(InetAddress.getByName(address), port);
			close();
			}
		catch (UnknownHostException e)
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		catch (IOException e)
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}
	
	public void actionCancel()
		{
		close();
		}
	
	}
