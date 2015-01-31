package util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.imageio.ImageIO;

import main.Game;
import main.Main;
import misc.Attack;
import misc.Bag;
import misc.Item;
import entities.Ambient;
import entities.Map;
import entities.Pokemon;
import entities.SNpc;
import entities.SObject;
import entities.Sprite;

public class ResourceLoader {
	
	public static final String MOVE_ANI_FOLDER = "res/moveAnim";
	
	private static final String STDEXT = "png";
	private static HashMap<String,BufferedImage> textures;
	private static HashMap<String,String[]> baseStats;
	private static HashMap<String,BufferedImage[]> ovImages;
	private static HashMap<String,Attack> attacks;
	private static HashMap<String,HashMap<String,Float>> typeEffs;
	private static HashMap<String,Item> items;
	private static HashMap<Integer, ArrayList<BufferedImage>> catchingImgs;
	private static HashMap<String, Bag> marketItems;
	
	private static ArrayList<String> slowExpGroup;
	private static ArrayList<String> mSlowExpGroup;
	private static ArrayList<String> mFastExpGroup;
	private static ArrayList<String> fastExpGroup;
	public static HashMap<String,BufferedImage> towns;
	public static HashMap<String,BufferedImage> buildings;
	
	public static ResourceLoader getInstance()
	{
		return new ResourceLoader();
	}
	
	public ResourceLoader()
	{
		
		textures = new HashMap<String,BufferedImage>();
		baseStats = new HashMap<String,String[]>();
		ovImages = new HashMap<String,BufferedImage[]>();
		attacks = new HashMap<String,Attack>();
		typeEffs = new HashMap<String,HashMap<String,Float>>();
		slowExpGroup = new ArrayList<String>();
		mSlowExpGroup = new ArrayList<String>();
		mFastExpGroup = new ArrayList<String>();
		fastExpGroup = new ArrayList<String>();
		towns= new HashMap<String,BufferedImage>();
		buildings = new HashMap<String,BufferedImage>();
		
		loadTilemaps();
		loadMiscs();
		loadAreaMaps();
		loadPkmnInfo();
		loadOvImages();
		loadAttacks();
		loadTypeEffs();
		loadExpGroups();
		loadItems();
		loadCatchingAnis();
		loadMarketItems();

	}
	
	private void loadTilemaps()
	{
		loadTexture("ambient1","tilemap");
		loadTexture("overworld","tilemap");
		loadTexture("trainers_dark","tilemap");
		loadTexture("trainers","tilemap");
	}
	
