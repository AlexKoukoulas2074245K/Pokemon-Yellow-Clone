package misc;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import battleStates.BSMain;
import main.Game;
import main.Main;
import util.BattleCalc;
import util.Counter;
import util.Input;
import util.ResourceLoader;
import util.Util;
import entities.Pokemon;
import entities.SPlayer;
import entities.Textbox;
import gameStates.Battle;

public class PokemonPanel {
	
	private final int ANI_DELAY = 16;
	
	private final String[] bOptions = new String[]{"SWITCH","STATS","CANCEL"};
	private final String[] nOptions = new String[]{"STATS","SWITCH","CANCEL"};
	
	private final BufferedImage CURSOR_IMG = ResourceLoader.getTexture("horCursor");
	private final BufferedImage CURSOR_SEL_IMG = ResourceLoader.getTexture("horCursorSel");
	private final BufferedImage TEXTBOX_IMG = ResourceLoader.getTexture("textbox");
	private final BufferedImage POKE_STATS = ResourceLoader.getTexture("pokeSelStats");
	private final BufferedImage SEL_OPTIONS = ResourceLoader.getTexture("pokeSelOptions");
	
	private final Font FONT = PokemonHud.FONT;
	private final Color WHITE = PokemonHud.WHITE;
	private final Color BLACK = PokemonHud.BLACK;
	private final Color GREEN = PokemonHud.GREEN;
	private final Color ORANGE = PokemonHud.ORANGE;
	private final Color RED = PokemonHud.RED;
	
	private static Textbox textbox;
	private Pokemon[] pokemon;
	
	private boolean battle;
	private boolean alive;
	private boolean selected;
	private boolean urgent;
	private boolean pokeSwitch;
	private boolean pokeSwitchAni;
	private boolean healing;

	
	private int cursorIndex;
	private int secondaryIndex;
	private int aniDelay;
	private float healingAmount;
	
	private int hp0;
	
	private SPlayer player;
	private Counter pokeSwitchCounter;
	private PokemonStats pkStats;
	
	private Item itemToUse;
	
	public static void destroyTextbox() 
	{
		textbox = null;
	}
	
	public PokemonPanel(Pokemon[] pokemon, boolean battle, boolean urgent, Item itemToUse, SPlayer player)
	{
		alive = true;
		selected = false;
		
		this.pokemon = pokemon;
		this.battle = battle;
		this.urgent = urgent;
		this.itemToUse = itemToUse;
		this.player = player;
		
		pokeSwitch = false;
		pokeSwitchAni = false;
		healing = false;
		healingAmount = 0;
		hp0 = 0;
		
		cursorIndex = 0;
		secondaryIndex = 0;
		
		aniDelay = ANI_DELAY;
		
		Input.A_TAPPED = false;
	}
	public PokemonPanel(Pokemon[] pokemon, boolean battle, boolean urgent)
	{
		this(pokemon,battle,urgent,null,null);
	}
	
	public void update()
	{
		if(healing)
		{
			float healingIncr = BattleCalc.getDamageDecr(pokemon[cursorIndex], pokemon[cursorIndex]);
			pokemon[cursorIndex].increaseStatBy(Pokemon.HP, healingIncr);
			healingAmount -= healingIncr;
			
			if(healingAmount <= 0)
			{
				healing = false;
			}
			
			else if(pokemon[cursorIndex].getStat(Pokemon.HP) >= pokemon[cursorIndex].getMaxHp())
			{
				pokemon[cursorIndex].setHp(pokemon[cursorIndex].getMaxHp());
				healing = false;
			}
			
			if(!healing)
			{
				textbox = Util.generateTextbox(pokemon[cursorIndex].getName() +
						"#recovered by " + String.valueOf((int)pokemon[cursorIndex].getStat(Pokemon.HP) - hp0) + "!");
			}
			
		}
		
		if(pkStats != null)
		{
			pkStats.update();
			if(!pkStats.getAlive())
			{
				pkStats = null;
				selected = false;
			}
			return;
		}
		
		if(textbox != null)
		{
			textbox.update();
			
			if(textbox == null)
			{
				selected = false;
				if(itemToUse != null)
				{
					alive = false;
					if(hp0 != 0 && hp0 != (int)pokemon[cursorIndex].getMaxHp())
					{
						if(player != null)
							player.getBag().tossItem(itemToUse.getName(), 1);
					}
				}
			}
			
			return;
		}
		
		if(pokeSwitchAni)
		{
			pokeSwitchCounter.update();
			if(!pokeSwitchCounter.getAlive())
			{
				pokeSwitch = false;
				pokeSwitchAni = false;
				Pokemon temp = pokemon[cursorIndex];
				pokemon[cursorIndex] = pokemon[secondaryIndex];
				pokemon[secondaryIndex] = temp;
				
				cursorIndex = secondaryIndex;
			}
			return;
		}
		
		input();
	}
	
