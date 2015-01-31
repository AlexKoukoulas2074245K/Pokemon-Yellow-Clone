package util;

public class Counter {
	
	private int time;
	private boolean alive;
	
	public Counter(int time)
	{
		this.time = time;
		alive = true;
	}
	
	public void update()
	{
		time --;
		if(time <= 0)
			alive = false;
	}
	
	//Getters
	public int getTime()
	{
		return time;
	}
	
	public boolean getAlive()
	{
		return alive;
	}
	
	//Setters
	public void setAlive(boolean alive)
	{
		this.alive = alive;
	}
}
