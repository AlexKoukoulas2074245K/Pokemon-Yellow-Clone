package battleStates;

import java.awt.Graphics2D;
import java.util.Random;

import main.Game;
import main.Main;
import misc.Attack;
import misc.CatchAnimation;
import misc.LevelUpPanel;
import misc.PokemonEvolution;
import misc.PokemonHud;
import misc.PokemonPanel;
import misc.ScreenShake;
import util.BattleCalc;
import util.Counter;
import util.GameInfo;
import util.Input;
import util.Util;
import entities.Pokemon;
import entities.SNpc;
import entities.SPlayer;
import gameStates.Battle;

public class BSMain extends BattleState{
	
	public static boolean isCrit;
	public static ScreenShake shake;
	public static Counter retreatCounter;
	
	private LevelUpPanel lup;
	private Counter counter;
	
	private PokemonPanel pokePanel;
	private PokemonEvolution pokeEvo;
	
	private Pokemon plrActivePkmn;
	private Pokemon trnActivePkmn;
	
	private Pokemon fasterPokemon;
	private Pokemon slowerPokemon;
	
	private Pokemon evo;
	
	private int[] plrPkmnPos;
	private int[] trnPkmnPos;
	
	private int trainerX;
	private int moveConfIndex;
	
	private int phase;
	private float psnTick;
	private float damage;
	
	private Attack psnAttack;
	private CatchAnimation catchAni;
	
	private boolean missed;
	private boolean noEff;
	private int doubleCounter;
	private int multiCounter;
	private int multiCounter0;
	private int catchResult;
	private int phase0;
	
	public BSMain(SPlayer player,SNpc trainer, boolean wildBattle)
	{
		super(player,trainer, wildBattle);
		
		plrActivePkmn = Battle.playerActive;
		trnActivePkmn = Battle.enemyActive;
	
		
		plrPkmnPos = BSIntro.FINAL_PLR_POS;
		trnPkmnPos = BSIntro.FINAL_TRN_POS;
		trainerX = trnPkmnPos[0] + Battle.IMAGE_SIZE;
		
		phase = 0;
		phase0 = 0;
		psnTick = 0;
		moveConfIndex = 0;
		
		doubleCounter = 1;
		resetMultiCounter();
	}
	
