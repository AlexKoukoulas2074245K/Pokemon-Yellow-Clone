package entities;

import gameStates.Battle;
import gameStates.GSOverworld;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Game;
import util.Graphics;
import util.Input;
import util.ResourceLoader;
public abstract class Sprite {
	
	public static BufferedImage OVERWORLD_TM = ResourceLoader.getTexture("overworld");
	public static BufferedImage TRAINERS_TM = ResourceLoader.getTexture("trainers");
	public static BufferedImage TRAINERS_D_TM = ResourceLoader.getTexture("trainers_dark");
	public static BufferedImage JUMP_IMG = ResourceLoader.getTexture("jumpingV");
	protected static final int JUMP_COUNTER = 40;
	
	protected BufferedImage imageCopy;
	
	protected BufferedImage[] BATTLE_IMGS;
	protected BufferedImage[] SOUTH_IMGS;
	protected BufferedImage[] NORTH_IMGS;
	protected BufferedImage[] WEST_IMGS;
	protected BufferedImage[] EAST_IMGS;
	
	protected int imageIndex;
	protected int direction;
	protected int direction0;
	protected int jumpCounter;
	
	protected float x;
	protected float y;
	
	protected Tile nextTile;
	protected Tile currTile;
	protected Tile[] surrTiles;
	
	protected boolean isNpc;
	protected boolean isObject;
	protected boolean movingNpc;
	protected boolean isTrainer;
	protected boolean isGymLeader;
	protected boolean moving;
	protected boolean autoMove;
	protected boolean doorAutoMove;
	protected boolean talking;
	protected boolean jumping;
	protected boolean menuOpen;
	protected boolean wildAttack;
	protected boolean trainerAttack;
	protected boolean joyBow;
	
	protected int animationDelayMax;
	protected int animationDelay;
	
	public static BufferedImage getJoyBow()
	{
		return OVERWORLD_TM.getSubimage(7 * Game.STDTSIZE, 4 * Game.STDTSIZE, 
											Game.STDTSIZE, Game.STDTSIZE);
	}
	
	public Sprite(float x,float y,boolean isNpc, boolean isObject)
	{
		this.x = x;
		this.y = y;
		this.isNpc = isNpc;
		this.isObject = isObject;
		
		SOUTH_IMGS = new BufferedImage[4];
		NORTH_IMGS = new BufferedImage[4];
		WEST_IMGS = new BufferedImage[4];
		EAST_IMGS  = new BufferedImage[4];
		
		imageIndex = 0;
		direction = 2;
		talking = false;
		joyBow = false;
		jumpCounter = JUMP_COUNTER;
	}
	
