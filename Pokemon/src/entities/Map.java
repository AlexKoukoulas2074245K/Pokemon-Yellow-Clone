package entities;

import gameStates.GSOverworld;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import main.Game;
import main.Main;
import util.GameInfo;
import util.Graphics;
import util.ResourceLoader;
import util.WildInfo;
import util.Wrap;

public class Map {
	
	public static int xoffset = 0;
	public static int yoffset = 0;
	
	private ResourceLoader rl;
	
	private ArrayList<Ambient> ambientEntities;
	private ArrayList<Ambient> seaBlocks;
	private ArrayList<Wrap> wraps;
	private ArrayList<SNpc> npcs;
	private ArrayList<SObject> objects;
	private ArrayList<WildInfo> wildEncounters;
	
	private GSOverworld worldRef;
	private SPlayer player;
	private String area;
	private String building;
	
	//the area map is a special PNG containing details
	//about the area structure
	private BufferedImage areaMap;
	private BufferedImage areaImage;
	
	private Tile[][] tilemap;

	private int width;
	private int height;
	
	private int encDensity;
	
	public static int[] findColRow(String inpArea, String inpBuilding, int[] rgb)
	{
		BufferedImage townMap = ResourceLoader.getTownMap(inpArea,inpBuilding);
		
		int[] result = new int[2];
		
		for (int y = 0; y < townMap.getHeight(); y++) {
		    for (int x = 0; x < townMap.getWidth(); x++) {
		    	
		    	int[] pixel = townMap.getRaster().getPixel(x,y,new int[3]);
		       
		    	if(pixel[0] == rgb[0] && pixel[1] == rgb[1] && pixel[2] == rgb[2])
		    	{
		    		result[0] = x;
		    		result[1] = y;
		    		return result;
		    	}
		    }
		}
		
		return result;
	}
	
	public Map(String area, String building, SPlayer player, ResourceLoader rl, GSOverworld worldRef)
	{
		this.player = player;
		this.area = area;
		this.building = building;
		this.rl = rl;
		this.worldRef = worldRef;
		
		areaImage = ResourceLoader.getImage(area,building);
		areaMap = ResourceLoader.getTownMap(area,building);
		
		width = areaImage.getWidth();
		height = areaImage.getHeight();
		
		ambientEntities = new ArrayList<Ambient>();

		seaBlocks = new ArrayList<Ambient>();
		wraps = new ArrayList<Wrap>();
		
		
		createTileMap();
		
		getAreaNpcs();
		getAreaObjects();
		getWildEncounters();
	}
	
	private void createTileMap()
	{
		tilemap = new Tile[areaMap.getWidth()][areaMap.getHeight()];
		
		int[] pixel;

		for (int y = 0; y < areaMap.getHeight(); y++) {
		    for (int x = 0; x < areaMap.getWidth(); x++) {
		    	
		    	pixel = areaMap.getRaster().getPixel(x,y,new int[3]);
		       
		    	createTile(pixel,x,y);	
		    }
		}
				
	}
	
	private void getAreaNpcs()
	{
		npcs = rl.getNpcs(area, building,this);
		
		checkForRemoval();
		
		for(SNpc npc: npcs)
		{
			if(!worldRef.getDefeatedTrainers().containsKey(area + " " + building))
				return;
			
			if(worldRef.getDefeatedTrainers().get(area + " " + building).contains(npc.getID()))
				npc.setDefeated(true);
			
			if(npc.getIsGymLeader() && building.equals("pewterGym"))
				npc.setMessage(GameInfo.BROCKAFTERSPEECH);
		}		
		
	}
	
	private void checkForRemoval()
	{
		for(int i = 0; i < npcs.size(); i ++)
		{
			//GARY route22 1st
			if(area.equals("route22") && npcs.get(i).getAlreadyDef())
			{
				npcs.remove(npcs.get(i));
				i --;
			}
		}
		
		if(player.getEmblems()[0])
			removePewterGuy();
	}
	
	private void getAreaObjects()
	{
		objects = rl.getObjects(area, building,this);
		
		checkForAdditions();
		
		for(int i = 0; i < objects.size(); i ++)
		{
			if(!worldRef.getObtainedItems().containsKey(area + " " + building))
				return;
			
			if(worldRef.getObtainedItems().get(area + " " + building).contains(objects.get(i).getID()))
			{
				objects.remove(i);
				i --;
			}
		}
	}
	
