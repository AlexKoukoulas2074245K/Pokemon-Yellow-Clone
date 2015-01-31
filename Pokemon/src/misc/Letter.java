package misc;

public class Letter {
	
	String letter;
	int x;
	int y;
	
	public Letter(String letter,int x,int y)
	{
		this.letter = letter;
		this.x = x;
		this.y = y;
	}

	//Getters
	public String getLetter() {
		return letter;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	//Setters
	public void setLetter(String letter) {
		this.letter = letter;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	
}
