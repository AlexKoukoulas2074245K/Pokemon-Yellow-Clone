package misc;

import entities.Pokemon;
import entities.SNpc;
import entities.SPlayer;
import gameStates.Battle;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Game;
import main.Main;
import util.ResourceLoader;
import battleStates.BSMain;

public class PokemonHud {
	
	private static final BufferedImage allTrainerPkmn = ResourceLoader.getTexture("enemy_pokemon");
	private static final BufferedImage 	allPlayerPkmn = ResourceLoader.getTexture("player_pokemon");	
	private static final BufferedImage pkBallBattle = ResourceLoader.getTexture("ballBattle");
	private static final BufferedImage pkBallDead = ResourceLoader.getTexture("ballDead");
	private static final BufferedImage pkBallStatus = ResourceLoader.getTexture("ballStatus");
	private static final BufferedImage playerImg = ResourceLoader.getTexture("player_stats");
	private static final BufferedImage enemyImg = ResourceLoader.getTexture("enemy_stats");
	
	public static final int FONT_SIZE = 28;
	public static Font FONT = new Font("Pokemon GB",Font.PLAIN,FONT_SIZE);
	public static Font BFONT = new Font("Pokemon GB",Font.BOLD,FONT_SIZE);
	
	private static final int PL_DRAW_X = 294;
	private static final int PL_DRAW_Y = Main.HEIGHT - Battle.TB_HEIGHT - playerImg.getHeight() - 20;
	private static final int EN_DRAW_X = Game.STDTSIZE/2;
	private static final int EN_DRAW_Y = Game.STDTSIZE/2 + 8;
	
	public static final int BAR_WIDTH = 192;
	public static final int BAR_HEIGHT = 8;
	
	private static final int PL_BAR_X = PL_DRAW_X + 96;
	private static final int PL_BAR_Y = PL_DRAW_Y + 36;
	private static final int TR_BAR_X = EN_DRAW_X + 84;
	private static final int TR_BAR_Y = EN_DRAW_Y + 36;
	
	private static final int HP_X = PL_DRAW_X + 161;
	private static final int HP_Y = PL_DRAW_Y + 84;
	
	private static final int MAX_HP_X = PL_DRAW_X + 274;
	private static final int MAX_HP_Y = PL_DRAW_Y + 84;
	
	public static final Color WHITE = new Color(248,248,248);
	public static final Color BLACK = new Color(24,24,24);
	public static final Color RED = new Color(255,0,0);
	public static final Color GREEN = new Color(0,255,0);
	public static final Color ORANGE = new Color(255,144,0);
	
	private Color color;
	
	private Pokemon pokemon;
	private int status;
	private boolean isPlayer;
	
	private float hp0;
	
	public PokemonHud(Pokemon pokemon, boolean isPlayer)
	{	
		this.pokemon = pokemon;
		this.isPlayer = isPlayer;
		
		status = pokemon.getStatus();
		color = GREEN;
	}
	
	
	public void render(Graphics2D g)
	{
		render(g,false);
	}
	
	public void render(Graphics2D g, boolean defeat)
	{
		int xOffset = 0;
		int yOffset = 0;
		
		if(BSMain.shake != null)
		{
			xOffset = BSMain.shake.getX();
			yOffset = BSMain.shake.getY();
		}
	
		drawBar(g,xOffset,yOffset,defeat);
		
		g.setFont(FONT);
		g.setColor(BLACK);
		if(isPlayer)
		{
			
			g.drawImage(playerImg, xOffset + PL_DRAW_X,yOffset + PL_DRAW_Y, null);
			g.drawString(pokemon.getName(), xOffset + PL_DRAW_X + 40, yOffset + PL_DRAW_Y - 8);
			
			if(status == Pokemon.OK)
				g.drawString(String.valueOf(pokemon.getLevel()), xOffset + PL_DRAW_X + 194,yOffset + PL_DRAW_Y + 19);
			else
			{
				g.setColor(WHITE);
				g.fillRect(xOffset + PL_DRAW_X + 162, PL_DRAW_Y + yOffset, 125, 30);
				g.setColor(BLACK);
				g.drawString(pokemon.getStatusName(), xOffset + PL_DRAW_X + 200,yOffset + PL_DRAW_Y + 24);
			}
			
			String currHp = String.valueOf((int)(Math.round(pokemon.getStat(Pokemon.HP))));
			String maxHp = String.valueOf((int)(Math.ceil(pokemon.getMaxHp())));
			
			g.drawString(currHp,xOffset + HP_X - (currHp.length() * FONT_SIZE),yOffset + HP_Y);
			g.drawString(maxHp, xOffset + MAX_HP_X - (maxHp.length() * FONT_SIZE), yOffset + MAX_HP_Y);
		}
		else
		{
			g.drawImage(enemyImg, xOffset + EN_DRAW_X, yOffset + EN_DRAW_Y,null);
			g.drawString(pokemon.getName(),xOffset + Game.STDTSIZE/2, yOffset + FONT_SIZE);
			
			if(status == Pokemon.OK)
				g.drawString(String.valueOf(pokemon.getLevel()), xOffset + EN_DRAW_X + 118,yOffset + EN_DRAW_Y + 19);
			else
			{
				g.setColor(WHITE);
				g.fillRect(xOffset + EN_DRAW_X + 88, yOffset + EN_DRAW_Y, 100, 25);
				g.setColor(BLACK);
				g.drawString(pokemon.getStatusName(), xOffset + EN_DRAW_X + 100, yOffset + EN_DRAW_Y + 20);
			}
		}
	}
	
