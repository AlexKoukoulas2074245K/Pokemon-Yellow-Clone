package misc;

import java.util.ArrayList;

import util.Util;

public class Bag {
	
	private ArrayList<String> items;
	private ArrayList<Integer> quantities;
	
	public Bag()
	{
		items = new ArrayList<String>();
		quantities = new ArrayList<Integer>();
		
		addItem("CANCEL",0);
	}
	
	public void addItem(String item, int quant)
	{
		if(items.contains(item) && quant != 0)
		{
			int index = Util.lastIndex(item,items);
			
			if(quantities.get(index) + quant < 100)
				quantities.set(index, quantities.get(index) + quant);
			
			else
			{
				if(!item.equals("CANCEL"))
				{
					items.set(size() - 1, item);
					quantities.set(size() - 1, quant);
				}
				
				items.add("CANCEL");
				quantities.add(0);
			}
		}
		else
		{
			if(!item.equals("CANCEL"))
			{
				items.set(size() - 1, item);
				quantities.set(size() - 1, quant);
			}
			
			items.add("CANCEL");
			quantities.add(0);
		}
	}
	
	public void tossItem(String item, int quant)
	{
		if(!items.contains(item)) return;
		
		int index = Util.lastIndex(item,items);
		
		quantities.set(index, quantities.get(index) - quant);
		
		if(quantities.get(index) < 1)
		{
			quantities.remove(index);
			items.remove(index);
		}
	}
	
	public String getItem(int index)
	{
		return items.get(index);
	}
	
	public ArrayList<String> getItems()
	{
		return items;
	}
	
	public ArrayList<Integer> getQuants()
	{
		return quantities;
	}
	
	public int getItemIndex(String item)
	{
		return items.indexOf(item);
	}
	
	public int getQuant(int index)
	{
		return quantities.get(index);
	}
	
	public int size()
	{
		return items.size();
	}
	
	public boolean itemInBag(String itemName)
	{
		for(String name: items)
		{
			if(name.equals(itemName))
			{
				return true;
			}
		}
		return false;
	}
	
	public int getQuant(String item)
	{
		return quantities.get(items.indexOf(item));
	}
}
