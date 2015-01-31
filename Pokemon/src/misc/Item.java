package misc;

import java.util.HashMap;

public class Item {
	
	public static HashMap<String,Integer> EMBLEMS;
	
	static
	{
		EMBLEMS = new HashMap<String,Integer>();
		EMBLEMS.put("BOULDERBADGE", 0);
	}
	
	private String name;
	
	private boolean usedInBattle;
	private boolean usedInOvworld;
	private boolean unique;
	
	private String effect;
	
	private int buyingPrice;
	private int sellingPrice;
	
	public Item(String name, boolean battle, boolean ovworld,
				boolean unique, String effect,
				int buyingPrice, int sellingPrice)
	{
		this.name = name;
		this.usedInBattle = battle;
		this.usedInOvworld = ovworld;
		this.unique = unique;
		this.effect = effect;
		this.buyingPrice = buyingPrice;
		this.sellingPrice = sellingPrice;
	}

	public String getName() 
	{
		if(name.equals("POK" + "\u00e9" + " BALL"))
			return "POKE BALL";
		else
			return name;
	}
	
	public boolean isPotion()
	{
		return effect.substring(2).equals("HP");
	}
	
	public int getEffectPower()
	{
		return Integer.parseInt(effect.substring(0,2));
	}
	
	public boolean isUsedInBattle()
	{
		return usedInBattle;
	}

	public boolean isUsedInOvworld() 
	{
		return usedInOvworld;
	}

	public boolean isUnique() 
	{
		return unique;
	}

	public String getEffect() 
	{
		return effect;
	}

	public int getBuyingPrice() 
	{
		return buyingPrice;
	}

	public int getSellingPrice() 
	{
		return sellingPrice;
	}
	

	
}