	public void update()
	{
		if(BSOptions.itemToUse != null)
		{
			itemUse();
			return;
		}
		if(phase == 0)
		{
			if(plrActivePkmn.getStat(Pokemon.SPEED) >= trnActivePkmn.getStat(Pokemon.SPEED))
			{
				fasterPokemon = plrActivePkmn;
				slowerPokemon = trnActivePkmn;
			}
			else
			{
				fasterPokemon = trnActivePkmn;
				slowerPokemon = plrActivePkmn;
			}
			
			if(plrActivePkmn.getNextAttack() == null)
			{
				phase = 6;
				return;
			}
			
			String message;
			
			boolean par = false;
			
			if(fasterPokemon.getStatus() == Pokemon.PAR)
				par = BattleCalc.getParalysis();
			
			if(fasterPokemon == plrActivePkmn)
			{
				if(!par)
					message = String.format("%s#used %s!", fasterPokemon.getName(), 
										fasterPokemon.getNextAttack().getName());
				else
				{
					Battle.setTextbox(String.format("%s's#fully paralyzed!",fasterPokemon.getName()),1);
					phase = 6;
					return;
				}
			}
			else
			{
				if(!par)
					message = String.format("Enemy %s#used %s!", fasterPokemon.getName(), 
						fasterPokemon.getNextAttack().getName());
				else
				{
					Battle.setTextbox(String.format("Enemy %s's#fully paralyzed!", fasterPokemon.getName()),1);
					phase = 6;
					return;
				}
			}
			Battle.textbox = Util.generateTextbox(message, 2);
			
			phase ++;
		}
		
		else if(phase == 1)
		{
			if(Battle.textbox != null)
				Battle.textbox.update();
			if(Battle.textbox == null || !Battle.textbox.getAlive())
			{
				
				if((Util.valueInArray(fasterPokemon.getNextAttack().getName(), 
						 Attack.multiAttacks) && multiCounter != multiCounter0) ||
				  (Util.valueInArray(fasterPokemon.getNextAttack().getName(),
						  Attack.doubleAttacks) && doubleCounter == 0))
				{
					missed = false;
					phase ++;
					return;
				}
				
				missed = BattleCalc.getMiss(fasterPokemon, slowerPokemon);			
				noEff = BattleCalc.getEffectiveness(fasterPokemon,slowerPokemon) == 0f;
				
				if((fasterPokemon.getNextAttack().getEffect().equals("EPSN") ||
				   fasterPokemon.getNextAttack().getEffect().equals("EPAR")) && 
				   slowerPokemon.getStatus() != Pokemon.OK)
					
					missed = true;
			
				if(missed || noEff)
				{
										
					Battle.destroyTextbox();
					
					damage = 0;
					phase += 4;
					
					if(missed)
					{
						if(fasterPokemon.getNextAttack().getPower() == 0)
							Battle.setTextbox("But it failed!", 1);
						else
						{
							if(fasterPokemon == Battle.playerActive)
								Battle.setTextbox(String.format("%s's#attack missed!", fasterPokemon.getName()), 1);
							else
								Battle.setTextbox(String.format("Enemy %s's#attack missed!", fasterPokemon.getName()),1);
						}
					}
					else
					{
						if(fasterPokemon == Battle.playerActive)
							Battle.setTextbox("It doesn't affect#Enemy " + slowerPokemon.getName() + "!",1);
						else
							Battle.setTextbox("It doesn't affect#" + slowerPokemon.getName() + "!", 1);
					}
				}
				else
					phase ++;
			}
		}
		
		else if(phase == 2)
		{
			if(fasterPokemon.getNextAttack().getFinishedAni())
			{
				fasterPokemon.getNextAttack().setFinishedAni(false);
				decidePostAnimation(fasterPokemon);
				phase ++;
			}
		}
		
		else if(phase == 3)
		{
			if(shake != null)
			{
				shake.update();
				if(shake.getFinished())
				{
					shake = null;
					
					slowerPokemon.resetOffsets();
					fasterPokemon.resetOffsets();
					
					if(fasterPokemon.getNextAttack().getPower() > 0)
					{
						damage = BattleCalc.getDamage(fasterPokemon,slowerPokemon);
						if(damage < 1)
							damage = 1;
					}
					else
						damage = 0;
					
					if(damage <= 0)
					{
						Battle.destroyTextbox();
						phase += 2;
						BattleCalc.applyEffect(fasterPokemon, slowerPokemon);
					}
					else
						phase ++;
				}
			}
			else
			{
				if(!slowerPokemon.getFlinching())
				{
					if(fasterPokemon.getNextAttack().getPower() > 0)
						damage = BattleCalc.getDamage(fasterPokemon,slowerPokemon);
					else
						damage = 0;
					
					if(damage <= 0)
					{
						Battle.destroyTextbox();
						phase += 2;
						BattleCalc.applyEffect(fasterPokemon, slowerPokemon);
					}
					else
						phase ++;
				}
			}
		}
		
		else if(phase == 4)
		{
			float damageDecr = BattleCalc.getDamageDecr(fasterPokemon,slowerPokemon, damage);
			
			if(damage != 0)
			{
				if(damageDecr > damage)
					damageDecr = damage;
				
				slowerPokemon.damage(damageDecr);
				damage -= damageDecr;
				
				if(!slowerPokemon.getAlive() || damage <= 0)
				{
					damage = 0;
					
					if(!slowerPokemon.getAlive())
						slowerPokemon.setFalling(true);
					
					if(isCrit)
						Battle.setTextbox("Critical hit!",1);
					else
						Battle.destroyTextbox();
					
					if(Battle.placeHolderTextbox != null && !isCrit)
					{
						Battle.setTextbox(Battle.placeHolderTextbox);
						Battle.destroyPlaceHolder();
					}
					phase ++;
				}
			}
		}
		
		else if(phase == 5)
		{
			if(Battle.textbox != null)
				Battle.textbox.update();
			else
			{
				
				if(fasterPokemon.getAlive() && slowerPokemon.getAlive())
				{
					if(!isCrit)
						Battle.updateHuds();
					if(isCrit)
					{
						Battle.updateHuds();
						isCrit = false;
						
						if(slowerPokemon.getAlive())
							Battle.setTextbox(Battle.placeHolderTextbox);
						return;
					}
				}
				
				if(slowerPokemon.getAlive())
				{
					if(fasterPokemon.getStatus() == Pokemon.PSN && 
						!(Util.valueInArray(fasterPokemon.getNextAttack().getName(), 
											Attack.doubleAttacks) && doubleCounter != 0) &&
						!(Util.valueInArray(fasterPokemon.getNextAttack().getName(),
											Attack.multiAttacks) && multiCounter != 1))
					{
						psnTick = BattleCalc.getPoisonTick(fasterPokemon);
						psnAttack = new Attack("POISON","NONE",0,255,100,"");
						
						if(fasterPokemon == Battle.playerActive)
							Battle.setTextbox(String.format("%s's#hurt by poison",fasterPokemon.getName()),2);
						else
							Battle.setTextbox(String.format("Enemy %s's#hurt by poison", fasterPokemon.getName()),2);
						
						doubleCounter = 1;
						phase = 30;
					}
					
					else if(Util.valueInArray(fasterPokemon.getNextAttack().getName(), Attack.doubleAttacks) &&
							doubleCounter == 1 && !missed)
					{
						doubleCounter --;
						phase = 1;
					}
					
					else if(Util.valueInArray(fasterPokemon.getNextAttack().getName(), Attack.multiAttacks) &&
							multiCounter != 1 && !missed)
					{
						multiCounter --;
						phase = 1;
					}
					
					else
					{
						if(doubleCounter == 0)
						{
							if(Battle.enemyActive == slowerPokemon)
								Battle.setTextbox("It hit the enemy#2 times!",1);
							else
								Battle.setTextbox("It hit#2 times!",1);
							
							if(fasterPokemon.getStatus() != Pokemon.PSN)
								doubleCounter = 1;
							
							phase = 99;
							phase0 = 5;
						}
						
						else if(multiCounter == 1)
						{
							if(Battle.enemyActive == slowerPokemon)
								Battle.setTextbox("It hit the enemy#" + multiCounter0 + " times!",1);
							else
								Battle.setTextbox("It hit#" + multiCounter0 + " times!",1);
							
							if(fasterPokemon.getStatus() != Pokemon.PSN)
							{
								resetMultiCounter();
							}
							
							phase = 99;
							phase0 = 5;
						}
						
						else
						{
							doubleCounter = 1;
							phase ++;
						}
					}
				}
				else
					if(!slowerPokemon.getFalling())
						phase = 12;
			}
		}
		
		else if(phase == 6)
		{
			
			if(plrActivePkmn.getNextAttack() == null)
			{
				slowerPokemon = trnActivePkmn;
				fasterPokemon = plrActivePkmn;
			}
			
			if(Battle.textbox != null)
			{
				
				if(Battle.textbox.getAlive())
					Battle.textbox.update();
				
				
				if(Battle.textbox == null)
					return;
				
				if(!Battle.textbox.getAlive())
				{
					if(Battle.placeHolderTextbox != null)
					{
						retreatCounter.update();
						if(!retreatCounter.getAlive())
						{
							retreatCounter = new Counter(Game.STDTSIZE);
							Battle.setTextbox(Battle.placeHolderTextbox);
							Battle.destroyPlaceHolder();
						}
					}
					
					else
					{
						retreatCounter.update();
						if(!retreatCounter.getAlive())
						{
							retreatCounter = null;
							Battle.destroyTextbox();
						}
					}
				}
				return;
			}
			
			if(plrActivePkmn != Battle.playerActive)
			{
				plrActivePkmn = Battle.playerActive;
				
				slowerPokemon = trnActivePkmn;
				fasterPokemon = plrActivePkmn;
				Battle.updateHuds();
			}
			
			if(slowerPokemon.getNextAttack() == null)
			{
				finished = true;
				return;
			}
			
			boolean par = false;
			
			if(slowerPokemon.getStatus() == Pokemon.PAR)
				par = BattleCalc.getParalysis();
			
			String message;
			
			
			if(slowerPokemon == plrActivePkmn)
			{
				if(!par)
					message = String.format("%s#used %s!", slowerPokemon.getName(), 
										slowerPokemon.getNextAttack().getName());
				else
				{
					Battle.setTextbox(String.format("%s's#fully paralyzed!",slowerPokemon.getName()),1);
					phase = 11;
					return;
				}
			}
			else
			{
				if(!par)
					message = String.format("Enemy %s#used %s!", slowerPokemon.getName(), 
						slowerPokemon.getNextAttack().getName());
				else
				{
					Battle.setTextbox(String.format("Enemy %s's#fully paralyzed!",slowerPokemon.getName()),1);
					phase = 11;
					return;
				}
			}
			
			Battle.textbox = Util.generateTextbox(message, 2);
			
			phase ++;
		}
		
		else if(phase == 7)
		{
			if(Battle.textbox != null)
				Battle.textbox.update();
			if(Battle.textbox == null || !Battle.textbox.getAlive())
			{
				
				if((Util.valueInArray(slowerPokemon.getNextAttack().getName(), 
						 Attack.multiAttacks) && multiCounter != multiCounter0) ||
				  (Util.valueInArray(slowerPokemon.getNextAttack().getName(),
						  Attack.doubleAttacks) && doubleCounter == 0))
				{
					missed = false;
					phase ++;
					return;
				}
				
				missed = BattleCalc.getMiss(slowerPokemon, fasterPokemon);			
				noEff = BattleCalc.getEffectiveness(slowerPokemon,fasterPokemon) == 0f;
				
				if((slowerPokemon.getNextAttack().getEffect().equals("EPSN") ||
				   slowerPokemon.getNextAttack().getEffect().equals("EPAR")) && 
				   fasterPokemon.getStatus() != Pokemon.OK)
				
				   missed = true;
						
				if(missed || noEff)
				{
					
					
					Battle.destroyTextbox();
					damage = 0;
					phase += 4;
					
					if(missed)
					{
						if(slowerPokemon.getNextAttack().getPower() == 0)
							Battle.setTextbox("But it failed!", 1);
						else
						{
							if(slowerPokemon == Battle.playerActive)
								Battle.setTextbox(String.format("%s's#attack missed!", slowerPokemon.getName()), 1);
							else
								Battle.setTextbox(String.format("Enemy %s's#attack missed!", slowerPokemon.getName()),1);
						}
					}
					else
					{
						if(slowerPokemon == Battle.playerActive)
							Battle.setTextbox("It doesn't affect#Enemy " + fasterPokemon.getName() + "!",1);
						else
							Battle.setTextbox("It doesn't affect#" + fasterPokemon.getName() + "!", 1);
					}
				}
				else
					phase ++;
			}
		}
		
		else if(phase == 8)
		{
			if(slowerPokemon.getNextAttack().getFinishedAni())
			{
				slowerPokemon.getNextAttack().setFinishedAni(false);
				decidePostAnimation(slowerPokemon);
				phase ++;
			}
		}
		
		else if(phase == 9)
		{
			if(shake != null)
			{
				shake.update();
				if(shake.getFinished())
				{
					
					fasterPokemon.resetOffsets();
					slowerPokemon.resetOffsets();
					
					shake = null;
					
					if(slowerPokemon.getNextAttack().getPower() > 0)
					{
						damage = BattleCalc.getDamage(slowerPokemon,fasterPokemon);
						if(damage < 1)
							damage = 1;
					}
					
					else
						damage = 0;
					
					if(damage <= 0)
					{
						Battle.destroyTextbox();
						phase += 2;
						BattleCalc.applyEffect(slowerPokemon, fasterPokemon);
					}
					else
						phase ++;
				}
			}
			else
			{
				if(!fasterPokemon.getFlinching())
				{
					if(slowerPokemon.getNextAttack().getPower() > 0)
						damage = BattleCalc.getDamage(slowerPokemon, fasterPokemon);
					else
						damage = 0;
					
					if(damage <= 0)
					{
						Battle.destroyTextbox();
						phase += 2;
						BattleCalc.applyEffect(slowerPokemon, fasterPokemon);
					}
					else
						phase ++;
				}
			}
		}
		
		else if(phase == 10)
		{
			float damageDecr = BattleCalc.getDamageDecr(slowerPokemon, fasterPokemon, damage);
			
			if(damage != 0)
			{
			
				if(damageDecr > damage)
					damageDecr = damage;
				
				fasterPokemon.damage(damageDecr);
				damage -= damageDecr;
				
				if(!fasterPokemon.getAlive() || damage <= 0)
				{
					damage = 0;
					
					if(!fasterPokemon.getAlive())
						fasterPokemon.setFalling(true);
					
					if(isCrit)
						Battle.setTextbox("Critical hit!",1);
					else
						Battle.destroyTextbox();
					
					if(Battle.placeHolderTextbox != null && !isCrit)
					{
						Battle.setTextbox(Battle.placeHolderTextbox);
						Battle.destroyPlaceHolder();
					}
		
					phase ++;
				}
			}
		}
		
		else if(phase == 11)
		{
			if(Battle.textbox != null)
				Battle.textbox.update();
			else
			{
				if(!isCrit)
					Battle.updateHuds();
				if(isCrit)
				{
					Battle.updateHuds();
					isCrit = false;
					if(fasterPokemon.getAlive())
						Battle.setTextbox(Battle.placeHolderTextbox);
					return;
				}
				if(fasterPokemon.getAlive())
				{
					if(slowerPokemon.getStatus() == Pokemon.PSN &&
							!(Util.valueInArray(slowerPokemon.getNextAttack().getName(), 
									Attack.doubleAttacks) && doubleCounter != 0) &&
							!(Util.valueInArray(slowerPokemon.getNextAttack().getName(),
									Attack.multiAttacks) && multiCounter != 1))
					{
						psnTick = BattleCalc.getPoisonTick(slowerPokemon);
						psnAttack = new Attack("POISON","NONE",0,255,100,"");
						
						if(slowerPokemon == Battle.playerActive)
							Battle.setTextbox(String.format("%s's#hurt by poison",slowerPokemon.getName()),2);
						else
							Battle.setTextbox(String.format("Enemy %s's#hurt by poison", slowerPokemon.getName()),2);
						
						doubleCounter = 1;
						phase = 31;
					}
					
					else if(Util.valueInArray(slowerPokemon.getNextAttack().getName(), Attack.doubleAttacks) &&
							doubleCounter == 1 && !missed)
					{
						doubleCounter --;
						phase = 7;
					}
					
					else if(Util.valueInArray(slowerPokemon.getNextAttack().getName(), Attack.multiAttacks) &&
							multiCounter != 1 && !missed)
						
					{
						multiCounter --;
						phase = 7;
					}
			
					else
					{
						if(doubleCounter == 0)
						{
							if(Battle.enemyActive == fasterPokemon)
								Battle.setTextbox("It hit the enemy#2 times!",1);
							else
								Battle.setTextbox("It hit#2 times!",1);
							
							if(slowerPokemon.getStatus() != Pokemon.PSN)
								doubleCounter = 1;
							
							phase = 99;
							phase0 = 11;
						}
						
						else if(multiCounter == 1)
						{
							if(Battle.enemyActive == fasterPokemon)
								Battle.setTextbox("It hit the enemy#" + multiCounter0 +" times!",1);
							else
								Battle.setTextbox("It hit#" + multiCounter0 + " times!",1);
							
							if(fasterPokemon.getStatus() != Pokemon.PSN)
							{
								resetMultiCounter();
							}
							
							phase = 99;
							phase0 = 11;
						}
						
						else
						{
							doubleCounter = 1;
							finished = true;
						}
					}
		
				}
				else
					if(!fasterPokemon.getFalling())
						phase ++;
			}
		}
		
		else if(phase == 12)
		{
			if(!trnActivePkmn.getAlive())
				Battle.setTextbox(String.format("Enemy %s#fainted!", trnActivePkmn.getName()), 1);
			else
				Battle.setTextbox(String.format("%s#fainted!",plrActivePkmn.getName()),1);
			
			phase ++;
		}
		
		else if(phase == 13)
		{
			if(Battle.textbox != null)
				Battle.textbox.update();
			else
			{
				if(plrActivePkmn.getAlive())
				{
					plrActivePkmn.awardXP(Util.calculateXPGain(wildBattle, trnActivePkmn));
					phase ++;
				}
				else
				{
					if(player.getDefeated())
					{
						Battle.setTextbox(String.format("%s is out#useable POKEMON!", GameInfo.PLAYERNAME),1);
						Battle.setPlaceHolderTextbox(String.format("%s blacked#out!",GameInfo.PLAYERNAME), 1);
						phase = 33;
					}
					else
					{
						pokePanel = new PokemonPanel(player.getPokemon(), true, true);
						phase = 32;
					}
				}
			}
		}
		
		else if(phase == 14)
		{
			if(Battle.textbox != null)
				Battle.textbox.update();
			else
			{
				if(plrActivePkmn.getLevelUp())
				{
					plrActivePkmn.levelUp();
					counter = new Counter(Game.STDTSIZE/2);
					phase ++;
				}
				else
				{
					counter = new Counter(Game.STDTSIZE/2);
					phase = 19;
				}
			}
		}
		
		else if(phase == 15)
		{
			if(Battle.textbox.getAlive())
				Battle.textbox.update();
			else
			{
				counter.update();
				if(!counter.getAlive())
				{
					lup = new LevelUpPanel(plrActivePkmn);
					phase ++;
				}
			}
		}
		
		else if(phase == 16)
		{
			if(Input.A_TAPPED)
			{
				lup = null;
				phase ++;
			}
		}
		
		else if(phase == 17)
		{
			if(!Util.hasNewMoves(plrActivePkmn))
			{
				counter = new Counter(Game.STDTSIZE/2);
				phase = 19;
			}
			else
			{
				
				if(!Util.arrayIsFull(plrActivePkmn.getActiveAttacks()))
				{
					String attackName = plrActivePkmn.learnNextMove();
					Battle.setTextbox(String.format("%s learned#%s!", plrActivePkmn.getName(),attackName), 1);
					phase ++;
				}
				else
				{
					String attackName = Util.getNewAttack(plrActivePkmn).getName();
					Battle.setTextbox(String.format("%s is#trying to learn#%s!", plrActivePkmn.getName(), attackName),1);
					phase = 39;
				}
			}		
		}
		
		else if(phase == 18)
		{
			if(Battle.textbox != null)
				Battle.textbox.update();
			else
			{
				phase ++;
				counter = new Counter(Main.FPS/2);
				
				if(trainer.getDefeated())
					counter.setAlive(false);
			}
		}
		
		else if(phase == 19)
		{
			if(counter != null && counter.getAlive())
				counter.update();
			else
			{
				if(wildBattle)
				{
					if(plrActivePkmn.getReadyToEvolve())
					{
						Battle.setTextbox(String.format("What? %s#is evolving!", plrActivePkmn.getName()), 2);
						phase = 48;
						return;
					}
					
					Battle.endBattle();
					return;
				}
				
				if(!trainer.getDefeated())
				{
					Battle.enemyActive = trainer.getFirstAvail();
					trnActivePkmn = Battle.enemyActive;
					Battle.updateHuds();
					counter = new Counter(Game.STDTSIZE * 3/2);
					Battle.setTextbox(String.format("%s sent#out %s!", trainer.getTrainerName(), trnActivePkmn.getName()),2);
					phase ++;
				}
				else
				{					
					Battle.setTextbox(String.format("%s defeated#%s!", GameInfo.PLAYERNAME, trainer.getTrainerName()),2);
					phase = 22;
				}
						
			}
		}
		
		else if(phase == 20)
		{
			if(Battle.textbox.getAlive())
				Battle.textbox.update();
			else
				phase ++;
		}
		
		else if(phase == 21)
		{
			counter.update();
			if(!counter.getAlive())
			{
				Battle.destroyTextbox();
				finished = true;
			}
		}
		
		else if(phase == 22)
		{
			if(Battle.textbox.getAlive())
				Battle.textbox.update();
			else
			{
				if(trainerX > trnPkmnPos[0])
				{
					trainerX -= 10;
					if(trainerX <= trnPkmnPos[0])
						trainerX = trnPkmnPos[0];
				}
				else
				{
					Battle.setTextbox(String.format("%s got $%d#for winning!", GameInfo.PLAYERNAME,trainer.getValue()), 1);
					GameInfo.PLAYERMONEY += trainer.getValue();
					phase ++;
				}
			}
		}
		
		else if(phase == 23)
		{
			if(Battle.textbox != null)
				Battle.textbox.update();
			else
			{
				if(plrActivePkmn.getReadyToEvolve())
				{
					Battle.setTextbox(String.format("What? %s#is evolving!", plrActivePkmn.getName()), 2);
					phase = 48;
					return;
				}
				
				Battle.endBattle();
				return;
			}
		}
		
		else if(phase == 30)
		{
			
			float decr = BattleCalc.getDamageDecr(fasterPokemon, slowerPokemon);
			
			
			if(Battle.textbox.getAlive())
				Battle.textbox.update();
			else
			{
				if(psnAttack.getFinishedAni())
				{
					fasterPokemon.damage(decr);
					psnTick -= decr;
					
					if(!fasterPokemon.getAlive())
					{
						Battle.textbox = null;
						fasterPokemon.setFalling(true);
						phase = 11;
						return;
					}
					
					if(psnTick <= 0)
					{
						Battle.textbox = null;
						phase = 6;
					}
				
				}
			}
		}
		
		else if(phase == 31)
		{
			float decr = BattleCalc.getDamageDecr(slowerPokemon, fasterPokemon);
			
			if(Battle.textbox.getAlive())
				Battle.textbox.update();
			else
			{
				if(psnAttack.getFinishedAni())
				{
					slowerPokemon.damage(decr);
					psnTick -= decr;
					
	
					if(!slowerPokemon.getAlive())
					{
						Battle.textbox = null;
						slowerPokemon.setFalling(true);
						phase = 5;
						return;
					}
					if(psnTick <= 0)
					{
						Battle.textbox = null;
						finished = true;
					}
				}
			}
		}
		
		else if(phase == 32)
		{
			
			if(pokePanel.getAlive())
				pokePanel.update();
			
			else
			{
				if(Battle.textbox.getAlive())
					Battle.textbox.update();
				else
				{
					if(retreatCounter.getAlive())
						retreatCounter.update();
					else
					{
						Battle.destroyTextbox();
						Battle.updateHuds();
						finished = true;
						return;
					}
				}
			}
		}
		
		else if(phase == 33)
		{
			if(Battle.textbox != null)
			{
				Battle.textbox.update();
				if(Battle.textbox == null && Battle.placeHolderTextbox != null)
				{
					Battle.setTextbox(Battle.placeHolderTextbox);
					Battle.destroyPlaceHolder();
					return;
				}
				else if(Battle.textbox == null && Battle.placeHolderTextbox == null)
				{
					Battle.destroyTextbox();
					Battle.endBattle();
					return;
				}
			}
		}
		
		else if(phase == 34)
		{
			if(Battle.textbox == null)
			{
				catchAni = new CatchAnimation(catchResult);
				phase ++;
			}
			else
			{
				Battle.textbox.update();
				if(!Battle.textbox.getAlive())
				{
					catchAni = new CatchAnimation(catchResult);
					phase ++;
				}
			}
		}
		
		else if(phase == 35)
		{
			if(catchAni.getFinishedAni())
			{
				if(!wildBattle)
					Battle.setTextbox("The trainer#blocked the ball!", 1);
				
				if(catchResult != 4)
					catchAni = null;
				
				phase ++;
			}
		}
		
		else if(phase == 36)
		{
			if(Battle.textbox == null || !Battle.textbox.getAlive())
			{
				if(!wildBattle)
					Battle.setTextbox("Don't be a thief!", 1);
				else
				{
					if(catchResult == 0)
						Battle.setTextbox("You missed the#POK" + "\u00e9" + "MON!", 1);
					else if(catchResult == 1)
						Battle.setTextbox("Darn! The POK" + "\u00e9" + "MON#broke free!", 1);
					else if(catchResult == 2)
						Battle.setTextbox("Aww! It appeared#to be caught!", 1);
					else if(catchResult == 3)
						Battle.setTextbox("Shoot! It was so#close too!", 1);
					else
						Battle.setTextbox(String.format("All right!#%s was#caught!", trnActivePkmn.getName()),1);
				}
				phase ++;
			}
			
			else
				Battle.textbox.update();
		}
		
		else if(phase == 37)
		{
			Battle.textbox.update();
			if(Battle.textbox == null)
			{
				if(catchResult != 4)
					phase = 6;
				else
				{
					Battle.setTextbox("New POK" + "\u00e9" + 
							"DEX data#will be added for#" + trnActivePkmn.getName() + "!", 1);
					phase ++;
				}
			}
		}
		
		else if(phase == 38)
		{
			Battle.textbox.update();
			if(Battle.textbox == null)
			{
				player.addPokemon(trnActivePkmn, true);
				Battle.endBattle();
			}
		}
		
		else if(phase == 39)
		{
			Battle.textbox.update();
			if(Battle.textbox == null)
			{
				Battle.setTextbox(String.format("But %s#can't learn more#than 4 moves!", plrActivePkmn.getName()),1);
				phase ++;
			}
		}
		
		else if(phase == 40)
		{
			Battle.textbox.update();
			if(Battle.textbox == null)
			{
				Battle.setTextbox(String.format("Delete an older#move to make room#for %s?",
						Util.getNewAttack(plrActivePkmn).getName()) , 2);
			}
			else if(!Battle.textbox.getAlive())
			{
				phase ++;
				moveConfIndex = 0;
			}
		}
		
		else if(phase == 41)
		{
			moveConfInput();
		}
		
		else if(phase == 42)
		{
			Battle.textbox.update();
			if(!Battle.textbox.getAlive())
			{
				phase ++;
				moveConfIndex = 0;
			}
		}
		
		else if(phase == 43)
		{
			moveDeclInput();
		}	
		
		else if(phase == 44)
		{
			if(Battle.textbox.getAlive())
				Battle.textbox.update();
			else
			{
				moveConfIndex = 0;
				phase ++;
			}
		}
		
		else if(phase == 45)
		{
			moveDeleInput();
		}
		
		else if(phase == 46)
		{
			Battle.textbox.update();
			if(Battle.textbox == null)
			{
				if(Battle.placeHolderTextbox != null)
				{
					Battle.setTextbox(Battle.placeHolderTextbox);
					Battle.destroyPlaceHolder();
				}
				else
				{
					Battle.setTextbox("And...",1);
					Battle.setPlaceHolderTextbox(String.format(
							"%s learned#%s!", plrActivePkmn.getName(),
							Util.getNewAttack(plrActivePkmn).getName()), 1);
					phase ++;
				}
			}
		}
		
		else if(phase == 47)
		{
			Battle.textbox.update();
			if(Battle.textbox == null)
			{
				if(Battle.placeHolderTextbox != null)
				{
					Battle.setTextbox(Battle.placeHolderTextbox);
					Battle.destroyPlaceHolder();
				}
				
				else
				{
					plrActivePkmn.getActiveAttacks()[moveConfIndex] = null;
					plrActivePkmn.learnNextMove();
					
					if(pokeEvo != null)
						Battle.endBattle();
					else
						phase = 18;
				}
			}
		}
		
		else if(phase == 48)
		{
			Battle.textbox.update();
			if(!Battle.textbox.getAlive())
			{
				counter = new Counter(Game.STDTSIZE);
				phase ++;
			}
		}
		
		else if(phase == 49)
		{
			counter.update();
			if(!counter.getAlive())
			{
				evo = new Pokemon(plrActivePkmn.getNextEvName(), 
						plrActivePkmn.getLevel(), true, true, 
						plrActivePkmn.getActiveAttacks(),plrActivePkmn.getStat(Pokemon.HP));
				pokeEvo = new PokemonEvolution(plrActivePkmn,evo);
				phase ++;
			}
		}
		
		else if(phase == 50)
		{
			pokeEvo.update();
			if(pokeEvo.getAniFinished())
			{
				Battle.setTextbox(String.format("%s evolved#into %s!", plrActivePkmn.getName(), evo.getName()),2);
				counter = new Counter(Game.STDTSIZE/2);
				phase ++;
			}
		}
		
		else if(phase == 51)
		{
			if(Battle.textbox.getAlive())
				Battle.textbox.update();
			else
			{
				counter.update();
				if(!counter.getAlive())
				{
					player.evolution(plrActivePkmn,evo);
					plrActivePkmn = evo;
					
					if(!Util.hasNewMoves(plrActivePkmn))
						Battle.endBattle();
					else
					{
							
						if(!Util.arrayIsFull(plrActivePkmn.getActiveAttacks()))
						{
							String attackName = plrActivePkmn.learnNextMove();
							Battle.setTextbox(String.format("%s learned#%s!", plrActivePkmn.getName(),attackName), 1);
							phase ++;
						}
						else
						{
							String attackName = Util.getNewAttack(plrActivePkmn).getName();
							Battle.setTextbox(String.format("%s is#trying to learn#%s!", plrActivePkmn.getName(), attackName),1);
							phase = 39;
						}		
					}
				}
			}
		}
		
		else if(phase == 52)
		{
			if(Battle.textbox != null)
				Battle.textbox.update();
			else
				Battle.endBattle();
		}
		
		else if(phase == 99)
		{
			Battle.textbox.update();
			if(Battle.textbox == null)
			{
				if(phase0 == 5)
					phase = 6;
				else
					finished = true;
			}
		}
		
	}
	private void moveConfInput()
	{
		if(Input.UP_TAPPED)
		{
			moveConfIndex = moveConfIndex == 1 ? 0 : moveConfIndex;
		}
		
		else if(Input.DOWN_TAPPED)
		{
			moveConfIndex = moveConfIndex == 0 ? 1 : moveConfIndex;
		}
		
		if(Input.B_TAPPED || (Input.A_TAPPED && moveConfIndex == 1))
		{
			Battle.setTextbox(String.format("Abandon learning#%s?", 
					Util.getNewAttack(plrActivePkmn).getName()), 2);
			phase ++;
		}
		
		else if(Input.A_TAPPED && moveConfIndex == 0)
		{
			Battle.setTextbox("Which move should#be forgotten?", 2);
			phase = 44;
		}
	}
	
