package momonome.test;

import momonome.MonomeSequencer;
import momonome.MonomeSequencer.MonomeSequencerBeatEvent;
import momonome.MonomeSequencer.MonomeSequencerBeatListener;
import processing.core.PApplet;


public class SequencerTest extends PApplet implements MonomeSequencerBeatListener
{
	private MonomeSequencer monome;
	
	
	public static void main(String[] args)
	{
		PApplet.main(new String[]{ "momonome.test.SequencerTest" });
	}
	
	
	public void setup()
	{
		size(400,200);
		smooth();
		
		monome = new MonomeSequencer(this, "40h", 8000,8080, 8,8);
		monome.addBeatListener(this);
	}
	
	public void draw()
	{
	}
	
	
	public void onMonomeSequenceBeat(MonomeSequencerBeatEvent event)
	{
		background(64);
		strokeWeight(5);
		
		for(int i = 0; i < event.slice.length; i++)
		{
			if(event.slice[i] == MonomeSequencer.ON)
			{
				stroke(255, random(128,255));
				
				float y = (i + 1.0f) / (monome.ny + 1.0f) * height;
				line(0, y + random(-3,3), width, y + random(-3,3));
			}
		}
	}
}
