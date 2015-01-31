package entities;

import gameStates.Battle;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.imageio.ImageIO;

import main.Game;
import main.Main;
import misc.Attack;
import util.ResourceLoader;
import util.Util;
import battleStates.BSMain;

public class Pokemon {
	
	private final int MOVE_OUT_SPEED = 10;
	private final int MOVE_IN_SPEED = 16;
	private final int FALLING_SPEED = 10;
	
	private static final int IV = 8;
	
	public static final int OK = 0;
	public static final int PAR = 1;
	public static final int SLP = 2;
	public static final int BRN = 3;
	public static final int FRZ = 4;
	public static final int PSN = 5;
	
	public static final int SLOW = 0;
	public static final int MEDIUM_SLOW = 1;
	public static final int MEDIUM_FAST = 2;
	public static final int FAST = 1;
	
	public static final int DEX_N = 0;
	public static final int HP = 2;
	public static final int ATTACK = 3;
	public static final int DEFENSE = 4;
	public static final int SPEED = 5;
	public static final int SPECIAL = 6;
	public static final int ACCUR = -1;
	
	private static final int TYPES = 7;
	private static final int EXP_STAT = 8;
	private static final int CATCH_RATE = 9;
	private static final int OV_IMGS = 10;
	private static final int NEXT_EV = 11;
	
	private String dexN;
	private String id;
	private String name;
	private boolean player;
	
	private int level;
	private int status;
	
	private float maxHp;
	private float hp;
	private float attack;
	private float defense;
	private float speed;
	private float special;
	
	private float hp0;
	private float attack0;
	private float defense0;
	private float speed0;
	private float special0;
	
	private int attackStage;
	private int defenseStage;
	private int speedStage;
	private int specialStage;
	private int accurStage;
	
	private String[] types;
	private float catchRate;
	
	private BufferedImage[] ovImages;
	private BufferedImage[] bImages;
	
	private String nextEvName;
	private int nextEvLevel;
	
	private int expGroup;
	private int exp;
	private int exp2next;
	private float xpStat;
	
	private ArrayList <Attack> learnSet;
	private ArrayList <Integer> attackLevels;
	
	private Attack[] inheritAttacks;
	private Attack[] activeAttacks;
	private Attack nextAttack;
	
	private HashMap<Integer,Float> multipliers;
	
	private int flinchingCounter;
	private boolean flinching;
	
	private boolean alive;
	private boolean falling;
	private boolean evolved;
	private boolean readyToEvolve;
	
	private int xOffset;
	private int yOffset;
	
	public Pokemon(String name,int level, boolean player)
	{
		this(name,level,player,false, null, 0);
	}
	
	public Pokemon(String name,int level, boolean player, boolean evolved, Attack[] inheritAttacks, float inheritHp)
	{
		this.name = name;
		this.level = level;
		this.player = player;
		this.evolved = evolved;
		this.inheritAttacks = inheritAttacks;
		
		
		readyToEvolve = false;
		
		if(!evolved)
			getStats(true);
		else
		{
			getStats(false);
			hp = inheritHp;
		}
		
		loadImages();
		
		loadMoves();
		createMultiplierTable();
		
		flinching = false;
	
		alive = hp > 0;
		
		falling = false;
		
		status = OK;
		xOffset = 0;
		yOffset = 0;
		
		expGroup = ResourceLoader.getExpGroup(name);
		setExp();
		
		id = Util.getRandomID();
	}
	
	private void createMultiplierTable()
	{
		multipliers = new HashMap<Integer,Float>();
		multipliers.put(-6, 0.25f);
		multipliers.put(-5, 0.28f);
		multipliers.put(-4, 0.33f);
		multipliers.put(-3, 0.40f);
		multipliers.put(-2, 0.50f);
		multipliers.put(-1, 0.66f);
		multipliers.put( 0, 1f);
		multipliers.put( 1, 1.5f);
		multipliers.put( 2, 2.0f);
		multipliers.put( 3, 2.5f);
		multipliers.put( 4, 3.0f);
		multipliers.put( 5, 3.5f);
		multipliers.put( 6, 4.0f);
	}
	