	private void moveDeclInput()
	{
		if(Input.UP_TAPPED)
		{
			moveConfIndex = moveConfIndex == 1 ? 0 : moveConfIndex;
		}
		
		else if(Input.DOWN_TAPPED)
		{
			moveConfIndex = moveConfIndex == 0 ? 1 : moveConfIndex;
		}
		
		if(Input.B_TAPPED || (Input.A_TAPPED && moveConfIndex == 1))
		{
			Battle.setTextbox(String.format("Abandon learning#%s?", 
					Util.getNewAttack(plrActivePkmn).getName()), 2);
			phase = 17;
		}
		
		else if(Input.A_TAPPED && moveConfIndex == 0)
		{
			Battle.setTextbox(String.format("%s#did not learn#%s!", plrActivePkmn.getName(), 
					Util.getNewAttack(plrActivePkmn).getName()),1);
			
			if(pokeEvo != null)
				phase = 52;
			else
				phase = 18;
		}
	}
	
	private void moveDeleInput()
	{
		if(Input.UP_TAPPED)
		{
			if(moveConfIndex - 1 >= 0)
				moveConfIndex --;
		}
		
		else if(Input.DOWN_TAPPED)
		{
			if(moveConfIndex + 1 < 4)
				moveConfIndex ++;
		}
		
		if(Input.A_TAPPED)
		{
			Battle.setTextbox("1, 2, and... Poof!", 1);
			Battle.setPlaceHolderTextbox(String.format("%s forgot#%s!", 
					plrActivePkmn.getName(), plrActivePkmn.getActiveAttacks()[moveConfIndex].getName()), 1);
			phase ++;
		}
		
		else if(Input.B_TAPPED)
		{
			Battle.setTextbox(String.format("Abandon learning#%s?", 
					Util.getNewAttack(plrActivePkmn).getName()), 2);
			phase = 42;
		}
	}
	
