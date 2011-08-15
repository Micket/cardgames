package clientQT;
import clientData.ClientCard;

import com.trolltech.qt.gui.QGraphicsItemInterface;


public class AnimatedCard
	{
	public ClientCard cardData;

	public QGraphicsItemInterface imageFront;
	public QGraphicsItemInterface imageBack;
	public double posX, posY; //TODO original position
	public int posZ;
	public boolean isBeingDragged=false;
	
	public double rotation;
	
	public AnimatedCard(ClientCard cardData)
		{
		this.cardData=cardData;
		}
	
	/*
	public void loadImageFront(String file)
		{
		if(file.endsWith(".svg"))
			imageFront=new QGraphicsSvgItem(file);
		else
			imageFront=new QGraphicsPixmapItem(new QPixmap(file));
		}


	public void loadImageBack(String file)
		{
		if(file.endsWith(".svg"))
			imageBack=new QGraphicsSvgItem(file);
		else
			imageBack=new QGraphicsPixmapItem(new QPixmap(file));
		}*/
	
	}
