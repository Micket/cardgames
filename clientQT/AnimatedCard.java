package clientQT;
import clientData.ClientCard;

import com.trolltech.qt.gui.QGraphicsItemInterface;


public class AnimatedCard
	{
	public ClientCard cardData;

	public QtGraphicsData imageFront;
	public QtGraphicsData imageBack;
	
	public QGraphicsItemInterface curImageItem;
	
	public double posX, posY; //TODO original position
	public int posZ;
	public boolean isBeingDragged=false;
	
	public double rotation;

	/**
	 * Rotation: 0 is front-facing, PI is back-facing
	 */
	public double rotY;
	
	public AnimatedCard(ClientCard cardData)
		{
		this.cardData=cardData;
		rotY=shouldHaveRotation();
		}
	
	
	public double shouldHaveRotation()
		{
		if(cardData.showsFront)
			return 0;
		else
			return Math.PI;
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
