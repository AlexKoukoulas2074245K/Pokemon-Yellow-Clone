package misc;

/*
 	Code transfered from previous project
 */

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import main.Game;
import main.Main;

public class WildBattleAnimation extends BattleAnimation{
	
	private final int XVEL = 4;
	
	private int lifeCounter;

	private boolean stopMoving;
	
	private ArrayList<Rectangle> leftParts;
	private ArrayList<Rectangle> rightParts;
	
	public WildBattleAnimation()
	{
		lifeCounter = 220;
		aniFinished = false;
		stopMoving = false;
		
		leftParts = new ArrayList<Rectangle>();
		rightParts = new ArrayList<Rectangle>();
		
		int activeSide = -1;
		
		for(int i = 0; i < Main.HEIGHT; i += Game.STDTSIZE/2)
		{
			if(activeSide == -1)
				leftParts.add(new Rectangle(-2 * Main.WIDTH, i, Main.WIDTH*2, Game.STDTSIZE/2));
			else
				rightParts.add(new Rectangle(Main.WIDTH, i, Main.WIDTH, Game.STDTSIZE/2));
			
			activeSide *= -1;
		}
			
		
	}
	
	public void update()
	{
		if(!stopMoving)
		{
			for(Rectangle rect: leftParts)
			{
				rect.x += XVEL;
				if(rect.x > - Main.WIDTH - 4)
					stopMoving = true;
			}
			for(Rectangle rect: rightParts)
				rect.x -= XVEL;
		}
		
		lifeCounter --;
		if(lifeCounter == 0)
			aniFinished = true;
			
	}
	
	public void render(Graphics2D g)
	{
		g.setColor(PokemonHud.BLACK);
		for(Rectangle rect: leftParts)
			g.fillRect(rect.x, rect.y, rect.width, rect.height);
		for(Rectangle rect: rightParts)
			g.fillRect(rect.x, rect.y, rect.width, rect.height);
	}
}
