package momonome;

import java.util.ArrayList;

import momonome.OscMonome.MonomeEventListener;
import momonome.util.Metronome;
import momonome.util.Metronome.MetronomeListener;
import oscP5.OscP5;
import processing.core.PApplet;


public class MonomeSequencer extends OscMonome implements MonomeEventListener, MetronomeListener
{
	public Metronome metronome;
	public int position;
	public int patternIndex;
	
	protected int[][][] patterns;
	protected int[] playHeadSlice;
	
	protected MonomeCombo tapCombo;
	protected MonomeCombo[] switchCombo;
	protected MonomeCombo[] cueCombo;
	
	protected ArrayList<MonomeSequencerBeatListener> beatListeners;
	
	
	public MonomeSequencer(OscP5 osc, String oscName, int listenPort, int nx, int ny)
	{
		super(osc, oscName, listenPort, nx, ny);
		
		patterns = new int[nx][nx][ny];
		patternIndex = 0;
		
		int nx1 = nx - 1;
		int ny1 = ny - 1;
		
		// Tap Tempo Combo
		tapCombo = new MonomeCombo();
		tapCombo.add(0, ny1);
		tapCombo.add(nx1, ny1);
		addCombo(tapCombo);
		
		// Pattern Switch Combos
		switchCombo = new MonomeCombo[nx];
		cueCombo = new MonomeCombo[nx];
		for(int i = 0; i < nx; i++)
		{
			switchCombo[i] = new MonomeCombo();
			switchCombo[i].add(0, ny1);
			switchCombo[i].add(i, 0);
			addCombo(switchCombo[i]);
			
			cueCombo[i] = new MonomeCombo();
			cueCombo[i].add(1, ny1);
			cueCombo[i].add(i, 0);
			addCombo(cueCombo[i]);
		}
		
		// Slice to Show Current Play Head Position
		playHeadSlice = new int[ny];
		for(int i = 0; i < ny; i++)
			playHeadSlice[i] = ON;
		
		beatListeners = new ArrayList<MonomeSequencerBeatListener>();
		
		metronome = new Metronome();
		metronome.bpm = 140;
		metronome.resolution = nx / 4;
		metronome.addListener(this);
		
		cuePosition(0);
		
		addListener(this);
	}
	
	public MonomeSequencer(PApplet app, String oscName, int hostPort, int listenPort, int nx, int ny)
	{
		this(new OscP5(app, hostPort, OscP5.UDP), oscName, listenPort, nx, ny);
	}
	
	
	public void addBeatListener(MonomeSequencerBeatListener listener)
	{
		beatListeners.add(listener);
	}
	
	public void setBpm(float bpm)
	{
		metronome.bpm = bpm;
	}
	
	public void setPosition(int _position)
	{
		setLedCol(position, getSlice(position));
		position = _position;
		setLedCol(position, playHeadSlice);
	}
	
	public void cuePosition(int _position)
	{
		setLedCol(position, getSlice(position));
		position = _position == 0 ? nx - 1 : _position - 1;
		metronome.reset();
	}
	
	
	public int[] getSlice(int pos)
	{
		int[][] pattern = patterns[patternIndex];
		int[] slice = new int[ny];
		for(int i = 0; i < ny; i++)
			slice[i] = pattern[i][pos];
		return slice;
	}
	
	public int[][] getPattern(int index)
	{
		return patterns[index];
	}

	
	public void onMonomeButton(MonomeButtonEvent event)
	{
		if(event.state == OFF)
		{
			int[][] pattern = patterns[patternIndex];
			int state = pattern[event.y][event.x] == OFF ? ON : OFF;
			pattern[event.y][event.x] = state;
			setLed(event.x, event.y, state);
		}
	}

	public void onMonomeCombo(MonomeComboEvent event)
	{
		if(event.combo == tapCombo)
		{
			metronome.tap();
		}
		else {
			for(int i = 0; i < nx; i++)
			{
				if(event.combo == switchCombo[i])
				{
					patternIndex = i;
					setLedFrame(patterns[patternIndex]);
					break;
				}
			}
			for(int i = 0; i < nx; i++)
			{
				if(event.combo == cueCombo[i])
				{
					cuePosition(i);
					break;
				}
			}
		}
	}


	public void onBeat(Metronome m)
	{
		setPosition((position + 1) % nx);
		
		MonomeSequencerBeatEvent event = new MonomeSequencerBeatEvent(this);
		
		for(int i = 0, n = beatListeners.size(); i < n; i++)
			beatListeners.get(i).onMonomeSequenceBeat(event);
	}
	
	
	public class MonomeSequencerBeatEvent
	{
		public MonomeSequencer sequencer;
		
		public int[] slice;
		
		public MonomeSequencerBeatEvent(MonomeSequencer _sequencer)
		{
			sequencer = _sequencer;
			slice = getSlice(_sequencer.position);
		}
		
		public String toString()
		{
			String str = "[";
			for(int i = 0, n1 = slice.length - 1; i <= n1; i++)
			{
				str += String.valueOf(slice[i]);
				if(i < n1)
					str += ",";
			}
			return str + "]";
		}
	}
	
	public interface MonomeSequencerBeatListener
	{
		public void onMonomeSequenceBeat(MonomeSequencerBeatEvent event);
	}
}
