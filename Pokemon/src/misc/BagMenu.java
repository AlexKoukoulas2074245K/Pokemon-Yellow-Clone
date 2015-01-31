package misc;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Main;
import util.GameInfo;
import util.Input;
import util.ResourceLoader;
import util.Util;
import entities.Map;
import entities.SNpc;
import entities.SPlayer;
import entities.Textbox;
import gameStates.Battle;
import gameStates.GSOverworld;

public class BagMenu{
	
	private static Textbox textbox;
	
	private final Color COLOR = PokemonHud.BLACK;
	private final Font FONT = PokemonHud.FONT;
	
	private final BufferedImage cursorImg = ResourceLoader.getTexture("horCursor");
	private final BufferedImage selCursor = ResourceLoader.getTexture("horCursorSel");
	private final BufferedImage quantity = ResourceLoader.getTexture("quantity");
	private final BufferedImage bagImg = ResourceLoader.getTexture("bag");
	private final BufferedImage useToss = ResourceLoader.getTexture("useToss");
	private final BufferedImage selNumImg = ResourceLoader.getTexture("nSel");
	private final BufferedImage confImg = ResourceLoader.getTexture("confirmation");
	private final BufferedImage sellImg = ResourceLoader.getTexture("buySellIndi");
	private final BufferedImage poundSymbol = ResourceLoader.getTexture("poundSymbol");
	
	private int[] mainPos;
	private int[] useTossPos;
	private int[] selNumPos;
	private int[] confPos;
	private int[] confOptionsPos0;
	private int[] confIndexPos;
	private int[] selNumQuantImgPos;
	private int[] selNumQuantPos;
	private int[] tossIndexPos;
	private int[] tossStringPos;
	private int[] itemPos0;
	private int[] cursorPos0;
	private int[] sellIndiPos;
	
	private int quantX;
	
	private boolean battle;
	private boolean selected;
	private boolean selNum;
	private boolean alive;
	private boolean confirmation;
	private boolean confirmed;
	private boolean buying;
	private boolean selling;
	private boolean sellIndi;
	private boolean sellIndiConf;
	private boolean notEnoughMoney;
	
	private int mainIndex;
	private int displIndex;
	private int tossIndex;
	private int confIndex;
	
	private int tossQuant;
	private int sellQuant;
	
	private int[] shownBoundaries;
	
	private SPlayer player;
	private Bag bag;
	private Item itemToUse;
	private PokemonPanel pokePanel;
	
	public static void destroyTextbox()
	{
		textbox = null;
	}
	
	public BagMenu(SPlayer player, boolean battle)
	{
		this(player,null, battle,false,false, null);
	}
	public BagMenu(SPlayer player, Map map, boolean battle, boolean buying, boolean selling, SNpc seller)
	{
		
		this.player = player;
		this.battle = battle;
		
		this.buying = buying;
		this.selling = selling;
		
		if(buying) {
		}
		
		alive = true;
		selected = false;
		selNum = false;
		confirmation = false;
		confirmed = false;
		sellIndi = false;
		sellIndiConf = false;
		notEnoughMoney = false;
		
		if(!buying)
			bag = player.getBag();
		else
			bag = ResourceLoader.getMarketItems(map.getArea());
		
		getIndices(battle);
	
		tossIndex = 0;
		confIndex = 0;
		tossQuant = 1;
		sellQuant = 1;
		
		setDrawingPos();
	}
	
	
	private void setDrawingPos()
	{
		mainPos = new int[]{Main.WIDTH - bagImg.getWidth(), 54};
		
		if(battle)
			mainPos[1] += 38;
		
		useTossPos = new int[]{Main.WIDTH - useToss.getWidth(), 
				54 + bagImg.getHeight() - useToss.getHeight()/2};
		selNumPos = new int[]{Main.WIDTH - selNumImg.getWidth(), 
				useTossPos[1] - selNumImg.getHeight()/2 + 9};
		confPos = new int[]{Main.WIDTH - confImg.getWidth(), 
				Main.HEIGHT - confImg.getHeight() - Battle.TB_HEIGHT};
		confOptionsPos0 = new int[]{confPos[0] + 72, confPos[1] + 58};
		confIndexPos = new int[]{confPos[0] + 36 , confPos[1] + 34};
		selNumQuantImgPos = new int[]{selNumPos[0] + 44, selNumPos[1] + 42};
		selNumQuantPos = new int[]{selNumQuantImgPos[0] + 25, selNumQuantImgPos[1] + 20};
		tossIndexPos = new int[]{useTossPos[0] + 36, useTossPos[1] + 34};
		tossStringPos = new int[]{useTossPos[0] + 72, useTossPos[1] + 58};
		itemPos0 = new int[]{mainPos[0] + 60, mainPos[1] + 84};
		cursorPos0 = new int[]{mainPos[0] + 29, mainPos[1] + 54};
		sellIndiPos = new int[]{Main.WIDTH - sellImg.getWidth(), 296};
		
		quantX = itemPos0[0] + 236;
	}
	
