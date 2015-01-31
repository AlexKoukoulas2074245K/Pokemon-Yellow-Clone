package misc;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;
import java.util.jar.JarFile;

import main.Game;
import main.Launcher;
import main.Main;
import util.Counter;
import util.GameInfo;
import util.Input;
import util.ResourceLoader;
import util.Util;
import entities.Pokemon;
import entities.SPlayer;
import entities.Textbox;
import gameStates.Battle;
import gameStates.GSOverworld;

public class SaveMenu {
	
	private static Textbox tb;
	
	private final Font FONT = PokemonHud.FONT;
	private final Color COLOR = PokemonHud.BLACK;
	
	private final BufferedImage saveImage = ResourceLoader.getTexture("saveMenu");
	private final BufferedImage confImage = ResourceLoader.getTexture("confirmation");
	private final BufferedImage confCursor = ResourceLoader.getTexture("horCursor");
	
	private final int[] savePos = new int[]{Main.WIDTH - saveImage.getWidth() - 2, 0};
	private final int[] saveStringPos = new int[]{savePos[0] + 36, savePos[1] + 90};
	private final int[] confPos = new int[]{0, Main.HEIGHT - Battle.TB_HEIGHT - confImage.getHeight()};
	private final int[] confOptionsPos0 = new int[]{confPos[0] + 72, confPos[1] + 58};
	private final int[] confIndexPos = new int[]{confPos[0] + 36 , confPos[1] + 34};
	private final int saveAttX = savePos[0] + 470;
	
	private GSOverworld worldRef;
	private SPlayer player;
	private Counter savingCounter;
	
	private boolean alive;
	private boolean showConf;
	private boolean saving;
	
	private String badges;
	private String pkDexOwn;
	private String time;
	
	private int confIndex;
	
	public static void destroyTextbox()
	{
		tb = null;
	}
	
	public SaveMenu(SPlayer player, GSOverworld worldRef)
	{
		this.player = player;
		this.worldRef = worldRef;
		
		alive = true;
		
		badges = String.valueOf(Util.getEmblemN(player.getEmblems()));
		pkDexOwn = "0";
		
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	    time = sdf.format(new Date());
	    
	    tb = Util.generateTextbox("Would you like to#SAVE the game?", 2);
	    showConf = false;
	    saving = false;
	    
	    savingCounter = new Counter(Game.STDTSIZE);
	    confIndex = 0;
	}
	
	public void update()
	{
		
		if(saving)
		{
			tb.update();
			if(tb == null)
			{
				alive = false;
				save();
				return;
			}
			
			if(!tb.getAlive() && savingCounter != null)
			{
				savingCounter.update();
				if(!savingCounter.getAlive())
					tb = Util.generateTextbox(GameInfo.PLAYERNAME + " saved#the game!",0);
			}
			
			return;
		}
		
		if(tb != null && tb.getAlive())
		{
			tb.update();
			if(!tb.getAlive())
				showConf = true;
		}
		else
		{
			if(Input.UP_TAPPED)
			{
				confIndex = confIndex == 1 ? 0 : confIndex;
				return;
			}
			else if(Input.DOWN_TAPPED)
			{
				confIndex = confIndex == 0 ? 1 : confIndex;
				return;
			}
			
			else if(Input.B_TAPPED || (Input.A_TAPPED && confIndex == 1))
				alive = false;
			
			else if(Input.A_TAPPED && confIndex == 0)
			{
				tb = Util.generateTextbox("Saving..",2);
				saving = true;
				showConf = false;
			}
		}
	}
	
