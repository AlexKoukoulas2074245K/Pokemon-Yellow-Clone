package entities;

import java.awt.Graphics2D;

public class SObject extends Sprite{
	
	private String message;
	
	private int id;
	
	private boolean obtainable;
	private boolean solid;
	private boolean hasImage;
	private boolean hasMessage;
	private boolean canBeCut;
	private boolean isCut;
	private boolean obtained;

	public SObject(int id, int x,int y,String message,
					  boolean obtainable,boolean solid,
					  int[] imageCoords, Map world, boolean canBeCut)
	{
		super(x,y,false,true);
		
		this.id = id;
		world.getTileAt(x, y);
		
		this.message = message;
		this.obtainable = obtainable;
		this.solid = solid;
		this.canBeCut = canBeCut;
		obtained = false;
		
		hasMessage = !message.equals("%");
		isCut = false;
		if(imageCoords[0] != - 1 && imageCoords[1] != -1)
		{
			createImages(imageCoords,imageCoords,
					 	 imageCoords,imageCoords,
					 	 imageCoords,imageCoords);
			hasImage = true;
		}
		else
			hasImage = false;
	}
	
	//Getters
	
	public int getID()
	{
		return id;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public boolean hasMessage()
	{
		return hasMessage;
	}
	
	public boolean getObtainable()
	{
		return obtainable;
	}
	
	public boolean getHasImage()
	{
		return hasImage;
	}
	
	public boolean getSolid()
	{
		return solid;
	}
	
	public boolean getCanBeCut()
	{
		return canBeCut;
	}
	
	public boolean isCut()
	{
		return isCut;
	}
	
	public boolean getObtained()
	{
		return obtained;
	}
	
	//Setters
	public void setSolid(boolean solid)
	{
		this.solid = solid;
	}
	
	public void setObtained(boolean obtained)
	{
		this.obtained = obtained;
	}
	
	public void render(Graphics2D g)
	{
		if(hasImage)
			super.render(g);
	}
	
	public void setIsCut(boolean isCut)
	{
		this.isCut = isCut;
	}
}