	public void update()
	{
	
		if(textbox != null)
		{
			updateTextbox();
			
			if(confirmation)
				input();
		}
		
		else if(pokePanel != null)
		{
			pokePanel.update();
			if(!pokePanel.getAlive())
			{
				pokePanel = null;
				selected = false;
			}
		}
		
		else
			input();
	}
	
	private void updateTextbox()
	{
		

		textbox.update();
		
		if(sellIndi && !textbox.getAlive())
		{
			sellIndi = false;
			sellIndiConf = true;
			return;
		}
		
		if(textbox == null)
		{
			if(notEnoughMoney)
			{
				destroy(battle);
				return;
			}
			
			if(sellIndi && sellIndiConf)
			{
				sellIndi = false;
				sellIndiConf = false;
			}
			
			if(selling && itemToUse.isUnique())
			{
				destroy(true);
				return;
			}
			
			if(!confirmed)
				selected = false;
			else
			{
				selNum = false;
				selected = false;
				confirmed = false;
				tossItem();
			}
			
			return;
		}
		
		if(!textbox.getAlive())
		{
			confirmation = true;
			return;
		}
		
	}
	
	private void input()
	{
		
		if(Input.UP_TAPPED)
		{
			
			if(sellIndi && !sellIndiConf)
			{
				sellQuant ++;
				
				if(buying)
				{
					if(sellQuant > 99)
						sellQuant = 1;
				}
				
				else if(selling)
				{
					if(sellQuant > bag.getQuant(itemToUse.getName()))
						sellQuant = 1;
				}
				
				
				return;
			}
			
			if(confirmation || sellIndiConf)
			{
				confIndex = confIndex == 1 ? 0 : confIndex;
				return;
			}
			
			if(selNum)
			{
				tossQuant ++;
				if(tossQuant > bag.getQuant(mainIndex))
					tossQuant = 1;
				
				return;
			}
			
			if(selected)
			{
				tossIndex = tossIndex == 1 ? 0 : tossIndex;
				return;
			}
			
			if(mainIndex - 1 >= 0)
			{
				mainIndex --;
				displIndex --;
				if(displIndex < 0)
				{
					shownBoundaries[0] --;
					shownBoundaries[1] --;
					displIndex = 0;
				}
			}
		}
		
		else if(Input.DOWN_TAPPED)
		{
			
			if(sellIndi && !sellIndiConf)
			{
				sellQuant --;
				
				if(buying)
				{
					if(sellQuant < 1)
						sellQuant = 99;
				}
				
				else
				{
					if(sellQuant < 1)
						sellQuant = bag.getQuant(bag.getItem(mainIndex));
				}
				return;
			}
			
			if(confirmation || sellIndiConf)
			{
				confIndex = confIndex == 0 ? 1 : confIndex;
				return;
			}
			
			if(selNum)
			{
				tossQuant --;
				
				if(tossQuant < 1)
					tossQuant = bag.getQuant(mainIndex);
			}
			
			if(selected)
			{
				tossIndex = tossIndex == 0 ? 1 : tossIndex;
				return;
			}
			
			if(mainIndex + 1 <= bag.size() - 1)
			{
				mainIndex ++;
				displIndex ++;
				if(displIndex > 2)
				{
					shownBoundaries[0] ++;
					shownBoundaries[1] ++;
					displIndex = 2;
				}
			}
		
		}
		
		if(Input.A_TAPPED)
		{
			
			if(sellIndiConf && confIndex == 0)
			{
				sellIndiConf = false;
				sellIndi = false;
				confirmation = false;
				textbox = null;
				
				if(buying)
				{
					if(GameInfo.PLAYERMONEY >= itemToUse.getBuyingPrice() * sellQuant)
					{
						player.getBag().addItem(itemToUse.getName(), sellQuant);
						GameInfo.PLAYERMONEY -= itemToUse.getBuyingPrice() * sellQuant;
						textbox = Util.generateTextbox("Here you are!#Thank you!",1);
					}
					else
					{
						textbox = Util.generateTextbox("You don't have#enough money!",1);
						notEnoughMoney = true;
					}
				}
				else if(selling)
				{
					bag.tossItem(itemToUse.getName(), sellQuant);
					GameInfo.PLAYERMONEY += itemToUse.getSellingPrice() * sellQuant;
				}
				itemToUse = null;
				sellQuant = 1;
				confIndex = 0;
				return;
			}
			
			else if(sellIndiConf && confIndex == 1)
			{
				sellIndiConf = false;
				sellIndi = false;
				confirmation = false;
				sellQuant = 1;
				confIndex = 0;
				textbox = null;
				return;
			}
			
			if(sellIndi)
			{
				if(buying)
					textbox = Util.generateTextbox(itemToUse.getName() + "?#That will be#$" + 
							  String.valueOf(tossQuant * itemToUse.getBuyingPrice()) + ". OK?", 2);
				
				else if(selling)
					textbox = Util.generateTextbox("I can pay you#$" + String.valueOf(
							tossQuant * itemToUse.getSellingPrice()) + " for that.",2);		
				return;
			}
			
			if(battle)
			{
				if(mainIndex == bag.size() - 1)
					destroy(battle);
				
				else
					actionItemSel();
				
				return;
			}
			
			if(confirmation)
			{
				if(confIndex == 0)
				{
					confirmation = false;
					textbox = Util.generateTextbox("Threw away#" + bag.getItem(mainIndex) + ".", 1);
					confirmed = true;
				}
				
				else
				{
					destroyTextbox();
					confirmation = false;
					selNum = false;
					selected = false;
					tossIndex = 0;
				}
				
				return;
			}
			
			if(selNum)
			{
				textbox = Util.generateTextbox(String.format("Is it OK to toss#%s?", bag.getItem(mainIndex)), 2);
				return;
			}
			
			if(selected)
			{
				if(tossIndex == 0)
					actionItemSel();
				
				else
					actionItemToss();
				
				return;
			}
			
			if(mainIndex == bag.size() - 1)
			{
				destroy(battle);
			}
			
			else
			{
				selected = true;
				tossIndex = 0;
				tossQuant = 1;
			}
		}
		
		else if(Input.B_TAPPED)
		{
			if(sellIndiConf)
			{
				sellIndiConf = false;
				sellIndi = false;
				sellQuant = 1;
				confIndex = 0;
				confirmation = false;
				textbox = null;
				return;
			}
			
			if(sellIndi)
			{
				sellIndi = false;
				return;
			}
			
			if(!selected)
				destroy(battle);
			
			else
			{
				selected = false;
				if(selNum)
					selNum = false;
			}
		}
	}
	
