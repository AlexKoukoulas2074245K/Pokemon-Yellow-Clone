package entities;

import events.PewterScriptedEvent;
import events.PewterScriptedMuseumEvent;
import events.PokeCenterEvent;
import events.PokeMarketEvent;
import gameStates.Battle;
import gameStates.GSOverworld;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Game;
import main.Main;
import misc.BagMenu;
import misc.Letter;
import misc.PokemonHud;
import misc.PokemonPanel;
import misc.SaveMenu;
import util.Counter;
import util.GameInfo;
import util.Input;
import util.ResourceLoader;
import util.Util;
import battleStates.BSMain;

public class Textbox {
	
	private static final int FONT_SIZE = 28;
	public static final Font FONT = new Font("Pokemon GB",Font.PLAIN,FONT_SIZE);
	
	private static final int TEXT_SPEED = 4;

	private static final BufferedImage TEXTBOX_IMAGE = ResourceLoader.getTexture("textbox");
	private static final BufferedImage TEXTBOX_CURSOR = ResourceLoader.getTexture("verCursor");
	private static final BufferedImage POUND_SYMBOL = ResourceLoader.getTexture("poundSymbol");
	
	private static final int X = 0;
	private static final int Y = Main.HEIGHT - TEXTBOX_IMAGE.getHeight();
	
	private static final int CURSOR_X = Main.WIDTH - 64;
	private static final int CURSOR_Y = Main.HEIGHT - 56;
	
	private static final int X_OFFSET = X + 32;
	private static final int Y1_OFFSET = Y + 86;
	private static final int Y2_OFFSET = Y + 146;
	
	private static final int CURSOR_COUNTER = 96;
	
	private int cursorX;
	private int cursorY;
	
	private String[] sentences;
	private String reformedMessage;
	
	private ArrayList<Letter> rendLetters;
	
	private int textSpeedCounter;
	private int messageIndex;
	private int phraseIndex;
	private int cursorCounter;
	
	private int flag;
	
	
	private Counter counter;
	private int counterIndex;
	private int totalIndex;
	private boolean showCursor;
	private boolean firstPhrase;
	private boolean lastPhrase;
	
	private boolean alive;
	
	private boolean persists;
	private boolean battle;
	
	public Textbox(String message,int flag)
	{
		
		message = replaceConstants(message);
		findCounterIndex(message);
		
		reformedMessage = message;
		
		flagOptions(flag);
		
		this.sentences = message.split("@");	
		this.flag = flag;
		
		cursorX = X_OFFSET;
		cursorY = Y1_OFFSET;
		
		messageIndex = 0;
		phraseIndex = 0;
		totalIndex = 0;
		
		firstPhrase = true;
		lastPhrase = false;
		showCursor = false;
		textSpeedCounter = TEXT_SPEED;
		cursorCounter = CURSOR_COUNTER;
		
		rendLetters = new ArrayList<Letter>();
		
		alive = true;
	}
	
	private void flagOptions(int flag)
	{
		if(flag == 0)
		{
			battle = false;
			persists = false;
		}
		
		else if(flag == 1)
		{
			battle = true;
			persists = false;
		}
		
		else if(flag == 2)
		{
			battle = false;
			persists = true;
		}
	}
	
