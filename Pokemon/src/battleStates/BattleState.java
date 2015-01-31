package battleStates;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import main.Main;
import util.ResourceLoader;
import entities.Pokemon;
import entities.SNpc;
import entities.SPlayer;
import gameStates.Battle;

public abstract class BattleState {

	protected SPlayer player;
	protected SNpc trainer;
	
	protected BufferedImage[] playerImgs;
	protected Image[] trainerImgs;
	protected BufferedImage confImage;
	protected BufferedImage cursorImage;
	protected BufferedImage moveDeleImage;
	
	protected Pokemon[] playerPkmn;
	protected Pokemon[] trainerPkmn;
	
	protected boolean wildBattle;
	protected boolean finished;
	
	protected int[] confPos;
	protected int[] confStringPos;
	protected int[] confCursorPos;
	protected int[] moveDelePos;
	protected int[] moveDeleStringPos;
	protected int[] moveDeleCursorPos;
	
	public BattleState(SPlayer player, SNpc trainer, boolean wildBattle)
	{
		this.player = player;
		this.trainer = trainer;
		this.wildBattle = wildBattle;
		
		playerPkmn = player.getPokemon();
		trainerPkmn = trainer.getPokemon();
		
		finished = false;
		
		getImages();
		
		setPositions();
		
	}
	
	private void setPositions()
	{
		confPos = new int[]{Main.WIDTH - confImage.getWidth(), 
				Main.HEIGHT - confImage.getHeight() - Battle.TB_HEIGHT - 2};
		confStringPos = new int[]{confPos[0] + 62, confPos[1] + 58};
		confCursorPos = new int[]{confPos[0] + 36 , confPos[1] + 34};
		moveDelePos = new int[]{Main.WIDTH - moveDeleImage.getWidth(), 
				Main.HEIGHT - Battle.TB_HEIGHT - moveDeleImage.getHeight() + 30};
		moveDeleStringPos = new int[]{moveDelePos[0] + 74, moveDelePos[1] + 66};
		moveDeleCursorPos = new int[]{moveDelePos[0] + 36, moveDelePos[1] + 40};
	}
	
	private void getImages()
	{
		playerImgs = player.getBattleImages();
		
		if(wildBattle)
			trainerImgs = trainer.getFirstAvail().getBattleImages();
		else
			trainerImgs = trainer.getBattleImages();
		
		confImage = ResourceLoader.getTexture("confirmation");
		cursorImage = ResourceLoader.getTexture("horCursor");
		moveDeleImage = ResourceLoader.getTexture("moveDele");
		
	}
		
	public void update()
	{
		
	}
	
	public void render(Graphics2D g)
	{
		
	}
	
	public boolean getFinished()
	{
		return finished;
	}
	
	public void setFinished(boolean finished)
	{
		this.finished = finished;
	}
}
