package entities;

import gameStates.GSOverworld;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Random;

import main.Game;
import main.Main;
import util.Counter;
import util.GameInfo;
import util.ResourceLoader;

public class SNpc extends Sprite{
	
	public static final HashMap<String,Integer> BASE_MONEY;
	public static final HashMap<String,int[]> TRAINER_IMGS;
	
	static
	{
		BASE_MONEY = new HashMap<String,Integer>();
		BASE_MONEY.put("BEAUTY",70);	 BASE_MONEY.put("BIKER",20);        BASE_MONEY.put("BIRD KEEPER",25);
		BASE_MONEY.put("BLACK BELT",25); BASE_MONEY.put("BOSS",99);         BASE_MONEY.put("BUG CATCHER",10);
		BASE_MONEY.put("BURGLAR",90);	 BASE_MONEY.put("CHANNELER",30);    BASE_MONEY.put("COOL TRAINERM",35);
		BASE_MONEY.put("COOL TRAINERF", 35);
		BASE_MONEY.put("CUE BAL",25);    BASE_MONEY.put("ENGINEER", 50);    BASE_MONEY.put("FISHERMAN",35);
		BASE_MONEY.put("GAMBLER",70);    BASE_MONEY.put("GENTLEMAN",70);    BASE_MONEY.put("ROCKET",30);
		BASE_MONEY.put("HIKER", 35);	 BASE_MONEY.put("JR.TRAINERM",20);   BASE_MONEY.put("JUGGLER", 35);
		BASE_MONEY.put("JR.TRAINERF",20);
		BASE_MONEY.put("LASS", 15);      BASE_MONEY.put("POKE MANIAC", 50); BASE_MONEY.put("PSYCHIC", 10);
		BASE_MONEY.put("ROCKER", 25);    BASE_MONEY.put("SUPER NERD", 25);  BASE_MONEY.put("SWIMMER", 5);
		BASE_MONEY.put("SAILOR", 30);    BASE_MONEY.put("SCIENTIST", 50);   BASE_MONEY.put("TAMER", 40);
		BASE_MONEY.put("YOUNGSTER", 15); BASE_MONEY.put(GameInfo.RIVALNAME, 65); BASE_MONEY.put("RIVALNAME", 65);
		BASE_MONEY.put("BROCK", 99);
		
		TRAINER_IMGS = new HashMap<String,int[]>();
		
		TRAINER_IMGS.put("BEAUTY", new int[]{3,3});        TRAINER_IMGS.put("BIKER", new int[]{5,2});
		TRAINER_IMGS.put("BIRD_KEEPER", new int[]{4,2});   TRAINER_IMGS.put("BLACK BELT", new int[]{2,3});
		TRAINER_IMGS.put("BOSS", new int[]{7,0});          TRAINER_IMGS.put("BUG CATCHER", new int[]{8,0});
		TRAINER_IMGS.put("BURGLAR", new int[]{1,3});       TRAINER_IMGS.put("CHANNELER", new int[]{4,1});
		TRAINER_IMGS.put("COOL TRAINERM", new int[]{4,3}); TRAINER_IMGS.put("COOL TRAINERF", new int[]{7,2});
		TRAINER_IMGS.put("FISHERMAN", new int[]{2,2});     TRAINER_IMGS.put("GAMBLER", new int[]{1,2});
		TRAINER_IMGS.put("GENTLEMAN", new int[]{9,1});     TRAINER_IMGS.put("ROCKET", new int[]{2,1});
		TRAINER_IMGS.put("HIKER", new int[]{3,1});         TRAINER_IMGS.put("JR.TRAINERM", new int[]{9,0});
		TRAINER_IMGS.put("JR.TRAINERF", new int[]{6,1});
		TRAINER_IMGS.put("JUGGLER", new int[]{6,2});       TRAINER_IMGS.put("LASS", new int[]{0,1});
		TRAINER_IMGS.put("POKE MANIAC", new int[]{8,3});   TRAINER_IMGS.put("PSYCHIC", new int[]{3,3});
		TRAINER_IMGS.put("ROCKER", new int[]{8,1});        TRAINER_IMGS.put("SUPER NERD", new int[]{1,1});
		TRAINER_IMGS.put("SWIMMER", new int[]{5,1});       TRAINER_IMGS.put("SAILOR", new int[]{7,1});
		TRAINER_IMGS.put("SCIENTIST", new int[]{6,3});     TRAINER_IMGS.put("TAMER", new int[]{5,3});
		TRAINER_IMGS.put("YOUNGSTER", new int[]{0,2});
		TRAINER_IMGS.put("BROCK", new int[]{0,0});         TRAINER_IMGS.put(GameInfo.RIVALNAME, new int[]{4,4});
	}
	
	public static final int RESET_COUNTER = Main.FPS * 2;
	public static final int MOVE_COUNTER = Main.FPS * 2;
	public static final int NORMAL_SPEED = Game.STDTSIZE/40;
	public static final int FAST_SPEED   = Game.STDTSIZE/20;
	public static final int TRAINER_VICINITY = 4;
	public static final int IDENT_COUNTER = Game.STDTSIZE;
	
