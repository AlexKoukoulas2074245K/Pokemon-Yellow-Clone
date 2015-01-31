package battleStates;

import entities.SNpc;
import entities.SPlayer;
import gameStates.Battle;

import java.awt.Graphics2D;

import main.Game;
import main.Main;
import misc.PokemonHud;
import util.Counter;

public class BSIntro extends BattleState{
	
	private static final int INTRO_SPEED = 6; 
	private static final int SUMMON_SPEED = 10;
	
	public static final int[] FINAL_PLR_POS = new int[]{Game.STDTSIZE/2,Main.HEIGHT - Battle.TB_HEIGHT - Battle.IMAGE_SIZE};
	public static final int[] FINAL_TRN_POS = new int[]{Main.WIDTH - Battle.IMAGE_SIZE, 0};
	
	private Counter counter;
	
	private int phase;
	
	private int[] playerPos;
	private int[] trainerPos;
	private int[] tempPlayerPos;
	private int[] tempTrainerPos;
	
	public BSIntro(SPlayer player, SNpc trainer, boolean wildBattle)
	{
		super(player,trainer,wildBattle);
		
		playerPos = new int[2];
		trainerPos = new int[2];
		
		playerPos[0] = Main.WIDTH + Game.STDTSIZE/2;
		playerPos[1] = Main.HEIGHT - Battle.TB_HEIGHT - playerImgs[0].getHeight();
		trainerPos[0] = - trainerImgs[0].getWidth(null);
		trainerPos[1] = 0;
		
		phase = 0;
	}
	
	public void update()
	{
		updatePhase();
	}
	
	public void updatePhase()
	{
		if(phase == 0)
		{
			playerPos[0] -= INTRO_SPEED;
			trainerPos[0] += INTRO_SPEED;
			
			if(playerPos[0] <= Game.STDTSIZE/2)
			{
				playerPos[0] = Game.STDTSIZE/2;
				trainerPos[0] = Main.WIDTH - Battle.IMAGE_SIZE;
				
				tempTrainerPos = new int[]{trainerPos[0],trainerPos[1]};
				tempPlayerPos = new int[]{playerPos[0],playerPos[1]};
				
				phase ++;
				
				if(wildBattle)
					Battle.setTextbox(String.format("Wild %s#appeared!",trainer.getFirstAvail().getName()), 1);
				else
					Battle.setTextbox(String.format("%s wants#to fight!",trainer.getTrainerName()), 1);
			}
		}
		
		else if(phase == 1)
		{
			Battle.textbox.update();
			
			if(Battle.textbox == null)
				phase ++;
		}
		
		else if(phase == 2)
		{
			if(wildBattle)
				phase += 2;
			
			
			tempTrainerPos[0] += SUMMON_SPEED;
			
			if(tempTrainerPos[0] > Main.WIDTH)
			{
				Battle.setTextbox(String.format("%s sent#out %s!", trainer.getTrainerName(),trainer.getFirstAvail().getName()),2);
				phase ++;
				counter = new Counter(Game.STDTSIZE * 3/2);
			}
		}
		
		else if(phase == 3)
		{
			Battle.textbox.update();
			
			if(!Battle.textbox.getAlive())
			{
				counter.update();
				if(!counter.getAlive())
					phase ++;
			}
				
		}
		
		else if(phase == 4)
		{
			Battle.destroyTextbox();
			
			tempPlayerPos[0] -= SUMMON_SPEED;
			if(tempPlayerPos[0] < - playerImgs[0].getWidth())
			{
				Battle.setTextbox(String.format("Go! %s!",player.getFirstPkmn().getName()), 2);
				phase ++;
				counter = new Counter(Game.STDTSIZE * 3/2);
			}
		}
		
		else if(phase == 5)
		{
			Battle.textbox.update();
			
			if(!Battle.textbox.getAlive())
			{
				counter.update();
				if(!counter.getAlive())
				{
					finished = true;
					Battle.destroyTextbox();
				}
			}
		}
	}
	public void render(Graphics2D g)
	{
	
		if(phase == 0)
		{
			if(wildBattle)
				g.drawImage(trainerImgs[1], trainerPos[0] - 160, trainerPos[1], Battle.IMAGE_SIZE, Battle.IMAGE_SIZE,null);
			else
				g.drawImage(trainerImgs[1], trainerPos[0], trainerPos[1], null);
			
			g.drawImage(playerImgs[1], playerPos[0], playerPos[1],null);
		}
		
		else if(phase == 1)
		{
			if(wildBattle)
				g.drawImage(trainerImgs[0], trainerPos[0], trainerPos[1], Battle.IMAGE_SIZE, Battle.IMAGE_SIZE, null);
			else
				g.drawImage(trainerImgs[0], trainerPos[0], trainerPos[1], null);
			
			g.drawImage(playerImgs[0], playerPos[0], playerPos[1],null);
			
			PokemonHud.drawPokemonBalls(2,wildBattle, player, trainer, g);
		}
		
		else if(phase == 2)
		{
			if(wildBattle)
				g.drawImage(trainerImgs[0], tempTrainerPos[0], tempTrainerPos[1],Battle.IMAGE_SIZE, Battle.IMAGE_SIZE, null);
			else
				g.drawImage(trainerImgs[0], tempTrainerPos[0], tempTrainerPos[1], null);
			
			g.drawImage(playerImgs[0], playerPos[0], playerPos[1],null);
		}
		
		else if(phase == 3)
		{
			g.drawImage(playerImgs[0], playerPos[0], playerPos[1],null);
			if(!Battle.textbox.getAlive())
				trainer.getFirstAvail().render(g, trainerPos[0], trainerPos[1], false);
		}
		
		else if(phase == 4)
		{
			g.drawImage(playerImgs[0], tempPlayerPos[0], tempPlayerPos[1], null);
			trainer.getFirstAvail().render(g, trainerPos[0], trainerPos[1], false);
			Battle.getHud(false).render(g);
		}
		
		else if(phase == 5)
		{
			if(!Battle.textbox.getAlive())
				player.getFirstPkmn().render(g, playerPos[0], playerPos[1], true);
			trainer.getFirstAvail().render(g, trainerPos[0], trainerPos[1], false);
			Battle.getHud(false).render(g);
			Battle.getHud(true).render(g);
		}
	}
	
	public void setPhase(int phase)
	{
		this.phase = phase;
	}
}
