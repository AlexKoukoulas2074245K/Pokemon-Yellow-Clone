package entities;

import gameStates.Battle;
import gameStates.GSOverworld;

import java.awt.Graphics2D;

import main.Game;
import misc.Bag;
import misc.Item;
import misc.Menu;
import util.GameInfo;
import util.Graphics;
import util.Input;
import util.Loader;
import util.ResourceInfo;
import util.Util;
import util.Wrap;

public class SPlayer extends Sprite{
	
	private final float NORMAL_SPEED = Game.STDTSIZE/20;
	
	private Pokemon[] activePokemon;
	private boolean[] emblems;
	
	private SNpc trainerRef;
	private SObject objectRef;
	
	private Wrap destWrap;
	
	private int nEscapes;
	
	private boolean mapChange;
	private boolean mapChange0;
	
	private boolean healingPokemon;
	private boolean buying;
	private boolean forceJump;
	private boolean pewterForceEvent;
	private boolean pewterMuseumEvent;
	private boolean hasPayed;
	
	private String home;

	private Menu menu;
	private Bag bag;
	
	public SPlayer(Loader loader)
	{
		super(loader.getPlayerX(), loader.getPlayerY(), false, false);
		
		createImages(ResourceInfo.PLAYER_SOUTH, ResourceInfo.PLAYER_NORTH,
				 ResourceInfo.PLAYER_WEST, ResourceInfo.PLAYER_SOUTH1,
				 ResourceInfo.PLAYER_NORTH1,  ResourceInfo.PLAYER_WEST1);
	
		createBattleImages(ResourceInfo.PLAYER_BATTLE);
		
		direction = loader.getPlayerDirection();
		
		movingNpc = false;
		moving = false;
		jumping = false;
		autoMove = false;
		doorAutoMove = false;
		forceJump = false;
		hasPayed = false;
		pewterMuseumEvent = false;
		surrTiles = new Tile[4];
		
		animationDelayMax = 9;
		animationDelay = animationDelayMax;
		
		mapChange = false;
		mapChange0 = mapChange;
		
		menuOpen = false;
		
		activePokemon = loader.getPlayerActivePkmn();
		nEscapes = 0;
		emblems = loader.getPlayerEmblems();
		
		bag = new Bag();
		
		for(int i = 0; i < loader.getItemNames().size(); i ++)
			bag.addItem(loader.getItemNames().get(i), loader.getItemQuants().get(i));

		wildAttack = false;
		trainerAttack = false;
		
		home = loader.getHome();
		
		healingPokemon = false;
		buying = false;
	}
	
	public SPlayer()
	{
		super(Game.STDTSIZE * 4,Game.STDTSIZE * 7,false, false);
		
		createImages(ResourceInfo.PLAYER_SOUTH, ResourceInfo.PLAYER_NORTH,
				 ResourceInfo.PLAYER_WEST, ResourceInfo.PLAYER_SOUTH1,
				 ResourceInfo.PLAYER_NORTH1,  ResourceInfo.PLAYER_WEST1);
	
		createBattleImages(ResourceInfo.PLAYER_BATTLE);
		
		movingNpc = false;
		moving = false;
		jumping = false;
		autoMove = false;
		doorAutoMove = false;
		forceJump = false;
		hasPayed = false;
		pewterMuseumEvent = false;
		surrTiles = new Tile[4];
		
		animationDelayMax = 9;
		animationDelay = animationDelayMax;
		
		mapChange = false;
		mapChange0 = mapChange;
		
		menuOpen = false;
		
		activePokemon = new Pokemon[6];
		addPokemon("PIKACHU",5, true);
		
		emblems = new boolean[8];
		nEscapes = 0;
		
		bag = new Bag();
		bag.addItem("POKE BALL", 5);
		
		wildAttack = false;
		trainerAttack = false;
		
		home = "pallet";
		
		healingPokemon = false;
		buying = false;
		pewterForceEvent = false;
	}
	
