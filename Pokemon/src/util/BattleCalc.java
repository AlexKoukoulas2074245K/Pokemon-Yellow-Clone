package util;

import java.util.Random;

import misc.Attack;
import misc.Item;
import battleStates.BSMain;
import entities.Pokemon;
import entities.SPlayer;
import gameStates.Battle;

public class BattleCalc {
	
	public static boolean getRunResult(SPlayer player, Pokemon plrActive, Pokemon enemy)
	{
		int nEscapes = player.getEscapes();
		float plrSpeed = plrActive.getStat(Pokemon.SPEED);
		float eneSpeed = enemy.getStat(Pokemon.SPEED);
		
		float a = plrSpeed;
		float b = (eneSpeed/4)%256;
		float c = nEscapes;
		
		float prob = (a * 32)/b + 30*c;
		int rand = new Random().nextInt(255);
		
		if(prob > 255)
			return true;
		else
			return rand < prob;
	}
	
	public static int getDamage(Pokemon attacker, Pokemon defender)
	{
		Attack nextAttack = attacker.getNextAttack();
		
		float aLevel = attacker.getLevel();
		float aAttack = attacker.getStat(Pokemon.ATTACK);
		float dDefense = defender.getStat(Pokemon.DEFENSE);
		float aPower = nextAttack.getPower();
		
		if(attacker.getNextAttack().isSpecialMove())
		{
			aAttack = (int)attacker.getStat(Pokemon.SPECIAL);
			dDefense = (int)defender.getStat(Pokemon.SPECIAL);
		}
		
		
		//critical hit
		float aSpeed = attacker.getStat(Pokemon.SPEED);

		if(Util.valueInArray(attacker.getNextAttack().getName(), Attack.highCrit))
			BSMain.isCrit = new Random().nextInt(100) <= aSpeed * 100f/64;
		else
			BSMain.isCrit = new Random().nextInt(100) <= aSpeed * 100f/512;
		
		if(BSMain.isCrit)
			aLevel *= 2;
		//Core formula
		int	damage;
		if(BSMain.isCrit)
			damage = (int)((int)((int)(2 * aLevel/ 5 + 2) * attacker.getInitStat(Pokemon.ATTACK)* aPower / defender.getInitStat(Pokemon.DEFENSE))/50);
		else
			damage = (int)((int)((int)(2 * aLevel/ 5 + 2) * aAttack * aPower / dDefense)/50);
		
		
		//Stab attachment
		if(attacker.getTypes().length == 1)
		{
			if(attacker.getTypes()[0].equals(nextAttack.getType()))
				damage *= 1.5;
		}
		else
		{
			if(attacker.getTypes()[0].equals(nextAttack.getType()) || 
			   attacker.getTypes()[1].equals(nextAttack.getType()))
			   
			    damage *= 1.5;
		}	
		//Effectiveness
		float eff = getEffectiveness(attacker,defender);
		damage *= eff;
		
		if(eff == 1)
			Battle.destroyPlaceHolder();
		else if(eff < 1)
			Battle.setPlaceHolderTextbox("It's not very#effective...", 1);
		else if(eff > 1)
			Battle.setPlaceHolderTextbox("It's super#effective!", 1);
		
		damage += 2;
		
		//Damage Variance		
		float var = 1;
		if(damage < 768)
			var = ((float)new Random().nextInt(255 - 217) + 217) / 255;
		
		damage *= var;
		return damage;
	}
	
	public static float getEffectiveness(Pokemon attacker, Pokemon defender)
	{
		float eff = ResourceLoader.getEff(attacker.getNextAttack().getType(), defender.getTypes()[0]);
		
		if(defender.getTypes().length == 2)
			eff *= ResourceLoader.getEff(attacker.getNextAttack().getType(), defender.getTypes()[1]);
		
		return eff;
	}
	
	public static float getDamageDecr(Pokemon pokemon1, Pokemon pokemon2)
	{
		return (float)((float)(pokemon1.getLevel() + pokemon2.getLevel())/10 * 0.05f + 0.12f); 
	}
	
	public static float getDamageDecr(Pokemon pokemon1, Pokemon pokemon2, float damage)
	{
		float damageDecr = (float)((float)(pokemon1.getLevel() + pokemon2.getLevel())/10 * 0.05f + 0.17f);
	    
		return (float)(Math.round(damageDecr*10)/10f);
	}
	
	public static boolean getMiss(Pokemon attacker, Pokemon defender)
	{
		float acc = attacker.getAccur() * attacker.getNextAttack().getAccur();
		int rand = new Random().nextInt(255);
		
		return rand > acc;
	}
	
	public static boolean getParalysis()
	{
		return new Random().nextInt(100) < 25;
	}
	
	public static int getPoisonTick(Pokemon pokemon)
	{
		return (int)(pokemon.getMaxHp() * 1/16f);
	}
	
