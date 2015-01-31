package gameStates;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;

import main.Game;
import misc.BattleAnimation;
import misc.Item;
import misc.TrainerBattleAnimation;
import misc.WildBattleAnimation;
import util.EventQueue;
import util.GameInfo;
import util.Graphics;
import util.Loader;
import util.ResourceLoader;
import util.Util;
import util.Wrap;
import entities.Map;
import entities.Pokemon;
import entities.SNpc;
import entities.SObject;
import entities.SPlayer;
import entities.Textbox;
import events.PewterScriptedEvent;
import events.PewterScriptedMuseumEvent;
import events.PokeCenterEvent;
import events.PokeMarketEvent;

public class GSOverworld extends GameState{
	
	public static final int MAP_CHANGE_TIMER = 50;
	public static boolean changeEffect;
	public static Textbox textbox;
	
	public static int bagDisIndex = 0;
	public static int bagIndex = 0;
	public static int[] bagBounds = new int[]{0,3};
	private static Item itemForPlayer;
	private static Battle battle;
	private ResourceLoader res;
	
	
	private HashMap<String, ArrayList<Integer>> defeatedTrainers;
	private HashMap<String, ArrayList<Integer>> obtainedItems;
	
	private BattleAnimation battleAni;
	
	private Loader loader;
	private Map map;
	private SPlayer player;

	private String area;
	private String building;

	private int changeTimer = MAP_CHANGE_TIMER;
	
	private EventQueue scriptedEvents;
	
	public static void setTextbox(String message)
	{
		textbox = Util.generateTextbox(message);
		itemForPlayer = Util.getItemFromMessage(textbox.getMessage());
	}
	public static void destroyTextbox()
	{
		textbox = null;
	}
	
	public static void endBattle()
	{
		battle = null;
	}
	
	public GSOverworld(Game game,ResourceLoader res)
	{
		this.res = res;
		
		loader = new Loader();
		
		if(loader.hasSave())
		{
			area = loader.getArea();
			Graphics.tilemapRecolouring(area);
			building = loader.getBuilding();
			defeatedTrainers = loader.getDefeatedTrainers();
			obtainedItems = loader.getObtainedItems();
			player = new SPlayer(loader);
		}
		else
		{
			area = "pallet";
			building = "playerHouse1";
			Graphics.tilemapRecolouring(area);
			defeatedTrainers = new HashMap<String, ArrayList<Integer>>();
			obtainedItems    = new HashMap<String, ArrayList<Integer>>();
			player = new SPlayer();
		}
		
		map = new Map(area,building,player,res,this);
		
		scriptedEvents = new EventQueue();
	}

	public void update()
	{
		if(battleAni != null)
		{
			battleAni.update();
			if(battleAni.getAniFinished())
			{
				battleAni = null;
				if(player.getWildAttack())
				{
					player.setWildAttack(false);
					startWildBattle();
				}
				else
				{
					player.setTrainerAttack(false);
					startTrainerBattle();
				}
			}
			
			return;
		}
	
		if(scriptedEvents.size() > 0)
		{
			updateScriptedEvents();
			return;
		}
		if(textboxUpdated())
			return;
		
		if(battle != null)
		{

			battle.update();
			if(Battle.finished)
			{
				if(!battle.getWild() && battle.getTrainer().getDefeated())
				{
					player.getTrainerRef().setDefeated(true);
					addTrainerToDefeated(battle.getTrainer());
					player.resetPokemonStats();
					if(player.getTrainerRef().getIsGymLeader())
					{
						setTextbox(player.getTrainerRef().getMessage());
						player.addBadge(itemForPlayer.getName());
						player.getTrainerRef().setMessage(GameInfo.BROCKAFTERSPEECH);
						map.defeatAll();
					}
				}
				else if(player.getDefeated())
					player.returnHome(map);

					
				battle = null;
			}
			return;
		}
		
		checkMapChange();
		
		if(!player.getTrainerAttack())
		{
			player.update(map);
			checkForScriptedEvents();
		}
		
		if(player.getWildAttack())
			battleAni = new WildBattleAnimation();
		
		if(player.getMenuOpen())
			return;
		
		map.update();
		
		transitionCheck();
	}
	
	private void addTrainerToDefeated(SNpc npc)
	{	
		String key = map.getArea() + " " + map.getBuilding();
		
		if(defeatedTrainers.containsKey(key))
			defeatedTrainers.get(key).add(npc.getID());
		else
		{
			ArrayList<Integer> values = new ArrayList<Integer>();
			values.add(npc.getID());
			defeatedTrainers.put(key, values);
		}
	}
	
	private void addItemToObtained(SObject object)
	{
		String key = map.getArea() + " " + map.getBuilding();
		
		if(obtainedItems.containsKey(key))
			obtainedItems.get(key).add(object.getID());
		else
		{
			ArrayList<Integer> values = new ArrayList<Integer>();
			values.add(object.getID());
			obtainedItems.put(key, values);
		}
	}
	
