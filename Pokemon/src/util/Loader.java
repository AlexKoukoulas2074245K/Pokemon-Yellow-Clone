package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.JarFile;

import main.Launcher;
import misc.Attack;
import entities.Pokemon;

public class Loader {
	
	private File player;
	private Pokemon[] activePkmn;
	
	private HashMap<String, ArrayList<Integer>> defeatedTrainers;
	private HashMap<String, ArrayList<Integer>> obtainedItems;
	
	private ArrayList<String> itemNames;
	private ArrayList<Integer> itemQuants;
	
	private boolean[] playerEmblems;
	private boolean jarExecution;
	
	private String area;
	private String building;
	private String home;
	
	private float playerX;
	private float playerY;
	
	private int playerDirection;
	
	private boolean hasSave;

	public Loader()
	{
		jarExecution = false;
		hasSave = fileExists();
		
		if(!hasSave) return;
		
		loadPlayer();
		loadDefTrainers();
		loadObtItems();
	}
	
	private boolean fileExists()
	{
		
		try 
		{
			File f;
			f = new File(Launcher.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			JarFile jf = new JarFile(f);
			jf.close();
			
			jarExecution = true;
			player = new File("sav/player.sav");
			return player.isFile();
		}
		catch (FileNotFoundException fnfe)
		{
			jarExecution = false;
			player = new File("res/saves/player.sav");
			return player.isFile();
		}
		catch (URISyntaxException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	private void loadPlayer()
	{
		InputStream in = null;
		BufferedReader br = null;
		
		if(jarExecution)
		{
			try 
			{
				br = new BufferedReader(new FileReader("sav/player.sav"));
			}
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
		}
		
		else
		{
			in = getClass().getResourceAsStream("/saves/player.sav");
			br = new BufferedReader(new InputStreamReader(in));
		}
		
		try
		{
			GameInfo.PLAYERNAME = br.readLine();
			GameInfo.PLAYERMONEY = Integer.parseInt(br.readLine());
			GameInfo.RIVALNAME = br.readLine();
			
			String[] splitLocation = br.readLine().split("\\s+");
			area = splitLocation[0];
			building = splitLocation[1];
			home = splitLocation[2];
			
			String[] splitPosition = br.readLine().split(",");
			playerX = Float.parseFloat(splitPosition[0]);
			playerY = Float.parseFloat(splitPosition[1]);
			playerDirection = Integer.parseInt(br.readLine());
			
			String[] items = br.readLine().split(",");
			itemNames = new ArrayList<String>();
			itemQuants = new ArrayList<Integer>();
			
			if(items[0].split("/").length > 1)
			{
				for(int i = 0; i < items.length; i ++)
				{
					String[] splitInfo = items[i].split("/");
					itemNames.add(splitInfo[0].replace("_", " "));
					itemQuants.add(Integer.parseInt(splitInfo[1]));
				}
			}
			String[] emblems = br.readLine().split("\\s+");
			playerEmblems = new boolean[emblems.length];
			
			for(int i = 0; i < emblems.length; i ++)
				playerEmblems[i] = emblems[i].equals("true");
			
			String[] pokemon = br.readLine().split(",");
			activePkmn = new Pokemon[6];
			
			for(int i = 0; i < pokemon.length; i ++)
			{
				String[] info = pokemon[i].split("/");
				String pkmnName = info[0];
				int pkmnLevel = Integer.parseInt(info[1]);
				String pkmnID = info[2];
				float pkmnHp = Float.parseFloat(info[3]);
				int pkmnStatus = Integer.parseInt(info[4]);
				Attack[] pkmnMoves = new Attack[4];
				String[] moves = info[5].split(";");
				
				for(int y = 0; y < 4; y ++)
				{
	
					if(moves[y].equals("none%0"))
					{
						continue;
					}
					else
					{
						String[] components = moves[y].split("%");
						Attack attack = ResourceLoader.getAttack(components[0]);
						attack.setPool(Integer.parseInt(components[1]));
						pkmnMoves[y] = attack;
					}
				}
				
				Pokemon pkmn = new Pokemon(pkmnName,pkmnLevel, true);
				pkmn.setID(pkmnID);
				pkmn.setHp(pkmnHp);
				pkmn.setStatus(pkmnStatus);
				pkmn.setActiveAttacks(pkmnMoves);
				
				activePkmn[i] = pkmn;
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	
	}
	
	private void loadDefTrainers()
	{
		defeatedTrainers = new HashMap<String,ArrayList<Integer>>();
		
		InputStream in = null;
		BufferedReader br = null;
		
		if(jarExecution)
		{
			try 
			{
				br = new BufferedReader(new FileReader("sav/defTrn.sav"));
			}
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
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
			while(!(line = br.readLine()).equals("@"))
			{
				String[] components = line.split("\\s+");
				String area = components[0];
				String building = components[1];
				
				String key = area + " " + building;
				ArrayList<Integer> values = new ArrayList<Integer>();
				
				defeatedTrainers.put(key, values);
				
				String[] trainerIDs = components[2].split(",");
				
				for(int i = 0; i < trainerIDs.length; i ++)
					defeatedTrainers.get(key).add(Integer.parseInt(trainerIDs[i]));
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	private void loadObtItems()
	{
		obtainedItems = new HashMap<String,ArrayList<Integer>>();
		
		InputStream in = null;
		BufferedReader br = null;
		
		if(jarExecution)
		{
			try 
			{
				br = new BufferedReader(new FileReader("sav/obtItems.sav"));
			}
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
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
			while(!(line = br.readLine()).equals("@"))
			{
				String[] components = line.split("\\s+");
				String area = components[0];
				String building = components[1];
				
				String key = area + " " + building;
				ArrayList<Integer> values = new ArrayList<Integer>();
				
				obtainedItems.put(key, values);
				
				String[] trainerIDs = components[2].split(",");
				
				for(int i = 0; i < trainerIDs.length; i ++)
					obtainedItems.get(key).add(Integer.parseInt(trainerIDs[i]));
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Getters
	public boolean hasSave()
	{
		return hasSave;
	}
	
	public HashMap<String,ArrayList<Integer>> getDefeatedTrainers()
	{
		return defeatedTrainers;
	}
	
	public HashMap<String,ArrayList<Integer>> getObtainedItems()
	{
		return obtainedItems;
	}

	public Pokemon[] getPlayerActivePkmn() 
	{
		return activePkmn;
	}

	public ArrayList<String> getItemNames() 
	{
		return itemNames;
	}

	public ArrayList<Integer> getItemQuants() 
	{
		return itemQuants;
	}

	public boolean[] getPlayerEmblems() 
	{
		return playerEmblems;
	}
	
	public String getHome()
	{
		return home;
	}
	
	public String getArea() 
	{
		return area;
	}

	public String getBuilding() 
	{
		return building;
	}

	public float getPlayerX() 
	{
		return playerX;
	}

	public float getPlayerY() 
	{
		return playerY;
	}

	public int getPlayerDirection() 
	{
		return playerDirection;
	}	
}