	private void createTile(int[] pixel, int col, int row)
	{
		Tile tile;
		if(pixel[0] == 0 && pixel[1] == 0 && pixel[2] == 0)
			tile = new Tile(col,row,true,false);
		
		else if(pixel[0] == 255 && pixel[1] == 255 && pixel[2] == 255)
			tile = new Tile(col,row,false,false);
		
		else if(pixel[0] == 64 && pixel[1] == 64 && pixel[2] == 64)
			tile = new Tile(col,row,false,true);
		
		else if(pixel[0] == 255 && pixel[1] == 255 && pixel[2] == 0)
			tile = new Tile(col,row,false,false,true);
		else
			tile = new Tile(col,row,false,false);
		
		checkForAmbient(pixel,col,row);
		checkForWrap(pixel,col,row);
		
		tilemap[col][row] = tile;
		
		addSeaBlock(col,row);
	}
	
	private void checkForAmbient(int[] pixel, int col, int row)
	{
		if ((pixel[0] == 0 && pixel[1] == 128 && pixel[2] == 0) ||
			(pixel[0] == 0 && pixel[1] == 255 && pixel[2] == 0))
			
			ambientEntities.add(new Ambient(pixel,col,row));
			
	}
	
	private void checkForWrap(int[] pixel,int col,int row)
	{
		if((pixel[0] == 0    && pixel[1] == 0 && pixel[2] == 255) ||
			(pixel[0] == 0   && pixel[1] == 128 && pixel[2] == 255)||
			(pixel[0] == 0   && pixel[1] == 128 && pixel[2] == 228) ||
			(pixel[0] == 0   && pixel[1] == 128 && pixel[2] == 200) ||
			(pixel[0] == 0   && pixel[1] == 128 && pixel[2] == 150) ||
			(pixel[0] == 0   && pixel[1] == 128 && pixel[2] == 128) ||
			(pixel[0] == 0   && pixel[1] == 128 && pixel[2] == 100) ||
			(pixel[0] == 0   && pixel[1] == 128 && pixel[2] == 50) ||
			(pixel[0] == 0   && pixel[1] == 128 && pixel[2] == 25) ||
			(pixel[0] == 0   && pixel[1] == 0 && pixel[2] == 200) ||
			(pixel[0] == 20  && pixel[1] == 0 && pixel[2] == 255) ||
			(pixel[0] == 20  && pixel[1] == 0 && pixel[2] == 200) ||
			(pixel[0] == 0   && pixel[1] == 0 && pixel[2] == 150) ||
			(pixel[0] == 0   && pixel[1] == 0 && pixel[2] == 100) ||
			(pixel[0] == 20  && pixel[1] == 0 && pixel[2] == 100) ||
			(pixel[0] == 0   && pixel[1] == 0 && pixel[2] == 50) ||
			(pixel[0] == 20  && pixel[1] == 0 && pixel[2] == 50) ||
			(pixel[0] == 0   && pixel[1] == 0 && pixel[2] == 25) ||
			(pixel[0] == 0   && pixel[1] == 0 && pixel[2] == 20) ||
			(pixel[0] == 0   && pixel[1] == 0 && pixel[2] == 10) ||
			(pixel[0] == 255 && pixel[1] == 0 && pixel[2] == 0) ||
			(pixel[0] == 254 && pixel[1] == 0 && pixel[2] == 0) ||
			(pixel[0] == 248 && pixel[1] == 0 && pixel[2] == 0) ||
			(pixel[0] == 240 && pixel[1] == 0 && pixel[2] == 0) ||
			(pixel[0] == 200 && pixel[1] == 0 && pixel[2] == 0) ||
			(pixel[0] == 160 && pixel[1] == 0 && pixel[2] == 0) ||
			(pixel[0] == 148 && pixel[1] == 0 && pixel[2] == 0) ||
			(pixel[0] == 128 && pixel[1] == 0 && pixel[2] == 0) ||
			(pixel[0] == 120 && pixel[1] == 0 && pixel[2] == 0))
			
				wraps.add(new Wrap(area,building,pixel,col,row));	
	}
	