	private void loadMiscs()
	{
		try 
		{
			File f = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			
			JarFile jf = new JarFile(f);
			
			Enumeration<JarEntry> entries = jf.entries();
			
			while(entries.hasMoreElements())
			{	
				JarEntry nextEntry = entries.nextElement();
				if(nextEntry.getName().contains("misc/") && !nextEntry.isDirectory() && !nextEntry.getName().contains("class") && !nextEntry.getName().contains("META-INF"))
					loadTexture(nextEntry.getName().replaceFirst(".png", "").replace("misc/", ""),"misc");
		
			}
			
			jf.close();
		}
		catch(FileNotFoundException e)
		{
			File dir = new File("res/misc");
			
			for(File file: dir.listFiles())
			{
				loadTexture(file.getName().replaceFirst(".png",""),"misc");
			}
		}
		catch (URISyntaxException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	private void loadAreaMaps()
	{
		
		try 
		{
			File f = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			
			JarFile jf = new JarFile(f);
			
			Enumeration<JarEntry> entries = jf.entries();
			
			while(entries.hasMoreElements())
			{
				
				JarEntry nextEntry = entries.nextElement();
				if(nextEntry.getName().contains("textures/buildings/") && !nextEntry.isDirectory())
				{
					loadTexture(nextEntry.getName().replaceFirst(".png", "").replace("textures/buildings/", ""),"building");
					loadTexture(nextEntry.getName().replaceFirst(".png", "").replace("textures/buildings/", "") + "Map","townmap");
				}
				
				else if(nextEntry.getName().contains("textures/towns/") && !nextEntry.isDirectory())
				{
					loadTexture(nextEntry.getName().replaceFirst(".png", "").replace("textures/towns/", ""),"town");
					loadTexture(nextEntry.getName().replaceFirst(".png", "").replace("textures/towns/", "") + "Map","townmap");
				}
			}
			
			jf.close();
		}
		catch(FileNotFoundException e)
		{
			File buildings = new File("res/textures/buildings");
			File towns = new File("res/textures/towns");
			
			for(File file: buildings.listFiles())
			{
				loadTexture(file.getName().replaceFirst(".png", ""),"building");
				loadTexture(file.getName().replaceFirst(".png", "") + "Map","townmap");
			}
			
			for(File file: towns.listFiles())
			{
				loadTexture(file.getName().replaceFirst(".png", ""),"town");
				loadTexture(file.getName().replaceFirst(".png", "") + "Map","townmap");
			}
		}
		catch (URISyntaxException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}	
	}
	
	private void loadPkmnInfo()
	{
		
		InputStream in = getClass().getResourceAsStream("/info/base_stats.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		try
		{
			String line;
			while((line = br.readLine()) != null)
			{
				String[] components = line.split("\\s+");
				baseStats.put(components[1], components);
			}
		}
		catch(Exception ioe)
		{
			ioe.printStackTrace();
		}
		
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loadAttacks()
	{
		InputStream in = getClass().getResourceAsStream("/info/moves.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		int counter = 0;
		try
		{
			while((br.readLine())!= null)
			{
				counter ++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		InputStream in2 = getClass().getResourceAsStream("/info/moves.txt");
		BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));
		
		int counter2 = 0;
		
		try
		{
			String line;
			while((line = br2.readLine()) != null)
			{
				String[] components = line.split("\\s+");
				
				String name = components[0];
				String type = components[1];
				float power = Float.parseFloat(components[2]);
				float accur = Float.parseFloat(components[3]);
				int pool = Integer.parseInt(components[4]);
				
				String effect = "";
				if(components.length == 6)
					effect = components[5];
				
				attacks.put(name, new Attack(name,type,power,accur,pool,effect));
				
				counter2 ++;
				System.out.println(String.format("Loading: %d%%", counter2*100/counter));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void loadTypeEffs()
	{
		InputStream in = getClass().getResourceAsStream("/info/type_eff.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		try
		{
			String line;
			while((line = br.readLine()) != null)
			{
				String[] components = line.split("\\s+");
				String currType = components[0];
				
				HashMap<String,Float> types = new HashMap<String,Float>();
				types.put("NORMAL", Float.valueOf(components[1]));
				types.put("FIGHTING", Float.valueOf(components[2]));
				types.put("FLYING", Float.valueOf(components[3]));
				types.put("POISON", Float.valueOf(components[4]));
				types.put("GROUND", Float.valueOf(components[5]));
				types.put("ROCK", Float.valueOf(components[6]));
				types.put("BUG", Float.valueOf(components[7]));
				types.put("GHOST", Float.valueOf(components[8]));
				types.put("STEEL", Float.valueOf(components[9]));
				types.put("FIRE", Float.valueOf(components[10]));
				types.put("WATER", Float.valueOf(components[11]));
				types.put("GRASS", Float.valueOf(components[12]));
				types.put("ELECTRIC", Float.valueOf(components[13]));
				types.put("PSYCHIC", Float.valueOf(components[14]));
				types.put("ICE", Float.valueOf(components[15]));
				types.put("DRAGON", Float.valueOf(components[16]));
				
				typeEffs.put(currType,types);
			}
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void loadOvImages()
	{
		String[] names = new String[]{"BALL","BEAST","BUG","DRAGON","FLYING",
									"FOSSIL","GRASS","NORMAL","PIKACHU","WATER"};
		
		try
		{
			for(String name: names)
			{
				BufferedImage image1 = ImageIO.read(getClass().getResourceAsStream("/textures/pkmnov/" + name + "1." + STDEXT));
				BufferedImage image2 = ImageIO.read(getClass().getResourceAsStream("/textures/pkmnov/" + name + "2." + STDEXT));
				ovImages.put(name, new BufferedImage[]{image1,image2});
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	private void loadExpGroups()
	{
		InputStream in = getClass().getResourceAsStream("/info/exp_groups.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		try
		{
			String[] slowLine = br.readLine().split(",");
			String[] mSlowLine = br.readLine().split(",");
			String[] mFastLine = br.readLine().split(",");
			String[] fastLine = br.readLine().split(",");
			
			for(String name: slowLine)
				slowExpGroup.add(name);
			for(String name: mSlowLine)
				mSlowExpGroup.add(name);
			for(String name: mFastLine)
				mFastExpGroup.add(name);
			for(String name: fastLine)
				fastExpGroup.add(name);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void loadItems()
	{
		items = new HashMap<String,Item>();
		
		InputStream in = getClass().getResourceAsStream("/info/items.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		String line;
		
		try
		{
			while((line = br.readLine()) != null)
			{
				String[] components = line.split("\\s+");
				String name = components[0].replace("_"," ");
				boolean battle = components[1].equals("1") || components[1].equals("2");
				boolean ovworld = components[1].equals("0") || components[1].equals("2");
				boolean unique = components[2].equals("true");
				String effect = components[3];
				int buyingPrice = Integer.parseInt(components[4]);
				int sellingPrice = Integer.parseInt(components[5]);
				
				items.put(name, new Item(name, battle, ovworld, unique, effect, buyingPrice, sellingPrice));
			}
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void loadCatchingAnis()
	{
		catchingImgs = new HashMap<Integer,ArrayList<BufferedImage>>();
		
		
		try 
		{
			File f = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			
			JarFile jf = new JarFile(f);
			
			Enumeration<JarEntry> entries = jf.entries();
			
			ArrayList<BufferedImage> images0 = new ArrayList<BufferedImage>();
			ArrayList<BufferedImage> images1 = new ArrayList<BufferedImage>();
			ArrayList<BufferedImage> images2 = new ArrayList<BufferedImage>();
			ArrayList<BufferedImage> images3 = new ArrayList<BufferedImage>();
			ArrayList<BufferedImage> images4 = new ArrayList<BufferedImage>();
			
			while(entries.hasMoreElements())
			{
				
				JarEntry nextEntry = entries.nextElement();

				if(nextEntry.getName().contains("catching/0/") && !nextEntry.isDirectory())
				{
					images0.add(ImageIO.read(getClass().getResourceAsStream("/" + nextEntry.getName())));
				}
				
				else if(nextEntry.getName().contains("catching/1/") && !nextEntry.isDirectory())
				{
					images1.add(ImageIO.read(getClass().getResourceAsStream("/" + nextEntry.getName())));
				}
				
				else if(nextEntry.getName().contains("catching/2/") && !nextEntry.isDirectory())
				{
					images2.add(ImageIO.read(getClass().getResourceAsStream("/" + nextEntry.getName())));
				}
				
				else if(nextEntry.getName().contains("catching/3/") && !nextEntry.isDirectory())
				{
					images3.add(ImageIO.read(getClass().getResourceAsStream("/" + nextEntry.getName())));
				}
				
				else if(nextEntry.getName().contains("catching/4/") && !nextEntry.isDirectory())
				{
					images4.add(ImageIO.read(getClass().getResourceAsStream("/" + nextEntry.getName())));
				}
				
			}
			
			catchingImgs.put(0, images0);
			catchingImgs.put(1, images1);
			catchingImgs.put(2, images2);
			catchingImgs.put(3, images3);
			catchingImgs.put(4, images4);
			
			jf.close();
		}
		catch(FileNotFoundException e)
		{
			for(int i = 0; i < 5; i ++)
			{
				String dirPath = "res/catching/" + String.valueOf(i);
				File directory = new File(dirPath);
				ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
				
				for(File image: directory.listFiles())
				{
					String path = String.format("/catching/%d/%s", i, image.getName());
					try 
					{
						BufferedImage loadedImg = ImageIO.read(getClass().getResourceAsStream(path));
						images.add(loadedImg);
					}
					catch (IOException e2) 
					{
						e2.printStackTrace();
					}
				}
				
				catchingImgs.put(i, images);
			}
		}
		catch (URISyntaxException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	private void loadMarketItems()
	{
		marketItems = new HashMap<String,Bag>();
		
		InputStream in = getClass().getResourceAsStream("/info/markets.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		try
		{
			String line;
			
			while((line = br.readLine()) != null)
			{
				String[] components = line.split("\\s+");
				String marketName = components[0];
				
				Bag bag = new Bag();
				
				for(String itemName: components)
				{
					if(itemName.equals(marketName))
						continue;
					
					bag.addItem(itemName.replace("_", " "), 1);
				}
				
				marketItems.put(marketName, bag);
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	private void loadTexture(String name,String type)
	{
		String imagePath = findTexturePath(name,type);
		
		System.out.println(imagePath);
		if(imagePath.contains("META-INF") || imagePath.contains("/misc/catching/.png"))
			return;
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(getClass().getResourceAsStream(imagePath));
			if(type.equals("tilemap"))
				image = Graphics.convertToARGB(image);
			else if(type.equals("town"))
				towns.put(name,image);
			else if(type.equals("building"))
				buildings.put(name,image);
			
		} catch (IOException e) {
			System.err.println(imagePath);
			e.printStackTrace();
		}
		textures.put(name, image);	
	}
	
	private String findTexturePath(String name,String type)
	{
		if(type.equals("tilemap"))
			return String.format("/tilemaps/%s.%s", name, STDEXT);
		else if(type.equals("townmap"))
			return String.format("/townmaps/%s.%s",name,STDEXT);
		else if(type.equals("town"))
			return String.format("/textures/towns/%s.%s", name,STDEXT);
		else if(type.equals("route"))
			return String.format("/textures/routes/%s.%s", name,STDEXT);
		else if(type.equals("building"))
			return String.format("/textures/buildings/%s.%s", name,STDEXT);
		else if(type.equals("misc"))
			return String.format("/misc/%s.%s", name,STDEXT);
		else
			return "";
	}
	
	public static BufferedImage extractFromTileMap(int column,int row,BufferedImage tilemap)
	{
		return tilemap.getSubimage(column*Main.STDTSIZE,
								   row*Main.STDTSIZE,
								   Main.STDTSIZE, Main.STDTSIZE);
	}
	
	public static BufferedImage getTexture(String name)
	{
		if(textures.get(name) == null)
		{
			System.out.println(name);
			return textures.get("overworld");
		}
		return textures.get(name);
	}
	
	public static BufferedImage getTownMap(String area,String building)
	{
		if(building.equals("default"))
		{
			String result = String.format("%sMap", area);
			return textures.get(result);
		}
		else
		{
			String result = String.format("%sMap",building);
			return textures.get(result);
		}
	}
	
	public static BufferedImage getImage(String area,String building)
	{
		if(building.equals("default"))
			return textures.get(area);

		else
			return textures.get(building);
	}
	
	public ArrayList<SNpc> getNpcs(String area,String building, Map world)
	{
		
		ArrayList<SNpc> npcs = new ArrayList<SNpc>();
		
		InputStream in = getClass().getResourceAsStream("/loader/npcs.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		int id = 0;
		String line;
		
		try {
			while((line = br.readLine()) != null)
			{
				String[] splitLine = line.split("\\s+");
				
				if(!splitLine[0].equals(area)) continue;
				if(!splitLine[1].equals(building)) continue;
				
				int[] position = Util.stringToTuple(splitLine[2]);
				
				int[] initCoords = Util.stringToTuple(splitLine[3]);
				int coordX = initCoords[0];
				int coordY = initCoords[1];
				
				int[][] restCoords = new int[5][2];
				
				for(int i = 0; i < 5; i ++)
				{
					coordX ++;
					if(coordX > 7)
					{
						coordX = 0;
						coordY ++;
					}
					
					restCoords[i] = new int[]{coordX,coordY};
				}
				
				boolean movingNpc = splitLine[4].equals("true");			
				int direction = Integer.parseInt(splitLine[5]);
				
				boolean isTrainer = splitLine[6].equals("true");
				
				int[] battleImageCoords = new int[]{-1,-1};
				Pokemon[] pokemon = null;
				String trainerName = "";
				int value = 0;
				boolean isGymLeader = false;
				
				if(isTrainer)
				{
					trainerName = splitLine[7].replace("_", " ");
					trainerName = trainerName.replace("RIVALNAME",GameInfo.RIVALNAME);
					
					battleImageCoords = SNpc.TRAINER_IMGS.get(trainerName);
					pokemon = Util.getPokemon(splitLine[8]);
					value = pokemon[pokemon.length-1].getLevel() * SNpc.BASE_MONEY.get(trainerName);
					isGymLeader = trainerName.equals("BROCK") || trainerName.equals("MISTY");
					
					if(trainerName.equals("COOL TRAINERM") || trainerName.equals("COOL TRAINERF") ||
					   trainerName.equals("JR.TRAINERM") || trainerName.equals("JR.TRAINERF"))
					   
					   trainerName = trainerName.substring(0, trainerName.length() - 1);
				}
				
				String message = br.readLine();
				
				npcs.add(new SNpc(id, position[0] * Game.STDTSIZE,position[1] * Game.STDTSIZE,
								  initCoords,restCoords[0],
								  restCoords[1],restCoords[2],
								  restCoords[3],restCoords[4],
								  isTrainer, trainerName, pokemon, battleImageCoords,
								  value,isGymLeader,
								  movingNpc,direction,message, world));
				
				id ++;
			}
			
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		return npcs;
		
	}
	
	public ArrayList<SObject> getObjects(String area,String building, Map world)
	{
		
		ArrayList<SObject> objects = new ArrayList<SObject>();
		
		InputStream in = getClass().getResourceAsStream("/loader/objects.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		int id = 0;
		String line;
		try {
			while((line = br.readLine()) != null)
			{
				
				String[] splitLine = line.split("\\s+");
				
				if(!splitLine[0].equals(area)) continue;
				if(!splitLine[1].equals(building)) continue;
				
				int[] position = Util.stringToTuple(splitLine[2]);
				int[] imageCoords = Util.stringToTuple(splitLine[3]);
				
				boolean obtainable = splitLine[4].equals("true");
				boolean solid = splitLine[5].equals("true");
				boolean canBeCut = splitLine[6].equals("true");
				
				String message = br.readLine();
				
				objects.add(new SObject(id,position[0] * Game.STDTSIZE,position[1] * Game.STDTSIZE,
								  		message, obtainable, solid, imageCoords, world, canBeCut));
				
				id++;
			}
			
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		return objects;
		
	}
	
	
	public void resetAreaTexture(String area, String building) 
	{
		if(building.equals("default"))
		{
			textures.remove(area);
			loadTexture(area,"town");
		}
		else
		{
			textures.remove(building);
			loadTexture(building,"building");
		}
		
	}
	
	public static float getEff(String att, String def)
	{
		return typeEffs.get(att).get(def);
	}
	
	public static Item getItem(String name)
	{
		
		name = name.replace("\u00e9", "E");
		
		if(items.containsKey(name))
			return items.get(name);
		else
		{
			System.out.println("ITEM NOT FOUND!!!");
			return items.get("POTION");
		}
	}
	
	public static String[] getInfo(String name)
	{
		return baseStats.get(name);
	}
	
	public static BufferedImage[] getOvImages(String type)
	{
		return ovImages.get(type);
	}
	
	public static Attack getAttack(String name)
	{
		if(attacks.containsKey(name))
			return attacks.get(name);
		else
		{
			System.out.println("Attack not found: " + name);
			return attacks.get("TACKLE");
		}
	}
	
	public void resetTilemaps()
	{
		textures.remove("overworld");
		textures.remove("ambient1");
		
		loadTexture("overworld","tilemap");
		loadTexture("ambient1","tilemap");
		
		Sprite.OVERWORLD_TM = getTexture("overworld");
		Ambient.ambientTilemap = getTexture("ambient1");
	}
	
	public static int getExpGroup(String name)
	{
		if(slowExpGroup.contains(name))
			return Pokemon.SLOW;
		else if(mSlowExpGroup.contains(name))
			return Pokemon.MEDIUM_SLOW;
		else if(mFastExpGroup.contains(name))
			return Pokemon.MEDIUM_FAST;
		else
			return Pokemon.FAST;
	}
	
	public static ArrayList<BufferedImage> getCatchImgs(int shakes)
	{
		return catchingImgs.get(shakes);
	}
	
	public static Bag getMarketItems(String area)
	{
		return marketItems.get(area);
	}

}
