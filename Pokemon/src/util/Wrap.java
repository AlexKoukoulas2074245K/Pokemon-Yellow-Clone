package util;

import entities.Map;

public class Wrap {
	
	
	private String area;
	private String building;
	
	private boolean isDoor;
	private boolean isAreaConnection;
	private boolean isInside;
	
	private int[] rgb;
	private int col;
	private int row;
	
	public Wrap(String area, String building, int[] rgb,int col,int row)
	{
		this.area = area;
		this.building = building;
		this.rgb = rgb;
		this.col = col;
		this.row = row;
		
		this.isInside = rgb[0] == 0 && rgb[1] == 128;
		this.isDoor = rgb[0] == 20;
		this.isAreaConnection = rgb[0] == 255 || rgb[0] == 254 ||
								rgb[0] == 248 || rgb[0] == 240 || 
								rgb[0] == 200 || rgb[0] == 160 ||
								rgb[0] == 148 || rgb[0] == 128 ||
								rgb[0] == 120;
	}
	
	public Wrap(String area,String building, int[] rgb)
	{
		int[] result = Map.findColRow(area,building,rgb);
		this.area = area;
		this.building = building;
		this.rgb = rgb;
		this.col = result[0];
		this.row = result[1];
		
		this.isInside = rgb[0] == 0 && rgb[1] == 128;
		this.isDoor = rgb[0] == 20;
		this.isAreaConnection = rgb[0] == 255 || rgb[0] == 254 ||
								rgb[0] == 248 || rgb[0] == 240 || 
								rgb[0] == 200 || rgb[0] == 160 ||
								rgb[0] == 148 || rgb[0] == 128 ||
								rgb[0] == 120;
		
	}
	
	public Wrap findWrapExit()
	{
		if(area.equals("pallet"))
		{
			if(building.equals("default"))
			{
				if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 255)
					return new Wrap("pallet","playerHouse2",new int[]{20,0,200});
				else if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 200)
					return new Wrap("pallet","rivalHouse",new int[]{20,0,200});
				else if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 100)
					return new Wrap("pallet","oaksLab",new int[]{20,0,200});	
				else if(rgb[0] == 255 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("route1","default",new int[]{255,0,0});
				else if(rgb[0] == 128 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("route1","default",new int[]{128,0,0});
			}
			
			else if(building.equals("playerHouse2"))
			{
				if(rgb[0] == 20 && rgb[1] == 0 && rgb[2] == 200)
					return new Wrap("pallet","default",new int[]{0,0,255});		
				else if(rgb[0] == 0 && rgb[1] == 128 && rgb[2] == 255)
					return new Wrap("pallet","playerHouse1",new int[]{0,128,255});
			}
			
			else if(building.equals("playerHouse1"))
				return new Wrap("pallet","playerHouse2",new int[]{0,128,255});
			else if(building.equals("rivalHouse"))
				return new Wrap("pallet","default",new int[]{0,0,200});
			else if(building.equals("oaksLab"))
				return new Wrap("pallet","default", new int[]{0,0,100});
			
		}
	
		
		else if(area.equals("route1") && building.equals("default"))
		{
			if(rgb[0] == 255 && rgb[1] == 0 && rgb[2] == 0)
				return new Wrap("pallet","default",new int[]{255,0,0});
			else if(rgb[0] == 128 && rgb[1] == 0 && rgb[2] == 0)
				return new Wrap("pallet","default",new int[]{128,0,0});
			else if(rgb[0] == 248 && rgb[1] == 0 && rgb[2] == 0)
				return new Wrap("viridian","default",new int[]{248,0,0});
			else if(rgb[0] == 148 && rgb[1] == 0 && rgb[2] == 0)
				return new Wrap("viridian", "default", new int[]{148,0,0});
		}
		
