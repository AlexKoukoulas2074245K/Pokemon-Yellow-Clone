package misc;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Game;
import main.Main;
import entities.Pokemon;
import gameStates.Battle;
import util.Counter;
import util.Graphics;

public class PokemonEvolution {
	
	private BufferedImage[] pokImages;
	private BufferedImage[] evoImages;
	private BufferedImage imageToDraw;
	
	private Pokemon pkmn;
	private Pokemon evo;
	
	private int phase;
	
	private boolean aniFinished;
	
	private Counter aniCounter;
	private Counter smallCounter;
	
	private int[] pos;
	
	public PokemonEvolution(Pokemon pkmn, Pokemon evo)
	{
		this.pkmn = pkmn;
		this.evo = evo;
		
		aniFinished = false;
		
		getImages();
		
		pos = new int[]{Main.WIDTH/2 - pokImages[0].getWidth()/2 - 80, 
						Main.HEIGHT/2 - pokImages[1].getHeight()/2 - 170};
		
		aniCounter = new Counter(Game.STDTSIZE/2);
		smallCounter = new Counter(3);
	}
	
	private void getImages()
	{
		pokImages = new BufferedImage[]{
				Graphics.getHFlippedImage(pkmn.getBattleImages()[0]),
				Graphics.getHFlippedImage(pkmn.getBattleImages()[1])
					};
		evoImages = new BufferedImage[]{
				Graphics.getHFlippedImage(evo.getBattleImages()[0]),
				Graphics.getHFlippedImage(evo.getBattleImages()[1])
					};
		
		imageToDraw = pokImages[0];
	}
	
	public void update()
	{
		if(phase == 0)
		{
			aniCounter.update();
			if(!aniCounter.getAlive())
			{
				phase ++;
				aniCounter = new Counter(89);
			}
		}
		
		else if(phase == 1)
		{
			aniCounter.update();
			if(aniCounter.getTime()%30 == 0)
				imageToDraw = evoImages[1];
			else
				imageToDraw = pokImages[1];
			
			if(!aniCounter.getAlive())
			{
				phase ++;
				aniCounter = new Counter(Game.STDTSIZE * 3);
			}
		}
		
		else if(phase == 2)
		{
			aniCounter.update();
			if(!aniCounter.getAlive())
			{
				aniCounter = new Counter(Game.STDTSIZE * 2);
				phase ++;
			}
			else
			{
				smallCounter.update();
				if(!smallCounter.getAlive())
				{
					smallCounter = new Counter(3);
					if(imageToDraw == evoImages[1])
						imageToDraw = pokImages[1];
					else
						imageToDraw = evoImages[1];
				}
			}
		}
		
		else if(phase == 3)
		{
			imageToDraw = evoImages[0];
			
			aniCounter.update();
			if(!aniCounter.getAlive())
				aniFinished = true;
		}
	}
	
	public void render(Graphics2D g)
	{
		g.setColor(PokemonHud.WHITE);
		g.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);
		g.drawImage(imageToDraw, pos[0], pos[1], Battle.IMAGE_SIZE, Battle.IMAGE_SIZE, null);
	}
	
	public boolean getAniFinished()
	{
		return aniFinished;
	}
}
