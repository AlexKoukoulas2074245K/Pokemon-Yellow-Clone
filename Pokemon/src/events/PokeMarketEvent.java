package events;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Game;
import main.Main;
import misc.BagMenu;
import misc.PokemonHud;
import util.GameInfo;
import util.Input;
import util.ResourceLoader;
import util.Util;
import entities.Map;
import entities.SNpc;
import entities.SPlayer;

public class PokeMarketEvent extends ScriptedEvent {
	
	
	private final BufferedImage optionsImg = ResourceLoader.getTexture("marketDialog");
	private final BufferedImage moneyImg   = ResourceLoader.getTexture("marketMoney");
	private final BufferedImage cursorImg  = ResourceLoader.getTexture("horCursor");
	private final BufferedImage selCursor  = ResourceLoader.getTexture("horCursorSel");
	
	private final int[] optionsPos = new int[]{0,0};
	private final int[] moneyPos = new int[]{Main.WIDTH - moneyImg.getWidth(), 0};
	private final int[] stringPos = new int[]{optionsPos[0] + 60,optionsPos[1] + 65};
	private final int[] cursorPos = new int[]{optionsPos[0] + 29, optionsPos[1] + 40};

	private SNpc seller;
	private SPlayer player;
	private Map map;
	private BagMenu bm;
	
	private int phase;
	private int cursorIndex;
	
	private boolean buying;
	private boolean selling;
	
	public PokeMarketEvent(SNpc seller,SPlayer player, Map map)
	{
		this.seller = seller;
		this.player = player;
		this.map = map;
		
		finished = false;
		if(player.getDirection() == 0)
			seller.setDirection(2);
		
		buying = false;
		selling = false;
		
		textbox = Util.generateTextbox("Hi there!#May I help you?",2);
		
		phase = 0;
		cursorIndex = 0;
	}
	
	public void update()
	{
		if(bm != null)
		{
			bm.update();
			if(!bm.getAlive())
			{
				
				textbox = Util.generateTextbox("Is there anything#I can do for you?",2);
				bm = null;
				selling = false;
				buying = false;
				phase = 1;
				cursorIndex = 0;
			}
			return;
		}
		
		if(phase == 0)
		{
			textbox.update();
			if(!textbox.getAlive())
				phase ++;
		}
		
		else if(phase == 1)
		{
			if(textbox != null && textbox.getAlive())
				textbox.update();
			else
				input();
		}
		
		else if(phase == 2)
		{
			textbox.update();
			if(!textbox.getAlive())
				bm = new BagMenu(player,map,true,buying,selling,seller);
		}
		
		else if(phase == 8)
		{
			textbox.update();
			if(textbox == null)
				finished = true;
		}
	}
	
	private void input()
	{
		if(Input.DOWN_TAPPED)
		{
			if(cursorIndex != 2)
				cursorIndex ++;
		}
		
		else if(Input.UP_TAPPED)
		{
			if(cursorIndex != 0)
				cursorIndex --;
		}

		else if(Input.A_TAPPED && cursorIndex == 0)
		{
			buying = true;
			textbox = Util.generateTextbox("Take your time.",2);
			phase ++;
		}
		else if(Input.A_TAPPED && cursorIndex == 1)
		{
			selling = true;
			textbox = Util.generateTextbox("What would you#like to sell?",2);
			phase ++;
		}
		
		else if(Input.B_TAPPED || (Input.A_TAPPED && cursorIndex == 2))
		{
			textbox = Util.generateTextbox("Thank you!");
			phase = 8;
		}
	}
	
	public void render(Graphics2D g)
	{
		if(phase != 0)
		{
			g.drawImage(optionsImg,optionsPos[0],optionsPos[1],null);
			g.drawImage(moneyImg, moneyPos[0], moneyPos[1], null);
			g.drawImage(cursorImg, cursorPos[0], cursorPos[1] + 60 * cursorIndex, null);
			g.setFont(PokemonHud.FONT);
			g.setColor(PokemonHud.BLACK);
			g.drawString("BUY", stringPos[0], stringPos[1]);
			g.drawString("SELL", stringPos[0], stringPos[1] + 60);
			g.drawString("QUIT", stringPos[0], stringPos[1] + 120);
			g.drawString(String.valueOf(GameInfo.PLAYERMONEY), moneyPos[0] + Game.STDTSIZE, 
															   moneyPos[1] + Game.STDTSIZE);
		}
		
		if(phase == 2 || phase == 8)
			g.drawImage(selCursor, cursorPos[0], cursorPos[1] + 60 * cursorIndex, null);
	
		if(textbox != null)
			textbox.render(g);
		
		if(bm != null)
			bm.render(g);
	}
}