		else if(area.equals("viridian"))
		{
			if(building.equals("default"))
			{
				if(rgb[0] == 255 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("route2","default",new int[]{255,0,0});
				else if(rgb[0] == 254 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("route2","default",new int[]{200,0,0});
				else if(rgb[0] == 248 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("route1","default",new int[]{248,0,0});
				else if(rgb[0] == 148 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("route1","default",new int[]{148,0,0});
				else if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 255)
					return new Wrap("viridian","viridian1",new int[]{20,0,200});
				else if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 200)
					return new Wrap("viridian","viridian2",new int[]{20,0,200});
				else if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 100)
					return new Wrap("viridian","viridianPokeCenter", new int[]{20,0,200});
				else if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 50)
					return new Wrap("viridian","viridianMarket",new int[]{20,0,200});
				else if(rgb[0] == 240 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("route22","default",new int[]{240,0,0});
				else if(rgb[0] == 200 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("route22","default",new int[]{200,0,0});
				else if(rgb[0] == 160 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("route22","default",new int[]{160,0,0});
				else if(rgb[0] == 120 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("route22","default",new int[]{120,0,0});
			}
			
			else if(building.equals("viridian1"))
				return new Wrap("viridian","default",new int[]{0,0,255});
			else if(building.equals("viridian2"))
				return new Wrap("viridian","default",new int[]{0,0,200});
			else if(building.equals("viridianPokeCenter"))
				return new Wrap("viridian","default",new int[]{0,0,100});
			else if(building.equals("viridianMarket"))
				return new Wrap("viridian","default",new int[]{0,0,50});
		}
		
		
		else if(area.equals("route2"))
		{
			if(building.equals("default"))
			{
				if(rgb[0] == 255 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("viridian","default",new int[]{255,0,0});
				else if(rgb[0] == 200 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("viridian","default",new int[]{254,0,0});
				else if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 200)
					return new Wrap("route2","route2Building", new int[]{20,0,200});
			}
			
			else if(building.equals("route2Building"))
			{
				if(rgb[0] == 20 && rgb[1] == 0 && rgb[2] == 200)
					return new Wrap("route2","default",new int[]{0,0,200});
				else if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 255)
					return new Wrap("viridianForest","default",new int[]{20,0,200});
			}

		}
		
		else if(area.equals("viridianForest"))
		{
			if(building.equals("default"))
			{
				if((rgb[0] == 20 && rgb[1] == 0 && rgb[2] == 200) ||
				   (rgb[0] == 20 && rgb[1] == 0 && rgb[2] == 255))
				   return new Wrap("route2","route2Building",new int[]{0,0,255});
				
				else if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 255)
					return new Wrap("viridianForest","viridianForestBuilding",new int[]{20,0,200});
			}
				
			else if(building.equals("viridianForestBuilding"))
			{
				if(rgb[0] == 20 && rgb[1] == 0 && rgb[2] == 200)
					return new Wrap("viridianForest","default",new int[]{0,0,255});
				else if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 255)
					return new Wrap("route2Cont","default", new int[]{20,0,200});
			}
		}

		
		else if(area.equals("route2Cont") && building.equals("default"))
		{
			if(rgb[0] == 20 && rgb[1] == 0 && rgb[2] == 200)
				return new Wrap("viridianForest","viridianForestBuilding",new int[]{0,0,255});
			else if(rgb[0] == 255 && rgb[1] == 0 && rgb[2] == 0)
				return new Wrap("pewter","default",new int[]{255,0,0});
			else if(rgb[0] == 200 && rgb[1] == 0 && rgb[2] == 0)
				return new Wrap("pewter","default",new int[]{200,0,0});
		}
		
		else if(area.equals("pewter"))
		{
			if(building.equals("default"))
			{
				if(rgb[0] == 255 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("route2Cont","default",new int[]{255,0,0});
				else if(rgb[0] == 200 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("route2Cont","default",new int[]{200,0,0});
				else if(rgb[0] == 160 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("route3","default",new int[]{160,0,0});
				else if(rgb[0] == 148 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("route3","default",new int[]{148,0,0});
				else if(rgb[0] == 128 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("route3","default",new int[]{128,0,0});
				else if(rgb[0] == 120 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("route3","default",new int[]{120,0,0});
				else if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 100)
					return new Wrap("pewter","pewterPokeCenter",new int[]{20,0,200});
				else if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 50)
					return new Wrap("pewter","pewterMarket", new int[]{20,0,200});
				else if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 255)
					return new Wrap("pewter","pewter1",new int[]{20,0,200});
				else if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 200)
					return new Wrap("pewter","pewter2",new int[]{20,0,200});
				else if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 150)
					return new Wrap("pewter","pewterGym",new int[]{20,0,200});
				else if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 25)
					return new Wrap("pewter","pewterMuseum",new int[]{20,0,200});
				else if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 10)
					return new Wrap("pewter","pewterMuseum",new int[]{20,0,255});
				
			}
			else if(building.equals("pewterPokeCenter"))
				return new Wrap("pewter","default",new int[]{0,0,100});
			else if(building.equals("pewterMarket"))
				return new Wrap("pewter","default",new int[]{0,0,50});
			else if(building.equals("pewter1"))
				return new Wrap("pewter","default",new int[]{0,0,255});
			else if(building.equals("pewter2"))
				return new Wrap("pewter","default",new int[]{0,0,200});
			else if(building.equals("pewterGym"))
				return new Wrap("pewter","default",new int[]{0,0,150});
			else if(building.equals("pewterMuseum"))
			{
				if(rgb[0] == 0 && rgb[1] == 128 && rgb[2] == 255)
					return new Wrap("pewter","pewterMuseumTop",new int[]{0,128,255});
				else if(rgb[0] == 20 && rgb[1] == 0 && rgb[2] == 200)
					return new Wrap("pewter","default",new int[]{0,0,25});
				else if(rgb[0] == 20 && rgb[1] == 0 && rgb[2] == 255)
					return new Wrap("pewter","default",new int[]{0,0,10});
			}
			else if(building.equals("pewterMuseumTop"))
				return new Wrap("pewter","pewterMuseum", new int[]{0,128,255});
		}
		
		else if(area.equals("route3"))
		{
			if(building.equals("default"))
			{
				if(rgb[0] == 160 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("pewter","default",new int[]{160,0,0});
				else if(rgb[0] == 148 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("pewter","default",new int[]{148,0,0});
				else if(rgb[0] == 128 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("pewter","default",new int[]{128,0,0});
				else if(rgb[0] == 120 && rgb[1] == 0 && rgb[2] == 0)
					return new Wrap("pewter","default",new int[]{120,0,0});
				else if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 255)
					return new Wrap("mtmoon","default",new int[]{20,0,200});
				else if(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 200)
					return new Wrap("route3","route3PokeCenter",new int[]{20,0,200});
			}
			
			else if(building.equals("route3PokeCenter"))
				return new Wrap("route3","default",new int[]{0,0,200});
		}
		
		else if(area.equals("mtmoon"))
		{
			if(building.equals("default"))
			{
				if(rgb[0] == 20 && rgb[1] == 0 && rgb[2] == 200)
					return new Wrap("route3","default",new int[]{0,0,255});
				else if(rgb[0] == 0 && rgb[1] == 128 && rgb[2] == 255)
					return new Wrap("mtmoon","mtmoon1",new int[]{0,128,200});
				else if(rgb[0] == 0 && rgb[1] == 128 && rgb[2] == 200)
					return new Wrap("mtmoon","mtmoon1",new int[]{0,128,128});
				else if(rgb[0] == 0 && rgb[1] == 128 && rgb[2] == 128)
					return new Wrap("mtmoon","mtmoon1",new int[]{0,128,255});
			}
			
			else if(building.equals("mtmoon1"))
			{
				if(rgb[0] == 0 && rgb[1] == 128 && rgb[2] == 200)
					return new Wrap("mtmoon","default",new int[]{0,128,255});
				else if(rgb[0] == 0 && rgb[1] == 128 && rgb[2] == 128)
					return new Wrap("mtmoon","default",new int[]{0,128,200});
				else if(rgb[0] == 0 && rgb[1] == 128 && rgb[2] == 255)
					return new Wrap("mtmoon","default",new int[]{0,128,128});
				else if(rgb[0] == 0 && rgb[1] == 128 && rgb[2] == 150)
					return new Wrap("mtmoon","mtmoon2",new int[]{0,128,100});
				else if(rgb[0] == 0 && rgb[1] == 128 && rgb[2] == 228)
					return new Wrap("mtmoon","mtmoon2",new int[]{0,128,200});
				else if(rgb[0] == 0 && rgb[1] == 128 && rgb[2] == 100)
					return new Wrap("mtmoon","mtmoon2",new int[]{0,128,255});
				else if(rgb[0] == 0 && rgb[1] == 128 && rgb[2] == 28)
					return new Wrap("mtmoon","mtmoon2",new int[]{0,128,128});
				else if(rgb[0] == 0 && rgb[1] == 128 && rgb[2] == 50)
					return new Wrap("route4","default",new int[]{0,0,255});
			}
			
			else if(building.equals("mtmoon2"))
			{
				if(rgb[0] == 0 && rgb[1] == 128 && rgb[2] == 100)
					return new Wrap("mtmoon","mtmoon1",new int[]{0,128,150});
				else if(rgb[0] == 0 && rgb[1] == 128 && rgb[2] == 200)
					return new Wrap("mtmoon","mtmoon1",new int[]{0,128,228});
				else if(rgb[0] == 0 && rgb[1] == 128 && rgb[2] == 255)
					return new Wrap("mtmoon","mtmoon1",new int[]{0,128,100});
				else if(rgb[0] == 0 && rgb[1] == 128 && rgb[2] == 128)
					return new Wrap("mtmoon","mtmoon1",new int[]{0,128,28});
			}
		}
		
		else if(area.equals("route22") && building.equals("default"))
		{
			if(rgb[0] == 240 && rgb[1] == 0 && rgb[2] == 0)
				return new Wrap("viridian","default",new int[]{240,0,0});
			else if(rgb[0] == 200 && rgb[1] == 0 && rgb[2] == 0)
				return new Wrap("viridian","default",new int[]{200,0,0});
			else if(rgb[0] == 160 && rgb[1] == 0 && rgb[2] == 0)
				return new Wrap("viridian","default",new int[]{160,0,0});
			else if(rgb[0] == 120 && rgb[1] == 0 && rgb[2] == 0)
				return new Wrap("viridian","default",new int[]{120,0,0});

		}
		else return this;
		return null;
	}

	public String getArea() {
		return area;
	}

	public String getBuilding() {
		return building;
	}

	public int[] getRGB() {
		return rgb;
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}
	
	public boolean getIsDoor()
	{
		return isDoor;
	}
	
	public boolean getIsAreaConnection()
	{
		return isAreaConnection;
	}
	
	public boolean isInside()
	{
		return isInside;
	}

	
	
	
	
	
}