	protected void createImages(int[] southCoords, int[] northCoords,
									int[] westCoords, int[] south1Coords,
									int[] north1Coords,  int[] west1Coords)
	{
		try
		{
			//create the south images
			BufferedImage southImage  = OVERWORLD_TM.getSubimage(southCoords[0] * Game.STDTSIZE, southCoords[1] * Game.STDTSIZE, Game.STDTSIZE, Game.STDTSIZE);
			BufferedImage south1Image = OVERWORLD_TM.getSubimage(south1Coords[0]* Game.STDTSIZE, south1Coords[1]* Game.STDTSIZE, Game.STDTSIZE, Game.STDTSIZE);
			BufferedImage south2Image = Graphics.getImageCopy(southImage);
			BufferedImage south3Image = Graphics.getHFlippedImage(south1Image);
			
			//add to the south image array
			SOUTH_IMGS[0] = southImage;
			SOUTH_IMGS[1] = south1Image;
			SOUTH_IMGS[2] = south2Image;
			SOUTH_IMGS[3] = south3Image;
			
			//create the north images
			BufferedImage northImage  = OVERWORLD_TM.getSubimage(northCoords[0]*Game.STDTSIZE, northCoords[1] * Game.STDTSIZE, Game.STDTSIZE, Game.STDTSIZE);
			BufferedImage north1Image = OVERWORLD_TM.getSubimage(north1Coords[0]*Game.STDTSIZE, north1Coords[1] * Game.STDTSIZE, Game.STDTSIZE, Game.STDTSIZE);
			BufferedImage north2Image = Graphics.getImageCopy(northImage);
			BufferedImage north3Image = Graphics.getHFlippedImage(north1Image);
			
			//add to the north image array
			NORTH_IMGS[0] = northImage;
			NORTH_IMGS[1] = north1Image;
			NORTH_IMGS[2] = north2Image;
			NORTH_IMGS[3] = north3Image;
			
			//create the west images
			BufferedImage westImage   = OVERWORLD_TM.getSubimage(westCoords[0] * Game.STDTSIZE, westCoords[1]* Game.STDTSIZE, Game.STDTSIZE, Game.STDTSIZE);
			BufferedImage west1Image  = OVERWORLD_TM.getSubimage(west1Coords[0] * Game.STDTSIZE, west1Coords[1] * Game.STDTSIZE, Game.STDTSIZE,Game.STDTSIZE);
			BufferedImage west2Image  = Graphics.getImageCopy(westImage);
			BufferedImage west3Image  = west1Image;
			
			//add to the west image array
			WEST_IMGS[0] = westImage;
			WEST_IMGS[1] = west1Image;
			WEST_IMGS[2] = west2Image;
			WEST_IMGS[3] = west3Image;
			
			//create the east images
			BufferedImage eastImage   = Graphics.getHFlippedImage(westImage);
			BufferedImage east1Image  = Graphics.getHFlippedImage(west1Image);
			BufferedImage east2Image  = Graphics.getHFlippedImage(west2Image);
			BufferedImage east3Image  = Graphics.getHFlippedImage(west3Image);
			
			//add to the east image array
			EAST_IMGS[0] = eastImage;
			EAST_IMGS[1] = east1Image;
			EAST_IMGS[2] = east2Image;
			EAST_IMGS[3] = east3Image;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected void createBattleImages(int[] coords)
	{
		
		BATTLE_IMGS = new BufferedImage[2];

		BATTLE_IMGS[0] = TRAINERS_TM.getSubimage(coords[0] * Battle.IMAGE_SIZE, coords[1] * Battle.IMAGE_SIZE,
												 Battle.IMAGE_SIZE, Battle.IMAGE_SIZE);
		BATTLE_IMGS[1] = TRAINERS_D_TM.getSubimage(coords[0] * Battle.IMAGE_SIZE, coords[1] * Battle.IMAGE_SIZE, 
												 Battle.IMAGE_SIZE, Battle.IMAGE_SIZE);
		
	}
	

	public void battleRender(Graphics2D g, int xDraw,int yDraw,boolean dark)
	{
		if(dark)
			g.drawImage(BATTLE_IMGS[1],xDraw,yDraw,null);
		else
			g.drawImage(BATTLE_IMGS[0],xDraw,yDraw,null);
	}
	
	public void render(Graphics2D g, boolean menuOpen, boolean playerTalking, boolean battleAni, boolean scriptedEvents) 
	{
		
		if(moving && !menuOpen && !playerTalking && !battleAni && !GSOverworld.changeEffect &&
					 !scriptedEvents && GSOverworld.textbox == null)	
			animation();
		
		else
			if((!Input.UP_KEY && !Input.DOWN_KEY && !Input.LEFT_KEY && !Input.RIGHT_KEY) || 
			   GSOverworld.changeEffect || talking || isNpc || menuOpen || wildAttack  ||
			   trainerAttack || isObject || playerTalking || battleAni || scriptedEvents ||
			   GSOverworld.textbox != null)
			{
				imageIndex = 0;
				animationDelay = animationDelayMax;
			}
			else
				animation();
		
		if(joyBow)
		{
			g.drawImage(getJoyBow(),(int) x + Map.xoffset, (int) y + Map.yoffset - Game.STDTSIZE/4, null);
			return;
		}
		
		if(!jumping)
		{
			if(direction == 0)
				g.drawImage(NORTH_IMGS[imageIndex], (int) x + Map.xoffset,(int) y + Map.yoffset - Game.STDTSIZE/4, null);
			else if(direction == 1)
				g.drawImage(EAST_IMGS[imageIndex], (int) x + Map.xoffset, (int) y + Map.yoffset - Game.STDTSIZE/4, null);
			else if(direction == 2)
				g.drawImage(SOUTH_IMGS[imageIndex], (int) x + Map.xoffset, (int) y + Map.yoffset- Game.STDTSIZE/4, null);
			else
				g.drawImage(WEST_IMGS[imageIndex], (int) x + Map.xoffset, (int) y + Map.yoffset - Game.STDTSIZE/4, null);
		}
		else
		{
			jumpCounter --;
			if(jumpCounter == 0)
				jumpCounter = JUMP_COUNTER;
			
			if(direction == 0)
				g.drawImage(NORTH_IMGS[imageIndex], (int) x + Map.xoffset,(int) y + Map.yoffset - Game.STDTSIZE/4, null);
			else if(direction == 1)
				g.drawImage(EAST_IMGS[imageIndex], (int) x + Map.xoffset, (int) y + Map.yoffset - Game.STDTSIZE/4, null);
			else if(direction == 2)
			{
				
				g.drawImage(JUMP_IMG,(int)x + Map.xoffset,(int)y + Map.yoffset + Game.STDTSIZE/4, null);
				
				if(jumpCounter < JUMP_COUNTER/2)
					g.drawImage(SOUTH_IMGS[imageIndex], (int) x + Map.xoffset, 
														(int) y + Map.yoffset- Game.STDTSIZE/4  -2*jumpCounter, null);
				else
					g.drawImage(SOUTH_IMGS[imageIndex], (int) x + Map.xoffset, 
														(int) y + Map.yoffset- Game.STDTSIZE/4 - 80 + 2*jumpCounter, null);
			}		
			else
				g.drawImage(WEST_IMGS[imageIndex], (int) x + Map.xoffset, 
												   (int) y + Map.yoffset - Game.STDTSIZE/4, null);
		}
		
	}
	
	public void render(Graphics2D g)
	{
		render(g,false,false,false, false);
	}
	
	private void animation()
	{
		animationDelay --;
		if(animationDelay <= 0)
		{

			animationDelay = animationDelayMax;
			imageIndex ++;
			if (imageIndex > 3)
				imageIndex = 0;
		}
	}

	//Getters
	public int getDirection() {
		return direction;
	}
	
	public boolean getTalking(){
		return talking;
	}
	
	public boolean getJumping()
	{
		return jumping;
	}
	
	public boolean isNpc() {
		return isNpc;
	}
	
	public boolean isObject()
	{
		return isObject;
	}

	public boolean getWildAttack()
	{
		return wildAttack;
	}
	
	public boolean getTrainerAttack()
	{
		return trainerAttack;
	}
	
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	
	public Tile getCurrTile()
	{
		return currTile;
	}
	
	public Tile getNextTile()
	{
		return nextTile;
	}
	
	public BufferedImage getCurrImage()
	{
		if(direction == 0)
			return NORTH_IMGS[imageIndex];
		else if(direction == 1)
			return EAST_IMGS[imageIndex];
		else if(direction == 2)
			return SOUTH_IMGS[imageIndex];
		else
			return WEST_IMGS[imageIndex];
	}
	
	public BufferedImage[] getBattleImages()
	{
		return BATTLE_IMGS;
	}

	//Setters
	public void setDirection(int direction) {
		this.direction = direction;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public void setCurrTile(Tile currTile)
	{
		this.currTile = currTile;
	}
	
	public void setTalking(boolean talking)
	{
		this.talking = talking;
	}

	public void setNpc(boolean isNpc) {
		this.isNpc = isNpc;
	}
	
	public void setJumping(boolean jumping)
	{
		this.jumping = jumping;
	}
	
	public void setJoyBow(boolean joyBow)
	{
		this.joyBow = joyBow;
	}
	
	public void setSurrTiles(int index, Tile tile)
	{
		surrTiles[index] = tile;
	}
	
	public void setNextTile(int i)
	{
		nextTile = surrTiles[i];
	}
	
	public void setCurrImage(BufferedImage image)
	{
		if(direction == 0)
			NORTH_IMGS[imageIndex] = image;
		else if(direction == 1)
			EAST_IMGS[imageIndex] = image;
		else if(direction == 2)
			SOUTH_IMGS[imageIndex] = image;
		else
			WEST_IMGS[imageIndex] = image;
	}
	
	public void setWildAttack(boolean wildAttack)
	{
		this.wildAttack = wildAttack;
	}
	
	public void setTrainerAttack(boolean trainerAttack)
	{
		this.trainerAttack = trainerAttack;
	}
}