	public void update(Map map)
	{
		
		if(mapChange && mapChange0)
			return;
		
		else if(!mapChange && mapChange0)
			mapChange(map);
		
		if(menuOpen)
		{
			menu.update();
			if(!menu.getAlive())
			{
				menuOpen = false;
				menu = null;
			}
			
			return;
		}
		
		//set new tiles
		setNewTiles(map);
		
		//keyboard input
		checkInput(map);
		
		//movement
		movement(map);
		
		mapChange0 = mapChange;
	}
	
	private void mapChange(Map map)
	{
		
		String area0 = map.getArea();
		String newArea = destWrap.findWrapExit().getArea();
		
		if(destWrap.getIsAreaConnection())
			Graphics.tilemapRecolouring(area0, newArea);
		else
			Graphics.tilemapRecolouring(newArea);
		
		map.changeMap(destWrap);
		
		resetImages();
		
		
		x = destWrap.findWrapExit().getCol() * Game.STDTSIZE;
		y = destWrap.findWrapExit().getRow() * Game.STDTSIZE;
		
		autoMove = false;
		doorAutoMove = false;
		
		if(!destWrap.findWrapExit().getBuilding().contains("pewterMuseum"))
			hasPayed = false;
		
		setNewTiles(map);
		
		if(map.getBuilding().equals("default") || 
				(destWrap.getBuilding() != map.getBuilding()))
		{
			if(destWrap.getIsAreaConnection())
			{
				nextTile = map.getTileAt(surrTiles[direction].getX(),surrTiles[direction].getY());
				moving = true;
			}
			else
			{
				if(!destWrap.findWrapExit().getIsDoor() && !destWrap.isInside())
				{
					nextTile = map.getTileAt(currTile.getX(), currTile.getY() + Game.STDTSIZE);
					moving = true;
				}
			}
		}
		mapChange = false;
	}
	
	public void resetImages()
	{
		
		createImages(ResourceInfo.PLAYER_SOUTH, ResourceInfo.PLAYER_NORTH,
					 ResourceInfo.PLAYER_WEST, ResourceInfo.PLAYER_SOUTH1,
					 ResourceInfo.PLAYER_NORTH1,  ResourceInfo.PLAYER_WEST1);
	}
	
	
	public void setNewTiles(Map map)
	{
		if(!moving)
		{
			currTile = map.getTileAt((int)x, (int)y);	
			nextTile = currTile;
			surrTiles[0] = map.getTileAt((int)x, (int)y - Game.STDTSIZE);
			surrTiles[1] = map.getTileAt((int)x + Game.STDTSIZE, (int)y);
			surrTiles[2] = map.getTileAt((int)x, (int)y + Game.STDTSIZE);
			surrTiles[3] = map.getTileAt((int)x - Game.STDTSIZE, (int)y);
			
			if(surrTiles[2] == null)
				surrTiles[2] = currTile;
			
			forceJump = false;
			//check for wraps
			if(map.wrapAt(currTile.getCol(), currTile.getRow()) && (autoMove || doorAutoMove))
			{
				
				destWrap = map.getWrap(currTile.getCol(),currTile.getRow());
			
				if(destWrap.getIsDoor() && !Input.DOWN_KEY)
					return;		
	
				mapChange = true;
			}	
		}	
	}
	
