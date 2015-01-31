package misc;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

import util.GameInfo;
import util.Input;
import util.ResourceLoader;
import entities.SPlayer;

public class PlayerPanel {
	
	public static final int EMBLEM_WIDTH = 120;
	public static final int EMBLEM_HEIGHT = 92;
	
	private final Font FONT = PokemonHud.FONT;
	private final Color COLOR = PokemonHud.BLACK;
	
	private final int[] namePos = new int[]{228,92};
	private final int[] moneyPos = new int[]{294,155};
	private final int[] timePos = new int[]{294,218};
	private final int[] emblemPos0 = new int[]{64,356};
	
	private BufferedImage playerPanel = ResourceLoader.getTexture("playerPanel");
	private BufferedImage emblemTilemap = ResourceLoader.getTexture("emblems");
	private BufferedImage[] emblems;
	
	private SPlayer player;
	
	private String time;
	private boolean alive;
	
	public PlayerPanel(SPlayer player)
	{
		this.player = player;
		
		alive = true;
		
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	    time = sdf.format(new Date());
	    
	    createEmblems();
	}
	
	private void createEmblems()
	{
		emblems = new BufferedImage[8];
		
		for(int i = 0; i < 8; i ++)
		{
			if(i < 4)
				emblems[i] = emblemTilemap.getSubimage(i * EMBLEM_WIDTH, 0, EMBLEM_WIDTH, EMBLEM_HEIGHT);
			else
				emblems[i] = emblemTilemap.getSubimage((i - 4)* EMBLEM_WIDTH, EMBLEM_HEIGHT, EMBLEM_WIDTH, EMBLEM_HEIGHT);
		}
	}
	
	public void update()
	{
		if(Input.A_TAPPED || Input.B_TAPPED)
			alive = false;
	}
	
	public boolean getAlive()
	{
		return alive;
	}
	
	public void render(Graphics2D g)
	{
		g.drawImage(playerPanel, 0, 0, null);
		
		g.setFont(FONT);
		g.setColor(COLOR);
		g.drawString(GameInfo.PLAYERNAME, namePos[0], namePos[1]);
		g.drawString(String.valueOf(GameInfo.PLAYERMONEY), moneyPos[0], moneyPos[1]);
		g.drawString(time, timePos[0], timePos[1]);
		
		for(int i = 0; i < 8; i++)
			if(player.getEmblems()[i])
			{
				if(i < 4)
					g.drawImage(emblems[i], emblemPos0[0] + i * EMBLEM_WIDTH,emblemPos0[1], null);
				else
					g.drawImage(emblems[i], emblemPos0[0] + (i - 4) * EMBLEM_WIDTH,emblemPos0[1] + EMBLEM_HEIGHT, null);
			}
			
	}
}
