package misc;

/*
 Code transfered from previous project
 */

import java.awt.Graphics2D;
import java.util.ArrayList;

import main.Main;
import util.Util;

public class TrainerBattleAnimation extends BattleAnimation{
	
	private final int RECTSIZE = 40;
	
	private int lifeCounter;
	private int direction;
	
	private ArrayList<int[]> rectPos;
	
	public TrainerBattleAnimation()
	{
		aniFinished = false;
		lifeCounter = 300;
		direction = 2;
		
		rectPos = new ArrayList<int[]>();
		rectPos.add(new int[]{0,0});
	}
	
	public void update()
	{

		int[] lastPos = rectPos.get(rectPos.size() - 1);
		
		if(direction == 0)
		{
			int[] newRect = new int[]{lastPos[0],lastPos[1] - RECTSIZE};
			
			if(!Util.arrayContains(rectPos,newRect) && lastPos[1] > 0)
				rectPos.add(newRect);
			else
				direction = 3;
		}
		
		else if(direction == 1)
		{
			int[] newRect = new int[]{lastPos[0] + RECTSIZE, lastPos[1]};
			
			if(!Util.arrayContains(rectPos,newRect)  && lastPos[0] < Main.WIDTH - RECTSIZE)
				rectPos.add(newRect);
			else
				direction = 0;
		}
		
		else if(direction == 2)
		{
			int[] newRect = new int[]{lastPos[0], lastPos[1] + RECTSIZE};
			
			if(!Util.arrayContains(rectPos,newRect) && lastPos[1] < Main.HEIGHT - RECTSIZE)
				rectPos.add(newRect);
			else
				direction = 1;
		}
		
		else
		{
			int[] newRect = new int[]{lastPos[0] - RECTSIZE, lastPos[1]};
			
			if(!Util.arrayContains(rectPos,newRect)  && lastPos[0] > 0)
				rectPos.add(newRect);
			else
				direction = 2;
		}
		
		lifeCounter --;
		if(lifeCounter == 0)
			aniFinished = true;
		
		
	}
	
	public void render(Graphics2D g)
	{
		g.setColor(PokemonHud.BLACK);
		for(int[] rect: rectPos)
			g.fillRect(rect[0], rect[1], RECTSIZE, RECTSIZE);
	}
}
