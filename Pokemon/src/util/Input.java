package util;

import java.awt.event.KeyEvent;

public class Input {
	
	public static boolean UP_KEY = false;
	public static boolean DOWN_KEY = false;
	public static boolean LEFT_KEY = false;
	public static boolean RIGHT_KEY = false;
	public static boolean START_KEY = false;
	public static boolean SELECT_KEY = false;
	public static boolean A_KEY = false;
	public static boolean B_KEY = false;
	
	public static boolean UP_TAPPED = false;
	public static boolean DOWN_TAPPED = false;
	public static boolean LEFT_TAPPED = false;
	public static boolean RIGHT_TAPPED = false;
	public static boolean START_TAPPED = false;
	public static boolean SELECT_TAPPED = false;
	public static boolean A_TAPPED = false;
	public static boolean B_TAPPED = false;
	
	public static boolean UP_KEY0 = true;
	public static boolean DOWN_KEY0 = true;
	public static boolean LEFT_KEY0 = true;
	public static boolean RIGHT_KEY0 = true;
	public static boolean START_KEY0 = true;
	public static boolean SELECT_KEY0 = true;
	public static boolean A_KEY0 = true;
	public static boolean B_KEY0 = true;
	
	public static void keyPress(KeyEvent key)
	{
		
		//check for key pressing
		if (key.getKeyCode() == KeyEvent.VK_UP)
		{
			UP_KEY = true;
			if(UP_KEY0)
			{
				UP_TAPPED = true;
				UP_KEY0 = false;
			}
		}	
		if (key.getKeyCode() == KeyEvent.VK_DOWN)
		{
			DOWN_KEY = true;
			if(DOWN_KEY0)
			{
				DOWN_TAPPED = true;
				DOWN_KEY0 = false;
			}
		}
		if (key.getKeyCode() == KeyEvent.VK_LEFT)
		{
			LEFT_KEY = true;
			if(LEFT_KEY0)
			{
				LEFT_TAPPED = true;
				LEFT_KEY0 = false;
			}
		}
		if (key.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			RIGHT_KEY = true;
			if(RIGHT_KEY0)
			{
				RIGHT_TAPPED = true;
				RIGHT_KEY0 = false;
			}
		}
		if (key.getKeyCode() == KeyEvent.VK_ENTER)
		{
			START_KEY = true;
			if(START_KEY0)
			{
				START_TAPPED = true;
				START_KEY0 = false;
			}
		}
		if (key.getKeyCode() == KeyEvent.VK_BACK_SPACE)
		{
			SELECT_KEY = true;
			if(SELECT_KEY0)
			{
				SELECT_TAPPED = true;
				SELECT_KEY0 = false;
			}
		}
		if (key.getKeyCode() == KeyEvent.VK_A)
		{
			A_KEY = true;
			if(A_KEY0)
			{
				A_TAPPED = true;
				A_KEY0 = false;
			}
		}
		if (key.getKeyCode() == KeyEvent.VK_D)
		{
			B_KEY = true;
			if(B_KEY0)
			{
				B_TAPPED = true;
				B_KEY0 = false;
			}
		}
	}
	
	public static void resetTapped()
	{
		if(UP_TAPPED)
			UP_TAPPED = false;
		if(DOWN_TAPPED)
			DOWN_TAPPED = false;
		if(LEFT_TAPPED)
			LEFT_TAPPED = false;
		if(RIGHT_TAPPED)
			RIGHT_TAPPED = false;
		if(START_TAPPED)
			START_TAPPED = false;
		if(SELECT_TAPPED)
			SELECT_TAPPED = false;
		if(A_TAPPED)
			A_TAPPED = false;
		if(B_TAPPED)
			B_TAPPED = false;
	}
	
	public static void keyRelease(KeyEvent key)
	{
		if (key.getKeyCode() == KeyEvent.VK_UP)
		{
			UP_KEY = false;
			UP_TAPPED = false;
			UP_KEY0 = true;
		}
		if (key.getKeyCode() == KeyEvent.VK_DOWN)
		{
			DOWN_KEY = false;
			DOWN_TAPPED = false;
			DOWN_KEY0 = true;
		}
		if (key.getKeyCode() == KeyEvent.VK_LEFT)
		{
			LEFT_KEY = false;
			LEFT_TAPPED = false;
			LEFT_KEY0 = true;
		}
		if (key.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			RIGHT_KEY = false;
			RIGHT_TAPPED = false;
			RIGHT_KEY0 = true;
		}
		if (key.getKeyCode() == KeyEvent.VK_ENTER)
		{
			START_KEY = false;
			START_TAPPED = false;
			START_KEY0 = true;
		}
		if (key.getKeyCode() == KeyEvent.VK_BACK_SPACE)
		{
			SELECT_KEY = false;
			SELECT_TAPPED = false;
			SELECT_KEY0 = true;
		}
		if (key.getKeyCode() == KeyEvent.VK_A)
		{
			A_KEY = false;
			A_TAPPED = false;
			A_KEY0 = true;
		}
		if (key.getKeyCode() == KeyEvent.VK_D)
		{
			B_KEY = false;
			B_TAPPED = false;
			B_KEY0 = true;
		}
	}
}