	private void checkInput(Map map)
	{
		//input rejected if on auto move or unless very close to the 
		//next indicated tile 
		if(nextTile != currTile &&
		   (Math.abs(nextTile.getX() - currTile.getX()) > 5 ||
			Math.abs(nextTile.getY() - currTile.getY()) > 5))
			return;
		
		if(autoMove || talking)return;
		
		if(Input.UP_KEY && !menuOpen)
		{
			if(!moving)
				direction = 0;
			
			if(!surrTiles[0].getSolid() && !surrTiles[0].getLedge() && 
			   !map.solidObjectAt(surrTiles[0]) &&
			   !map.npcAt(surrTiles[0].getX(),surrTiles[0].getY()) &&
			   !map.npcCurrTile(surrTiles[0]) && !map.npcNextTile(surrTiles[0]))
			{
				
				nextTile = surrTiles[0];
				moving = true;
				if(map.wrapAt(surrTiles[0].getCol(), surrTiles[0].getRow()))
					initAutoMove(surrTiles[0],map);
			}
		}
		else if(Input.RIGHT_KEY && !menuOpen)
		{
			if(!moving)
				direction = 1;
			
			if(!surrTiles[1].getSolid() && !surrTiles[1].getLedge() &&
			   !map.solidObjectAt(surrTiles[1]) &&
			   !map.npcAt(surrTiles[1].getX(), surrTiles[1].getY()) &&
			   !map.npcCurrTile(surrTiles[1]) && !map.npcNextTile(surrTiles[1]))
			{
				nextTile = surrTiles[1];
				moving = true;
				if(map.wrapAt(surrTiles[1].getCol(), surrTiles[1].getRow()))
					initAutoMove(surrTiles[1],map);
			}
		}		
		else if(Input.DOWN_KEY && !menuOpen)
		{
			if(!moving)
				direction = 2;
			
			if(!surrTiles[2].getSolid() && !map.solidObjectAt(surrTiles[2]) &&
			   !map.npcAt(surrTiles[2].getX(), surrTiles[2].getY()) &&
			   !map.npcCurrTile(surrTiles[2]) && !map.npcNextTile(surrTiles[2]))
			{
				if(surrTiles[2].getLedge())
				{
					surrTiles[2] = map.getTileAt(surrTiles[2].getX(),
												   surrTiles[2].getY() + Game.STDTSIZE);
					jumping = true;
				}
				
				nextTile = surrTiles[2];
				moving = true;
				if(map.wrapAt(surrTiles[2].getCol(), surrTiles[2].getRow()))
					initAutoMove(surrTiles[2],map);
			}
		}
		else if(Input.LEFT_KEY && !menuOpen)
		{
			if(!moving)
				direction = 3;
			
			if(!surrTiles[3].getSolid() && !surrTiles[3].getLedge() &&
			   !map.solidObjectAt(surrTiles[3]) &&
			   !map.npcAt(surrTiles[3].getX(), surrTiles[3].getY()) &&
			   !map.npcCurrTile(surrTiles[3]) && !map.npcNextTile(surrTiles[3]))
			{
				nextTile = surrTiles[3];
				moving = true;
				if(map.wrapAt(surrTiles[3].getCol(), surrTiles[3].getRow()))
					initAutoMove(surrTiles[3],map);
			}
		}
		
		else if(Input.START_TAPPED)
		{
			if(!menuOpen)
			{
				menu = new Menu(this,map);
				menuOpen = true;
			}
		}
		
		else if(Input.B_TAPPED && menuOpen)
			menuOpen = false;
		
		else if(Input.A_TAPPED && !menuOpen)
		{
			if(scriptedEvents(map))
				return;
			
			if(direction == 0 && map.npcAt(surrTiles[0].getX(), surrTiles[0].getY()))
			{
				talking = true;
				SNpc npc = map.getNpc(surrTiles[0].getX(), surrTiles[0].getY());
				trainerRef = npc;
				npc.setDirection(2);
				
				if(map.getTrainerAttack() != null)	
					trainerAttack = true;
						
				else
				{
					npc.setTalking(true);
					npc.getMessage();
					GSOverworld.setTextbox(npc.getMessage());
				}
			}
			else if(direction == 0 && map.objectAt(surrTiles[0].getX(),surrTiles[0].getY()))
			{
				SObject object = map.getObject(surrTiles[0].getX(),surrTiles[0].getY());
				objectRef = object; 
				if(object.hasMessage())
				{
					talking = true;
					object.setTalking(true);
					GSOverworld.setTextbox(object.getMessage());
				}
			}
			
			else if(direction == 1 && map.npcAt(surrTiles[1].getX(), surrTiles[1].getY()))
			{
				talking = true;
				SNpc npc = map.getNpc(surrTiles[1].getX(), surrTiles[1].getY());
				npc.setDirection(3);
				trainerRef = npc;
				if(map.getTrainerAttack() != null)
					trainerAttack = true;
				
				else
				{
					npc.setTalking(true);
					GSOverworld.setTextbox(npc.getMessage());
				}
			}
			
			else if(direction == 1 && map.objectAt(surrTiles[1].getX(), surrTiles[1].getY()))
			{
				
				SObject object = map.getObject(surrTiles[1].getX(), surrTiles[1].getY());
				objectRef = object;
				if(object.hasMessage())
				{
					talking = true;
					object.setTalking(true);
					GSOverworld.setTextbox(object.getMessage());
				}
			}
			
			else if(direction == 2 && map.npcAt(surrTiles[2].getX(), surrTiles[2].getY()))
			{
				talking = true;
				SNpc npc = map.getNpc(surrTiles[2].getX(), surrTiles[2].getY());
				npc.setDirection(0);
				trainerRef = npc;
				
				if(map.getTrainerAttack() != null)
					trainerAttack = true;
				
				else
				{
					npc.setTalking(true);
					GSOverworld.setTextbox(npc.getMessage());
				}
			}
			
			else if(direction == 2 && map.objectAt(surrTiles[2].getX(), surrTiles[2].getY()))
			{
				SObject object = map.getObject(surrTiles[2].getX(), surrTiles[2].getY());
				objectRef = object;
				if(object.hasMessage())
				{
					talking = true;
					object.setTalking(true);
					GSOverworld.setTextbox(object.getMessage());
				}
			}
			
			else if(direction == 3 && map.npcAt(surrTiles[3].getX(), surrTiles[3].getY()))
			{
				talking = true;
				SNpc npc = map.getNpc(surrTiles[3].getX(), surrTiles[3].getY());
				npc.setDirection(1);
				trainerRef = npc;
				
				if(map.getTrainerAttack() != null)
					trainerAttack = true;
				
				else
				{
					npc.setTalking(true);
					GSOverworld.setTextbox(npc.getMessage());
				}
			}
			
			else if(direction == 3 && map.objectAt(surrTiles[3].getX(), surrTiles[3].getY()))
			{
				SObject object = map.getObject(surrTiles[3].getX(), surrTiles[3].getY());
				objectRef = object;
				if(object.hasMessage())
				{
					talking = true;
					object.setTalking(true);
					GSOverworld.setTextbox(object.getMessage());
				}
			}
		}
		
		
		if(map.wrapAt(currTile.getCol(), currTile.getRow()) &&
		   map.getWrap(currTile.getCol(), currTile.getRow()).getIsDoor())
		{
			if(!map.getWrap(currTile.getCol(), currTile.getRow()).findWrapExit().getIsDoor())
				doorAutoMove = true;
		}
	}
	