	private final BufferedImage trainerEx = ResourceLoader.getTexture("trainerEx");
	
	private String trainerName;
	private Pokemon[] pokemon;
	
	private boolean resetPosition;
	private boolean defeated;
	private boolean identState;
	
	private int id;
	
	private int resetCounter;
	private int moveCounter;
	private int value;
	private String message;
	private String defeatMessage;
	
	private Counter identCounter;
	
	public SNpc(int id, float x,float y,int[] southCoords, int[] northCoords,
									 int[] westCoords, int[] south1Coords,
									int[] north1Coords,  int[] west1Coords,
									boolean isTrainer, String trainerName,
									Pokemon[] pokemon, int[] battleImageCoords,
									int value, boolean isGymLeader,
									boolean movingNpc, int direction,
									String message, Map world)
	{
		this(id,x,y,southCoords, northCoords, westCoords, south1Coords,
			north1Coords, west1Coords, isTrainer, trainerName, pokemon, battleImageCoords,
			value, isGymLeader, movingNpc, direction, message, world, false);
	}
	
	public SNpc(int id, float x,float y,int[] southCoords, int[] northCoords,
								int[] westCoords, int[] south1Coords,
								int[] north1Coords,  int[] west1Coords,
								boolean isTrainer, String trainerName,
								Pokemon[] pokemon, int[] battleImageCoords,
								int value, boolean isGymLeader,
								boolean movingNpc, int direction,
								String message, Map world, boolean dummy)
	{
		super(x,y,true, false);
		
		if(!dummy)
			createImages(southCoords,northCoords,
					 westCoords,south1Coords,
					 north1Coords,west1Coords);
		
		this.id = id;
		this.movingNpc = movingNpc;
		this.isTrainer = isTrainer;
		this.isGymLeader = isGymLeader;
		this.trainerName = trainerName;
		this.direction = direction;
		this.pokemon = pokemon;
		this.value = value;
		
		identState = false;
		
		getMessage(message);
		
		defeated = false;
		direction0 = direction;
		
		surrTiles = new Tile[4];
		animationDelayMax = 9;
		animationDelay = animationDelayMax;
		
		Random rand = new Random();
		moveCounter = MOVE_COUNTER + rand.nextInt(100);
		resetCounter = 0;
		resetPosition = false;
		
		identCounter = new Counter(IDENT_COUNTER);
		
		if(!dummy)
			setTiles(world);
		
		if(isTrainer)
			createBattleImages(battleImageCoords);
	}
	
	private void getMessage(String message)
	{
		String[] split = message.split("&");
		if(split.length == 1)
			this.message = message;
		else
		{
			this.message = split[0];
			this.defeatMessage = split[1];
		}
	}
	
	public void attackUpdate(Map world,SPlayer player)
	{
		if(identCounter.getAlive())
		{
			identState = true;
			identCounter.update();
			if(!identCounter.getAlive())
			{
				identState = false;
				moving = true;
				nextTile = world.getNextTile(this);
			}
		}
		else
		{
			attackMovement(world);
			if(!moving)
				GSOverworld.setTextbox(message);
		}
	}
	
	public void update(Map world,SPlayer player,boolean fastMovement)
	{
		if(talking)
		{
			if(!resetPosition) return;
			
			resetCounter --;
			if(resetCounter > 0) return;
	
			resetPosition = false;
			talking = false;
			direction = direction0;
		}
			
		if(!movingNpc) return;
		
		if(!moving || GSOverworld.changeEffect)
		{
			setTiles(world);
			moveCounter --;
			if(moveCounter <= 0)
			{
				setNextTile(world,player);
			}
		}
		else
			movement(fastMovement);
	
	}
	
	public void update(Map world,SPlayer player)
	{
		update(world,player,false);
	}
	
	public void setTiles(Map world)
	{
		currTile = world.getTileAt((int)x, (int)y);	
		nextTile = currTile;
		surrTiles[0] = world.getTileAt((int)x, (int)y - Game.STDTSIZE);
		surrTiles[1] = world.getTileAt((int)x + Game.STDTSIZE, (int)y);
		surrTiles[2] = world.getTileAt((int)x, (int)y + Game.STDTSIZE);
		surrTiles[3] = world.getTileAt((int)x - Game.STDTSIZE, (int)y);
	}
	
	public void setNextTile(Map world,SPlayer player)
	{
		Random rand = new Random();
		int nextDir = rand.nextInt(4);
		
		if(!surrTiles[nextDir].getSolid() && !surrTiles[nextDir].getLedge()&&
		   !(surrTiles[nextDir] == player.getCurrTile() || 
		   	 surrTiles[nextDir] == player.getNextTile()) &&
		   !checkNpcOverlap(world,nextDir))
		{
			int idleRand = rand.nextInt(3);
			if(idleRand != 1)
			{
				nextTile = surrTiles[nextDir];
				moving = true;
			}
		}
		
		direction = nextDir;
		moveCounter = MOVE_COUNTER + (rand.nextInt(Game.STDTSIZE));

	}
	
