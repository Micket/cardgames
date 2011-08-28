package clientQT;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;


import com.trolltech.qt.core.QSize;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QGraphicsItemInterface;
import com.trolltech.qt.gui.QGraphicsPixmapItem;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QStyleOptionGraphicsItem;
import com.trolltech.qt.gui.QPainter.RenderHint;
import com.trolltech.qt.svg.QGraphicsSvgItem;
import com.trolltech.qt.svg.QSvgRenderer;

/**
 * Wrapper to handle both Pixmap and SVG objects, without them being QGraphicsItems
 *
 */
public class QtGraphicsData
	{
	private QSvgRenderer svg;
	private QPixmap bmp;
	
	
	public QtGraphicsData(QPixmap bmp)
		{
		this.bmp=bmp;
		}
	
	
	public QtGraphicsData(File file)
		{
		if(file.exists())
			{
			if(file.getName().endsWith(".svg"))
				svg=new QSvgRenderer(file.getAbsolutePath());
			else
				bmp=new QPixmap(file.getAbsolutePath());
			}
		}
	
	
	public QGraphicsItemInterface createGraphicsItem()
		{
		if(svg!=null)
			{
			QGraphicsSvgItem item=new QGraphicsSvgItem();
			item.setSharedRenderer(svg);
			return item;
			}
		else
			{
			QGraphicsPixmapItem item=new QGraphicsPixmapItem(bmp);
			return item;
			}
		}
	
	
	public QtGraphicsData getScaledImage(double zoom)
		{
		if(svg!=null)
			return rasterizeSvg(svg, zoom);
		else
			throw new RuntimeException("png - baaad. need to implement rescaling here");
		}
	
	
	
	/**
	 * Turn an SVG graphics item into pixmap graphics item
	 */
	public static QtGraphicsData rasterizeSvg(QSvgRenderer g, double scale)
		{
		QSize bb=g.defaultSize();
		int w=(int)(bb.width()*scale)+1;
		int h=(int)(bb.height()*scale)+1;
		QPixmap pm=new QPixmap(new QSize(w,h));
		pm.fill(QColor.transparent);
		QPainter painter=new QPainter(pm);
		painter.setRenderHint(RenderHint.HighQualityAntialiasing);
		painter.scale(scale, scale);
		
		QGraphicsSvgItem g2=new QGraphicsSvgItem();
		g2.setSharedRenderer(g);
		g2.paint(painter, new QStyleOptionGraphicsItem(), null);
		painter.end();

		g2.dispose();

		return new QtGraphicsData(pm);
		}


	public int width()
		{
		if(bmp!=null)
			return bmp.width();
		else
			throw new RuntimeException("eek");
		}

	public int height()
		{
		if(bmp!=null)
			return bmp.height();
		else
			throw new RuntimeException("eek");
		}


	/**
	 * Get an image for a given theme
	 */
	public static QtGraphicsData getImage(String theme, String image)
		{
		File fileSVG=new File(new File("images",theme),image+".svg");
		if(fileSVG.exists())
			return new QtGraphicsData(fileSVG);
		else
			{
			File filePNG=new File(new File("images",theme),image+".png");
			if(filePNG.exists())
				return new QtGraphicsData(filePNG);
			}
		return null;
		}


	/**
	 * Images should be stored on the client for bandwidth. But it would make sense to allow the server to provide missing cards,
	 * in case a game need a very specialized card - this allows for more stupid clients.
	 * 
	 * Interface problem: Now this depends on QT
	 */
	public static QtGraphicsData getImage(String image)
		{
		QtGraphicsData img=QtGraphicsData.getImage("Default", image);
		if(img==null)
			throw new RuntimeException("No such image, "+image); //Later: Request image from server
		else
			return img;
		}


	/**
	 * Get all graphical themes
	 */
	public static Set<String> getThemes()
		{
		File dir=new File("images");
		Set<String> themes=new TreeSet<String>();
		for(File f:dir.listFiles())
			if(f.isDirectory() && !f.getName().startsWith("."))
				themes.add(f.getName());
		return themes;
		}
	
	}
