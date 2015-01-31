package gameStates;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Main;
import misc.PokemonHud;
import util.ResourceLoader;
import util.Util;
import battleStates.BSIntro;
import battleStates.BSMain;
import battleStates.BSOptions;
import battleStates.BattleState;
import entities.Pokemon;
import entities.SNpc;
import entities.SPlayer;
import entities.Textbox;

public class Battle {
	public static final BufferedImage staticTb = ResourceLoader.getTexture("textbox");
	public static final int TB_WIDTH = staticTb.getWidth();
	public static final int TB_HEIGHT = staticTb.getHeight();

	private static PokemonHud playerHud;
	private static PokemonHud enemyHud;
	
	public static final int IMAGE_SIZE = 56 * 4;
	public static int xoffset = 0;
	public static int yoffset = 0;
	
	public static boolean finished;
	public static Textbox textbox;
	public static Textbox placeHolderTextbox;
	
	public static int prevAttackIndex = 0;
	
	private SPlayer player;
	private SNpc trainer;
	
	public static Pokemon playerActive;
	public static Pokemon enemyActive;
	
	public static int bagIndex = 0;
	public static int[] bagBounds = new int[]{0,3}; 
	public static int bagDisIndex = 0;
	
	private int activeState;
	private ArrayList<BattleState> bStates;
	
	private boolean wildBattle;
	
	private BSMain mainState;
	
	public static void setTextbox(String message,int flag)
	{
		textbox = Util.generateTextbox(message,flag);
	}
	
	public static void setPlaceHolderTextbox(String message, int flag)
	{
		placeHolderTextbox = Util.generateTextbox(message,flag);
	}
	
	public static void destroyTextbox()
	{
		textbox = null;
	}
	
	public static void destroyPlaceHolder()
	{
		placeHolderTextbox = null;
	}
	
	public static void endBattle()
	{
		finished = true;
		prevAttackIndex = 0;
	}
	
	public static void setTextbox(Textbox other)
	{
		textbox = other;
	}
	
	public Battle(SPlayer player, SNpc trainer, boolean wildBattle)
	{
		this.player = player;
		this.trainer = trainer;
		this.wildBattle = wildBattle;
		
		playerActive = player.getFirstPkmn();
		enemyActive = trainer.getFirstAvail();
		initializeStates();
		
		activeState = 0;
		
		playerHud = new PokemonHud(playerActive, true);
		enemyHud = new PokemonHud(enemyActive, false);
		
		finished = false;
	
	}
	
	private void initializeStates()
	{
		bStates = new ArrayList<BattleState>();
		bStates.add(new BSIntro(player,trainer,wildBattle));
		bStates.add(new BSOptions(player,trainer,wildBattle));
		mainState = new BSMain(player,trainer,wildBattle);
		bStates.add(mainState);
	}
	
	public void update()
	{
		bStates.get(activeState).update();
		if(bStates.get(activeState).getFinished())
		{
			bStates.get(activeState).setFinished(false);
			activeState ++;
			if(activeState > bStates.size() - 1)
			{
				initializeStates();
				activeState = 1;
			}
		}
		
	}
	
	public void render(Graphics2D g)
	{
		if(finished)
		{
			g.setColor(PokemonHud.WHITE);
			g.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);
		}
		int xOffset = 0;
		int yOffset = 0;
		
		if(BSMain.shake != null)
		{
			xOffset = BSMain.shake.getX();
			yOffset = BSMain.shake.getY();
		}
		
		g.setColor(PokemonHud.WHITE);
		g.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);
		
		g.drawImage(staticTb, xOffset,yOffset + Main.HEIGHT - staticTb.getHeight(), null);
		
		bStates.get(activeState).render(g);
		
		if(textbox != null)
			textbox.render(g);
		
		if(activeState == 2 && mainState.getPhase() == 45)
			mainState.drawOnTop(g);
			
	}
	
	public static PokemonHud getHud(boolean player)
	{
		if(player)
			return playerHud;
		else
			return enemyHud;
	}
	
	public boolean getWild()
	{
		return wildBattle;
	}
	
	public SNpc getTrainer()
	{
		return trainer;
	}
	
	/*
	public static void updateActives(SPlayer player, SNpc trainer)
	{
		playerHud = new PokemonHud(player.getFirstPkmn(),true);
		enemyHud = new PokemonHud(trainer.getFirstAvail(),false);
		playerActive = player.getFirstPkmn();
		enemyActive = trainer.getFirstAvail();
	}
	*/
	
	public static void updateHuds()
	{
		playerHud = new PokemonHud(playerActive,true);
		enemyHud = new PokemonHud(enemyActive,false);
	}
}
