package clientQT;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


import action.Message;
import action.UserActionClickedCard;
import action.UserActionGameDesign;
import action.UserActionGameStateUpdate;
import clientData.Client;
import clientData.ClientCard;
import clientData.ClientGameData;
import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QTimer;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.opengl.QGL;
import com.trolltech.qt.opengl.QGLFormat;
import com.trolltech.qt.opengl.QGLWidget;

/**
 * One ongoing game playfield
 * 
 * 
 * see http://doc.trolltech.com/latest/qsvgwidget.html
 * for a maybe better base class
 */
public class BoardView extends QGraphicsView
	{
	//TODO unload these!
	private QPixmap bg;
	private QtGraphicsData emptyPic;
	
	public List<AnimatedCard> cards=new LinkedList<AnimatedCard>();
	public List<EmptyPos> emptyPosList=new LinkedList<EmptyPos>();
	
	public double zoom=0.3;

	public ClientGameData gameData=new ClientGameData(); 
	private BoardLayout layout;
	
	private Client client;

	private QPoint oldMousePos=new QPoint();
	public int gameID;

	public BoardView(Client client, QWidget parent, int gameID)
		{
		super(parent);
		this.client=client;	
		this.gameID=gameID;
		
		layout=new BoardLayout(this, client);
		
		setViewport(new QGLWidget(new QGLFormat(new QGL.FormatOptions(QGL.FormatOption.SampleBuffers))));

    setHorizontalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOff);
    setVerticalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOff);

		setMouseTracking(true);
		setEnabled(true);
		
		QTimer timer = new QTimer(this);
		timer.timeout.connect(this,"eventTimer()");
    timer.start(1000/50);

		//Place cards on board
		layout.doLayout();
		redoLayout();

		setSizePolicy(QSizePolicy.Policy.Ignored,QSizePolicy.Policy.Ignored);
		}

	
	public void eventTimer()
		{
		if(layout.doLayout())
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
			//Handle user clicking on a card
			AnimatedCard card=getCardUnderPress(event);
			if(card!=null)
				{
				ClientCard cc=card.cardData;
				
				UserActionClickedCard a=new UserActionClickedCard();
				a.gameID=gameID;
				a.stack=cc.stack;
				a.stackPos=cc.stackPos;
				a.player=cc.cardPlayer;
				
				client.send(new Message(a));
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
	 * Get an image after rescaling.
	 * TODO should handle rescaling at suitable moments. moving cards should not be rasterized
	 */
	public QtGraphicsData getScaledImage(String front)
		{
		return gameData.getImageForCard(front).getScaledImage(zoom);
		}
	
	public void redoLayout()
		{
		QGraphicsScene s=new QGraphicsScene();
		setScene(s);
		s.setSceneRect(0, 0, width()+4000,height()+4000); //this is, at best, a hack
		
		//Place background
		if(bg==null)
		bg=new QPixmap("cards/tiledtable.png");
		for(int ax=0;ax<10;ax++)
			for(int ay=0;ay<10;ay++)
				{
				QGraphicsPixmapItem item=new QGraphicsPixmapItem(bg);
				item.setZValue(-1000);
				item.resetTransform();
				item.setTransform(QTransform.fromScale(zoom, zoom), true);
				item.translate(bg.width()*ax, bg.height()*ay);
				s.addItem(item);
				}

		
		//Draw all empty positions
		if(emptyPic==null)
			emptyPic=getScaledImage("emptypos"); //TODO should only create one scaled image and use instances
		for(EmptyPos p:emptyPosList)
			{
			QGraphicsItemInterface item=emptyPic.createGraphicsItem();
			item.setZValue(-900);
			item.setTransform(QTransform.fromTranslate(p.x*zoom, p.y*zoom), true);
			item.rotate(p.rotation*180/Math.PI);
			item.setTransform(QTransform.fromTranslate(-item.boundingRect().width()*zoom/2, -item.boundingRect().height()*zoom/2), true);
			
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
			QGraphicsItemInterface item;
			
			
			//Get the image for the card if it is not loaded
			if(card.cardData.showsFront)
				{
				if(card.imageFront==null)
					card.imageFront=getScaledImage(card.cardData.front).createGraphicsItem(); //TODO don't change image all the time!
				item=card.imageFront;
				}
			else
				{
				if(card.imageBack==null)
					card.imageBack=getScaledImage(card.cardData.back).createGraphicsItem();
				item=card.imageBack;
				}
			
			
			item.setZValue(curz);
			item.resetTransform();
			item.setTransform(QTransform.fromTranslate(card.posX*zoom, card.posY*zoom), true);
			item.rotate(card.rotation*180/Math.PI);
			item.setTransform(QTransform.fromTranslate(-item.boundingRect().width()*zoom/2, -item.boundingRect().height()*zoom/2), true);
			
			s.addItem(item);

			curz++;
			}
		
		}


	
	
	public void setGameDesign(UserActionGameDesign msg)
		{
		layout.newDesign(msg.design);
		layout.doLayout();
		QApplication.invokeLater(new Runnable() {
			public void run() {
				redoLayout();
			}
		});
		
		}


	public void setGameState(UserActionGameStateUpdate msg)
		{
		layout.newState(msg);
		layout.doLayout();
		QApplication.invokeLater(new Runnable() {
			public void run() {
				redoLayout();
			}
		});
		}
	
	
	}
