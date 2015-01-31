package entities;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Game;
import util.Input;
import util.ResourceLoader;

public class Component {
	
	private static final BufferedImage POINTER_IMG = ResourceLoader.getTexture("horCursor");
	private static final int FONT_SIZE = 28;
	private static final Font FONT = new Font("Pokemon GB",Font.PLAIN,FONT_SIZE);
	
	private BufferedImage image;
	
	private int pointerX;
	private int pointerY;
	private int pointerIndex;
	
	private int x;
	private int y;
	
	private int titleX;
	private int titleY;
	
	private String[] titles;
	
	public Component(BufferedImage image,int x,int y,String[] titles)
	{
		this.image = image;
		this.x = x;
		this.y = y;
		this.titles = titles;
		
		pointerIndex = 0;
		
		pointerX = x + Game.STDTSIZE/2 - Game.STDTSIZE/6;
		pointerY = y + Game.STDTSIZE/2 + FONT_SIZE;
		
		titleX =  x + Game.STDTSIZE - 16;
		titleY = (FONT_SIZE * 2 ) + y + Game.STDTSIZE/2;
	}
	
	public void update()
	{
		if(Input.DOWN_TAPPED)
		{
			System.out.println("down down");
			pointerIndex ++;
			if(pointerIndex > titles.length - 1)
				pointerIndex = 0;
		}
		else if(Input.UP_TAPPED)
		{
			System.out.println("up down");
			pointerIndex --;
			if(pointerIndex < 0)
				pointerIndex = titles.length - 1;
		}
	}
	
	public void render(Graphics2D g)
	{
		g.drawImage(image,x,y,null);
		g.setFont(FONT);
		for(int i = 0; i < titles.length; i ++)
			g.drawString(titles[i], titleX, 
					titleY + (i * (Game.STDTSIZE/2) * 2));
			
		g.drawImage(POINTER_IMG, pointerX,
					pointerY + (pointerIndex * (Game.STDTSIZE/2) * 2), null);
	}
	
	
}