	private void tossItem()
	{
		bag.tossItem(bag.getItem(mainIndex), tossQuant);
	}
	
	private void actionItemSel()
	{
		if(buying || selling)
		{	
			itemToUse = ResourceLoader.getItem(bag.getItem(mainIndex));
			if(itemToUse.isUnique())
				textbox = Util.generateTextbox("I can't put a#price on that!",1);
			else
				sellIndi = true;
			return;
		}
	
		
		if(ResourceLoader.getItem(bag.getItem(mainIndex)).isUsedInBattle() && battle)
		{
			itemToUse = ResourceLoader.getItem(bag.getItem(mainIndex));
			destroy(battle);
		}
		
		else if(ResourceLoader.getItem(bag.getItem(mainIndex)).isUsedInOvworld() && !battle)
		{	
			itemToUse = ResourceLoader.getItem(bag.getItem(mainIndex));
			pokePanel = new PokemonPanel(player.getPokemon(),false,false,itemToUse,player);
		}
		else
			textbox = Util.generateTextbox(String.format(
					"OAK: %s!#This isn't the#time to use that!", GameInfo.PLAYERNAME), 1);
	}
	
	private void actionItemToss()
	{
		boolean unique = ResourceLoader.getItem(bag.getItem(mainIndex)).isUnique();
		
		if(unique)
			textbox = Util.generateTextbox(
					"That's too impor-#tant to toss!", 1);
		else
			selNum = true;
	}
	
