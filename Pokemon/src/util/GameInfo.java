package util;

import main.Game;

public class GameInfo {
	
	public static String PLAYERNAME = "Alex";
	public static String RIVALNAME = "Aim";
	
	public static int PLAYERMONEY = 100;
	
	public static int[] PALLETHOME = new int[]{9 * Game.STDTSIZE,13 * Game.STDTSIZE};
	public static int[] VIRIDIANHOME = new int[]{23 * Game.STDTSIZE, 26 * Game.STDTSIZE};
	public static int[] PEWTERHOME = new int[]{14 * Game.STDTSIZE, 29 * Game.STDTSIZE};
	public static int[] ROUTE3HOME = new int[]{66 * Game.STDTSIZE, 8 * Game.STDTSIZE};
	
	public static String BROCKAFTERSPEECH = "There are all#kinds of trainers#in the world!#"
											+ "@Some raise POKEMON#for fights. Some#see "
											+ "them as pets.#@I'm in training to#become a "
											+ "POKEMON#breeder.#@If you take your#POKEMON training#"
											+ "seriously, go#visit the GYM in#CERULEAN"
											+ " and test#your abilities!";
	
	public static int[] getHomePos(String home)
	{
		if(home.equals("pallet"))
			return PALLETHOME;
		else if(home.equals("viridian"))
			return VIRIDIANHOME;
		else if(home.equals("pewter"))
			return PEWTERHOME;
		else if(home.equals("route3"))
			return ROUTE3HOME;
		else
			return PALLETHOME;
	}
}
