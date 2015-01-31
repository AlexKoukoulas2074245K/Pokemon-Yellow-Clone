package util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import main.Main;
import misc.Attack;
import misc.PokemonHud;
import battleStates.BSOptions;
import entities.Map;

public class Graphics {

	public static final Color REP_WHITE = new Color(255,255,255);
	public static final Color REP_BLACK = new Color(0,0,0);
	
	public static final Color UNI_BLUE = new Color(88,184,248);
	public static final Color WHITE = new Color(248,248,248);
	public static final Color BLACK = new Color(24,24,24);
	public static final Color PALLET = new Color(184,136,248);
	public static final Color GREEN = new Color(147,247,31);
	public static final Color PEWTER = new Color(144,144,120);
	public static final Color CAVE = new Color(184,64,0);
	public static final Color CAVE_UNI = new Color(136,112,88);
	public static final Color CERULEAN = new Color(40,64,248);
	public static final Color VERMILLION = new Color(248,152,0);
	public static final Color LAVENDER = new Color(200,32,248);
	public static final Color CINNABAR = new Color(248,64,64);

	public static HashMap<String,Color> areaColors;
	
	static
	{
		areaColors = new HashMap<String,Color>();
		areaColors.put("pallet", PALLET);
		areaColors.put("route1", GREEN);
		areaColors.put("viridian", GREEN);
		areaColors.put("route2",GREEN);
		areaColors.put("route2Cont",GREEN);
		areaColors.put("route22", GREEN);
		areaColors.put("viridianForest",GREEN);
		areaColors.put("pewter",PEWTER);
		areaColors.put("route3", GREEN);
		areaColors.put("mtmoon", CAVE);
	}
	
	public static void shiftToBlack(BufferedImage img, boolean town) 
	{	
		
		int xStart;
		int yStart;
		
		if((xStart = -(Map.xoffset/4)) < 0)
			town = false;
		if((yStart = - (Map.yoffset/4)) < 0)
			town = false;
		
	    ArrayList<Integer> colors = new ArrayList<Integer>();

	    if(!town)
	    {
	    	for(int i = 0; i < img.getRaster().getWidth(); i ++)
	    	{
	    		for(int k = 0; k < img.getRaster().getHeight(); k ++)
	    		{	
	    			if(!colors.contains(img.getRGB(i, k)))
	    				colors.add(img.getRGB(i,k));
	    		}
	    	}
	    }
	    
	    else
	    {
	    	xStart = -(Map.xoffset/4);
	    	yStart = -(Map.yoffset/4);
	    	int width  = Main.WIDTH/4;
	    	int height = Main.HEIGHT/4;
	    	
	    	if(xStart + width > img.getWidth())
	    		width = img.getWidth() - xStart;
	    	else if(yStart + height > img.getHeight())
	    		height = img.getHeight() - yStart;
	    	
	    	for(int i = xStart; i < xStart + width; i ++)
	    	{
	    		for(int y = yStart; y < yStart + height; y ++)
	    		{
	    			if(!colors.contains(img.getRGB(i, y)))
	    				colors.add(img.getRGB(i,y));
	    		}
	    	}
	    }
    
	    Collections.sort(colors);
	    Collections.reverse(colors);
	    
	    
	    if(!town)
	    {
	    	for(int i = 0; i < img.getRaster().getWidth(); i ++)
	    		for(int k = 0; k < img.getRaster().getHeight(); k ++)
	    		{
	    			int c = img.getRGB(i, k);
	    			if(c == 0) continue;
	    			int index = colors.indexOf(c);
	    			int c2 = c;
	    			if(index < colors.size() - 1)
	    				c2 = colors.get(index + 1);
	    			
	    			img.setRGB(i, k, c2);
	    		}
	    }
	    else
	    {
	    	xStart = -(Map.xoffset/4);
	    	yStart = -(Map.yoffset/4);
	    	int width  = Main.WIDTH/4;
	    	int height = Main.HEIGHT/4;
	    	
	    	if(xStart + width > img.getWidth())
	    		width = img.getWidth() - xStart;
	    	else if(yStart + height > img.getHeight())
	    		height = img.getHeight() - yStart;
	    	
	 
	    	for(int i = xStart; i < xStart + width; i ++)
	    	{
	    		for(int y = yStart; y < yStart + height; y ++)
	    		{
	    			int c = img.getRGB(i,y);
	    			if(c == 0) continue;
	    			int index = colors.indexOf(c);
	    			int c2 = c;
	    			if(index < colors.size() - 1)
	    				c2 = colors.get(index + 1);
	    			
	    			img.setRGB(i, y, c2);
	    		}
	    	}		
	    }
	    
	}
	
