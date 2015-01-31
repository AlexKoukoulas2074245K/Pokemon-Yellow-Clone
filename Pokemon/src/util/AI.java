package util;

import java.util.Random;

import misc.Attack;
import entities.Pokemon;

public class AI {
	
	public static Attack getAttack(Pokemon pokemon)
	{
		int maxIndex = Util.getMaxIndex(pokemon.getActiveAttacks());
		return pokemon.getActiveAttacks()[new Random().nextInt(maxIndex + 1)];
	}
}
