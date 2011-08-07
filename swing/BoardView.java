package swing;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;


public class BoardView extends JPanel
	{
	public BoardView()
		{
		
		
		// TODO Auto-generated constructor stub
		}
	
	
	@Override
	protected void paintComponent(Graphics g)
		{
		g.setColor(Color.GREEN);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		
		
		
		}
	}
