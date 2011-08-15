package clientQT;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


import action.Message;
import action.UserActionClickedButton;
import clientData.Client;
import clientData.ClientGameData;

import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.QSize;
import com.trolltech.qt.core.QTimer;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.gui.QPainter.RenderHint;
import com.trolltech.qt.opengl.QGL;
import com.trolltech.qt.opengl.QGLFormat;
import com.trolltech.qt.opengl.QGLWidget;
import com.trolltech.qt.svg.QGraphicsSvgItem;
import com.trolltech.qt.svg.QSvgRenderer;

/**
 * One ongoing game playfield
 * 
 * 
 * see http://doc.trolltech.com/latest/qsvgwidget.html
 * for a maybe better base class
 */
public class BoardView extends QGraphicsView
	{
	public List<AnimatedCard> cards=new LinkedList<AnimatedCard>();
	public List<QPoint> emptyPosList=new LinkedList<QPoint>();
	
	public double zoom=1;

	public ClientGameData gameData=new ClientGameData(); 
	private BoardLayout layout=new BoardLayout();
	
	private Client client;

	private QPoint oldMousePos=new QPoint();
	public int gameID;

	public BoardView(Client client, QWidget parent, int gameID)
		{
		super(parent);
		this.client=client;	
		this.gameID=gameID;
		
		setViewport(new QGLWidget(new QGLFormat(new QGL.FormatOptions(QGL.FormatOption.SampleBuffers))));


    setHorizontalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOff);
    setVerticalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOff);

		setMouseTracking(true);
		setEnabled(true);
		
		QTimer timer = new QTimer(this);
		timer.timeout.connect(this,"eventTimer()");
    timer.start(1000/50);

		//Place cards on board
		layout.doLayout(this, client);
		redoLayout();

		setSizePolicy(QSizePolicy.Policy.Ignored,QSizePolicy.Policy.Ignored);
		}

	
	public void eventTimer()
		{
		if(layout.doLayout(this, client))
			{
			redoLayout();
			//System.out.println("timer req update");
			}
		}
	
	private AnimatedCard getCardUnderPress(QMouseEvent event)
		{
		QGraphicsItemInterface picked=scene().itemAt(event.posF());
		if(picked!=null)
			for(AnimatedCard card:cards)
				if(card.imageFront==picked || card.imageBack==picked) //TODO. bug. what if a card exists multiple times?
					return card;
		return null;
		}
	
	protected void mouseDoubleClickEvent(QMouseEvent event)
		{
		if(event.button()==MouseButton.LeftButton)
			{
			AnimatedCard card=getCardUnderPress(event);
			if(card!=null)
				{
				Message msg=new Message(new UserActionClickedButton());
				client.send(msg);
				}
			}
		}
	
	
	
	@Override
	protected void mouseMoveEvent(QMouseEvent event)
		{		
		boolean needRedraw=false;
		QPoint dx=event.pos().subtract(oldMousePos);
		for(AnimatedCard card:cards)
			if(card.isBeingDragged) //A separate list of dragged cards would make this faster and scale better!
				{
				card.posX+=dx.x()/zoom;
				card.posY+=dx.y()/zoom;
				needRedraw=true;
				}
		
		if(needRedraw)
			{
			redoLayout();
			}
		
		oldMousePos=event.pos();

		}
	
	protected void mousePressEvent(QMouseEvent event)
		{
		if(event.button()==MouseButton.LeftButton)
			{
			AnimatedCard card=getCardUnderPress(event);
			if(card!=null)
				{
				card.isBeingDragged=true;
				//TODO sometimes there are cards on top to move as well
				}
			}
		System.out.println("here!");
		}
	
	@Override
	protected void mouseReleaseEvent(QMouseEvent event)
		{
		if(event.button()==MouseButton.LeftButton)
			for(AnimatedCard card:cards)
				card.isBeingDragged=false;
		redoLayout();
		}
	
	@Override
	protected void moveEvent(QMoveEvent event)
		{
		
		}

	/**
	 * Turn an SVG graphics item into pixmap graphics item
	 */
	public static QGraphicsItemInterface rasterizeSvg(QGraphicsSvgItem g, double zoom)
		{
		double scale=zoom;

		QRectF bb=g.boundingRect();
		int w=(int)(bb.width()*scale)+1;
		int h=(int)(bb.height()*scale)+1;
		QPixmap pm=new QPixmap(new QSize(w,h));
		pm.fill(QColor.transparent);
		QPainter painter=new QPainter(pm);
		painter.setRenderHint(RenderHint.HighQualityAntialiasing);
		painter.scale(scale, scale);
		g.paint(painter, new QStyleOptionGraphicsItem(), null);  
		painter.end();

		return new QGraphicsPixmapItem(pm);
		}
	
	
	//private QImage bg;
	private QPixmap bg;
	private QSvgRenderer emptyPic;
	
	public void redoLayout()
		{
		QGraphicsScene s=new QGraphicsScene();
		setScene(s);
		s.setSceneRect(0, 0, width()+4000,height()+4000); //this is, at best, a hack
		
		//Place background
		if(bg==null)
		//	bg=new QImage("cards/tiledtable.png");
		bg=new QPixmap("cards/tiledtable.png");
		
//		for(int ax=0;bg.width()*zoom*(ax+1)<width();ax++)
		for(int ax=0;ax<4;ax++)
			for(int ay=0;ay<4;ay++)
				{
				QGraphicsPixmapItem g=new QGraphicsPixmapItem(bg);
				g.setZValue(-1000);
				g.resetTransform();
				g.setTransform(QTransform.fromScale(zoom, zoom), true);
				g.translate(bg.width()*ax, bg.height()*ay);
				s.addItem(g);
				}
		/*bg.setZValue(-1);
		bg.resetTransform();
		bg.setTransform(QTransform.fromScale(zoom, zoom), true);
		s.addItem(bg);*/
 //   QBrush bgbrush = new QBrush(bg);
//    setBackgroundBrush(bgbrush);

		
		//Draw all empty positions
		if(emptyPic==null)
			emptyPic=new QSvgRenderer("cards/empty.svg");
		for(QPoint p:emptyPosList)
			{
			QGraphicsSvgItem item=new QGraphicsSvgItem();
			item.setSharedRenderer(emptyPic);
			item.setZValue(-900);
			item.resetTransform();
			item.setTransform(QTransform.fromScale(zoom, zoom), true);
			item.setTransform(QTransform.fromTranslate(p.x(), p.y()), true);
			s.addItem(item);
			}
		
		//Sort the cards in Z to ensure the right drawing order
		Collections.sort(cards, new Comparator<AnimatedCard>()
			{
			public int compare(AnimatedCard o1, AnimatedCard o2)
				{
				//If cards are being dragged then they should be above all other cards.
				//But dragged cards in turn have an order
				int o1level=o1.posZ;
				int o2level=o2.posZ;
				if(o1.isBeingDragged)
					o1level+=1000;
				if(o2.isBeingDragged)
					o2level+=1000;				
				
				if(o1level<o2level)
					return -1;
				else if(o1level>o2level)
					return 1;
				else
					return 0;
				}
			});
		
		//Render cards, in order
		int curz=0;
		for(AnimatedCard card:cards)
			{
			QGraphicsItemInterface cardImage;
			
			if(card.cardData.showsFront)
				{
				if(card.imageFront==null)
					card.imageFront=gameData.getImageForCard(card.cardData.front);
				cardImage=card.imageFront;
				}
			else
				{
				if(card.imageBack==null)
					card.imageBack=gameData.getImageForCard(card.cardData.back);
				cardImage=card.imageBack;
				}
				
			
			//QGraphicsItemInterface item=
			//cardImage=rasterizeSvg(new QGraphicsSvgItem("cards/pokerbackside.svg"));
			
			cardImage.setZValue(curz);
			cardImage.resetTransform();
			//card.image.setPos(card.posX*zoom, card.posY*zoom);
			cardImage.setTransform(QTransform.fromScale(zoom, zoom), true);
			cardImage.setTransform(QTransform.fromTranslate(card.posX, card.posY), true);
			cardImage.rotate(card.rotation*180/Math.PI);
			
			
			s.addItem(cardImage);

			curz++;
			}
		
		}
	
	
	}