	private boolean scriptedEvents(Map map)
	{
		
		if(currTile.getCol() == 38 && currTile.getRow() == 11 && 
		   map.getArea().equals("viridian") && map.getBuilding().equals("default") && !forceJump)
		{
			GSOverworld.setTextbox("The GYM's doors#are locked...");
			forceJump = true;
			return true;
		}
		
		if(!emblems[0] && x >= 38 * Game.STDTSIZE && map.getArea().equals("pewter"))
		{
			pewterForceEvent = true;
			return true;
		}
		
		if(y <= 5 * Game.STDTSIZE && x <= 11 * Game.STDTSIZE && !hasPayed && map.getBuilding().equals("pewterMuseum"))
		{
			pewterMuseumEvent = true;
		}
		
		if(direction == 0 && map.getBuilding().equals(map.getArea() + "PokeCenter"))
		{
			if(currTile.getCol() == 4 && currTile.getRow() == 4)
				healingPokemon = true;
		}
		
		else if(direction == 3 && map.getBuilding().equals(map.getArea() + "Market"))
		{
			if(currTile.getCol() == 3 && currTile.getRow() == 6)
				buying = true;
		}
		
		else if(direction == 0 && map.getBuilding().equals(map.getArea() + "Market"))
		{
			if(currTile.getCol() == 1 && currTile.getRow() == 8)
				buying = true;
		}
		
		return healingPokemon == true || buying == true;
	}
	
