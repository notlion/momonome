package momonome.examples;

import momonome.OscMonome;
import momonome.OscMonome.MonomeButtonEvent;
import momonome.OscMonome.MonomeCombo;
import momonome.OscMonome.MonomeComboEvent;
import momonome.OscMonome.MonomeEventListener;
import processing.core.PApplet;


public class ComboExample extends PApplet implements MonomeEventListener
{
	private OscMonome monome;
	
	
	public static void main(String[] args)
	{
		PApplet.main(new String[]{ "momonome.examples.ComboExample" });
	}
	
	public void setup()
	{
		size(200,200);
		
		monome = new OscMonome(this, "40h", 8000,8080, 16,16);
		MonomeCombo combo = monome.getNewCombo();
		combo.add(0, 0);
		combo.add(1, 0);
		combo.add(0, 1);
		monome.addCombo(combo);
		monome.addListener(this);
	}
	
	public void draw()
	{
	}
	

	public void onMonomeButton(MonomeButtonEvent event)
	{
		if(event.state == OscMonome.OFF)
			monome.toggleLed(event.x, event.y);
	}

	public void onMonomeCombo(MonomeComboEvent event)
	{
		println("hello");
		monome.toggleLedFrame();
	}
}
