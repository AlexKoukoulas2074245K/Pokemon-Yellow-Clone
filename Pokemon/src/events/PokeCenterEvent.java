package events;

import entities.Map;
import entities.SNpc;
import entities.SPlayer;
import gameStates.Battle;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Game;
import main.Main;
import misc.PokemonHud;
import util.Counter;
import util.Input;
import util.ResourceLoader;
import util.Util;

public class PokeCenterEvent extends ScriptedEvent{
	
	private final BufferedImage pokeBallHeal1 = ResourceLoader.getTexture("ballHeal1");
	private final BufferedImage pokeBallHeal2 = ResourceLoader.getTexture("ballHeal2");
	private final BufferedImage confirmation  = ResourceLoader.getTexture("cancelHeal");
	private final BufferedImage cursorImage   = ResourceLoader.getTexture("horCursor");
	
	private final int BALL_DELAY = 30;
	private final int FLASH_DELAY = 80;
	
	private final int[] confPos = new int[]{Main.WIDTH - confirmation.getWidth(),
											Main.HEIGHT - Battle.TB_HEIGHT - confirmation.getHeight()};
	private final int[] optPos = new int[]{confPos[0] + 60, confPos[1] + 84};
	private final int[] cursorPos = new int[]{confPos[0] + 29, confPos[1] + 54};
	private final int[][] ballPos = new int[][]{
												new int[]{172,120},
												new int[]{196,120},
												new int[]{172,140},
												new int[]{196,140},
												new int[]{172,160},
												new int[]{196,160}
												};

	private Counter delayCounter;
	private Counter ballDelayCounter;
	private Counter flashingDelayCounter;
	private Counter joyBowCounter;
	
	private SPlayer player;
	private SNpc joy;
	private Map map;
	
	private int nPok;
	private int phase;
	private int cursorIndex;
	private int ballsShown;
	
	public PokeCenterEvent(SNpc joy, SPlayer player, Map map)
	{
		
		this.joy = joy;
		this.player = player;
		this.map = map;
		
		nPok = Util.getMaxIndex(player.getPokemon()) + 1;
		
		finished = false;
		
		ballsShown = 0;
		cursorIndex = 0;
		phase = 0;
		textbox = Util.generateTextbox("Welcome to our#POKEMON CENTER!#@We heal your#POKEMON back to#perfect health!", 2);
		
		delayCounter = new Counter(Game.STDTSIZE);
		ballDelayCounter = new Counter(BALL_DELAY);
		flashingDelayCounter = new Counter(FLASH_DELAY);
		joyBowCounter = new Counter(Game.STDTSIZE * 2/3);
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
			if(!textbox.getAlive())
			{
				delayCounter.update();
				if(!delayCounter.getAlive())
				{
					joy.setDirection(3);
					phase ++;
				}
			}
		}
		
		else if(phase == 3)
		{
			ballDelayCounter.update();
			if(!ballDelayCounter.getAlive())
			{
				ballDelayCounter = new Counter(BALL_DELAY);
				ballsShown ++;
				if(ballsShown > nPok - 1)
					phase ++;
			}
		}
		
		else if(phase == 4)
		{
			ballDelayCounter.update();
			if(!ballDelayCounter.getAlive())
				phase ++;
			
		}
		
		else if(phase == 5)
		{
			flashingDelayCounter.update();
			if(!flashingDelayCounter.getAlive())
			{
				flashingDelayCounter = new Counter(30);
				phase ++;
			}
		}
		else if(phase == 6)
		{
			flashingDelayCounter.update();
			if(!flashingDelayCounter.getAlive())
			{
				textbox = Util.generateTextbox("Thank you!#Your POKEMON are#fighting fit!",1);
				ballsShown = 0;
				joy.setDirection(2);
				phase ++;
			}
		}
		
		else if(phase == 7)
		{
			if(textbox != null)
			{
				textbox.update();
				if(textbox == null)
					joy.setJoyBow(true);
			}
			
			else
			{
				joyBowCounter.update();
				if(!joyBowCounter.getAlive())
				{
					joy.setJoyBow(false);
					phase ++;
					textbox = Util.generateTextbox("We hope to see#you again!");
				}
			}
		}
		
		else if(phase == 8)
		{
			textbox.update();
			if(textbox == null)
			{
				player.resetPokemonStats(true);
				player.setHome(map.getArea());
				finished = true;
			}
		}
		
	}
	
	private void input()
	{
		if(Input.DOWN_TAPPED)
		{
			cursorIndex = cursorIndex == 0 ? 1 : cursorIndex;
		}
		
		else if(Input.UP_TAPPED)
		{
			cursorIndex = cursorIndex == 1 ? 0 : cursorIndex;
		}
		
		else if(Input.A_TAPPED && cursorIndex == 0)
		{
			textbox = Util.generateTextbox("OK. We'll need#your POKEMON.",2);
			phase ++;
		}
		else if(Input.A_TAPPED && cursorIndex == 1 || Input.B_TAPPED)
		{
			textbox = Util.generateTextbox("We hope to see#you again!");
			phase = 8;
		}
		
	}
	
	public void render(Graphics2D g)
	{
		for(int i = 0; i < ballsShown; i ++)
		{
			if(phase != 5)
				g.drawImage(pokeBallHeal1, ballPos[i][0] + Map.xoffset, ballPos[i][1] + Map.yoffset, null);
			else
			{
				int time = flashingDelayCounter.getTime();
				if((time <= 80 && time > 70) || (time <= 60 && time > 50) ||
					(time <= 40 && time > 30) || (time <= 20 && time > 10))
					g.drawImage(pokeBallHeal2, ballPos[i][0] + Map.xoffset, ballPos[i][1] + Map.yoffset, null);
				else
					g.drawImage(pokeBallHeal1, ballPos[i][0] + Map.xoffset, ballPos[i][1] + Map.yoffset, null);
			}
		}
		if(phase == 1)
		{
			g.drawImage(confirmation, confPos[0], confPos[1], null);
			g.drawImage(cursorImage, cursorPos[0], cursorPos[1] + 60 * cursorIndex, null);
			g.setFont(PokemonHud.FONT);
			g.setColor(PokemonHud.BLACK);
			g.drawString("HEAL", optPos[0], optPos[1]);
			g.drawString("CANCEL", optPos[0], optPos[1] + 60);
		}
		
		if(textbox != null)
			textbox.render(g);
	}
}