	public void initAutoMove(Tile targetTile,Map map)
	{
		//if the target tile is a door wrap the player can't be in auto move state
		//as he has to press the corresponding key as well to exit
		if(!map.getWrap(targetTile.getCol(), targetTile.getRow()).getIsDoor())
			autoMove = true;
		else
			doorAutoMove = true;
	}
	
	public void movement(Map map)
	{
		if(nextTile == currTile) return;
		 
		if(nextTile == surrTiles[0])
		{
			y -= NORMAL_SPEED;
			if(y < surrTiles[0].getY())
			{
				y = surrTiles[0].getY();
				moving = false;
				jumping = false;
				jumpCounter = JUMP_COUNTER;
	
				if(scriptedEvents(map))
					return;
	
				if(map.getTileAt((int)x, (int)y).getEncounter())
					wildAttack = map.getWildAttack();
				if(map.getTrainerAttack() != null)
				{
					trainerRef = map.getTrainerAttack();
					trainerAttack = true;
				}
			}
			
		}
		
		else if(nextTile == surrTiles[1])
		{
			x += NORMAL_SPEED;
			
			if(x > surrTiles[1].getX())
			{
				x = surrTiles[1].getX();
				moving = false;
				jumping = false;
				jumpCounter = JUMP_COUNTER;
				if(scriptedEvents(map))
					return;
				
				if(map.getTileAt((int)x, (int)y).getEncounter())
					wildAttack = map.getWildAttack();
				if(map.getTrainerAttack() != null)
				{
					trainerRef = map.getTrainerAttack();
					trainerAttack = true;
				}
			}
		}
		
		else if(nextTile == surrTiles[2])
		{
			y += NORMAL_SPEED;
			
			if(y > surrTiles[2].getY())
			{
				y = surrTiles[2].getY();
				moving = false;
				jumping = false;
				jumpCounter = JUMP_COUNTER;

				if(scriptedEvents(map))
					return;

				if(map.getTileAt((int)x, (int)y).getEncounter())
					wildAttack = map.getWildAttack();
				if(map.getTrainerAttack() != null)
				{
					trainerRef = map.getTrainerAttack();
					trainerAttack = true;
				}
			}
		}
		
		else
		{
			x -= NORMAL_SPEED;
			
			if(x < surrTiles[3].getX())
			{
				x = surrTiles[3].getX();
				moving = false;
				jumping = false;
				jumpCounter = JUMP_COUNTER;
				if(scriptedEvents(map))
					return;
				
				if(map.getTileAt((int)x, (int)y).getEncounter())
					wildAttack = map.getWildAttack();
				if(map.getTrainerAttack() != null)
				{
					trainerRef = map.getTrainerAttack();
					trainerAttack = true;
				}
			}
		}
	}
	
	//Getters
	public boolean getMoving()
	{
		return moving;
	}
	
	public boolean[] getEmblems()
	{
		return emblems;
	}
	
	public Pokemon[] getPokemon()
	{
		return activePokemon;
	}
	
	public Pokemon getFirstPkmn()
	{
		for(int i = 0; i < activePokemon.length; i ++)
		{
			if(activePokemon[i] == null) return null;
			
			if(activePokemon[i].getStat(Pokemon.HP) > 0)
				return activePokemon[i];
		}
		
		return null;
	}
	
	public void render(Graphics2D g,boolean battleAni, boolean scriptedEvents)
	{

		super.render(g, menuOpen, talking, battleAni,scriptedEvents);
	
		drawGrass(g);
		
		if(menuOpen)
			menu.render(g);
	}
	
	private void drawGrass(Graphics2D g)
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
	
	public boolean getMapChange()
	{
		return mapChange;
	}
	
	public boolean getHealingPokemon()
	{
		return healingPokemon;
	}
	
	public String getHome()
	{
		return home;
	}
	
	public boolean getBuying()
	{
		return buying;
	}
	
	public boolean getPewterForceEvent()
	{
		return pewterForceEvent;
	}
	
