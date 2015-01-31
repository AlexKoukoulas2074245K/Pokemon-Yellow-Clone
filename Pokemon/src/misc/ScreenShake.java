package misc;

public class ScreenShake {
	
	public static final int LEFT = 0;
	public static final int LEFT_EXT = 1;
	public static final int RIGHT = 2;
	public static final int DOWN = 3;
	
	private static final int SHAKE_LEFT_MAX = 12;
	private static final int SHAKE_LEFT_DEC = 1;
	
	private static final int SHAKE_DOWN_MAX = 30;
	private static final int SHAKE_DOWN_DEC = 2;
	private static final int SHAKE_DOWN_INT = 6;
	
	private static final int SHAKE_RIGHT_MAX = 30;
	private static final int SHAKE_RIGHT_DEC = 2;
	private static final int SHAKE_RIGHT_INT = 6;
	
	private int x;
	private int y;
	
	private int type;
	
	private int phase;
	
	private boolean finished;
	private boolean dir;
	
	public ScreenShake(int type)
	{
		this.type = type;
		
		x = 0;
		y = 0;
		phase = 0;
		
		dir = true;
		finished = false;
		
		adjustToType();
	}
	
	private void adjustToType()
	{
		if(type == LEFT)
			x =  SHAKE_LEFT_MAX;
		else if(type == DOWN)
			y = SHAKE_DOWN_MAX;
		else if(type == RIGHT)
			x = SHAKE_RIGHT_MAX;
	}
	
	public void update()
	{
		if(type == LEFT)
		{
			x -= SHAKE_LEFT_DEC;
			if(x <= 0)
			{
				if(phase == 0)
				{
					x = SHAKE_LEFT_MAX/2;
					phase = 1;
				}
				else
				{
					x = 0;
					finished = true;
				}
			}
		}
		
		else if(type == LEFT_EXT)
		{
			if(dir)
			{
				x += SHAKE_LEFT_DEC * 5/2;
				if(x >= SHAKE_LEFT_MAX * 2)
				{
					dir = false;
					phase ++;
				}
			}
			else
			{
				x -= SHAKE_LEFT_DEC * 5/2;
				if(x <= 0)
				{
					dir = true;
					phase ++;
					if(phase == 4)
					{
						finished = true;
						x = 0;
					}
				}
			}
		}
		
		else if(type == DOWN)
		{
			if(y > 0)
			{
				y -= SHAKE_DOWN_DEC;
			}
			else
			{
				phase ++;
				
				if(phase == 5)
				{
					finished = true;
					return;
				}
				y = SHAKE_DOWN_MAX - (SHAKE_DOWN_INT * phase);
			}
		}
		
		else if(type == RIGHT)
		{
			if(x > 0)
			{
				x -= SHAKE_RIGHT_DEC;
			}
			else
			{
				phase ++;
				if(phase == 5)
				{
					x = 0;
					finished = true;
					return;
				}
				x = SHAKE_RIGHT_MAX - (SHAKE_RIGHT_INT * phase);
			}
		}
	}
	
	public boolean getFinished()
	{
		return finished;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
}
