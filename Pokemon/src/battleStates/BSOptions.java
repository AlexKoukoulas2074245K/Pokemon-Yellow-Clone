package battleStates;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Main;
import misc.BagMenu;
import misc.Item;
import misc.PokemonPanel;
import util.AI;
import util.BattleCalc;
import util.Graphics;
import util.Input;
import util.ResourceLoader;
import util.Util;
import entities.Pokemon;
import entities.SNpc;
import entities.SPlayer;
import gameStates.Battle;

public class BSOptions extends BattleState{
	
	public static final int FONT_SIZE = 28;
	public static Font FONT = new Font("Pokemon GB",Font.PLAIN,FONT_SIZE);
	
	private final BufferedImage options = ResourceLoader.getTexture("battle_options");
	private final BufferedImage moveExp = ResourceLoader.getTexture("battle_move_selection");
	public static final BufferedImage cursor  = ResourceLoader.getTexture("horCursor");
	
	private final int OPTIONS_X = Main.WIDTH - options.getWidth();
	private final int OPTIONS_Y = Main.HEIGHT - options.getHeight();
	
	private final int MAIN = -1;
	private final int FIGHT = 0;
	private final int PKMN = 1;
	private final int ITEM = 2;
	private final int RUN = 3;
	
	private int state;
	private int cursorIndex;
	private int moveSelIndex;
	private int moveSelMax;
	
	private boolean runResult;
	
	private Pokemon plrActivePkmn;
	private Pokemon	trnActivePkmn;
	
	private int[] plrPkmnPos;
	private int[] trnPkmnPos;
	
	private ArrayList<int[]> mainCursorPos;
	
	private PokemonPanel pokePanel;
	private BagMenu bagMenu;
	public static Item itemToUse;
		
	public BSOptions(SPlayer player,SNpc trainer, boolean wildBattle)
	{
		super(player,trainer,wildBattle);
		
		plrPkmnPos = BSIntro.FINAL_PLR_POS;
		trnPkmnPos = BSIntro.FINAL_TRN_POS;
		
		plrActivePkmn = Battle.playerActive;
		trnActivePkmn = Battle.enemyActive;
		
		state = MAIN;
		cursorIndex = 0;
		moveSelIndex = Battle.prevAttackIndex;
		
		moveSelMax = Util.getMaxIndex(plrActivePkmn.getActiveAttacks());
		
		mainCursorPos = new ArrayList<int[]>();
		mainCursorPos.add(new int[]{OPTIONS_X + 23,OPTIONS_Y + 48});
		mainCursorPos.add(new int[]{OPTIONS_X + 216,OPTIONS_Y + 48});
		mainCursorPos.add(new int[]{OPTIONS_X + 23,OPTIONS_Y + 111});
		mainCursorPos.add(new int[]{OPTIONS_X + 216, OPTIONS_Y + 111});
		
		runResult = false;
	}
	
	public void update()
	{
		if(pokePanel != null)
		{
			pokePanel.update();
			if(!pokePanel.getAlive())
			{
				pokePanel = null;
				itemToUse = null;
			}
			return;
		}
		
		if(Battle.textbox == null)
			input();
		
		updateStates();
	}
	
