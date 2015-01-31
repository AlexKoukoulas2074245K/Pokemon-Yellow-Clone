package misc;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Main;
import util.ResourceLoader;
import entities.Pokemon;
import entities.Textbox;
import gameStates.Battle;

public class LevelUpPanel {
	
	private static final BufferedImage image = ResourceLoader.getTexture("levelUpPanel");
	
	private final Font FONT   = Textbox.FONT;
	private final Color COLOR = PokemonHud.BLACK;
	
	private String attack;
	private String defense;
	private String speed;
	private String special;
	
	private int drawX;
	private int drawY;
	private int drawLetterX;
	private int drawLetterY;
	private int drawPowerX;
	
	public LevelUpPanel(Pokemon pokemon)
	{
		attack = String.valueOf((int)pokemon.getStat(Pokemon.ATTACK));
		defense = String.valueOf((int)pokemon.getStat(Pokemon.DEFENSE));
		speed = String.valueOf((int)pokemon.getStat(Pokemon.SPEED));
		special = String.valueOf((int)pokemon.getStat(Pokemon.SPECIAL));
		
		drawX = Main.WIDTH - image.getWidth();
		drawY = Main.HEIGHT - Battle.TB_HEIGHT - image.getHeight() - 6;
		drawLetterX = drawX + 82;
		drawPowerX = Main.WIDTH - 2 * FONT.getSize();
		drawLetterY = drawY + 58;
	}
	
	public void render(Graphics2D g)
	{
		g.setFont(FONT);
		g.setColor(COLOR);
		g.drawImage(image, drawX, drawY, null);
		g.drawString("ATTACK",drawLetterX,drawLetterY);
		g.drawString(attack, drawPowerX - attack.length() * FONT.getSize(), drawLetterY + FONT.getSize() + 2);
		g.drawString("DEFENSE", drawLetterX, drawLetterY + 2 * (FONT.getSize() + 2));
		g.drawString(defense, drawPowerX - defense.length() * FONT.getSize(), drawLetterY + 3 * (FONT.getSize() + 2));
		g.drawString("SPEED", drawLetterX, drawLetterY + 4 * (FONT.getSize() + 2));
		g.drawString(speed, drawPowerX - speed.length() * FONT.getSize(), drawLetterY + 5 * (FONT.getSize() + 2));
		g.drawString("SPECIAL", drawLetterX, drawLetterY + 6 * (FONT.getSize() + 2));
		g.drawString(special, drawPowerX - special.length() * FONT.getSize(), drawLetterY + 7*(FONT.getSize() + 2));
	}
}