	public static void applyEffect(Pokemon attacker, Pokemon defender)
	{
		
		String effect = attacker.getNextAttack().getEffect();
		if(effect.length() < 1)	return;
		
		//Status effects
		
		if(effect.equals("EPAR"))
		{
			if(attacker == Battle.playerActive)
				Battle.setTextbox(String.format("Enemy %s's#paralyzed! It may#not attack!",defender.getName()),1);
			else
				Battle.setTextbox(String.format("%s's#paralyzed! It may#not attack!", defender.getName()),1);
			
			defender.setStatus(Pokemon.PAR);
			return;
		}
		
		else if(effect.equals("EPSN"))
		{
			if(attacker == Battle.playerActive)
				Battle.setTextbox(String.format("Enemy %s's#poisoned!", defender.getName()),1);
			else
				Battle.setTextbox(String.format("%s's#poisoned!",defender.getName()),1);
			
			defender.setStatus(Pokemon.PSN);
			return;
		}
		
		//Attribute changes
		int modifier = Integer.parseInt(effect.substring(2, 4));
		char statMod = effect.charAt(1);
		
		String effSev = "";
		
		if(effect.charAt(0) == 'E')
		{
			if(modifier < 0)
			{
				defender.decreaseStage(Attack.statModifiers.get(statMod), -modifier);
				effSev = modifier == -1 ? "fell" : "greatly fell";
			}
			else
			{
				defender.increaseStage(Attack.statModifiers.get(statMod), modifier);
				effSev = modifier == 1 ? "rose" : "greatly rose";
			}
			
			attacker.adjustStats();
			defender.adjustStats();
			
			if(Battle.textbox != null) return;
			
			if(defender == Battle.playerActive)
				Battle.setTextbox(String.format("%s's#%s %s!",defender.getName(), Attack.statModifierNames.get(statMod), effSev), 1);
			else
				Battle.setTextbox(String.format("Enemy %s's#%s %s!",defender.getName(), Attack.statModifierNames.get(statMod), effSev), 1);
		}
		else
		{
			if(modifier < 0)
			{
				attacker.decreaseStage(Attack.statModifiers.get(statMod), -modifier);
				effSev = modifier == -1 ? "fell" : "#greatly fell";
			}
			else
			{
				attacker.increaseStage(Attack.statModifiers.get(statMod), modifier);
				effSev = modifier == 1 ? "rose" : "#greatly rose";
			}
			
			attacker.adjustStats();
			defender.adjustStats();
			
			if(Battle.textbox != null) return;
			
			if(attacker == Battle.playerActive)
				Battle.setTextbox(String.format("%s's#%s %s!",attacker.getName(), Attack.statModifierNames.get(statMod), effSev), 1);
			else
				Battle.setTextbox(String.format("Enemy %s's#%s %s!",attacker.getName(), Attack.statModifierNames.get(statMod), effSev), 1);
		}
	
	}
	
	public static int getCatchResult(Pokemon pkmn, Item ball)
	{
		
		if(ball.getName().equals("MASTER BALL")) return 4;
		
		Random rand = new Random();
		
		int R1;
		
		if(ball.getName().equals("POKE BALL"))
		{
			R1 = rand.nextInt(256);
		}
		else if(ball.getName().equals("GREAT BALL"))
			R1 = rand.nextInt(201);
		else
			R1 = rand.nextInt(151);
			
		int S;
		if(pkmn.getStatus() == Pokemon.OK)
			S = 0;
		else if(pkmn.getStatus() == Pokemon.SLP || pkmn.getStatus() == Pokemon.FRZ)
			S = 25;
		else
			S = 12;
		
		R1 -= S;
		
		if(R1 < 0)
			return 4;
		
		int F;
		
		F = (int)(pkmn.getMaxHp()) * 255;
		
		if(ball.getName().equals("GREAT BALL"))
			F /= 8;
		else
			F /= 12;
		
		int halfHp = (int)(pkmn.getStat(Pokemon.HP))/4;
		
		if(halfHp > 0)
			F /= halfHp;
		
		if(F > 255)
			F = 255;
		
		int catchRate = (int)pkmn.getCatchRate();
		
		if(catchRate < R1)
			return breakFree(pkmn,ball, F);
		
		else
		{
			int R2 = rand.nextInt(256);
			
			if(R2 <= F)
				return 4;
			else
				return breakFree(pkmn,ball, F);
		}
	}
	
	private static int breakFree(Pokemon pkmn, Item ball,int F)
	{
		int W = (int)(pkmn.getCatchRate()) * 100;
		
		if(ball.getName().equals("POKE BALL"))
			W /= 255;
		else if(ball.getName().equals("GREAT BALL"))
			W /= 200;
		else
			W /= 150;
		
		if(W > 255)
			return 3;
		
		W *= F;
		
		W /= 255;
		
		if(pkmn.getStatus() != Pokemon.OK)
		{
			if(pkmn.getStatus() == Pokemon.SLP || pkmn.getStatus() == Pokemon.FRZ)
				W += 10;
			else
				W += 5;
		}
		
		if(W < 10)
			return 0;
		
		else if(W >= 10 && W <= 29)
			return 1;
		
		else if(W >= 30 && W <= 69)
			return 2;
		
		else
			return 3;
	}
}