	public void input()
	{
		if(state == MAIN)
		{
			if(Input.LEFT_TAPPED && (cursorIndex == 1 || cursorIndex == 3))
				cursorIndex --;
			else if(Input.RIGHT_TAPPED && (cursorIndex == 0 || cursorIndex == 2))
				cursorIndex ++;
			else if(Input.DOWN_TAPPED && (cursorIndex == 0 || cursorIndex == 1))
				cursorIndex += 2;
			else if(Input.UP_TAPPED && (cursorIndex == 2 || cursorIndex == 3))
				cursorIndex -= 2;
			
			if(Input.A_TAPPED)
			{
				state = cursorIndex;
	
				if(state == RUN)
				{
					
					if(!wildBattle)
						Battle.textbox = Util.generateTextbox("No! There's no#running from a#trainer battle!", 1);
					else
					{
						runResult = BattleCalc.getRunResult(player,plrActivePkmn,trnActivePkmn);
						if(runResult)
							Battle.textbox = Util.generateTextbox("Got away safely!",1);
						else
						{
							Battle.textbox = Util.generateTextbox("Can't escape!",1);
						}
					}
				}
				else if(state == PKMN)
					pokePanel = new PokemonPanel(player.getPokemon(),true,false);
				
				else if(state == ITEM)
				{
					bagMenu = new BagMenu(player, true);
					Input.A_TAPPED = false;
					return;
				}
				
			}
			//TODO: delete
			if(Input.B_TAPPED)
				plrActivePkmn.reduceStatBy(Pokemon.HP,1);
		}
		
		else if(state == FIGHT)
		{
			if(Input.A_TAPPED)
				
			{
				if(plrActivePkmn.getActiveAttacks()[moveSelIndex].getPool() == 0)
					Battle.setTextbox("No PP left for#this move!", 1);
	
				else
				{
					plrActivePkmn.setNextAttack(plrActivePkmn.getActiveAttacks()[moveSelIndex]);
					plrActivePkmn.getNextAttack().decrementPool();
					trnActivePkmn.setNextAttack(AI.getAttack(trnActivePkmn));
					Battle.prevAttackIndex = moveSelIndex;
					finished = true;
				}
				return;
			}
			
			else if(Input.B_TAPPED)
				state = MAIN;
			else if(Input.UP_TAPPED)
				moveSelIndex --;
			else if(Input.DOWN_TAPPED)
				moveSelIndex ++;
			
			if(moveSelIndex < 0) moveSelIndex = moveSelMax;
			else if(moveSelIndex > moveSelMax) moveSelIndex = 0;
		}
	}
	
	private void updateStates()
	{
		if(state == RUN)
		{
				
			Battle.textbox.update();
			
			if(Battle.textbox == null)
			{
				if(!wildBattle)
					state = MAIN;
				else
				{
					if(runResult)
					{
						Battle.endBattle();
						return;
					}
					else
					{
						plrActivePkmn.setNextAttack(null);
						finished = true;
						trnActivePkmn.setNextAttack(AI.getAttack(trnActivePkmn));
					}
				}	
			}
		}
		
		else if(state == FIGHT)
		{
			if(Battle.textbox != null)
			{
				Battle.textbox.update();
				if(Battle.textbox == null)
					state = MAIN;
				return;
			}
		}
		else if(state == PKMN)
		{
			
			Pokemon pkmn0 = Battle.playerActive;
			if(pokePanel != null)
				pokePanel.update();

			{
				if(Battle.playerActive != pkmn0)
				{
					finished = true;
					Util.cancelAttacks(player.getPokemon());
					trnActivePkmn.setNextAttack(AI.getAttack(trnActivePkmn));
					pokePanel = null;
					itemToUse = null;
					return;
				}
				state = MAIN;
			}
		}
		
		else if(state == ITEM)
		{
			bagMenu.update();
			if(!bagMenu.getAlive())
			{
				if(bagMenu.getItem() != null)
				{
					itemToUse = bagMenu.getItem();
					itemUse();
				}
				
				bagMenu = null;
				state = MAIN;
			}
		}
		
		if(state == MAIN)
			Battle.destroyTextbox();
	}
	
	private void itemUse()
	{
		if(itemToUse.isPotion())
			pokePanel = new PokemonPanel(player.getPokemon(),false,false,itemToUse,player);
		
		else
		{
			Util.cancelAttacks(player.getPokemon());
			trnActivePkmn.setNextAttack(AI.getAttack(trnActivePkmn));
			finished = true;
		}
	}
	
	public void render(Graphics2D g)
	{
		Battle.getHud(true).render(g);
		Battle.getHud(false).render(g);
		
		plrActivePkmn.render(g, plrPkmnPos,true);
		trnActivePkmn.render(g, trnPkmnPos,false);
		
		if(state == MAIN)
		{
			g.drawImage(options, OPTIONS_X, OPTIONS_Y, null); 
			g.drawImage(cursor, mainCursorPos.get(cursorIndex)[0], mainCursorPos.get(cursorIndex)[1],null);
		}
		
		else if(state == FIGHT)
		{
			g.drawImage(moveExp, 0 , Main.HEIGHT - moveExp.getHeight() + 1, null);
			Graphics.drawMoveExps(g,plrActivePkmn.getActiveAttacks(),0,Main.HEIGHT - moveExp.getHeight() + 1, moveSelIndex);
		}
		
		else if(state == ITEM)
			bagMenu.render(g);
		
		if(pokePanel != null)
			pokePanel.render(g);
	}
}
