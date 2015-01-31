package events;

import entities.Map;
import entities.SPlayer;
import gameStates.Battle;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Game;
import main.Main;
import util.GameInfo;
import util.Input;
import util.ResourceLoader;
import util.Util;

public class PewterScriptedMuseumEvent extends ScriptedEvent {
	
	private final BufferedImage moneyImg   = ResourceLoader.getTexture("marketMoney");
	private final BufferedImage confImg = ResourceLoader.getTexture("confirmation");
	private final BufferedImage cursorImg = ResourceLoader.getTexture("horCursor");
	
	private final int[] moneyPos = new int[]{Main.WIDTH - moneyImg.getWidth(), 0};
	private final int[] confPos = new int[]{Main.WIDTH - confImg.getWidth(), 
			Main.HEIGHT - confImg.getHeight() - Battle.TB_HEIGHT};
	private final int[] confOptionsPos0 = new int[]{confPos[0] + 72, confPos[1] + 58};
	private final int[] confIndexPos = new int[]{confPos[0] + 36 , confPos[1] + 34};

	private SPlayer player;
	
	private int phase;
	private int confIndex;
		
	public PewterScriptedMuseumEvent(SPlayer player,Map map)
	{
		finished = false;
		this.player = player;
		player.setNewTiles(map);
		
		confIndex = 0;
		phase = 0;
		textbox = Util.generateTextbox("It costs $50 for a#child's ticket.#@Would you like to#come in?",2);
	}
	
	public void update()
	{
		if(phase == 0)
		{
			textbox.update();
			if(!textbox.getAlive())
				phase ++;
		}
		
		else if(phase == 1)
		{
			input();
		}
		
		else if(phase == 2)
		{
			textbox.update();
			if(textbox == null)
			{
				finished = true;
			}
		}
		
		else if(phase == 3)
		{
			textbox.update();
			if(textbox == null)
			{
				player.setDirection(2);
				player.setNextTile(2);
				player.setMoving(true);
				finished = true;
			}
		}
	}
	
	private void input()
	{
		if(Input.DOWN_TAPPED)
		{
			confIndex = confIndex == 0 ? 1 : confIndex;
		}
		
		else if(Input.UP_TAPPED)
		{
			confIndex = confIndex == 1 ? 0 : confIndex;
		}
		
		else if(Input.A_TAPPED)
		{
			if(confIndex == 0)
			{
				if(GameInfo.PLAYERMONEY >= 50)
				{
					phase ++;
					textbox = Util.generateTextbox("Right, $50!#Thank you!");
					GameInfo.PLAYERMONEY -= 50;
					player.setHasPayed(true);
				}
				else
				{
					phase += 2;
					textbox = Util.generateTextbox("You don't have#enough money.#@Come again!");
				}
					
			}
			else if(confIndex == 1)
			{
				phase += 2;
				textbox = Util.generateTextbox("Come again!");
			}
		}
		
		else if(Input.B_TAPPED)
		{
			phase = 8;
		}
	}
	
	public void render(Graphics2D g)
	{
		g.drawImage(moneyImg, moneyPos[0], moneyPos[1], null);
		g.drawString(String.valueOf(GameInfo.PLAYERMONEY), moneyPos[0] + Game.STDTSIZE, 
				   moneyPos[1] + Game.STDTSIZE);
		
		if(phase == 1)
			drawConfirmation(g);
		
		if(textbox != null)
			textbox.render(g);
	}
	
	private void drawConfirmation(Graphics2D g)
	{
		g.drawImage(confImg, confPos[0], confPos[1], null);
		g.drawString("YES", confOptionsPos0[0], confOptionsPos0[1]);
		g.drawString("NO", confOptionsPos0[0], confOptionsPos0[1] + 60);
		
		if(confIndex == 0)
			g.drawImage(cursorImg, confIndexPos[0],confIndexPos[1], null);
		else
			g.drawImage(cursorImg, confIndexPos[0],confIndexPos[1] + 60, null);
	}
}