	private void destroy(boolean battle)
	{
		alive = false;
		
		if(battle && !buying && !selling)
		{
			Battle.bagIndex = mainIndex;
			Battle.bagBounds = shownBoundaries;
			Battle.bagDisIndex = displIndex;
		}
		
		else if(!battle && !buying && !selling)
		{
			GSOverworld.bagIndex = mainIndex;
			GSOverworld.bagBounds = shownBoundaries;
			GSOverworld.bagDisIndex = displIndex;
		}
		
	}
	
	private void getIndices(boolean battle)
	{
		if(battle && !buying && !selling)
		{
			mainIndex  = Battle.bagIndex;
			shownBoundaries = Battle.bagBounds;
			displIndex = Battle.bagDisIndex;
		}
		else if(!battle && !buying && !selling)
		{
			mainIndex = GSOverworld.bagIndex;
			shownBoundaries = GSOverworld.bagBounds;
			displIndex = GSOverworld.bagDisIndex;
		}
		
		else
		{
			mainIndex = 0;
			displIndex = 0;
			shownBoundaries = new int[]{0,3};
		}
	}
	
	public void render(Graphics2D g)
	{
		g.drawImage(bagImg, mainPos[0], mainPos[1], null);
		drawItems(g);
		drawCursors(g);
		
		if(selected)
			drawSelected(g);
		
		if(selNum)
			drawSelNum(g);
		
		if(sellIndi || sellIndiConf)		
			drawSellIndicator(g);
		
		if(confirmation || sellIndiConf)
			drawConfirm(g);
		
		if(textbox != null)
			textbox.render(g);
		
		if(pokePanel != null)
			pokePanel.render(g);
	}
	
	private void drawItems(Graphics2D g)
	{
		g.setFont(FONT);
		g.setColor(COLOR);


		int nDraw = 0;
		
		for(int i = 0; i < bag.size(); i ++)
		{
		
			if( i < shownBoundaries[0] || i > shownBoundaries[1])
			{
				continue;
			}
			
			g.drawString(bag.getItem(i).replace("POKE", "POK" + "\u00e9"), itemPos0[0], itemPos0[1] + nDraw * 60);
			
			if(!buying)
			{
				if(bag.getQuant(i) > 0 && !ResourceLoader.getItem(bag.getItem(i)).isUnique())
				{
					g.drawImage(quantity,quantX, itemPos0[1] + 10 + nDraw * 60, null);
					g.drawString(String.valueOf(bag.getQuant(i)), quantX + 50, itemPos0[1] + 30 + nDraw * 60);
				}
			}
			
			else
			{
				if(!bag.getItem(i).equals("CANCEL"))
				{
					g.drawImage(poundSymbol, quantX, itemPos0[1] + 2 + nDraw * 60, null);
					g.drawString(String.valueOf(ResourceLoader.getItem(bag.getItem(i)).getBuyingPrice()), 
							quantX + 36, itemPos0[1] + 30 + nDraw * 60);
				}
			}
			
			nDraw ++;
		}
	}
	