	public void update()
	{
		
		if(counter != null)
		{
			counter.update();
			if(!counter.getAlive())
				counter = null;
			
			return;
		}
		
		if(lastPhrase)
		{
			if(persists) 
			{
				alive = false;
				return;
			}
			if(battle)
			{
				
				showCursor = true;
				cursorCounter --;
				if(cursorCounter == 0)
					cursorCounter = CURSOR_COUNTER;
			}
			if(Input.A_TAPPED || Input.B_TAPPED)
			{	
				if(!battle)
				{
					GSOverworld.destroyTextbox();
					SaveMenu.destroyTextbox();
					PokemonPanel.destroyTextbox();
					PokeCenterEvent.destroyTextbox();
					PokeMarketEvent.destroyTextbox();
					PewterScriptedEvent.destroyTextbox();
					PewterScriptedMuseumEvent.destroyTextbox();
				}
				else
				{
					PokemonPanel.destroyTextbox();
					Battle.destroyTextbox();
					BagMenu.destroyTextbox();
					PokeCenterEvent.destroyTextbox();
					if(!reformedMessage.equals("Here you are!#Thank you!"))
						PokeMarketEvent.destroyTextbox();
					alive = false;
				}
			}
			return;
		}
	
		if(showCursor && !Input.A_TAPPED && !Input.B_TAPPED)
		{
			cursorCounter--;
			if(cursorCounter == 0)
				cursorCounter = CURSOR_COUNTER;
			return;
		}
		
		if(showCursor && (Input.A_TAPPED || Input.B_TAPPED))
		{
			showCursor = false;
			cursorCounter = CURSOR_COUNTER;
			moveLetters();
			return;
		}
		
		if(Input.A_KEY || Input.B_KEY) 
			textSpeedCounter -= 2;
		else
			textSpeedCounter --;
		
		if(textSpeedCounter > 0) return;
		
		textSpeedCounter = TEXT_SPEED;
		
		if(phraseIndex == 0)
			rendLetters = new ArrayList<Letter>();
		
		
		if(sentences[messageIndex].charAt(phraseIndex) == '#')
		{
			
			if((!firstPhrase && phraseIndex < sentences[messageIndex].length())||
				(firstPhrase && sentences[messageIndex].length() == phraseIndex + 1))
				showCursor = true;
			
			firstPhrase = false;
			incrementIndices();
			cursorX = X_OFFSET;
		}
		else
		{				
			if(firstPhrase)
				cursorY = Y1_OFFSET;
			else
				cursorY = Y2_OFFSET;
			
			String letter = String.valueOf(sentences[messageIndex].charAt(phraseIndex));
			rendLetters.add(new Letter(letter,cursorX,cursorY));
			
			incrementIndices();
			
			cursorX += FONT_SIZE;
		}
	}
	
	public String replaceConstants(String message)
	{
		message = message.replaceAll("PLAYERNAME", GameInfo.PLAYERNAME);
		message = message.replaceAll("RIVALNAME",GameInfo.RIVALNAME);
		message = message.replaceAll("POKE", "POK" + "\u00e9");
		return message;
	}
	
	private void findCounterIndex(String message)
	{
		if(Util.getItemFromMessage(message) == null) 
		{
			counterIndex = 0;
			return;
		}
		
		String itemName = Util.getItemFromMessage(message).getName();
	
		String indicator = GameInfo.PLAYERNAME + " got#" + itemName + "!";
		if(!message.contains(indicator))
			indicator = GameInfo.PLAYERNAME + " found#" + itemName + "!";
	
		counterIndex = message.indexOf(indicator) + indicator.length() - Util.noOccurrences('@',message,message.indexOf(indicator));
		
		if(itemName.equals("POKE BALL"))
			counterIndex ++;
	}
	
	private void moveLetters()
	{
		for(int i = 0; i < rendLetters.size(); i ++)
			if(rendLetters.get(i).getY() == Y1_OFFSET)
			{
				rendLetters.remove(i);
				i --;
			}
			else
			{
				if(firstPhrase)
				{
					rendLetters.remove(i);
					i--;
				}
				else
					rendLetters.get(i).setY(Y1_OFFSET);
			}
	}
	
	private void incrementIndices()
	{
		phraseIndex ++;
		totalIndex ++;
		
		if(totalIndex == counterIndex)	
			counter = new Counter(Game.STDTSIZE * 3/2);
		
		
		if(phraseIndex > sentences[messageIndex].length() - 1)
		{
			phraseIndex = 0;
			firstPhrase = true;
			messageIndex ++;
		
			if(messageIndex > sentences.length - 1)
			{
				lastPhrase = true;
			}
		
		}
	}
	public void render(Graphics2D g)
	{
		int xOffset = 0;
		int yOffset = 0;
		
		if(BSMain.shake != null)
		{
			xOffset = BSMain.shake.getX();
			yOffset = BSMain.shake.getY();
		}
		
		g.drawImage(TEXTBOX_IMAGE,xOffset + X, yOffset + Y,null);
		
		g.setFont(FONT);
		g.setColor(PokemonHud.BLACK);
		
		for(Letter letter: rendLetters)
		{
			if(letter.getLetter() != "$")
				g.drawString(letter.getLetter(), xOffset + letter.getX(),yOffset + letter.getY());
			else
				g.drawImage(POUND_SYMBOL, xOffset + letter.getX(), yOffset + letter.getY(), null);
		}
		
		if(showCursor && cursorCounter > CURSOR_COUNTER/2)		
			g.drawImage(TEXTBOX_CURSOR, xOffset + CURSOR_X, yOffset + CURSOR_Y,null);	
	}
	
	public String getMessage()
	{
		return reformedMessage;
	}
	
	public boolean getAlive()
	{
		return alive;
	}
	
	public int getFlag()
	{
		return flag;
	}
}
