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
import com.trolltech.qt.svg.QGraphicsSvgItem;

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
	
	public double zoom=0.3;

	public ClientGameData gameData=new ClientGameData(); 
	private BoardLayout layout=new BoardLayout();
	
	private Client client;
	
	public BoardView(Client client, QWidget parent)
		{
		super(parent);
		this.client=client;	
		
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
	
	
	QPoint oldMousePos=new QPoint();
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

	
	public QGraphicsItemInterface rasterizeSvg(QGraphicsSvgItem g)
		{
		QRectF bb=g.boundingRect();

		double scale=zoom*0.5;

		int w=(int)(bb.width()*scale)+1;
		int h=(int)(bb.height()*scale)+1;
		System.out.println("wh "+w+"  "+h);
		QPixmap pm=new QPixmap(new QSize(w,h));
		pm.fill(QColor.red);
//		pm.fill(QColor.transparent);
		QPainter painter=new QPainter(pm);

//		painter.setBrush(QColor.black);
//		pm.fill(QColor.red);

		System.out.println(painter.combinedMatrix());
		
//		painter.scale(scale,scale);
		painter.scale(0.1,0.1);
//		QRectF b2=g.renderer().viewBoxF();
		QRectF b2=g.boundingRect();
		System.out.println(b2);
//		g.translate(-b2.left(), -b2.top());
//		g.renderer().render(painter);
		g.paint(painter, new QStyleOptionGraphicsItem(), null);  
		painter.end();

		return new QGraphicsPixmapItem(pm);
		}
	
	
	public void redoLayout()
		{
		QGraphicsScene s=new QGraphicsScene();
		setScene(s);
		s.setSceneRect(0, 0, width()+4000,height()+4000); //this is, at best, a hack
		
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
			//cardImage=rasterizeSvg(new QGraphicsSvgItem("cards/spades9.svg"));
			
			cardImage.setZValue(curz);
			cardImage.resetTransform();
			//card.image.setPos(card.posX*zoom, card.posY*zoom);
			cardImage.setTransform(QTransform.fromScale(zoom, zoom), true);
			cardImage.setTransform(QTransform.fromTranslate(card.posX, card.posY), true);
			
			s.addItem(cardImage);

			curz++;
			}
		
		}
	
	
	}
