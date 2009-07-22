package momonome;

import java.util.ArrayList;

import momonome.OscMonome.MonomeEventListener;
import oscP5.OscP5;
import processing.core.PApplet;


public class MonomeLevels extends OscMonome implements MonomeEventListener
{
	protected float[] levels;
	
	protected ArrayList<MonomeLevelListener> levelListeners;
	
	
	public MonomeLevels(OscP5 osc, String oscName, int listenPort, int nx, int ny)
	{
		super(osc, oscName, listenPort, nx, ny);
		
		levels = new float[nx];
		levelListeners = new ArrayList<MonomeLevelListener>();
		
		addListener(this);
	}
	
	public MonomeLevels(PApplet app, String oscName, int hostPort, int listenPort, int nx, int ny)
	{
		this(new OscP5(app, hostPort, OscP5.UDP), oscName, listenPort, nx, ny);
	}
	
	
	public void setLevel(int index, int level)
	{
		levels[index] = (float)level / (ny - 1);
		
		System.out.println(levels[index]);
		
		int[] ledStates = new int[ny];
		for(int i = (ny - 1) - level; i < ny; i++)
			ledStates[i] = ON;
		
		setLedCol(index, ledStates);
	}
	public void setLevel(int index, float level)
	{
		setLevel(index, (int)(level * (ny - 1)));
	}
	
	public float getLevel(int index)
	{
		return levels[index];
	}
	public float[] getLevels()
	{
		return levels;
	}

	
	public void onMonomeButton(MonomeButtonEvent event)
	{
		if(event.state == OFF)
		{
			setLevel(event.x, (ny - 1) - event.y);
			
			MonomeLevelEvent le = new MonomeLevelEvent(this, event.x);
			
			for(int i = 0, n = levelListeners.size(); i < n; i++)
				levelListeners.get(i).onMonomeLevel(le);
		}
	}
	
	public void onMonomeCombo(MonomeComboEvent event)
	{
	}
	
	
	public void addLevelListener(MonomeLevelListener listener)
	{
		levelListeners.add(listener);
	}
	
	public void removeLevelListener(MonomeLevelListener listener)
	{
		if(levelListeners.contains(listener))
			levelListeners.remove(listener);
	}
	
	
	public class MonomeLevelEvent
	{
		public MonomeLevels levels;
		
		public int index;
		public float level;
		
		public MonomeLevelEvent(MonomeLevels _levels, int _index)
		{
			levels = _levels;
			index = _index;
			level = levels.getLevel(_index);
		}
	}
	
	public interface MonomeLevelListener
	{
		public void onMonomeLevel(MonomeLevelEvent event);
	}
}