	private void checkForScriptedEvents()
	{
		if(player.getHealingPokemon())
		{
			scriptedEvents.add(new PokeCenterEvent(map.getNpc((int)player.getX(),
								(int)player.getY() - 2 * Game.STDTSIZE) ,player, map));
			player.setHealingPokemon(false);
		}
		
		else if(player.getBuying())
		{
			if(player.getDirection() == 3)
				scriptedEvents.add(new PokeMarketEvent(map.getNpc((int)player.getX() - 2 *Game.STDTSIZE,
										(int)player.getY()), player, map));
			else
				scriptedEvents.add(new PokeMarketEvent(map.getNpc((int)player.getX(), 
							(int)player.getY() - 2 * Game.STDTSIZE), player, map));
			player.setBuying(false);
		}
		
		else if(player.getPewterForceEvent())
		{
			if(scriptedEvents.size() == 0)
				scriptedEvents.add(new PewterScriptedEvent(map.getPewterGuy(),player,map));
			player.setPewterEvent(false);
		}
		
		else if(player.getPewterMuseumEvent())
		{
			if(scriptedEvents.size() == 0)
				scriptedEvents.add(new PewterScriptedMuseumEvent(player,map));
			player.setPewterMuseumEvent(false);
		}
		
		if(scriptedEvents.size() > 0)
			map.stopMovement();
	}
	private void updateScriptedEvents()
	{
		scriptedEvents.peek().update();
		if(scriptedEvents.peek().isFinished())
			scriptedEvents.pop();
	}
	
	private void startWildBattle()
	{
		Pokemon wildPokemon = map.getEncounter();
		SNpc npc = new SNpc(0,0,0,new int[]{0,0},new int[]{0,0},
				new int[]{0,0},new int[]{0,0},
				new int[]{0,0},new int[]{0,0},
				true, "", new Pokemon[]{wildPokemon},
				new int[]{0,0}, 0, false, false,
				0,"", map, true);
		
		battle = new Battle(player,npc,true);
		player.setWildAttack(false);
	}
	
	private void startTrainerBattle()
	{
		battle = new Battle(player, player.getTrainerRef(), false);
	}
	
	private void transitionCheck()
	{
		if(player.getMapChange() && !changeEffect)
		{
			Wrap destWrap = player.getDestWrap();
			if(destWrap != null && destWrap.getIsAreaConnection())
				player.setMapChange(false);
			else
			{
				changeEffect = true;
				res.resetTilemaps();
				res.resetAreaTexture(map.getArea(),map.getBuilding());
			}
		}
	}
	
	private boolean textboxUpdated()
	{
		
		if(textbox != null)
		{
			
			if(player.getObjectTalking() != null)
			{
				if(player.getObjectTalking().getObtainable())
				{
					player.getObjectTalking().setObtained(true);
					addItemToObtained(player.getObjectTalking());
				}
				map.removeObtainable();
			}
			
			textbox.update();
			if(textbox == null)
			{
				if(player.getTrainerAttack())
					battleAni = new TrainerBattleAnimation();
				
				else if(player.getObjectTalking() == null)
				{
					if(player.getTrainerRef().getIsGymLeader() && !player.getTrainerRef().getAlreadyDef())
						battleAni = new TrainerBattleAnimation();
				}
				
				if(itemForPlayer != null)
				{
					if(!itemForPlayer.getEffect().equals("BADGE"))
					{		
						player.getBag().addItem(itemForPlayer.getName(), 1);
						map.removeItems();
					}			
					for(SNpc npc: map.getNpcs())
						if(npc.getTalking())
						{
							npc.swapMessage();
							addTrainerToDefeated(npc);
						}
				}
				
				if(player.getForceJump())
				{
					player.setDirection(2);
					player.jump(2,map);

				}
			}
			return true;			
		}
		else
		{
			player.setTalking(false);
			map.resetNpcTalking();
			map.resetObjectTalking();
			return false;
		}
	}
	
	private void checkMapChange()
	{
		if(changeEffect)
		{
			changeTimer --;
			if(changeTimer <= 0)
			{
				changeTimer = MAP_CHANGE_TIMER;
				changeEffect = false;
				player.setMapChange(false);
				
				player.resetImages();
				
			}
		}
	}
	
	public void render(Graphics2D g)
	{	
		if(battle != null)
		{
			if(!Battle.finished)
			{
				battle.render(g);
				return;
			}
		}
		
		if(changeTimer <= 20) return;
		
		
		if(scriptedEvents.size() == 0 || !scriptedEvents.peek().isOwnDrawn())
		{
			map.render(g,player.getMenuOpen(),player.getTalking(),battleAni != null, scriptedEvents.size() > 0);
			player.render(g,battleAni != null, scriptedEvents.size() > 0);
		}
		
		if(player.getTrainerAttack())
			for(SNpc npc: map.getNpcs())
				if(npc.getIdentState())
					npc.drawIdentMarker(g);
		
		if(scriptedEvents.size() > 0)
			scriptedEvents.peek().render(g);
		
		if(battleAni != null)
			battleAni.render(g);
		
		if(textbox != null)
			textbox.render(g);
		
		if(changeEffect)
			renderMapChange();
	}
	
	private void renderMapChange()
	{
		if(changeTimer%10 == 0 && changeTimer > 20)
		{
			map.shiftEntities();
			Graphics.shiftToBlack(player.getCurrImage(),false);	
			Graphics.shiftToBlack(map.getImage(),map.getBuilding().equals("default"));
		}
	}
	
	
	//Getters
	public String getArea() 
	{
		return map.getArea();
	}
	
	public String getBuilding() 
	{
		return map.getBuilding();
	}
	
	public HashMap<String, ArrayList<Integer>> getDefeatedTrainers()
	{
		return defeatedTrainers;
	}
	
	public HashMap<String, ArrayList<Integer>> getObtainedItems()
	{
		return obtainedItems;
	}
	
	//Setters
	public void setArea(String area) 
	{
		this.area = area;
	}
}