	private void drawCursors(Graphics2D g)
	{
		if(!selected)
			g.drawImage(cursorImg, cursorPos0[0], cursorPos0[1] + 8 + displIndex * 60, null);
		else
			g.drawImage(selCursor, cursorPos0[0], cursorPos0[1] + 8 + displIndex * 60, null);
	}
	
	private void drawSelected(Graphics2D g)
	{
		g.drawImage(useToss, useTossPos[0], useTossPos[1], null);
		g.drawString("USE", tossStringPos[0], tossStringPos[1]);
		g.drawString("TOSS", tossStringPos[0], tossStringPos[1] + 60);
		
		if(selNum)
			g.drawImage(selCursor, tossIndexPos[0], tossIndexPos[1] + tossIndex * 60, null);
		else
			g.drawImage(cursorImg, tossIndexPos[0], tossIndexPos[1] + tossIndex * 60, null);
	}
	
	private void drawSelNum(Graphics2D g)
	{
		g.drawImage(selNumImg, selNumPos[0], selNumPos[1], null);
		g.drawImage(quantity, selNumQuantImgPos[0] - 3, selNumQuantImgPos[1] + 1, null);
		
		String quant = "";
		if(tossQuant > 9)
			quant = String.valueOf(tossQuant);
		else
			quant = "0" + String.valueOf(tossQuant);
		
		g.drawString(quant, selNumQuantPos[0], selNumQuantPos[1]);
		
	}
	
	private void drawConfirm(Graphics2D g)
	{
		
		g.drawImage(confImg, confPos[0], confPos[1], null);
		g.drawString("YES", confOptionsPos0[0], confOptionsPos0[1]);
		g.drawString("NO", confOptionsPos0[0], confOptionsPos0[1] + 60);
		
		if(confIndex == 0)
			g.drawImage(cursorImg, confIndexPos[0],confIndexPos[1], null);
		else
			g.drawImage(cursorImg, confIndexPos[0],confIndexPos[1] + 60, null);
	}
	
	private void drawSellIndicator(Graphics2D g)
	{
		g.drawImage(sellImg, sellIndiPos[0], sellIndiPos[1], null);
		g.drawImage(selCursor, cursorPos0[0], cursorPos0[1] + 8 + displIndex * 60, null);
		g.setFont(FONT);
		g.setColor(COLOR);
		g.drawImage(quantity, sellIndiPos[0] + 39, sellIndiPos[1] + 39, null);
		
		if(sellQuant < 10)
			g.drawString("0" + String.valueOf(sellQuant),sellIndiPos[0] + 70, sellIndiPos[1] + 60);
		else
			g.drawString(String.valueOf(sellQuant), sellIndiPos[0] + 70, sellIndiPos[1] + 60);
		
		String sellAmount = new String();
		
		if(buying)
			sellAmount = String.valueOf(itemToUse.getBuyingPrice() * sellQuant);
		else if(selling)
			sellAmount = String.valueOf(itemToUse.getSellingPrice() * sellQuant);
		
		g.drawString(sellAmount, sellIndiPos[0] + 360 - sellAmount.length() * FONT.getSize(), sellIndiPos[1] + 60);
		g.drawImage(poundSymbol, sellIndiPos[0] + 360 - sellAmount.length() * FONT.getSize() - poundSymbol.getWidth() - 4, 
								 sellIndiPos[1] + 33, null);
	}
	
	public boolean getAlive()
	{
		return alive;
	}
	
	public Item getItem()
	{
		return itemToUse;
	}
}