	private void drawBar(Graphics2D g, int xOffset, int yOffset,boolean defeat)
	{
		color = GREEN;
		if(pokemon.getStat(Pokemon.HP) < pokemon.getMaxHp()/2)
			color = ORANGE;
		if(pokemon.getStat(Pokemon.HP) < pokemon.getMaxHp()/4)
			color = RED;
		
		g.setColor(color);
		
		if(defeat)
			g.setColor(BLACK);
		
		int barWidth = (int)(BAR_WIDTH * pokemon.getStat(Pokemon.HP)/pokemon.getMaxHp());
		
		if(!pokemon.getAlive()) return;
		
		if(pokemon.getStat(Pokemon.HP) < 1 && hp0 == pokemon.getStat(Pokemon.HP))
			barWidth = 4;
		
		if(isPlayer)
			g.fillRect(xOffset + PL_BAR_X, yOffset + PL_BAR_Y, barWidth, BAR_HEIGHT);
		else
			g.fillRect(xOffset + TR_BAR_X, yOffset + TR_BAR_Y, barWidth, BAR_HEIGHT);
		
		hp0 = pokemon.getStat(Pokemon.HP);
	}
	
	public static void drawPokemonBalls(int flag, boolean wildBattle, SPlayer player, SNpc trainer,Graphics2D g)
	{
		Pokemon[] playerPkmn = player.getPokemon();
		Pokemon[] trainerPkmn = trainer.getPokemon();
		
		if(flag == 0 || flag  == 2)
		{
			g.drawImage(allPlayerPkmn, 288, 322, null);
		
			for(int i = 0; i < playerPkmn.length; i ++)
			{
				if(playerPkmn[i] == null) continue;
				
				if(playerPkmn[i].getAlive())
				{
					if(playerPkmn[i].getStatus() == Pokemon.OK)
						g.drawImage(pkBallBattle, 356 + (i * (pkBallBattle.getWidth() + 4)), 326, null);
					else
						g.drawImage(pkBallStatus, 356 + (i * (pkBallBattle.getWidth() + 4)), 326, null);
				}
				else
					g.drawImage(pkBallDead, 356 + (i * (pkBallBattle.getWidth() + 4)), 326, null);
			}
		}
		
		if (flag == 1 || flag == 2)
		{
			if(!wildBattle)
			{
				g.drawImage(allTrainerPkmn, Game.STDTSIZE/2, Game.STDTSIZE, null);
				for(int i = 0; i < trainerPkmn.length; i ++)
				{
					if(trainerPkmn[i] == null) continue;
					
					if(trainerPkmn[i].getAlive())
					{
						if(trainerPkmn[i].getStatus() == Pokemon.OK)
							g.drawImage(pkBallBattle, Game.STDTSIZE/2 + 212 - (i *(pkBallBattle.getWidth() + 4)), Game.STDTSIZE + 4, null);
						else
							g.drawImage(pkBallStatus, Game.STDTSIZE/2 + 212 - (i *(pkBallBattle.getWidth() + 4)), Game.STDTSIZE + 4, null);
					}
					else
						g.drawImage(pkBallDead, Game.STDTSIZE/2 + 212 - (i *(pkBallBattle.getWidth() + 4)), Game.STDTSIZE + 4, null);
				}
			}	
		}
	}
}
