package clientQT;

import com.trolltech.qt.gui.QGraphicsItemInterface;

public class EmptyPos
	{
	public double x, y;
	public double rotation;

	public QGraphicsItemInterface qtitem;
	
	
	
	public EmptyPos()
		{
		
		}
	
	public EmptyPos(double x, double y, double rotation)
		{
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		}
	
	
	}
