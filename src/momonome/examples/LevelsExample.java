package momonome.examples;

import momonome.MonomeLevels;
import momonome.MonomeLevels.MonomeLevelEvent;
import momonome.MonomeLevels.MonomeLevelListener;
import processing.core.PApplet;


public class LevelsExample extends PApplet implements MonomeLevelListener
{
	private MonomeLevels monome;
	
	private int[][] color;
	
	
	public static void main(String[] args)
	{
		PApplet.main(new String[]{ "momonome.examples.LevelsExample" });
	}
	
	
	public void setup()
	{
		size(400,200);
		smooth();
		
		monome = new MonomeLevels(this, "40h", 8000,8080, 8,8);
		monome.addLevelListener(this);
		
		color = new int[monome.nx][3];
	}
	
	public void draw()
	{
		background(64);
		noStroke();
		
		float[] lvls = monome.getLevels();
		for(int i = 0; i < lvls.length; i++)
		{
			fill(color[i][0]--, color[i][1]--, color[i][2]--);
			rect((float)i / lvls.length * width, height, (float)width / lvls.length - 2, lvls[i] * -height);
		}
	}
	
	
	public void onMonomeLevel(MonomeLevelEvent event)
	{
		color[event.index][0] = (int)random(256);
		color[event.index][1] = (int)random(256);
		color[event.index][2] = (int)random(256);
	}
}
