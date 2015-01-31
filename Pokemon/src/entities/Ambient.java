package entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Game;
import util.ResourceInfo;
import util.ResourceLoader;

public class Ambient {
	
	protected static final int MAX_FLUX = Game.STDTSIZE/4;
	
	public static BufferedImage ambientTilemap = ResourceLoader.getTexture("ambient1");
	
	protected int[] rgb;
	
	protected BufferedImage[] images;
	protected int x;
	protected int y;
	
	protected float fluxOffset;
	protected boolean fluxLeft;
	
	protected int imageIndex;
	protected int animDelayMax;
	protected int animDelay;
	
	public static BufferedImage getGrassImage()
	{
		return ambientTilemap.getSubimage(ResourceInfo.GRASS[0] * Game.STDTSIZE, 
										  ResourceInfo.GRASS[1] * Game.STDTSIZE,
										  Game.STDTSIZE,
										  Game.STDTSIZE);
	}
	
	public Ambient(int[] rgb, int col,int row)
	{
		this.rgb = rgb;

		x = col * Game.STDTSIZE;
		y = row * Game.STDTSIZE;
		
		imageIndex = 0;
		animDelayMax = 60;
		animDelay = animDelayMax;
		
		getImages();
		
		fluxOffset = 0;
		fluxLeft = true;
	}
	
	private void getImages()
	{
		
		if(rgb[0] == 0 && rgb[1] == 128 && rgb[2] == 0)
		{
			images = new BufferedImage[3];
			images[0] = ResourceLoader.extractFromTileMap(ResourceInfo.FLOWERBR1[0], ResourceInfo.FLOWERBR1[1], ambientTilemap);
			images[1] = ResourceLoader.extractFromTileMap(ResourceInfo.FLOWERBR2[0], ResourceInfo.FLOWERBR2[1], ambientTilemap);
			images[2] = ResourceLoader.extractFromTileMap(ResourceInfo.FLOWERBR3[0], ResourceInfo.FLOWERBR3[1], ambientTilemap);
		}
		
		else if(rgb[0] == 0 && rgb[1] == 255 && rgb[2] == 0)
		{
			images = new BufferedImage[3];
			images[0] = ResourceLoader.extractFromTileMap(ResourceInfo.FLOWERTL1[0], ResourceInfo.FLOWERTL1[1], ambientTilemap);
			images[1] = ResourceLoader.extractFromTileMap(ResourceInfo.FLOWERTL2[0], ResourceInfo.FLOWERTL2[1], ambientTilemap);
			images[2] = ResourceLoader.extractFromTileMap(ResourceInfo.FLOWERTL3[0], ResourceInfo.FLOWERTL3[1], ambientTilemap);
		}
		
		else if(rgb[0] == 256 && rgb[1] == 256 && rgb[2] == 256)
		{
			images = new BufferedImage[1];
			images[0] = ResourceLoader.extractFromTileMap(ResourceInfo.SEA[0], ResourceInfo.SEA[1], ambientTilemap);
		}
	}
	
	public void render(Graphics2D g, int mapWidth)
	{
		animation();
		
		if(!(x  + (int)fluxOffset < 1 || x + (int)fluxOffset + Game.STDTSIZE > mapWidth - 1))
			return;
		
		g.drawImage(images[imageIndex], x + Map.xoffset + (int)fluxOffset, y + Map.yoffset, null);
	}
	
	private void animation()
	{
		animDelay --;
		if(animDelay <= 0)
		{
			animDelay = animDelayMax;
			imageIndex ++;
			
			if(imageIndex > images.length - 1)
				imageIndex = 0;
		}
		
		if(rgb[0] == 256 && rgb[1] == 256 && rgb[2] == 256)
		{
			if(fluxLeft)
			{
				fluxOffset -= 0.1f;
				if(fluxOffset <= - MAX_FLUX)
					fluxLeft = false;
			}
			else
			{
				fluxOffset += 0.1f;
				if(fluxOffset >= MAX_FLUX)
					fluxLeft = true;
			}
		}
	}

	//Getters
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int[] getRGB()
	{
		return rgb;
	}
	
	public BufferedImage getCurrImage()
	{
		return images[imageIndex];
	}
	

	//Setters
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
	
}