	private void save()
	{

		try 
		{
			//check for jar state	
			File f;
			f = new File(Launcher.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			
			JarFile jf = new JarFile(f);
			jf.close();
			
			File dir = new File("sav");
			
			if(!dir.isDirectory())
				dir.mkdir();
			
			saveFiles(true);
		}
		catch(FileNotFoundException fnfe)
		{
			saveFiles(false);
		}
		catch (URISyntaxException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e2) 
		{
			e2.printStackTrace();
		}
	}
	
	private void saveFiles(boolean jarExecution)
	{
		
		/*
		 * The first file contains many fields from the player class
		 * most importantly all their items, positional variables
		 * all pokemon and many others
		 */
		File playerFile = null;
		
		if(jarExecution)
		{
			playerFile = new File("sav/player.sav");
		}
		else
		{
			playerFile = new File("res/saves/player.sav");
		}
		
		if(playerFile.isFile())
			playerFile.delete();
		
		BufferedWriter bw = null;
		
		try 
		{
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(playerFile)));
			
			try 
			{
				//Player info
				bw.write(GameInfo.PLAYERNAME); bw.newLine();
				bw.write(String.valueOf(GameInfo.PLAYERMONEY)); bw.newLine();
				bw.write(GameInfo.RIVALNAME); bw.newLine();
				bw.write(worldRef.getArea() + " " + worldRef.getBuilding() + " " + player.getHome()); bw.newLine();
				bw.write(String.valueOf(player.getX() + "," + String.valueOf(player.getY()))); bw.newLine();
				bw.write(String.valueOf(player.getDirection())); bw.newLine();
				
				//Item info
				for(int i = 0; i < player.getBag().size() - 1; i ++)
				{
					String itemName = player.getBag().getItem(i).replace(" ", "_");
					String quantName = String.valueOf(player.getBag().getQuant(i));
					bw.write(itemName + "/" + quantName);
					if(i != player.getBag().size() - 2)
						bw.write(",");
				} bw.newLine();
				
				//Emblems
				for(int i = 0; i < player.getEmblems().length; i ++)
				{
					String emblem = String.valueOf(player.getEmblems()[i]);
					bw.write(emblem);
					if(i != player.getEmblems().length - 1)
						bw.write(" ");
				} bw.newLine();
				
				//Pokemon info
				for(int i = 0; i <= Util.getMaxIndex(player.getPokemon()); i ++)
				{
					Pokemon pokemon = player.getPokemon()[i];
					String name = pokemon.getName();
					String level = String.valueOf(pokemon.getLevel());
					String id = pokemon.getID();
					String hp = String.valueOf(pokemon.getStat(Pokemon.HP));
					String status = String.valueOf(pokemon.getStatus());
				
					String attacks = "";
					for(int y = 0; y < 4; y ++)
					{
						if(pokemon.getActiveAttacks()[y] != null)
						{
							Attack attack = pokemon.getActiveAttacks()[y];
							attacks += String.format("%s%%%s",attack.getName().replace(" ", "_"), attack.getPool());
							
							if(y != 3)
								attacks += ";";
						}
						else
						{
							attacks += "none%0";
							if(y != 3)
								attacks += ";";
						}
					}
					
					bw.write(String.format("%s/%s/%s/%s/%s/%s",name,level,id,hp,status,attacks));
					if(i != Util.getMaxIndex(player.getPokemon()))
						bw.write(",");
				}	
			}
			
			catch (IOException e) {

				e.printStackTrace();
			}
		}
		
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		finally
		{
			try
			{
				bw.close();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
		/*
		 * The second file saves all the defeated trainers
		 */
		File trnDefFile = null;
		
		if(jarExecution)
		{
			trnDefFile = new File("sav/defTrn.sav");
		}
		else
		{
			trnDefFile = new File("res/saves/defTrn.sav");
		}
		

		if(trnDefFile.isFile())
		{
	
			BufferedReader br = null;
			InputStream in = null;
			
			if(jarExecution)
			{
				try 
				{
					br = new BufferedReader(new FileReader("sav/defTrn.sav"));
				}
				catch (FileNotFoundException e1) 
				{
					e1.printStackTrace();
				}
			}
			else
			{
				in = getClass().getResourceAsStream("/saves/defTrn.sav");
				br = new BufferedReader(new InputStreamReader(in));
			}
			
			String line;
			
			try 
			{
				while((line = br.readLine()) != null)
				{
					if(line.equals("@"))
						continue;
					String[] components = line.split("\\s+");
					String area = components[0];
					String building = components[1];
					int id = Integer.parseInt(components[2]);
					
					if(worldRef.getDefeatedTrainers().containsKey(area + " " + building))
						worldRef.getDefeatedTrainers().get(area + " " + building).add(id);
					else
					{
						ArrayList<Integer> values = new ArrayList<Integer>();
						values.add(id);
						worldRef.getDefeatedTrainers().put(area + " " + building, values);
					}
				}
			}
			
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			trnDefFile.delete();
		}
		
		
		try 
		{
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(trnDefFile)));
			
			try
			{
				
				for(Entry<String,ArrayList<Integer>> entry: worldRef.getDefeatedTrainers().entrySet())
				{
					String[] splitKey = entry.getKey().split("\\s+");
					String area = splitKey[0];
					String building = splitKey[1];
					
					bw.write(area + " " + building + " ");
					
					for(int i = 0; i < entry.getValue().size(); i++)
					{
						bw.write(String.valueOf(entry.getValue().get(i)));
						if(i != entry.getValue().size() - 1)
							bw.write(",");
					}
					
					bw.newLine();
				}
				bw.write("@");
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
		
		catch(FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		}
		
		finally
		{
			try 
			{
				bw.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
		/*
		 * The third file saves all the obtained items
		 */
		
		File itemObtainedFile = null;
		
		if(jarExecution)
		{
			itemObtainedFile = new File("sav/obtItems.sav");
		}
		else
		{
			itemObtainedFile = new File("res/saves/obtItems.sav");
		}
		
		if(itemObtainedFile.isFile())
		{

			BufferedReader br = null;
			InputStream in = null;
			
			if(jarExecution)
			{
				try
				{
					br = new BufferedReader(new FileReader("sav/obtItems.sav"));
				}
				catch (FileNotFoundException e1) 
				{
					e1.printStackTrace();
				}
			}
			
			else
			{
				in = getClass().getResourceAsStream("/saves/obtItems.sav");
				br = new BufferedReader(new InputStreamReader(in));
			}
			
			String line;
			
			try 
			{
				while((line = br.readLine()) != null)
				{
					if(line.equals("@"))
						continue;
					String[] components = line.split("\\s+");
					String area = components[0];
					String building = components[1];
					int id = Integer.parseInt(components[2]);
					
					if(worldRef.getObtainedItems().containsKey(area + " " + building))
						worldRef.getObtainedItems().get(area + " " + building).add(id);
					else
					{
						ArrayList<Integer> values = new ArrayList<Integer>();
						values.add(id);
						worldRef.getObtainedItems().put(area + " " + building, values);
					}
				}
			}
			
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			itemObtainedFile.delete();
		}
		
		
		try 
		{
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(itemObtainedFile)));
			
			try
			{
				
				for(Entry<String,ArrayList<Integer>> entry: worldRef.getObtainedItems().entrySet())
				{
					String[] splitKey = entry.getKey().split("\\s+");
					String area = splitKey[0];
					String building = splitKey[1];
					
					bw.write(area + " " + building + " ");
					
					for(int i = 0; i < entry.getValue().size(); i++)
					{
						bw.write(String.valueOf(entry.getValue().get(i)));
						if(i != entry.getValue().size() - 1)
							bw.write(",");
					}
					
					bw.newLine();
				}
				bw.write("@");
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
		
		catch(FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		}
		
		finally
		{
			try 
			{
				bw.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}

	public void render(Graphics2D g)
	{
		g.drawImage(saveImage, savePos[0], savePos[1], null);
		drawSaveMenu(g);
		tb.render(g);
		
		if(showConf)
			drawConf(g);
	}
	
	private void drawSaveMenu(Graphics2D g)
	{
		g.setFont(FONT);
		g.setColor(COLOR);
		g.drawString("PLAYER", saveStringPos[0], saveStringPos[1]);
		g.drawString(GameInfo.PLAYERNAME, saveAttX - FONT.getSize() * GameInfo.PLAYERNAME.length(), saveStringPos[1]);
		g.drawString("BADGES", saveStringPos[0], saveStringPos[1] + 60);
		g.drawString(badges, saveAttX - FONT.getSize(), saveStringPos[1] + 60);
		g.drawString("POK" + "\u00e9" + "DEX", saveStringPos[0], saveStringPos[1] + 120);
		g.drawString(pkDexOwn, saveAttX - FONT.getSize() * pkDexOwn.length(), saveStringPos[1] + 120);
		g.drawString("TIME", saveStringPos[0], saveStringPos[1] + 180);
		g.drawString(time, saveAttX - FONT.getSize() * time.length(), saveStringPos[1] + 180);
	}
	
	private void drawConf(Graphics2D g)
	{
		g.drawImage(confImage, confPos[0], confPos[1], null);
		g.setFont(FONT);
		g.setColor(COLOR);
		g.drawString("YES", confOptionsPos0[0], confOptionsPos0[1]);
		g.drawString("NO", confOptionsPos0[0], confOptionsPos0[1] + 60);
		g.drawImage(confCursor, confIndexPos[0],confIndexPos[1] + confIndex * 60, null);

	}
	
	//Getters
	public boolean getAlive()
	{
		return alive;
	}
	
	
}
