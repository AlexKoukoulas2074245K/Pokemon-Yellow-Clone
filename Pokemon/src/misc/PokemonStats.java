package misc;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import util.GameInfo;
import util.Graphics;
import util.Input;
import util.ResourceLoader;
import entities.Pokemon;
import gameStates.Battle;

public class PokemonStats {
	
	private final Font FONT = PokemonHud.FONT;
	private final Font BFONT = PokemonHud.BFONT;
	
	private final Color COLOR = PokemonHud.BLACK;
	
	private final BufferedImage screen = ResourceLoader.getTexture("pokemonDisplay");
	private final BufferedImage screen1 = ResourceLoader.getTexture("pokemonDisplay1");
	private final BufferedImage screen2 = ResourceLoader.getTexture("pokemonDisplay2");
	
	private final int[] dexNPos = new int[]{100,253};
	private final int[] namePos = new int[]{297, 29 + FONT.getSize()};
	private final int[] levelPos = new int[]{482,92};
	private final int[] hpPos = new int[]{468,160};
	private final int[] maxHpPos = new int[]{580,160};
	private final int[] barPos = new int[]{416,109};
	private final int[] statusPos = new int[]{514,220};
	private final int[] type1Pos = new int[]{352,348};
	private final int[] type2Pos = new int[]{352,412};
	
	private final int[] attackPos = new int[]{279,346};
	private final int[] defensePos = new int[]{279, 410};
	private final int[] speedPos = new int[]{279, 474};
	private final int[] specialPos = new int[]{279, 538};
	private final int[] idPos = new int[]{385,476};
	private final int[] trainPos = new int[]{385, 540};
	
	private final int[] totalXpPos = new int[]{616,152};
	private final int[] xpNextPos = new int[]{452,217};
	private final int[] nextLevelPos = new int[]{556,216};
	
	private final int moveNameX0 = 67;
	private final int moveNameY0 = 316;
	private final int movePP = 380;
	
	private String hp;
	private String maxHp;
	private String attack;
	private String defense;
	private String speed;
	private String special;
	private String totalXp;
	private String xpNext;
	private String nextLevel;
	
	private int phase;
	
	private Pokemon pokemon;
	
	private boolean alive;
	
	public PokemonStats(Pokemon pokemon)
	{
		phase = 0;
		this.pokemon = pokemon;
		alive = true;
		
		hp = String.valueOf((int)pokemon.getStat(Pokemon.HP));
		maxHp = String.valueOf((int)pokemon.getMaxHp());
		
		attack = String.valueOf((int)pokemon.getStat(Pokemon.ATTACK));
		defense = String.valueOf((int)pokemon.getStat(Pokemon.DEFENSE));
		speed = String.valueOf((int)pokemon.getStat(Pokemon.SPEED));
		special = String.valueOf((int)pokemon.getStat(Pokemon.SPECIAL));
		
		totalXp = String.valueOf(pokemon.getTotalXp());
		xpNext = String.valueOf(pokemon.getXpNext());
		nextLevel = String.valueOf(pokemon.getLevel() + 1);
	}
	
