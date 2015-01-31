package misc;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.imageio.ImageIO;

import main.Main;

import util.ResourceLoader;
import util.Util;
import entities.Pokemon;

public class Attack {
	
	public static final String[] specialMoves = new String[]{"FIRE","WATER","ELECTRIC","GRASS","ICE","PSYCHIC","DRAGON"};
	public static final String[] singleFor = new String[]{"TACKLE"};
	public static final String[] doubleFor = new String[]{"TAIL WHIP"};
	public static final String[] backForth = new String[]{"QUICK ATTACK","LOW KICK"};
	public static final String[] reverse = new String[]{"GROWL","GUST","SCREECH"};
	public static final String[] highCrit = new String[]{"RAZOR LEAF","SLASH","CRABHAMMER","KARATE CHOP"};
	public static final String[] doubleAttacks = new String[]{"DOUBLE KICK"};
	public static final String[] wrapAttacks = new String[]{"WRAP"};
	public static final String[] multiAttacks = new String[]{"DOUBLESLAP","FURY SWIPES"};
	
	public static HashMap<Character,Integer> statModifiers;
	public static HashMap<Character, String> statModifierNames;
	
	static
	{
		statModifiers = new HashMap<Character, Integer>();
		statModifiers.put('A', Pokemon.ATTACK);
		statModifiers.put('D', Pokemon.DEFENSE);
		statModifiers.put('S', Pokemon.SPEED);
		statModifiers.put('P', Pokemon.SPECIAL);
		statModifiers.put('H', Pokemon.ACCUR);
		
		statModifierNames = new HashMap<Character, String>();
		statModifierNames.put('A', "ATTACK");
		statModifierNames.put('D', "DEFENSE");
		statModifierNames.put('S', "SPEED");
		statModifierNames.put('P', "SPECIAL");
		statModifierNames.put('H', "ACCURACY");
	}
		
	private static final String resPath = ResourceLoader.MOVE_ANI_FOLDER;
	
	private static final int ANI_DELAY = 6;
	
	private ArrayList<BufferedImage> playerImages;
	private ArrayList<BufferedImage> enemyImages;
	
	private String name;
	private String type;
	private float power;
	private float accur;
	private int pool;
	private int maxPool;
	private String effect;
	
	private boolean finishedAni;
	
	private int imageIndex;
	private int aniDelay;
	
	public Attack(String name, String type,
				  float power, float accur,
				  int pool, String effect)
	{
		this.name = name.replace("_", " ");
		this.type = type;
		this.power = power;
		this.accur = accur;
	
		this.pool = pool;
	
		this.maxPool = pool;
		this.effect = effect;
		
		getImages();
		
		aniDelay = ANI_DELAY;
		imageIndex = 0;
		
		finishedAni = playerImages.size() == 0 || enemyImages.size() == 0;
	
	}
	
	private void getImages()
	{
		
		playerImages = new ArrayList<BufferedImage>();
		enemyImages = new ArrayList<BufferedImage>();
		
		try 
		{
			File f = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			
			JarFile jf = new JarFile(f);
			
			Enumeration<JarEntry> entries = jf.entries();
			
			while(entries.hasMoreElements())
			{
				
				JarEntry nextEntry = entries.nextElement();
				
				if(nextEntry.getName().contains(name + "/player") && !nextEntry.isDirectory())
				{
					playerImages.add(ImageIO.read(getClass().getResourceAsStream("/" + nextEntry.getName())));
				}
				
				else if(nextEntry.getName().contains(name + "/enemy") && !nextEntry.isDirectory())
				{
					enemyImages.add(ImageIO.read(getClass().getResourceAsStream("/" + nextEntry.getName())));
				}
			}
			
			jf.close();
		}
		catch(FileNotFoundException e)
		{
			File resPlayer = new File(String.format("%s/%s/player",resPath, name));
			File resEnemy = new File(String.format("%s/%s/enemy",resPath, name));

			if(resPlayer.isDirectory())
			{
				for(File file: resPlayer.listFiles())
					try 
					{
						
						playerImages.add(ImageIO.read(new File(resPlayer + "/" + file.getName())));
					} catch (IOException e2) {
						e2.printStackTrace();
					}
			}
			if(resEnemy.isDirectory())
			{
				for(File file: resEnemy.listFiles())
					try 
					{
						enemyImages.add(ImageIO.read(new File(resEnemy + "/" + file.getName())));
					} catch (IOException e2) {
						e2.printStackTrace();
					}
			}

		}
		catch (URISyntaxException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		if(Util.valueInArray(name, reverse))
			Collections.reverse(enemyImages);
	}
	
	
	public void render(Graphics2D g, boolean player)
	{
		if(playerImages.size() == 0 || enemyImages.size() == 0)
		{
			finishedAni = true;
			return;
		}
			
		animation(player);
		
		if(!finishedAni && player)
			g.drawImage(playerImages.get(imageIndex),0,0,null);
		else if(!finishedAni && !player)
			g.drawImage(enemyImages.get(imageIndex), 0, 40, null);
		
		else
		{
			imageIndex = 0;
			aniDelay = ANI_DELAY;
		}
	}
	
	private void animation(boolean player)
	{	
		aniDelay --;
		if(aniDelay == 0)
		{
			aniDelay = ANI_DELAY;
			imageIndex ++;
			
			if(player && imageIndex > playerImages.size() - 1)
				finishedAni = true;
			
			else if(!player && imageIndex > enemyImages.size() - 1)
				finishedAni = true;
		}
	}
	
	//Getters
	public String getName()
	{
		return name;
	}
	
	public String getType()
	{
		return type;
	}
	
	public float getPower()
	{
		return power;
	}
	
	public float getAccur()
	{
		return accur;
	}
	
	public int getPool()
	{
		return pool;
	}
	
	public int getMaxPool()
	{
		return maxPool;
	}
	
	public int getImageIndex()
	{
		return imageIndex;
	}
	
	public int getMaxIndex()
	{
		return playerImages.size() - 1;
	}
	
	public String getEffect()
	{
		return effect;
	}
	
	public boolean isSpecialMove()
	{
		return Util.valueInArray(type, specialMoves);
	}
	
	public boolean isSingleFor()
	{
		return Util.valueInArray(name, singleFor);
	}
	
	public boolean isDoubleFor()
	{
		return Util.valueInArray(name, doubleFor);
	}
	
	public boolean isBackForth()
	{
		return Util.valueInArray(name, backForth);
	}
	
	public boolean hasShake()
	{
		return !(name.equals("HARDEN") || name.equals("THUNDER WAVE"));
	}
	
	public boolean getFinishedAni()
	{
		return finishedAni;
	}
	
	//Setters
	public void setFinishedAni(boolean finishedAni)
	{
		this.finishedAni = finishedAni;
	}
	
	public void decrementPool()
	{
		pool --;
	}
	
	public void setPool(int pool)
	{
		this.pool = pool;
	}
}
