package misc;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import util.ResourceLoader;

public class CatchAnimation {
	
	private final int ANI_DELAY = 5;
	
	private ArrayList<BufferedImage> images;
	private int imageIndex;
	private int aniDelay;
	
	private boolean finishedAni;
	
	public CatchAnimation(int catchResult)
	{
		images = ResourceLoader.getCatchImgs(catchResult);
		imageIndex = 0;
		aniDelay = ANI_DELAY;
		
		finishedAni = false;
	}
	
	public void render(Graphics2D g)
	{
		animation();
		g.drawImage(images.get(imageIndex), 0, 0, null);
	}
		
	private void animation()
	{
		aniDelay --;
		if(aniDelay == 0)
		{
			aniDelay = ANI_DELAY;
			imageIndex ++;
			if(imageIndex > images.size() - 1)
			{
				imageIndex --;
				finishedAni = true;
			}
		}
		
	}
	
	public boolean getFinishedAni()
	{
		return finishedAni;
	}
}