	private void resetMultiCounter()
	{
		multiCounter = new Random().nextInt(4) + 2;
		multiCounter0 = multiCounter;
	}
	
	private void itemUse()
	{
		if(BSOptions.itemToUse.getEffect().equals("BALL"))
		{
			player.getBag().tossItem(BSOptions.itemToUse.getName(), 1);

			if(!wildBattle)
				catchResult = 0;
			else
				catchResult = BattleCalc.getCatchResult(trnActivePkmn, BSOptions.itemToUse);
		
			if(wildBattle)	
				Battle.setTextbox(String.format("%s used#%s!", GameInfo.PLAYERNAME, BSOptions.itemToUse.getName()), 2);

			BSOptions.itemToUse = null;
			phase = 34;
		}
	}
	private void decidePostAnimation(Pokemon attacker)
	{
		if(attacker == plrActivePkmn)
		{
			if(attacker.getNextAttack().getPower() > 0)
			{
				if(attacker.getNextAttack().isSpecialMove())
					shake = new ScreenShake(ScreenShake.LEFT);
				else
					trnActivePkmn.setFlinching(true);
			}
			else
				if(attacker.getNextAttack().hasShake())
					shake = new ScreenShake(ScreenShake.LEFT);
		}
		else
		{
			if(attacker.getNextAttack().getPower() > 0)
			{
				if(attacker.getNextAttack().isSpecialMove())
					shake = new ScreenShake(ScreenShake.RIGHT);
				else
					shake = new ScreenShake(ScreenShake.DOWN);
			}
			else
				if(attacker.getNextAttack().hasShake())
					shake = new ScreenShake(ScreenShake.LEFT_EXT);
		}
	}
	