	public void input()
	{
		if(Input.DOWN_TAPPED)
		{
			if(pokeSwitch)
			{
				secondaryIndex = secondaryIndex + 1 > Util.getMaxIndex(pokemon) ? 0 : secondaryIndex + 1;
				return;
			}
			
			if(!selected)
			{
				cursorIndex ++;
				if(cursorIndex > Util.getMaxIndex(pokemon))
					cursorIndex = 0;
				
				aniDelay = ANI_DELAY;
			}
			else
			{
				if(secondaryIndex < 2)
					secondaryIndex ++;
			}
		}
		
		else if(Input.UP_TAPPED)
		{
			
			if(pokeSwitch)
			{
				secondaryIndex = secondaryIndex - 1 < 0 ? Util.getMaxIndex(pokemon) : secondaryIndex - 1;
				return;
			}

			if(!selected)
			{
				cursorIndex --;
				if(cursorIndex < 0)
					cursorIndex = Util.getMaxIndex(pokemon);
				
				aniDelay = ANI_DELAY;	
			}
			else
			{
				if(secondaryIndex > 0)
					secondaryIndex --;
			}
		}
		
		else if(Input.A_TAPPED)
		{
			if(itemToUse != null)
			{
				if(itemToUse.isPotion())
				{
					hp0 = (int)pokemon[cursorIndex].getStat(Pokemon.HP);
					
					if(hp0 == (int)pokemon[cursorIndex].getMaxHp() || hp0 == 0)
						textbox = Util.generateTextbox("It won't have#any effect.",1);
		
					else
					{
						healing = true;
						healingAmount = itemToUse.getEffectPower();
					}
					
				}
				
				return;
			}
			
			if(pokeSwitch)
			{
				selected = false;
				pokeSwitchAni = true;
				pokeSwitchCounter = new Counter(Game.STDTSIZE/2);
				return;
			}
			
			if(!selected && !urgent)
			{
				selected = true;
				secondaryIndex = 0;
			}
			
			else
			{
				if(battle)
				{
					if(secondaryIndex == 0)
					{
						if(Battle.playerActive == pokemon[cursorIndex])
							textbox = Util.generateTextbox(String.format("%s is#already out!", pokemon[cursorIndex].getName()), 1);
						if(!pokemon[cursorIndex].getAlive())
							textbox = Util.generateTextbox("There's no will#to fight!",1);
						else
						{
							if(!urgent)
							{
								Battle.setTextbox(String.format("%s enough!#come back!",Battle.playerActive.getName()), 2);
								Battle.playerActive = pokemon[cursorIndex];
								Battle.setPlaceHolderTextbox(String.format("Go! %s!", Battle.playerActive.getName()), 2);
							}
							else
							{
								Battle.playerActive = pokemon[cursorIndex];
								Battle.setTextbox(String.format("Go! %s!", Battle.playerActive.getName()),2);
							}
							BSMain.retreatCounter = new Counter(Game.STDTSIZE);
							alive = false;
						}	
					
					}
					else if(secondaryIndex == 1)
						pkStats = new PokemonStats(pokemon[cursorIndex]);
					else
						alive = false;
				}
				else
				{
					if(secondaryIndex == 0)
						pkStats = new PokemonStats(pokemon[cursorIndex]);
					
					else if(secondaryIndex == 1)
					{
						pokeSwitch = true;
						selected = false;
						secondaryIndex = cursorIndex;
					}
					else
						alive = false;
				}
			}
		}
		else if(Input.B_TAPPED)
		{
			if(pokeSwitch)
				pokeSwitch = false;
			else if(selected)
				selected = false;
			else if(!selected)
				alive = false;
		}
	}
	
	public void render(Graphics2D g)
	{
		g.setColor(WHITE);
		g.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);
		
		if(!selected)
			g.drawImage(CURSOR_IMG, 8, 24 + cursorIndex * Game.STDTSIZE, null);
		else
			g.drawImage(CURSOR_SEL_IMG, 8, 24 + cursorIndex * Game.STDTSIZE, null);
		
		if(healing)
			g.drawImage(CURSOR_SEL_IMG, 8, 24 + cursorIndex * Game.STDTSIZE, null);
		
