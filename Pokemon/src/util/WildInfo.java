package util;

public class WildInfo {
	
	private String name;
	private int rate;
	private int level;
	
	public WildInfo(String name, int level, int rate)
	{
		this.name = name;
		this.level = level;
		this.rate = rate;
	}

	public String getName() {
		return name;
	}

	public int getRate() {
		return rate;
	}

	public int getLevel() {
		return level;
	}
}
