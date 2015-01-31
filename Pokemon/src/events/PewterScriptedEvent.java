package events;

import java.awt.Graphics2D;

import main.Game;
import main.Main;
import util.Util;
import entities.Map;
import entities.SNpc;
import entities.SPlayer;
import entities.Textbox;

public class PewterScriptedEvent extends ScriptedEvent{
	
	private Map map;
	private SNpc npc;
	private SPlayer player;
	
	private int phase;
	
	public static Textbox textbox;
	
	public static void destroyTextbox()
	{
		textbox = null;
	}
	
	public PewterScriptedEvent(SNpc npc, SPlayer player, Map map)
	{
		this.map = map;
		this.npc = npc;
		this.player = player;
		finished = false;
		ownDrawn = true;
		
		textbox = Util.generateTextbox("You're a trainer#right? BROCK's#looking for new#challengers!#Follow me!");
		
		player.setNewTiles(map);
		phase = 0;
	}
	
	public void update()
	{
		Map.xoffset = Main.WIDTH/2 - (int)player.getX();
		Map.yoffset = Main.HEIGHT/2 - (int)player.getY();
		

		if(phase == 0)
		{
			if(textbox != null)
				textbox.update();
			else
			{
				phase ++;
				npc.setMovingNpc(true);
				npc.setMoving(true);
				npc.setSurrTiles(2, map.getTileAt(36 * Game.STDTSIZE, 21 * Game.STDTSIZE));
				npc.setNextTile(2);
			}
		}
		
		else if(phase == 1)
		{
			npc.update(map, player, true);
			if(!npc.getMoving())
			{
				npc.setDirection(3);
				phase ++;
				
				if(player.getCurrTile().getRow() == 19 || player.getCurrTile().getRow() == 20)
				{
					player.setSurrTiles(2, map.getTileAt((int)player.getX(), 21 * Game.STDTSIZE));
					player.setNextTile(2);
					player.setDirection(2);
				}
				
				else if(player.getCurrTile().getRow() == 21)
				{
					player.setDirection(3);
					player.setNextTile(3);
					phase ++;
					
				}
				else if(player.getCurrTile().getRow() == 22)
				{
					player.setSurrTiles(0, map.getTileAt((int)player.getX(), 21 * Game.STDTSIZE));
					player.setNextTile(0);
					player.setDirection(0);
				}
				
				player.setMoving(true);
			}
		}
		
		else if(phase == 2)
		{
			player.movement(map);
			if(!player.getMoving())
			{
				phase ++;
				player.setDirection(3);
				player.setSurrTiles(3, map.getTileAt(37 * Game.STDTSIZE, 21 * Game.STDTSIZE));
				player.setNextTile(3);
				player.setMoving(true);
			}
		}
		
		else if(phase == 3)
		{
			player.movement(map);
			if(!player.getMoving())
			{
				player.setNewTiles(map);
				npc.setTiles(map);
				
				npc.setSurrTiles(3, map.getTileAt(21 * Game.STDTSIZE, 21 * Game.STDTSIZE));
				npc.setNextTile(3);
				npc.setMoving(true);
				
				player.setSurrTiles(3, map.getTileAt(22 * Game.STDTSIZE, 21 * Game.STDTSIZE));
				player.setNextTile(3);
				player.setMoving(true);
				phase ++;
			}
		}
		
		else if(phase == 4)
		{
			if(player.getMoving())
			{
	
				player.movement(map);
				npc.update(map, player, true);
			}
			
			else
			{
				npc.setTiles(map);
				player.setNewTiles(map);
				
				npc.setDirection(0);
				npc.setNextTile(0);
				npc.setMoving(true);
				
				player.setNextTile(3);
				player.setMoving(true);
				
				phase ++;
			}
			
		}
		
		else if(phase == 5)
		{
			if(player.getMoving())
			{
				npc.update(map, player, true);
				player.movement(map);
			}
			
			else
			{
				npc.setTiles(map);
				player.setNewTiles(map);
				
				npc.setSurrTiles(0, map.getTileAt(21 * Game.STDTSIZE, 16 * Game.STDTSIZE));
				npc.setNextTile(0);
				npc.setMoving(true);
				
				player.setDirection(0);
				player.setSurrTiles(0, map.getTileAt(21 * Game.STDTSIZE, 17 * Game.STDTSIZE));
				player.setNextTile(0);
				player.setMoving(true);
				
				phase ++;
			}
		}
		
		else if(phase == 6)
		{
			if(player.getMoving())
			{
				npc.update(map, player, true);
				player.movement(map);
			}
			else
			{
				npc.setTiles(map);
				player.setNewTiles(map);
				
				npc.setDirection(3);
				npc.setNextTile(3);
				npc.setMoving(true);
				
				player.setNextTile(0);
				player.setMoving(true);
				
				phase ++;
			}
		}
		
		else if(phase == 7)
		{
			if(player.getMoving())
			{
				player.movement(map);
				npc.update(map, player, true);
			}			
			else
			{
				npc.setTiles(map);
				player.setNewTiles(map);
				
				npc.setSurrTiles(3, map.getTileAt(10 * Game.STDTSIZE, 16 * Game.STDTSIZE));
				npc.setNextTile(3);
				npc.setMoving(true);
				
				player.setDirection(3);
				player.setSurrTiles(3, map.getTileAt(11 * Game.STDTSIZE, 16 * Game.STDTSIZE));
				player.setNextTile(3);
				player.setMoving(true);
				
				phase ++;
			}
		}
		
		else if(phase == 8)
		{
			if(player.getMoving())
			{
				player.movement(map);
				npc.update(map, player, true);
			}
			
			else
			{
				player.setNewTiles(map);
				npc.setTiles(map);
				
				npc.setDirection(2);
				npc.setNextTile(2);
				npc.setMoving(true);
				
				player.setNextTile(3);
				player.setMoving(true);
				
				phase ++;
			}
		}
		
		else if(phase == 9)
		{
			if(player.getMoving())
			{
				player.movement(map);
				npc.update(map, player, true);
			}
			
			else
			{
				npc.setTiles(map);
				player.setNewTiles(map);
				
				npc.setSurrTiles(2, map.getTileAt(10 * Game.STDTSIZE, 21 * Game.STDTSIZE));
				npc.setNextTile(2);
				npc.setMoving(true);
				
				player.setDirection(2);
				player.setSurrTiles(2, map.getTileAt(10 * Game.STDTSIZE, 20 * Game.STDTSIZE));
				player.setNextTile(2);
				player.setMoving(true);
				
				phase ++;
			}
		}
		
		else if(phase == 10)
		{
			if(player.getMoving())
			{
				player.movement(map);
				npc.update(map, player, true);
			}
			else
			{
				player.setNewTiles(map);
				npc.update(map, player, true);
				
				npc.setDirection(1);
				npc.setNextTile(1);
				npc.setMoving(true);
				
				player.setNextTile(2);
				player.setMoving(true);
				
				phase ++;
			}
		}
		
		else if(phase == 11)
		{
			if(player.getMoving())
			{
				player.movement(map);
				npc.update(map, player, true);
			}
			
			else
			{
				player.setNewTiles(map);
				npc.update(map, player);
				
				npc.setSurrTiles(1, map.getTileAt(13 * Game.STDTSIZE, 21 * Game.STDTSIZE));
				npc.setNextTile(1);
				npc.setMoving(true);
				
				player.setDirection(1);
				player.setSurrTiles(1, map.getTileAt(12 * Game.STDTSIZE, 21 * Game.STDTSIZE));
				player.setNextTile(1);
				player.setMoving(true);
				
				phase ++;
			}
		}
		
		else if(phase == 12)
		{
			if(player.getMoving())
			{
				player.movement(map);
				npc.update(map, player, true);
			}
			else
			{
				player.setNewTiles(map);
				npc.setTiles(map);
				
				npc.setDirection(3);
				textbox = Util.generateTextbox("If you have the#right stuff, go#take on BROCK!");
				phase ++;
			}
		}
		
		else if(phase == 13)
		{
			textbox.update();
			if(textbox == null)
			{
				npc.setDirection(1);
				npc.setSurrTiles(1, map.getTileAt(19 * Game.STDTSIZE, 21 * Game.STDTSIZE));
				npc.setNextTile(1);
				npc.setMoving(true);
				
				phase ++;
			}
		}
		
		else if(phase == 14)
		{
			if(npc.getMoving())
				npc.movement(false);
			else
			{
				npc.setDirection(2);
				npc.setMovingNpc(false);
				npc.setX(36 * Game.STDTSIZE);
				npc.setY(19 * Game.STDTSIZE);
				npc.setTiles(map);
				player.setNewTiles(map);
				player.setPewterEvent(false);
				finished = true;
			}
		}
		
	}
	
	public void render(Graphics2D g)
	{
		map.render(g, false, false, false, false);
		if(phase == 0 || phase == 1 || phase == 13 || phase == 14)
			player.render(g,true,true);
		else
			player.render(g);
		
		if(textbox != null)
			textbox.render(g);
		

	}
}
