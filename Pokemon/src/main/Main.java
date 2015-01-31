
package main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import misc.PokemonHud;
import util.Input;

public class Main extends JPanel implements Runnable, KeyListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//dimensions
	public static final int STDTSIZE = 64;
	public static final int WIDTH = STDTSIZE * 10;
	public static final int HEIGHT = STDTSIZE * 9;
	public static int FPS = 60;
	
	//game thread
	private Thread thread;
	private boolean running;
	
	private long targetTime = 1000/FPS;
	
	//image
	private BufferedImage image;
	private Graphics2D g;
	
	// objects
	private Game game;
	
	//constructor
	public Main() 
	{
		super();
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		requestFocus();
	}
	
	public void addNotify() 
	{
		super.addNotify();
		if (thread == null)
		{
			thread = new Thread(this);
			addKeyListener(this);
			thread.start();
		}
	}
	
	private void init()
	{
		image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		running = true;
		game = new Game();
		
	}
	
	public void run()
	{
		init();
		
		long start;
		long elapsed;
		long wait;
		// game loop
		while (running)
		{	
			start = System.nanoTime();
			
			update();
			render();
			drawToScreen();
			
			elapsed = System.nanoTime()-start;
			
			wait = targetTime - elapsed/1000000;
			
			if (wait<0) wait = 5;
			
			try{
				Thread.sleep(wait);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private void update()
	{
		game.update();
	}
	
	private void render()
	{
		clearScreen(g);
		game.render(g);
	}
	
	private void drawToScreen()
	{
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, WIDTH,HEIGHT,null);
		g2.dispose();
	}
	
	public void clearScreen(Graphics2D g)
	{
		g.setColor(PokemonHud.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
	}
	
	// Keyboard methods
	public void keyTyped(KeyEvent key)
	{
		
	}
	
	public void keyPressed(KeyEvent key)
	{
	
		Input.keyPress(key);
	}
	
	public void keyReleased(KeyEvent key)
	{
		Input.keyRelease(key);
	}

}
	
