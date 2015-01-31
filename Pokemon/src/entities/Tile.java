package entities;

import main.Game;

public class Tile {
	
	private final int SIZE = Game.STDTSIZE;
	
	private int col;
	private int row;
	
	private int x;
	private int y;
	
	private boolean solid;
	private boolean ledge;
	private boolean encounter;
	
	public Tile(int col, int row, boolean solid, boolean ledge, boolean encounter)
	{
		this.col = col;
		this.row = row;
		
		this.x = col * SIZE;
		this.y = row * SIZE;
		
		this.solid = solid;
		this.ledge = ledge;
		this.encounter = encounter;
	}
	
	public Tile(int col, int row, boolean solid, boolean ledge)
	{
		this(col, row, solid, ledge, false);
	}
	
	//Getters
	public int getX() 
	{
		return x;
	}

	public int getY() 
	{
		return y;
	}
	
	public boolean getSolid()
	{
		return solid;
	}
	
	public boolean getLedge()
	{
		return ledge;
	}
	
	public boolean getEncounter()
	{
		return encounter;
	}
	
	public int getCol()
	{
		return col;
	}
	
	public int getRow()
	{
		return row;
	}
	
	//Setters
	public void setX(int x) 
	{
		this.x = x;
	}

	public void setY(int y) 
	{
		this.y = y;
	}
	
	public void setSolid(boolean solid)
	{
		this.solid = solid;
	}
	
	public void setCol(int col)
	{
		this.col = col;
	}
	
	public void setRow(int row)
	{
		this.row = row;
	}

	
	
}