	public static int getNColors(BufferedImage image)
	{
		ArrayList<Integer> colorInts = new ArrayList<Integer>();
		
		for(int x = 0; x < image.getWidth(); x ++)
		{
			for(int y = 0; y < image.getHeight(); y ++)
			{
				int rgb = image.getRGB(x, y);
				if(colorInts.contains(rgb))
					continue;
				else
				{
					if(rgb != UNI_BLUE.getRGB() && rgb != BLACK.getRGB() && rgb != PALLET.getRGB() && rgb != WHITE.getRGB())
					{
						
					}
					colorInts.add(rgb);
				}
			}
		}
		
		return colorInts.size();
	}
	
	public static BufferedImage convertToARGB(BufferedImage image)
	{
	    BufferedImage newImage = new BufferedImage(
	        image.getWidth(), image.getHeight(),
	        BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = newImage.createGraphics();
	    g.drawImage(image, 0, 0, null);
	    g.dispose();
	    return newImage;
	}
	
	public static int getRGB(int[] rgb)
	{
		int result = rgb[0];
		result = (result << 8) + rgb[1];
		result = (result << 8) + rgb[2];
		
		return result;
	}
	public static int[] getRGB(int rgb)
	{
		int res[] = new int[3];
		
		res[0] = (rgb >> 16) & 0xFF;
		res[1] = (rgb >> 8) & 0xFF;
		res[2] = rgb & 0xFF;
		
		return res;
	}
	
	public static void removeTransparency(BufferedImage bi)
	{
		for(int i = 0; i < bi.getWidth(); i ++)
			for(int y = 0; y < bi.getHeight(); y ++)
				if(bi.getRGB(i, y) == -1)
					bi.setRGB(i, y, getRGB(new int[]{248,248,248}));
	}
	
	public static void adjustColors(BufferedImage bi)
	{
		for(int i = 0; i < bi.getWidth(); i ++)
			for(int y = 0; y < bi.getHeight(); y ++)
			{
				if(bi.getRGB(i, y) == -1)
					bi.setRGB(i, y, getRGB(new int[]{248,248,248}));
				else if(bi.getRGB(i, y) == getRGB(new int[]{0,0,0}))
					bi.setRGB(i, y, getRGB(new int[]{24,24,24}));
			}
	}
	
	public static BufferedImage deepCopy(BufferedImage bi)
	{
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null).getSubimage(0, 0, bi.getWidth(), bi.getHeight());
	}
	
	public static BufferedImage getImageCopy(BufferedImage image)
	{
		return image;
	}
	
	public static BufferedImage getVFlippedImage(BufferedImage image)
	{
		AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
		
		tx.translate(0, -image.getHeight(null));
		 
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		
		return op.filter(image, null);
	}
	
	public static BufferedImage getHFlippedImage(BufferedImage image)
	{
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		
		tx.translate(-image.getWidth(null), 0);
		 
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		
		return op.filter(image, null);
	}
	
	public static void drawMoveExps(Graphics2D g, Attack[] attacks,int xOrig,int yOrig, int cursorIndex)
	{
		g.setFont(PokemonHud.FONT);
		g.setColor(PokemonHud.BLACK);
		
		for(int i = 0; i < 4; i ++)
		{
			if(attacks[i] != null)
				g.drawString(attacks[i].getName(), xOrig + 186, yOrig + 209 + (i * 32));
			else
				g.fillRect(xOrig + 186, yOrig + 209 + (i * 34 - 15), 28, 4);
		}
		
		g.drawImage(BSOptions.cursor, xOrig + 156, yOrig + 182 + (cursorIndex * 32), null);
		g.drawString(attacks[cursorIndex].getType(), xOrig + 63, yOrig + 103);
		
		String pool = String.valueOf(attacks[cursorIndex].getPool());
		String maxPool = String.valueOf(attacks[cursorIndex].getMaxPool());
		
		g.drawString(pool, xOrig + 224 - 28 * pool.length(), yOrig + 140);
		g.drawString(maxPool, xOrig + 312 - 28 * maxPool.length(), yOrig + 140);
		
	}

	public static void changeColors(Color current, Color dest, BufferedImage image) 
	{

		for(int i = 0; i < image.getWidth(); i ++)
		{
			for(int y = 0; y < image.getHeight(); y ++)
			{
				if(image.getRGB(i, y) == current.getRGB())
				{
					image.setRGB(i,y, dest.getRGB());
				}
			}
		}
	}
	
	public static void tilemapRecolouring(String oldArea, String newArea)
	{
		Graphics.changeColors(areaColors.get(oldArea), areaColors.get(newArea), ResourceLoader.getTexture("overworld"));
		Graphics.changeColors(areaColors.get(oldArea), areaColors.get(newArea), ResourceLoader.getTexture("ambient1"));
	}
	
	public static void tilemapRecolouring(String newArea)
	{
		Graphics.changeColors(PALLET, areaColors.get(newArea), ResourceLoader.getTexture("overworld"));
		Graphics.changeColors(PALLET, areaColors.get(newArea), ResourceLoader.getTexture("ambient1"));
	}
}
