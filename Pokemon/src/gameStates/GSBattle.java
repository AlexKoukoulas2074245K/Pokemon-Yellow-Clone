package gameStates;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Game;
import main.Main;
import util.ResourceLoader;

public class GSBattle extends GameState{
	
	private BufferedImage image;
	public GSBattle(Game game,ResourceLoader res)
	{
		
	}
			
		
	public void update()
	{
		//wba.update();
	}
	
	public void render(Graphics2D g)
	{
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);
		g.drawImage(image,0,0,null);
//		if(!wba.aniFinished)
//			wba.render(g);
//	
	}
	
}
