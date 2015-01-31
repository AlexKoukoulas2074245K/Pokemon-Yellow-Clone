package misc;

import entities.Map;
import entities.SPlayer;
import gameStates.GSOverworld;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Main;
import util.GameInfo;
import util.Input;
import util.ResourceLoader;

public class Menu {
	
	private final Font FONT = PokemonHud.FONT;
	private final Color COLOR = PokemonHud.BLACK;
	
	private final BufferedImage imageDex = ResourceLoader.getTexture("playerMenu_Dex");
	private final BufferedImage cursorImg = ResourceLoader.getTexture("horCursor");
	
	private final int WIDTH = imageDex.getWidth();
	private final int[] pos = new int[]{Main.WIDTH - WIDTH, 0};
	private final int[] optionsPos0 = new int[]{pos[0] + 60, 84}; 
	private final int[] cursorPos0 = new int[]{pos[0] + 29, 64};
	
	private final int POKEMON = 1;
	private final int ITEM = 2;
	private final int PLAYER = 3;
	private final int SAVE = 4;
	private int mainIndex;
	
	private boolean alive;
	private boolean selected;
	
	private GSOverworld worldRef;
	private SPlayer player;
	private PokemonPanel pokePanel;
	private PlayerPanel playerPanel;
	private SaveMenu saveMenu;
	private BagMenu bagMenu;
	
	private String[] options;
	
	public Menu(SPlayer player, Map map)
	{
		this.player = player;
		this.worldRef = map.getWorldRef();
		
		alive = true;
	
		createOptions();
		
		mainIndex = 0;
		
		selected = false;
	}
	
	private void createOptions()
	{
		options = new String[]
				{
					"POK" + "\u00e9" + "DEX",
					"POK" + "\u00e9" + "MON",
					"ITEM",
					GameInfo.PLAYERNAME,
					"SAVE",
					"OPTION",
					"EXIT"
				};
	}
	
	public void update()
	{
		if(selected)
			updateSelected();
		else
			input();
	}
	
	private void updateSelected()
	{
		if(mainIndex == POKEMON)
		{
			pokePanel.update();
			if(!pokePanel.getAlive())
			{
				pokePanel = null;
				selected = false;
			}
		}
		
		else if(mainIndex == PLAYER)
		{
			playerPanel.update();
			if(!playerPanel.getAlive())
			{
				playerPanel = null;
				selected = false;
			}
		}
		
		else if(mainIndex == ITEM)
		{
			bagMenu.update();
			if(!bagMenu.getAlive())
			{
				bagMenu = null;
				selected = false;
			}
		}
		
		else if(mainIndex == SAVE)
		{
			saveMenu.update();
			if(!saveMenu.getAlive())
			{
				saveMenu = null;
				selected = false;
				alive = false;
			}
		}
	}
	
	private void input()
	{
		if(Input.DOWN_TAPPED)
		{
			mainIndex = mainIndex + 1 > options.length - 1 ? 0 : mainIndex + 1;
		}
		
		else if(Input.UP_TAPPED)
		{
			mainIndex = mainIndex - 1 < 0 ? options.length - 1 : mainIndex - 1;
		}
		
		else if(Input.A_TAPPED)
		{
			actionSelect();
		}
		
		else if(Input.B_TAPPED || Input.START_TAPPED)
			alive = false;
	}
	
	private void actionSelect()
	{
		selected = true;
		
		if(mainIndex == POKEMON)
			pokePanel = new PokemonPanel(player.getPokemon(), false,false);
		
		else if(mainIndex == ITEM)
			bagMenu = new BagMenu(player, false);
		
		else if(mainIndex == PLAYER)
			playerPanel = new PlayerPanel(player);
		
		else if(mainIndex == SAVE)
			saveMenu = new SaveMenu(player, worldRef);
		else
			alive = false;
	}
	
	public void render(Graphics2D g)
	{
		g.drawImage(imageDex, pos[0], pos[1], null);
		drawOptions(g);
		drawCursor(g);
		
		if(selected)
			drawSelected(g);
	}
	
	private void drawSelected(Graphics2D g)
	{
		if(mainIndex == POKEMON)
			pokePanel.render(g);
		
		else if(mainIndex == ITEM)
			bagMenu.render(g);
		
		else if(mainIndex == PLAYER)
			playerPanel.render(g);
		
		else if(mainIndex == SAVE)
			saveMenu.render(g);
	}
	
	private void drawOptions(Graphics2D g)
	{
		g.setColor(COLOR);
		g.setFont(FONT);
		
		for(int i = 0; i < options.length; i ++)
			g.drawString(options[i], optionsPos0[0], 
					optionsPos0[1] + i * (FONT.getSize() * 2));
	}
	
	private void drawCursor(Graphics2D g)
	{
		g.drawImage(cursorImg, cursorPos0[0], 
				cursorPos0[1] + mainIndex * (FONT.getSize() * 2), null);
	}
	
	//Getters
	public boolean getAlive()
	{
		return alive;
	}
			
}
