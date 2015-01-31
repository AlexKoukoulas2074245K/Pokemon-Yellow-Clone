package main;

import gameStates.GSBattle;
import gameStates.GSOverworld;
import gameStates.GameState;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.ArrayList;

import util.Input;
import util.ResourceLoader;

public class Game {
	
	public static final int STDTSIZE = Main.STDTSIZE;
	
	private int activeState;
	
	private ArrayList<GameState> gameStates;
	
	private GSOverworld overworld;
	private GSBattle battle;
	
	private static ResourceLoader resources;
	
	static
	{
		try 
		{
		     GraphicsEnvironment ge = 
		         GraphicsEnvironment.getLocalGraphicsEnvironment();
		     ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Game.class.getResourceAsStream("/loader/Pokemon GB.ttf")));
		}
		catch (IOException|FontFormatException e) 
		{
			e.printStackTrace();
		}
	}
	
	public Game()
	{		
		
		resources = new ResourceLoader();
		
		gameStates = new ArrayList<GameState>();
		activeState = 0;
		
		overworld = new GSOverworld(this,resources);
		battle = new GSBattle(this,resources);
				
		gameStates.add(overworld);
		gameStates.add(battle);
	}
	
	public void update()
	{
		
		gameStates.get(activeState).update();
		Input.resetTapped();
	}
	
	public void render(Graphics2D g)
	{
		gameStates.get(activeState).render(g);
	}
	
	public void nextState()
	{
		activeState ++;
		
		if(activeState > gameStates.size())
			activeState = 0;
	}
	
}