	public void forceNextTile(Tile tile)
	{
		this.nextTile = tile;
	}
	
	private boolean checkNpcOverlap(Map world,int nextDir)
	{
		for(SNpc npc: world.getNpcs())
		{
			if(npc == this) continue;
			
			if(surrTiles[nextDir] == npc.getCurrTile() ||
			   surrTiles[nextDir] == npc.getNextTile())
			   
			   return true;
		}
		
		return false;
	}
	
	private void attackMovement(Map world)
	{
	
		if(nextTile.getX() > x)
		{
			x += NORMAL_SPEED;
			if(x >= nextTile.getX())
			{
				x = nextTile.getX();
				setTiles(world);
			}
		}
		
		else if(nextTile.getX() < x)
		{
			x -= NORMAL_SPEED;
			if(x <= nextTile.getX())
			{
				x = nextTile.getX();
				setTiles(world);
			}
		}
		
		else if(nextTile.getY() > y)
		{
			y += NORMAL_SPEED;
			if(y >= nextTile.getY())
			{
				y = nextTile.getY();
				setTiles(world);
			}
		}
		
		else if(nextTile.getY() < y)
		{
			y -= NORMAL_SPEED;
			if(y <= nextTile.getY())
			{
				y = nextTile.getY();
				setTiles(world);
			}
		}
		
		if(currTile == nextTile) 
		{
			moving = false;
			return;
		}
	}
	
	public void movement(boolean fastMovement)
	{
		if(nextTile == currTile) return;
		
		if(nextTile == surrTiles[0])
		{
			if(fastMovement)
				y -= FAST_SPEED;
			else
				y -= NORMAL_SPEED;
			
			if(y < surrTiles[0].getY())
			{
				y = surrTiles[0].getY();
				moving = false;
			}
			
		}
		
		else if(nextTile == surrTiles[1])
		{
			if(fastMovement)
				x += FAST_SPEED;
			else
				x += NORMAL_SPEED;
			
			if(x > surrTiles[1].getX())
			{
				x = surrTiles[1].getX();
				moving = false;
			}
		}
		
		else if(nextTile == surrTiles[2])
		{
			if(fastMovement)
				y += FAST_SPEED;
			else
				y += NORMAL_SPEED;
			
			if(y > surrTiles[2].getY())
			{
				y = surrTiles[2].getY();
				moving = false;
			}
		}
		
		else
		{
			if(fastMovement)
				x -= FAST_SPEED;
			else
				x -= NORMAL_SPEED;
			
			if(x < surrTiles[3].getX())
			{
				x = surrTiles[3].getX();
				moving = false;
			}
		}		
	}
	
	public void drawGrass(Graphics2D g)
	{
		if(nextTile != null)
		{
			if(nextTile.getEncounter())
				g.drawImage(Ambient.getGrassImage(), 
						Map.xoffset + nextTile.getX(),
						Map.yoffset + nextTile.getY(), null);
		}
		if(currTile != null)
		{
			if(currTile.getEncounter())
				g.drawImage(Ambient.getGrassImage(), 
					Map.xoffset + currTile.getX(),
					Map.yoffset + currTile.getY(), null);
		}
	}
	
	public void drawIdentMarker(Graphics2D g)
	{
		g.drawImage(trainerEx,(int)x + Map.xoffset, (int)y - trainerEx.getHeight() + Map.yoffset - 16, null);
	}
	
	//Getters
	public Pokemon[] getPokemon()
	{
		return pokemon;
	}
	
	public boolean getDefeated()
	{
		for(Pokemon active: pokemon)
			if(active.getAlive())
				return false;
		return true;
	}
	
	public boolean getIdentState()
	{
		return identState;
	}
	
	public Pokemon getFirstAvail()
	{
		for(int i = 0; i < pokemon.length; i ++)
		{
			if(pokemon[i] == null) return null;
			
			if(pokemon[i].getAlive())
				return pokemon[i];
		}
		
		return null;
	}
	
	public boolean getAlreadyDef()
	{
		return defeated;
	}
	
	public boolean getMoving()
	{
		return moving;
	}
	
	public boolean getMovingNpc()
	{
		return movingNpc;
	}
	
	public boolean getIsTrainer()
	{
		return isTrainer;
	}
	
	public int getID()
	{
		return id;
	}
	
	public boolean getIsGymLeader()
	{
		return isGymLeader;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public String getTrainerName()
	{
		return trainerName;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	//Setters
	public void resetPosition()
	{
		resetPosition = true;
		resetCounter = RESET_COUNTER;
	}
	
	public void setDefeated(boolean defeated)
	{
		
		this.defeated = defeated;
		if(defeated)
			swapMessage();
	}
	
	public void swapMessage()
	{
		message = defeatMessage;
	}
	
	public void setMoving(boolean moving)
	{
		this.moving = moving;
	}
	
	public void setMovingNpc(boolean movingNpc)
	{
		this.movingNpc = movingNpc;
	}
	
	public void setMessage(String message)
	{
		defeatMessage = message;
		swapMessage();
	}
}
