package events;

import java.awt.Graphics2D;

import entities.Textbox;

public abstract class ScriptedEvent {
	
	protected boolean finished;
	protected boolean ownDrawn;
	
	public static Textbox textbox;
	
	public static void destroyTextbox()
	{
		textbox = null;
	}
	
	public void update()
	{
		
	}
	
	public void render(Graphics2D g)
	{
		
	}
	
	public boolean isFinished()
	{
		return finished;
	}
	
	public boolean isOwnDrawn()
	{
		return ownDrawn;
	}
}