	public void render(Graphics2D g)
	{
		
		if(plrActivePkmn.getAlive() || plrActivePkmn.getFalling())
			//if(!(phase == 6 && plrActivePkmn != Battle.playerActive))
				plrActivePkmn.render(g, plrPkmnPos,true);
		if((trnActivePkmn.getAlive() || trnActivePkmn.getFalling()) && phase != 20)
		{
			if(phase != 33)
				trnActivePkmn.render(g, trnPkmnPos,false);
			else
				g.drawImage(trnActivePkmn.getBattleImages()[1], trnPkmnPos[0], trnPkmnPos[1], Battle.IMAGE_SIZE, Battle.IMAGE_SIZE, null);
		}
		
		g.setColor(PokemonHud.WHITE);
	
		Battle.getHud(true).render(g);
		
		if(phase != 19  && phase != 20 && (trnActivePkmn.getAlive() || trnActivePkmn.getFalling()))
		{
			if(phase == 33)
				Battle.getHud(false).render(g,true);
			else
				Battle.getHud(false).render(g);
		}
		
		if(phase == 2)
		{
			if(fasterPokemon == plrActivePkmn)
				fasterPokemon.getNextAttack().render(g, true);
			else
				fasterPokemon.getNextAttack().render(g, false);
			fasterPokemon.animate();
			
		}
		
		else if(phase == 5 && !slowerPokemon.getAlive() && Battle.textbox == null)
		{
			slowerPokemon.animate();
			
			if(!slowerPokemon.getPlayer())
			{
				drawInvRect(g);
				Battle.getHud(true).render(g);
			}
			
			else
				g.drawImage(Battle.staticTb, 0, Main.HEIGHT - Battle.staticTb.getHeight(), null);
		}
			
		else if(phase == 8)
		{
			if(slowerPokemon == plrActivePkmn)
				slowerPokemon.getNextAttack().render(g, true);
			else
				slowerPokemon.getNextAttack().render(g, false);
			
			slowerPokemon.animate();
		}
		
		else if(phase == 11 && !fasterPokemon.getAlive() && Battle.textbox == null)
		{
			fasterPokemon.animate();
			
			if(!fasterPokemon.getPlayer())
			{
				drawInvRect(g);
				Battle.getHud(true).render(g);
			}
			
			else
				g.drawImage(Battle.staticTb, 0, Main.HEIGHT - Battle.staticTb.getHeight(), null);
		}
		
		else if(phase == 16 && lup != null)
			lup.render(g);
		
		else if(phase == 19 && !trainer.getDefeated())
			PokemonHud.drawPokemonBalls(1, wildBattle, player, trainer, g);
		
		else if(phase == 22 && !Battle.textbox.getAlive())
			g.drawImage(trainer.getBattleImages()[0], trainerX, trnPkmnPos[1], null);
		
		else if(phase == 23 || phase == 48 || (phase == 49 && pokeEvo == null))
			g.drawImage(trainer.getBattleImages()[0], trainerX, trnPkmnPos[1], null);
		
		else if(phase == 30 && !psnAttack.getFinishedAni() && !Battle.textbox.getAlive())
		{
			if(fasterPokemon == Battle.playerActive)
				psnAttack.render(g, true);
			else
				psnAttack.render(g,false);
		}
		else if(phase == 31 && !psnAttack.getFinishedAni() && !Battle.textbox.getAlive())
		{
			if(slowerPokemon == Battle.playerActive)
				psnAttack.render(g, true);
			else
				psnAttack.render(g, false);
		}
		
		if(pokeEvo != null)
			pokeEvo.render(g);
		
		if(phase == 41 || phase == 43)
		{
			g.drawImage(confImage, confPos[0], confPos[1], null);
			g.setFont(PokemonHud.FONT);
			g.setColor(PokemonHud.BLACK);
			g.drawString("YES",confStringPos[0], confStringPos[1]);
			g.drawString("NO", confStringPos[0], confStringPos[1] + 60);
			if(moveConfIndex == 0)
				g.drawImage(cursorImage, confCursorPos[0], confCursorPos[1], null);
			else
				g.drawImage(cursorImage, confCursorPos[0], confCursorPos[1] + 60, null);
		}
		
		if(catchAni != null)
			catchAni.render(g);
		if(pokePanel != null && pokePanel.getAlive())
			pokePanel.render(g);
	}
	
	public void drawOnTop(Graphics2D g)
	{
		g.setFont(PokemonHud.FONT);
		g.setColor(PokemonHud.BLACK);
		g.drawImage(moveDeleImage, moveDelePos[0], moveDelePos[1], null);
		for(int i = 0; i < 4; i ++)
			g.drawString(plrActivePkmn.getActiveAttacks()[i].getName(), 
					moveDeleStringPos[0], moveDeleStringPos[1] + i * 30);
		g.drawImage(cursorImage, moveDeleCursorPos[0], moveDeleCursorPos[1] + moveConfIndex * 30, null);
	}
	
	private void drawInvRect(Graphics2D g)
	{
		g.setColor(PokemonHud.WHITE);
		g.fillRect(BSIntro.FINAL_TRN_POS[0],BSIntro.FINAL_TRN_POS[1] + Battle.IMAGE_SIZE,
				   Battle.IMAGE_SIZE, Battle.IMAGE_SIZE/2);
	}
	
	public int getPhase()
	{
		return phase;
	}
}