	public boolean getPewterMuseumEvent()
	{
		return pewterMuseumEvent;
	}
	
	public boolean getHasPayed()
	{
		return hasPayed;
	}
	
	public Wrap getDestWrap()
	{
		return destWrap;
	}
	
	public Bag getBag() 
	{
		return bag;
	}
	
	public SNpc getTrainerRef()
	{
		return trainerRef;
	}
	
	public SObject getObjectTalking()
	{
		return objectRef;
	}
	
	public Tile getSurrTile(int direction)
	{
		return surrTiles[direction];
	}
	
	public int getEscapes()
	{
		return nEscapes;
	}
	
	public boolean getDefeated()
	{
		for(Pokemon pkmn: activePokemon)
		{
			if(pkmn == null) continue;
			if(pkmn.getAlive())
				return false;
		}
		return true;
	}
	
	public boolean getMenuOpen()
	{
		return menuOpen;
	}
	
	public boolean getForceJump()
	{
		return forceJump;
	}
	
	//Setters
	public void setMoving(boolean moving)
	{
		this.moving = moving;
	}
	
	public void setMapChange(boolean mapChange)
	{
		this.mapChange = mapChange;
	}
	
	public void resetPokemonStats(boolean center)
	{
		for(Pokemon pokemon: activePokemon)
		{
			if(pokemon == null) continue;
			pokemon.resetStats(center);
		}
	}
	public void resetPokemonStats()
	{
		resetPokemonStats(false);
	}
	
	public void setTrainerRef(SNpc npc)
	{
		this.trainerRef = npc;
	}
	
	public void addPokemon(Pokemon pokemon, boolean active)
	{
		pokemon.setPlayer(true);
		
		if(active)
			Util.insertValue(pokemon, activePokemon);
	}
	
	public void addPokemon(String name, int level, boolean active)
	{
		if(active)
			Util.insertValue(new Pokemon(name,level,true), activePokemon);
	}
	
	public void addBadge(String badgeName)
	{
		emblems[Item.EMBLEMS.get(badgeName)] = true;
	}
	
	public void setHealingPokemon(boolean healingPokemon)
	{
		this.healingPokemon = healingPokemon;
	}
	
	public void setBuying(boolean buying)
	{
		this.buying = buying;
	}
	
	public void setPewterEvent(boolean pewterForceEvent)
	{
		this.pewterForceEvent = pewterForceEvent;
	}
	
	public void setPewterMuseumEvent(boolean pewterMuseumEvent)
	{
		this.pewterMuseumEvent = pewterMuseumEvent;
	}
	
	public void setHasPayed(boolean hasPayed)
	{
		this.hasPayed = hasPayed;
	}
	
	public void setHome(String home)
	{
		this.home = home;
	}
	
	public void setForceJump(boolean forceJump)
	{
		this.forceJump = forceJump;
	}
	
	public void incrementEscapes()
	{
		nEscapes ++;
	}

	public void evolution(Pokemon plrActivePkmn, Pokemon evo)
	{
		for(int i = 0; i < 6; i ++)
			if(activePokemon[i] == plrActivePkmn)
			{
				activePokemon[i] = evo;
				Battle.playerActive = activePokemon[i];
				Battle.updateHuds();
			}
	}
	
	public void jump(int direction, Map map)
	{
		moving = true;
		jumping = true;
		surrTiles[direction] = map.getTileAt(surrTiles[direction].getX(),
				   surrTiles[2].getY() + Game.STDTSIZE);
		nextTile = surrTiles[direction];
	}
	
	public void returnHome(Map map)
	{	
		Graphics.tilemapRecolouring(map.getArea(), home);
		resetImages();
		
		map.changeMap(home,"default");
		x = GameInfo.getHomePos(home)[0];
		y = GameInfo.getHomePos(home)[1];
		
		surrTiles = new Tile[4];
		setNewTiles(map);
		
		for(int i = 0; i <= Util.getMaxIndex(activePokemon); i ++)
			activePokemon[i].resetStats(true);
		
		direction = 2;
	}
}