	private void addSeaBlock(int col,int row)
	{
		if(building.equals("default"))
			seaBlocks.add(new Ambient(new int[]{256,256,256},col,row));
	}
	
	private void checkForAdditions()
	{
		if(building.equals("pewterMuseum") && !player.getBag().itemInBag("OLD AMBER"))
			objects.add(new SObject(-1,17 * Game.STDTSIZE, 3 * Game.STDTSIZE,
					"The AMBER is#clear and gold!",true,false,new int[]{5,9},this,false));
	}
	
	public void removeItems()
	{
		if(player.getBag().itemInBag("OLD AMBER"))
			removeObject(-1);
	}
	
	private void removeObject(int id)
	{
		for(int i = 0; i < objects.size(); i ++)
		{
			if(objects.get(i).getID() == id)
			{
				objects.remove(i);
				i--;
			}
		}
	}
	
	private void getWildEncounters()
	{
		
		if(!(area.equals("route1") || area.equals("route2") || area.equals("route22") ||
		   area.equals("route2Cont") || area.equals("route3") || area.equals("viridianForest")))
			return;
		
		InputStream in = getClass().getResourceAsStream("/info/wildEncounters/" + area + ".txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		String line;

		wildEncounters = new ArrayList<WildInfo>();
		
		try
		{
			line = br.readLine();
			encDensity = Integer.parseInt(line);
			
			while((line = br.readLine()) != null)
			{
				String[] components = line.split("\\s+");
				
				int rate = Integer.parseInt(components[0]);
				int level = Integer.parseInt(components[1]);
				String name = components[2];
				
				wildEncounters.add(new WildInfo(name,level,rate));
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void changeMap(Wrap givenWrap)
	{
		Wrap destWrap = givenWrap.findWrapExit();
	
		area = destWrap.getArea();
		building = destWrap.getBuilding();
		
		changeMap(area,building);
	}
	
	public void changeMap(String area, String building)
	{
		this.area = area;
		this.building = building;
		
		areaImage = ResourceLoader.getImage(area,building);
		areaMap = ResourceLoader.getTownMap(area,building);
		
		width = areaImage.getWidth();
		height = areaImage.getHeight();
		
		ambientEntities = new ArrayList<Ambient>();
		wraps = new ArrayList<Wrap>();
		seaBlocks = new ArrayList<Ambient>();
		
		xoffset = 0;
		yoffset = 0;
	
		createTileMap();
		
		getAreaNpcs();
		getAreaObjects();
		getWildEncounters();
	}
	
	public void defeatAll()
	{
		for(SNpc npc: npcs)
		{
			if(npc.getIsGymLeader()) continue;
			
			npc.setDefeated(true);
		}
	}
	
	public void update()
	{
		
		xoffset = Main.WIDTH/2 - (int)player.getX();
		yoffset = Main.HEIGHT/2 - (int)player.getY();
		
		
		for(SNpc npc: npcs)
		{
			if(player.getTrainerAttack())
			{
				if(player.getTrainerRef() == npc)
					npc.attackUpdate(this, player);
				else
					continue;
			}
			
			else
			{
				npc.update(this,player);
				
				if(!player.getTalking())
					npc.setTalking(false);
			}
		}
	}
	
	public SNpc getTrainerAttack()
	{
		for(SNpc npc: npcs)
		{
			if(npc.getAlreadyDef())
				continue;
			if(!npc.getIsTrainer() || npc.getIsGymLeader())
				continue;
			if(!vicinityCheck(npc))
				continue;
			
			return npc;
		}	
		
		return null;
	}
	
	private boolean vicinityCheck(SNpc npc)
	{
		Tile playerTile = getTileAt((int)player.getX(),(int)player.getY());
		Tile npcTile = npc.getCurrTile();
		
		if(npc.getDirection() == 0)
			return (playerTile.getRow() < npcTile.getRow() && 
					npcTile.getRow() - playerTile.getRow() <= SNpc.TRAINER_VICINITY &&
					npcTile.getCol() == playerTile.getCol() && noObstacles(npc));
			
		
		else if(npc.getDirection() == 1)
			return (playerTile.getCol() > npcTile.getCol() &&
					playerTile.getCol() - npcTile.getCol() <= SNpc.TRAINER_VICINITY &&
					npcTile.getRow() == playerTile.getRow() && noObstacles(npc));
					
			
		else if(npc.getDirection() == 2)
			return (playerTile.getRow() > npcTile.getRow() &&
					playerTile.getRow() - npcTile.getRow() <= SNpc.TRAINER_VICINITY &&
					npcTile.getCol() == playerTile.getCol() && noObstacles(npc));
		
		else
			return (playerTile.getCol() < npcTile.getCol() &&
					npcTile.getCol() - playerTile.getCol() <= SNpc.TRAINER_VICINITY &&
					npcTile.getRow() == playerTile.getRow() && noObstacles(npc));

	}
	
	private boolean noObstacles(SNpc npc)
	{
		if(npc.getDirection() == 0)
		{
			for(int y = (int)player.getY(); y <= npc.getCurrTile().getY(); y += Game.STDTSIZE)
			{
				if(getTileAt(npc.getCurrTile().getX(),y).getLedge() ||
				   getTileAt(npc.getCurrTile().getX(),y).getSolid())
					return false;
			}
			
			return true;
		}
		
		else if(npc.getDirection() == 1)
		{
			for(int x = npc.getCurrTile().getX(); x <= (int)player.getX(); x += Game.STDTSIZE)
			{
				if(getTileAt(x,npc.getCurrTile().getY()).getLedge() ||
				   getTileAt(x,npc.getCurrTile().getY()).getSolid())
					return false;
			}
			
			return true;
		}
		
		else if(npc.getDirection() == 2)
		{
			for(int y = npc.getCurrTile().getY(); y < (int)player.getY(); y += Game.STDTSIZE)
			{
				if(getTileAt(npc.getCurrTile().getX(),y).getLedge() ||
				   getTileAt(npc.getCurrTile().getX(),y).getSolid())
					return false;
			}
			
			return true;
		}
		
		else
		{
			for(int x = (int)player.getX(); x <= npc.getCurrTile().getX(); x += Game.STDTSIZE)
			{
				if(getTileAt(x,npc.getCurrTile().getY()).getLedge() ||
				   getTileAt(x,npc.getCurrTile().getY()).getSolid())
					return false;
			}
			
			return true;
		}
	}
	
	public Tile getNextTile(SNpc npc)
	{
		if(npc.getDirection() == 0)
			return getTileAt((int)player.getX(), (int)player.getY() + Game.STDTSIZE);
		else if(npc.getDirection() == 1)
			return getTileAt((int)player.getX() - Game.STDTSIZE, (int)player.getY());
		else if(npc.getDirection() == 2)
			return getTileAt((int)player.getX(), (int)player.getY() - Game.STDTSIZE);
		else
			return getTileAt((int)player.getX() + Game.STDTSIZE, (int)player.getY());
	}
	
	public void shiftEntities()
	{
		for(Ambient amb: ambientEntities)
			Graphics.shiftToBlack(amb.getCurrImage(),false);
		
		for(SObject object: objects)
			if(object.getHasImage())
				Graphics.shiftToBlack(object.getCurrImage(), false);
		
		for(SNpc npc: npcs)
			Graphics.shiftToBlack(npc.getCurrImage(), false);
	}
	
	public void stopMovement()
	{
		for(SNpc npc: npcs)
			npc.setMoving(false);
	}
	
	public void removeObtainable()
	{
		for(int i = 0; i < objects.size(); i ++)
		{
			if(objects.get(i).getObtainable() && objects.get(i).getObtained())
			{
				objects.remove(i);
				i --;
			}
		}
	}
	
	public void render(Graphics2D g, boolean menuOpen, boolean playerTalking, boolean battleAni, boolean scriptedEvents)
	{
		if(!GSOverworld.changeEffect)
			for(Ambient seaBlock: seaBlocks)
				seaBlock.render(g, areaImage.getWidth());
					
		g.drawImage(areaImage, xoffset, yoffset , areaImage.getWidth() * 4, areaImage.getHeight() * 4, null);
		
		for(Ambient amb: ambientEntities)
			amb.render(g,areaImage.getWidth());
		
		for(SObject object: objects)
			object.render(g, menuOpen, playerTalking, battleAni, scriptedEvents);
		
		for(SNpc npc: npcs)
			npc.render(g, menuOpen, playerTalking, battleAni, scriptedEvents);
		
		for(SNpc npc: npcs)
			npc.drawGrass(g);
	}
	
	public boolean solidObjectAt(Tile tile)
	{
		for(SObject object: objects)
			if(object.getX() == tile.getX() && 
			   object.getY() == tile.getY() &&
			   object.getSolid())
				
				return true;
		
		return false;
	}
	
	public boolean objectAt(int x,int y)
	{
		for(SObject object: objects)
			if(object.getX() == x && object.getY() == y)
				return true;
		return false;
	}
	
	public void resetNpcTalking()
	{
		for(SNpc npc: npcs)
			if(npc.getTalking() && !npc.getMovingNpc())
				npc.resetPosition();	
	}
	
	public void resetObjectTalking()
	{
		for(SObject object: objects)
			if(object.getTalking())
				object.setTalking(false);
	}
	
	public boolean npcAt(int x,int y)
	{
		for(SNpc npc: npcs)
			if(npc.getX() == x && npc.getY() == y)
				return true;
	
		return false;
	}
	
	public boolean npcCurrTile(Tile tile)
	{
		for(SNpc npc: npcs)
			if(npc.getCurrTile() == tile)
				return true;
		return false;
	}
	
	public boolean npcNextTile(Tile tile)
	{
		for(SNpc npc: npcs)
			if(npc.getNextTile() == tile)
				return true;
		return false;
	}

	//Getters
	public SNpc getNpc(int x,int y)
	{
		for(SNpc npc: npcs)
			if(npc.getX() == x && npc.getY() == y)
				return npc;
		return null;
	}
	
	public GSOverworld getWorldRef()
	{
		return worldRef;
	}
	
	public ArrayList<SNpc> getNpcs()
	{
		return npcs;
	}
	
	public SNpc getNpc(int id)
	{
		for(SNpc npc: npcs)
			if(npc.getID() == id)
				return npc;
		
		return null;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public Tile getTileAt(int x,int y)
	{
		int col = x/Game.STDTSIZE;
		int row = y/Game.STDTSIZE;
		
		if(col >= tilemap.length || row >= tilemap[0].length)
			return null;
		
		return tilemap[col][row];
	}
	
	public Wrap getWrap(int col,int row)
	{
		for(Wrap wrap: wraps)
			if(wrap.getCol() == col && wrap.getRow() == row)
				return wrap;
		return null;
	}
	public String getArea() {
		return area;
	}

	public String getBuilding() {
		return building;
	}

	public boolean wrapAt(int col,int row)
	{
		for(Wrap wrap: wraps)
			if(wrap.getCol() == col && wrap.getRow() == row)
				return true;
		return false;
	}
	
	public SObject getObject(int x,int y)
	{
		for(SObject object: objects)
			if(object.getX() == x && object.getY() == y)
				return object;
		
		return null;
	}
	
	public SNpc getPewterGuy()
	{
		for(SNpc npc: npcs)
		{
			if(npc.getMessage().equals("If you have the#right stuff, go#take on BROCK!"))
				return npc;
		}
		
		return null;
	}
	
	public BufferedImage getImage()
	{
		return areaImage;
	}
	
	public int getDensity()
	{
		return encDensity;
	}
	
	public Pokemon getEncounter()
	{
		int enc = new Random().nextInt(256);
		
		int sum = 0;
		
		for(WildInfo wi: wildEncounters)
		{
			sum += wi.getRate();
			
			if(sum >= enc)
				return new Pokemon(wi.getName(), wi.getLevel(), false);
		}
		
		return null;
	}
	
	public boolean getWildAttack()
	{
		int res = new Random().nextInt(256);
		return res < encDensity;
	}
	
	//Setters
	public void setWidth(int width) 
	{
		this.width = width;
	}

	public void setHeight(int height) 
	{
		this.height = height;
	}
	
	public void removePewterGuy()
	{
		npcs.remove(getPewterGuy());
	}
}