		for(int i = 0; i <= Util.getMaxIndex(pokemon); i ++)
		{
			if(pokeSwitchAni)
			{
				if(i == cursorIndex)
					continue;
				
				if(i == secondaryIndex && pokeSwitchCounter.getTime() < Game.STDTSIZE/4)
					continue;
			}
			
			if(cursorIndex == i && !selected)
			{
				if(animation())
					g.drawImage(pokemon[i].getOvImages()[1], 44, i * Game.STDTSIZE, null);
				else
					g.drawImage(pokemon[i].getOvImages()[0], 44, i * Game.STDTSIZE, null);
			}
			else
				g.drawImage(pokemon[i].getOvImages()[0], 44, i * Game.STDTSIZE, null);
			
			drawBars(g,pokemon[i],i);
			
			g.drawImage(POKE_STATS, 110 + FONT.getSize(), 6 + i * 64, null);
			g.setColor(BLACK);
			g.drawString(pokemon[i].getName(), 110, FONT.getSize() + i * 64);
			g.drawString(String.valueOf(pokemon[i].getLevel()), 454, FONT.getSize() - 2 + i * 64);
			
			String hp = String.valueOf((int)(pokemon[i].getStat(Pokemon.HP)));
			String maxHp = String.valueOf((int)(pokemon[i].getMaxHp()));
			
			g.drawString(hp, 510 - hp.length() * FONT.getSize(), 2* FONT.getSize() - 2 + i * 64);
			g.drawString(maxHp, 640 - maxHp.length() * FONT.getSize(),2*FONT.getSize() - 2 + i * 64);
			
			if(pokemon[i].getAlive())
			{
				if(pokemon[i].getStatus() != Pokemon.OK)
					g.drawString(pokemon[i].getStatusName(), 454 + 3 * FONT.getSize(), FONT.getSize() + i * 64);
			}
			else
				g.drawString("FNT", 454 + 3 * FONT.getSize(), FONT.getSize() + i * 64);
		}	
			
		g.drawImage(TEXTBOX_IMG, 0, Main.HEIGHT - Battle.TB_HEIGHT, null);
		g.setColor(BLACK);
		
		if(itemToUse == null)
			g.drawString("Choose a POK" + "\u00e9" + "MON.", 40, Main.HEIGHT - Battle.TB_HEIGHT + 86);
		else
		{
			g.drawString("Use item on which", 40, Main.HEIGHT - Battle.TB_HEIGHT + 86);
			g.drawString("POK" + "\u00e9" + "MON?", 40, Main.HEIGHT - Battle.TB_HEIGHT + 146);
		}
		
		if(selected)
		{
			g.drawImage(SEL_OPTIONS, Main.WIDTH - SEL_OPTIONS.getWidth(), 
									 Main.HEIGHT - SEL_OPTIONS.getHeight(), null);
			
			if(battle)
			{
				for(int i = 0; i < 3; i ++)
					g.drawString(bOptions[i], Main.WIDTH - SEL_OPTIONS.getWidth() + Game.STDTSIZE,
											  Main.HEIGHT - SEL_OPTIONS.getHeight() + 30 + 
											  FONT.getSize() + i * (FONT.getSize() * 2));
			}
			else
			{
				for(int i = 0; i < 3; i ++)
					g.drawString(nOptions[i], Main.WIDTH - SEL_OPTIONS.getWidth() + Game.STDTSIZE,
											  Main.HEIGHT - SEL_OPTIONS.getHeight() + 30 +
											  FONT.getSize() + i * (FONT.getSize() * 2));
			}
			
			g.drawImage(CURSOR_IMG, Main.WIDTH - SEL_OPTIONS.getWidth() + 30, 
								    Main.HEIGHT - SEL_OPTIONS.getHeight() + 30 + secondaryIndex * (FONT.getSize() * 2), null);
			
		}
		
		if(pokeSwitch)
		{
			g.drawImage(CURSOR_SEL_IMG, 8, 24 + cursorIndex * Game.STDTSIZE, null);
			g.drawImage(CURSOR_IMG, 8, 24 + secondaryIndex * Game.STDTSIZE, null);
		}
		
		if(textbox != null)
			textbox.render(g);
		
		if(pkStats != null)
			pkStats.render(g);
		
	}
	
	private void drawBars(Graphics2D g, Pokemon curr, int index)
	{
		if(curr.getStat(Pokemon.HP) <= 0) return;
		
		Color color = GREEN;
		
		if(curr.getStat(Pokemon.HP) < curr.getMaxHp()/2)
			color = ORANGE;
		if(curr.getStat(Pokemon.HP) < curr.getMaxHp()/4)
			color = RED;
		
		g.setColor(color);
		
		int barWidth = (int)(PokemonHud.BAR_WIDTH * curr.getStat(Pokemon.HP)/curr.getMaxHp());
		
		g.fillRect(110 + FONT.getSize() + 60, 6 + index * 64 + 36 , barWidth, PokemonHud.BAR_HEIGHT);
	}
	
	private boolean animation()
	{
		aniDelay --;
		if(aniDelay == 0)
			aniDelay = ANI_DELAY;
		
		return aniDelay < ANI_DELAY/2;
	}
	
	//Getters
	public boolean getAlive()
	{
		return alive;
	}
	
	public Pokemon getSelection()
	{
		return pokemon[cursorIndex];
	}

	
	//Setters
}
