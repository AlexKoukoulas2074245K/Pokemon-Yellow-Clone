package util;

import java.util.ArrayList;
import java.util.Random;

import misc.Attack;
import misc.Item;
import entities.Pokemon;
import entities.Textbox;

public class Util {
	
	public static void reverse(Attack[] attacks)
	{
		ArrayList<Attack> rev = new ArrayList<Attack>();
		for(int i = attacks.length - 1; i > - 1; i --)
			if(attacks[i] != null)
				rev.add(attacks[i]);
		
		for(int i = 0; i < rev.size(); i ++)
			attacks[i] = rev.get(i);
	}
	
	public static int getMaxIndex(Object[] array)
	{	
		for(int i = 0; i < array.length; i ++)
			if(array[i] == null)
				return i - 1;
		
		return array.length - 1;
	}
	
	public static int getEmblemN(boolean[] emblems)
	{
		int counter = 0;
		for(boolean emblem: emblems)
			if(emblem) counter ++;
		
		return counter;
	}
	
	public static int lastIndex(int item, ArrayList<Integer> array)
	{
		for(int i = array.size() - 1; i >= 0 ; i --)
			if(array.get(i) == item)
				return i;
		
		return 0;
	}
	
	public static int lastIndex(String item, ArrayList<String> array)
	{
		for(int i = array.size() - 1; i >= 0 ; i --)
			if(array.get(i).equals(item))
				return i;
		
		return 0;
	}
	
	public static int[] stringToTuple(String input)
	{
		String[] splitString = input.split(",");
		
		return new int[]{Integer.parseInt((splitString[0])),
						 Integer.parseInt(splitString[1])};
	}
	
	public static Pokemon[] getPokemon(String input)
	{
		String[] splitString = input.split(",");
		Pokemon[] result = new Pokemon[splitString.length];
		
		for(int i = 0; i < splitString.length; i ++)
		{
			String[] components = splitString[i].split("/");
			result[i] = new Pokemon(components[0],Integer.parseInt(components[1]), false);
		}
		
		return result;
	}
	
	public static boolean arrayIsFull(Attack[] array)
	{
		for(Attack attack: array)
			if(attack == null) return false;
		return true;
	}
	
	public static boolean valueInArray(Attack val, Attack[] array)
	{
		for(Attack attack: array)
		{
			if(attack == null) continue;
	
			if(attack.getName().equals(val.getName()))
				return true;
		}
		return false;
	}
	
	public static boolean arrayContains(ArrayList<int[]> array, int[] value)
	{
		for(int[] other: array)
			if(other[0] == value[0] && other[1] == value[1])
				return true;
		return false;
	}
	
	public static boolean valueInArray(String name, String[] array)
	{
		for(String other: array)
			if(other.equals(name))
				return true;
		return false;
	}
	
	public static boolean valueInArray(String name, Attack[] array)
	{
		for(Attack other: array)
		{
			if(other == null)
				continue;
			
			if(other.getName() == name)
				return true;
		}
		return false;
	}
	
	public static void insertValue(Object val, Object[] array)
	{
		for(int i = 0; i < array.length; i ++)
			if(array[i] == null)
			{
				array[i] = val;
				return;
			}
	}
	
	public static boolean hasNewMoves(Pokemon pokemon)
	{
		ArrayList<Attack> learnSet = pokemon.getLearnSet();
		
		for(int i = 0; i < learnSet.size(); i++)
		{
			
			if(learnSet.get(i) == null)
				continue;
			
			if(valueInArray(learnSet.get(i).getName(),pokemon.getActiveAttacks()))
				continue;
			else
			{
				
				if(pokemon.getAttackLevels().get(i) != pokemon.getLevel())
					continue;
				else
					return true;
			}
		}
		return false;
	}
	
	public static Attack getNewAttack(Pokemon pokemon)
	{
		ArrayList<Attack> learnSet = pokemon.getLearnSet();
		
		for(int i = 0; i < learnSet.size(); i++)
		{
			if(learnSet.get(i) == null)
				continue;
			
			if(valueInArray(learnSet.get(i).getName(),pokemon.getActiveAttacks()))
				continue;
			else
			{
				if(pokemon.getAttackLevels().get(i) != pokemon.getLevel())
					continue;
				else
					return learnSet.get(i);
			}
		}
		return null;
	}
	
	public static Textbox generateTextbox(String message) {
		
		return new Textbox(message,0);
	}
	
	public static Textbox generateTextbox(String message,int flag)
	{
		return new Textbox(message, flag);
	}
	
	public static int calculateXP(int level, int group)
	{
		if(group == Pokemon.FAST)
			return (int)((4f/5) * Math.pow(level, 3));
		
		else if(group == Pokemon.MEDIUM_FAST)
			return (int)(Math.pow(level, 3));
		
		else if(group == Pokemon.MEDIUM_SLOW)
			return (int)((6f/5) * Math.pow(level, 3) - 15 * Math.pow(level, 2) + 100 * level - 140);
		
		else
			return (int)((5f/4) * Math.pow(level, 3));
	}
	
	public static int calculateXPGain(boolean wildBattle, Pokemon other)
	{
		float mult = wildBattle ? 1.5f : 2f ;
		
		return (int)((mult * other.getXpStat() * other.getLevel())/ 7);
	}

	public static void cancelAttacks(Pokemon[] pokemon) 
	{
		for(Pokemon pkmn: pokemon)
		{
			if(pkmn != null)
				pkmn.setNextAttack(null);
		}
	}
	
	public static String getRandomID()
	{
		String id = "";
		
		Random randGen = new Random();
		for(int i = 0; i < 6; i ++)
			id += String.valueOf(randGen.nextInt(10));
		
		return id;
	}
	
	public static Item getItemFromMessage(String message)
	{
		String indicator = GameInfo.PLAYERNAME + " got#";
		String objIndicator = GameInfo.PLAYERNAME +" found#";
		
		int startingIndex = 0;
		
		if(!message.contains(indicator))
		{
			indicator = objIndicator;
			if(!message.contains(indicator))
				return null;
		}
		
		startingIndex = message.indexOf(indicator) + indicator.length();
		
		String itemName = "";
		
		int i = startingIndex;
		
		while(message.charAt(i) != '!')
		{
			itemName += String.valueOf(message.charAt(i));
			i ++;
		}
		

		if(itemName.equals(""))
			return null;
		else
			return ResourceLoader.getItem(itemName);
	}
	
	public static int noOccurrences(char element, String sentence, int finalIndex)
	{
		int counter = 0;
		
		for(int i = 0; i < finalIndex; i ++)
			if(sentence.charAt(i) == element)
				counter ++;
		
		return counter;
	}
	
	
	
	

}