	private float adjustToLevel(float stat, boolean isHp)
	{
		if(isHp)
			return (float) Math.floor((IV + stat + 50)*level/50 + 10);
		else
			return (float) Math.floor((IV + stat)*level/50 + 5);	
	}
	
	private void loadMoves()
	{
		learnSet = new ArrayList<Attack>();
		attackLevels = new ArrayList<Integer>();
		
		activeAttacks = new Attack[4];
		
		InputStream in = getClass().getResourceAsStream("/info/pkmnInfo/" + name + ".txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		try
		{
			String line;
			while((line = br.readLine()) != null)
			{
				String[] components = line.split("\\s+");
				attackLevels.add(Integer.parseInt(components[0])); 
				learnSet.add(ResourceLoader.getAttack(components[1]));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(evolved)
			activeAttacks = inheritAttacks;
		else
			fillActiveAttacks();
	}
	
	private void fillActiveAttacks()
	{
		Collections.reverse(attackLevels);
		Collections.reverse(learnSet);
		
		for(int i = 0; i < attackLevels.size(); i++)
		{
			if(Util.arrayIsFull(activeAttacks)) return;
			if(attackLevels.get(i) > level) continue;
			if(Util.valueInArray(learnSet.get(i).getName(),activeAttacks)) continue;
			
			Util.insertValue(learnSet.get(i),activeAttacks);
		}
		
		Util.reverse(activeAttacks);
	}
	
	private void getStats(boolean init)
	{
		String[] origStats = ResourceLoader.getInfo(name);
		
		dexN = origStats[DEX_N];
		maxHp = adjustToLevel(Float.parseFloat(origStats[HP]), true);
		attack = adjustToLevel(Float.parseFloat(origStats[ATTACK]), false);
		defense = adjustToLevel(Float.parseFloat(origStats[DEFENSE]), false);
		speed = adjustToLevel(Float.parseFloat(origStats[SPEED]), false);
		special = adjustToLevel(Float.parseFloat(origStats[SPECIAL]), false);
		
		
		if(init)
			hp = maxHp;
		
		types = origStats[TYPES].split("/");
		xpStat = Float.parseFloat(origStats[EXP_STAT]);
		catchRate = Float.parseFloat(origStats[CATCH_RATE]);
	
		if(origStats.length == 12)
		{
			
			nextEvName = origStats[NEXT_EV].split(",")[0];
			if(origStats[NEXT_EV].equals("MOON_STONE"))
				nextEvLevel = Integer.parseInt(origStats[NEXT_EV].split(",")[1]);
		}
		
		hp0 = maxHp;
		attack0 = attack;
		defense0 = defense;
		speed0 = speed;
		special0 = special;
		
		attackStage = 0;
		defenseStage = 0;
		speedStage = 0;
		specialStage = 0;
		accurStage = 0;
		
	}
	
	private void loadImages()
	{
		ovImages = ResourceLoader.getOvImages(ResourceLoader.getInfo(name)[OV_IMGS]);		
		
		bImages = new BufferedImage[3];
		
		try
		{
			BufferedImage frontImage = ImageIO.read(getClass().getResourceAsStream("/textures/pkmnfront/" + name + ".png"));
			BufferedImage darkImage = ImageIO.read(getClass().getResourceAsStream("/textures/pkmnfront/" + name + "_DARK" + ".png"));
			BufferedImage backImage = ImageIO.read(getClass().getResourceAsStream("/textures/pkmnback/" + name + ".png"));
			
			bImages[0] = frontImage;
			bImages[1] = darkImage;
			bImages[2] = backImage;
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public void render(Graphics2D g, int x,int y, boolean player)
	{
		
		if(BSMain.shake != null)
		{
			xOffset = BSMain.shake.getX();
			yOffset = BSMain.shake.getY();
		}
		
		if(flinchCheck())
			return;
		
		if(player)
			g.drawImage(bImages[2], xOffset + x, yOffset + y, Battle.IMAGE_SIZE, Battle.IMAGE_SIZE, null);
		else
			g.drawImage(bImages[0], xOffset + x, yOffset + y, Battle.IMAGE_SIZE, Battle.IMAGE_SIZE, null);
	}
	
	public void render(Graphics2D g, int[] pos, boolean player)
	{
		
		if(BSMain.shake != null)
		{
			xOffset = BSMain.shake.getX();
			yOffset = BSMain.shake.getY();
		}
	
		if (flinchCheck())
			return;
		
		if(player)
			g.drawImage(bImages[2], xOffset + pos[0],yOffset + pos[1], Battle.IMAGE_SIZE, Battle.IMAGE_SIZE, null);
		else
			g.drawImage(bImages[0], xOffset + pos[0], yOffset + pos[1], Battle.IMAGE_SIZE, Battle.IMAGE_SIZE, null);
	}
	
	public void animate()
	{
		if(falling)
		{
			yOffset += FALLING_SPEED;
			if(yOffset > Battle.IMAGE_SIZE/2)
				falling = false;
			
			return;
		}
		
		if(nextAttack.isSingleFor())
		{
			if(nextAttack.getImageIndex() > 1 && nextAttack.getImageIndex() < 4)
				xOffset = player ? 40 : - 40;
			else
				xOffset = 0;
		}
		else if(nextAttack.isDoubleFor())
		{
			if((nextAttack.getImageIndex() > 1 && nextAttack.getImageIndex() < 4) ||
			   (nextAttack.getImageIndex() > 5 && nextAttack.getImageIndex() < 8))
				xOffset = player? 40: - 40;
			else
				xOffset = 0;
		}
		
		else if(nextAttack.isBackForth() && player)
		{
			if((nextAttack.getImageIndex() > 0 && nextAttack.getImageIndex() < 7) &&
				xOffset > - Game.STDTSIZE/2 - Battle.IMAGE_SIZE)
				
				xOffset -= MOVE_OUT_SPEED;
			
			else if(nextAttack.getImageIndex() > 10 && xOffset <= - 13)
				xOffset += MOVE_IN_SPEED - 3;
			
			if(nextAttack.getImageIndex() == nextAttack.getMaxIndex())
				xOffset = 0;
		}
		
		else if(nextAttack.isBackForth() && !player)
		{
			if((nextAttack.getImageIndex() > 0 && nextAttack.getImageIndex() < 7) &&
				xOffset < Main.WIDTH)
					
				xOffset += MOVE_OUT_SPEED;
				
			else if(nextAttack.getImageIndex() > 10 && xOffset > 0)
				xOffset -= MOVE_IN_SPEED;
		}
	}
	
	private boolean flinchCheck()
	{
		if(flinching)
		{
			flinchingCounter --;
	
			if(flinchingCounter == 0)
				setFlinching(false);
			
			return !((flinchingCounter >= 54 && flinchingCounter <= 60) ||
					 (flinchingCounter >= 42 && flinchingCounter <= 48) ||
					 (flinchingCounter >= 30 && flinchingCounter <= 36) ||
					 (flinchingCounter >= 18 && flinchingCounter <= 24) ||
					 (flinchingCounter >= 6  && flinchingCounter <= 12)); 
		}	
		
		return false;
			
	}
	
	//Getters
	public String getDexN()
	{
		return dexN;
	}
	
	public String getID()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getNextEvName()
	{
		return nextEvName;
	}
	
	public float getMaxHp()
	{
		return maxHp;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public int getTotalXp()
	{
		return exp;
	}
	
	public int getXpNext()
	
	{
		return exp2next;
	}

	public BufferedImage[] getBattleImages()
	{
		return bImages;
	}
	
	public BufferedImage[] getOvImages()
	{
		return ovImages;
	}
	
	public boolean getFlinching()
	{
		return flinching;
	}
	
	public float getStat(int stat)
	{
		if(stat == HP)
			return hp;
		else if(stat == ATTACK)
			return attack;
		else if(stat == DEFENSE)
			return defense;
		else if(stat == SPEED)
			return speed;
		else if(stat == SPECIAL)
			return special;
		else
			return 0;
	}
	
	public float getInitStat(int stat)
	{
		if(stat == HP)
			return hp0;
		else if(stat == ATTACK)
			return attack0;
		else if(stat == DEFENSE)
			return defense0;
		else if(stat == SPEED)
			return speed0;
		else if(stat == SPECIAL)
			return special0;
		else
			return 0;
	}
	
	public int getStage(int stage)
	{
		if(stage == ATTACK)
			return attackStage;
		else if(stage == DEFENSE)
			return defenseStage;
		else if(stage == SPEED)
			return speedStage;
		else if(stage == SPECIAL)
			return specialStage;
		else if(stage == ACCUR)
			return accurStage;
		else
			return 0;
	}
	
	public Attack getNextAttack()
	{
		return nextAttack;
	}
	
	public String[] getTypes()
	{
		return types;
	}
	
	public float getAccur()
	{
		return multipliers.get(accurStage);
	}
	
	public float getCatchRate()
	{
		return catchRate;
	}
	
	public boolean getAlive()
	{
		return alive;
	}
	
	public boolean getFalling()
	{
		return falling;
	}
	
	public boolean getPlayer()
	{
		return player;
	}
	
	public boolean getReadyToEvolve()
	{
		return readyToEvolve;
	}
	
	public float getXpStat()
	{
		return xpStat;
	}
	
	public int getStatus()
	{
		return status;
	}
	
	public String getStatusName()
	{
		if(status == OK)
			return "OK";
		else if(status == PAR)
			return "PAR";
		else if(status == PSN)
			return "PSN";
		else if(status == BRN)
			return "BRN";
		else if(status == FRZ)
			return "FRZ";
		else
			return "SLP";
	}
	
	public boolean getLevelUp()
	{
		return exp2next <= 0;
	}
	
	public ArrayList<Attack> getLearnSet()
	{
		return learnSet;
	}
	
	public Attack[] getActiveAttacks()
	{
		return activeAttacks;
	}
	
	public ArrayList<Integer> getAttackLevels()
	{
		return attackLevels;
	}
	
	//Setters
	
	public void setID(String id)
	{
		this.id = id;
	}
	
	public void setActiveAttacks(Attack[] attacks)
	{
		this.activeAttacks = attacks;
	}
	
	public void setHp(float hp)
	{
		this.hp = hp;
	}
	
	public void increaseStage(int stage, int amount)
	{
		if(stage == ATTACK)
		{
			if(attackStage + amount <= 6)
				attackStage += amount;
			else
				Battle.setTextbox("Nothing happened!", 1);
		}
		
		else if(stage == DEFENSE)
		{
			if(defenseStage + amount <= 6)
				defenseStage += amount;
			else
				Battle.setTextbox("Nothing happened!", 1);
		}
		
		else if(stage == SPEED)
		{
			if(speedStage + amount <= 6)
				speedStage += amount;
			else
				Battle.setTextbox("Nothing happened!", 1);
		}
		
		else if(stage == SPECIAL)
		{
			if(specialStage + amount <= 6)
				specialStage += amount;
			else
				Battle.setTextbox("Nothing happened!", 1);
		}
		
		else if(stage == ACCUR)
		{
			if(accurStage + amount <= 6)
				accurStage += amount;
			else
				Battle.setTextbox("Nothing happened!", 1);
		}
	}
	
	public void decreaseStage(int stage, int amount)
	{
		if(stage == ATTACK)
		{
			if(attackStage - amount >= -6)
				attackStage -= amount;
			else
				Battle.setTextbox("Nothing happened!", 1);
		}
		
		else if(stage == DEFENSE)
		{
			if(defenseStage - amount >= -6)
				defenseStage -= amount;
			else
				Battle.setTextbox("Nothing happened!", 1);
		}
		
		else if(stage == SPEED)
		{
			if(speedStage - amount >= -6)
				speedStage -= amount;
			else
				Battle.setTextbox("Nothing happened!", 1);
		}
		
		else if(stage == SPECIAL)
		{
			if(specialStage - amount >= -6)
				specialStage -= amount;
			else
				Battle.setTextbox("Nothing happened!", 1);
		}
		
		else if(stage == ACCUR)
		{
			if(accurStage - amount >= -6)
				accurStage -= amount;
			else
				Battle.setTextbox("Nothing happened!", 1);
		}
	}
	
	public void damage(float damage)
	{
		hp -= damage;
		
		if(Math.round(hp) <= 0)
			alive = false;
	}
	public void reduceStatBy(int stat, float amount)
	{
		if(stat == HP)
			hp -= amount;
		else if(stat == ATTACK)
			attack -= amount;
		else if(stat == DEFENSE)
			defense -= amount;
		else if(stat == SPEED)
			speed -= amount;
		else if(stat == SPECIAL)
			special -= amount;
	}
	
	public void increaseStatBy(int stat, float amount)
	{
		if(stat == HP)
			hp += amount;
		else if(stat == ATTACK)
			attack += amount;
		else if(stat == DEFENSE)
			defense += amount;
		else if(stat == SPEED)
			speed += amount;
		else if(stat == SPECIAL)
			special += amount;
	}
	
	public void resetStats(boolean center)
	{
		if(center)
		{
			alive = true;
			hp = hp0;
			status = OK;
			xOffset = 0;
			yOffset = 0;
			
			for(Attack attack: activeAttacks)
			{
				if(attack == null) continue;
				attack.setPool(attack.getMaxPool());
			}
		}
		
		attack = attack0;
		defense = defense0;
		speed = speed0;
		special = special0;
		
		attackStage = 0;
		defenseStage = 0;
		speedStage = 0;
		specialStage = 0;
		accurStage = 0;
	}
	
	public void resetStats()
	{
		resetStats(false);
	}
	
	public void adjustStats()
	{
		attack = attack0 * multipliers.get(attackStage);
		defense = defense0 * multipliers.get(defenseStage);
		speed = speed0 * multipliers.get(speedStage);
		special = special0 * multipliers.get(specialStage);
	}
	
	public void setFlinching(boolean flinching)
	{
		this.flinching = flinching;
		flinchingCounter = Main.FPS;
	}
	
	public void setNextAttack(Attack attack)
	{
		this.nextAttack = attack;
	}
	
	public void setFalling(boolean falling)
	{
		this.falling = falling;
	}
	
	public void setPlayer(boolean player)
	{
		this.player = player;
	}
	
	public void resetOffsets()
	{
		xOffset = 0;
		yOffset = 0;
	}
	
	public void setStatus(int status)
	{
		this.status = status;
		
		if(status == PAR)
			speed = speed * 0.25f;
	}

	
	public void setExp()
	{
		exp = Util.calculateXP(level,expGroup);
		exp2next = Util.calculateXP(level + 1, expGroup) - Util.calculateXP(level, expGroup);
	}
	
	public void awardXP(int xp)
	{
		if(level < 100)
		{
			exp2next -= xp;
			exp += xp;
		}
		Battle.setTextbox(String.format("%s gained#%d EXP. Points!", name, xp), 1);
		
	}
	
	public void levelUp()
	{
		int maxHp0 = (int)maxHp;
		level ++;
		setExp();
		getStats(false);
		
		if(hp + maxHp - maxHp0 <= maxHp)
			hp += (maxHp - maxHp0);
		
		Battle.setTextbox(String.format("%s grew#to level %d!", name, level), 2);
		
		if(level == nextEvLevel)
			readyToEvolve = true;
	}
	
	public String learnNextMove()
	{
		Attack attack = Util.getNewAttack(this);
		Util.insertValue(attack, activeAttacks);
		return attack.getName();
	}
}