	public void update()
	{
		if(Input.A_TAPPED || Input.B_TAPPED)
		{
			if(phase == 0)
				phase = 1;
			else
				alive = false;
		}	
	}
	public void render(Graphics2D g)
	{
		drawScreen(g);
		g.drawImage(Graphics.getHFlippedImage(pokemon.getBattleImages()[0]), 0, 0, Battle.IMAGE_SIZE, Battle.IMAGE_SIZE, null);
		
		g.setFont(FONT);
		g.setColor(COLOR);
		g.drawString(pokemon.getDexN(), dexNPos[0], dexNPos[1]);
		g.drawString(pokemon.getName(), namePos[0], namePos[1]);
		
		if(phase == 0)
		{
			g.drawString(String.valueOf(pokemon.getLevel()), levelPos[0], levelPos[1]);
			g.drawString(hp, hpPos[0] - hp.length() * FONT.getSize(), hpPos[1]);
			g.drawString(maxHp, maxHpPos[0] - hp.length() * FONT.getSize(), maxHpPos[1]);
			g.drawString(pokemon.getStatusName(), statusPos[0], statusPos[1]);
			g.drawString(pokemon.getTypes()[0], type1Pos[0], type1Pos[1]);
			if(pokemon.getTypes().length == 2)
				g.drawString(pokemon.getTypes()[1], type2Pos[0], type2Pos[1]);
			
			g.drawString(attack, attackPos[0] - attack.length() * FONT.getSize(), attackPos[1]);
			g.drawString(defense, defensePos[0] - defense.length() * FONT.getSize(), defensePos[1]);
			g.drawString(speed, speedPos[0] - speed.length() * FONT.getSize(), speedPos[1]);
			g.drawString(special, specialPos[0] - special.length() * FONT.getSize(), specialPos[1]);
			g.drawString(pokemon.getID(), idPos[0], idPos[1]);
			g.drawString(GameInfo.PLAYERNAME, trainPos[0], trainPos[1]);
			
			drawBars(g);
		}
		
		else
		{
			g.drawString(totalXp, totalXpPos[0] - totalXp.length() * FONT.getSize(), totalXpPos[1]);
			g.drawString(xpNext, xpNextPos[0] - xpNext.length() * FONT.getSize(), xpNextPos[1]);
			g.drawString(nextLevel, nextLevelPos[0], nextLevelPos[1]);
			
			for(int i = 0; i < 4; i ++)
				drawMove(g,pokemon.getActiveAttacks()[i], i);
			
		}
	}
	
	private void drawMove(Graphics2D g, Attack attack, int i)
	{
		
		g.setFont(FONT);
		g.setColor(COLOR);
		
		if(attack == null)
		{
			g.fillRect(moveNameX0, moveNameY0 + i * (FONT.getSize() * 2), 28, 4);
			g.fillRect(movePP, moveNameY0 + 30 + i * ( FONT.getSize() * 2), 28, 4);
			g.fillRect(movePP + 30, moveNameY0 + 30 + i * (FONT.getSize() * 2), 28, 4);
		}
		else
		{
			g.drawString(attack.getName(), moveNameX0, moveNameY0 + i * 60);
			g.setFont(BFONT);
			g.drawString("pp", movePP, moveNameY0 + 30 + i * 60);
			g.setFont(FONT);
			g.drawString(String.format("%d/%d", attack.getPool(), attack.getMaxPool()), movePP + 90, moveNameY0 + 30 + i * 60);
		}
		
	}
	
	private void drawScreen(Graphics2D g)
	{
		if(phase == 0 && pokemon.getTypes().length == 1)
		g.drawImage(screen, 0, 0, null);
	
	else if(phase == 0 && pokemon.getTypes().length == 2)
		g.drawImage(screen1, 0, 0, null);
	
	else
		g.drawImage(screen2, 0, 0, null);
	}
	
	private void drawBars(Graphics2D g)
	{
		
		Pokemon curr = pokemon;
		if(curr.getStat(Pokemon.HP) <= 0) return;
		
		Color color = PokemonHud.GREEN;
		
		if(curr.getStat(Pokemon.HP) < curr.getMaxHp()/2)
			color = PokemonHud.ORANGE;
		if(curr.getStat(Pokemon.HP) < curr.getMaxHp()/4)
			color = PokemonHud.RED;
		
		g.setColor(color);
		
		int barWidth = (int)(PokemonHud.BAR_WIDTH * curr.getStat(Pokemon.HP)/curr.getMaxHp());
		
		g.fillRect(barPos[0], barPos[1], barWidth, PokemonHud.BAR_HEIGHT);
	}
	
	public boolean getAlive()
	{
		return alive;
	}
}
