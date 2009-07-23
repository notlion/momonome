package momonome;

import momonome.MonomeSequencer.MonomeSequencerBeatListener;
import oscP5.OscP5;
import processing.core.PApplet;


public class MonomeRippleSequencer extends MonomeSequencer implements MonomeSequencerBeatListener
{
	protected int[] pripple;
	protected int[] cripple;
	protected int[] nripple;
	
	protected int[] rcheck_off;
	
	protected int nr;
	
	public int rippleSteps = 6;
	
	
	public MonomeRippleSequencer(OscP5 osc, String oscName, int listenPort, int nx, int ny)
	{
		super(osc, oscName, listenPort, nx, ny);
		
		nr = (ny + 2) * (nx + 2);
		
		pripple = new int[nr];
		cripple = new int[nr];
		nripple = new int[nr];
		
		rcheck_off = new int[]{
			1, nx + 2, -1, -(nx + 2)
		};
		
		addBeatListener(this);
	}
	
	public MonomeRippleSequencer(PApplet app, String oscName, int hostPort, int listenPort, int nx, int ny)
	{
		this(new OscP5(app, hostPort, OscP5.UDP), oscName, listenPort, nx, ny);
	}
	
	
	public void onMonomeSequenceBeat(MonomeSequencerBeatEvent event)
	{
		for(int y = 0; y < ny; y++)
			if(event.slice[y] == ON)
				touchRipple(position, y);
	}
	
	
	public void touchRipple(int x, int y)
	{
		cripple[(y + 1) * (nx + 2) + x + 1] = rippleSteps;
	}
	
	
	public void step()
	{
		int nx2 = nx + 2;
		
		for(int i, y = ny + 1; --y > 0;)
		{
			for(int x = nx + 1; --x > 0;)
			{
				i = y * nx2 + x;
				
				nripple[i] = OFF;
				if(cripple[i] == OFF && pripple[i] == OFF)
				{
					for(int chk, o = rcheck_off.length; --o >= 0;)
					{
						chk = cripple[i + rcheck_off[o]];
						if(chk >= ON)
						{
							nripple[i] = chk - 1;
							break;
						}
					}
				}
			}
		}
		
		System.arraycopy(cripple, 0, pripple, 0, nr);
		System.arraycopy(nripple, 0, cripple, 0, nr);
		
		updateFrame();
	}
	
	
	public void updateFrame()
	{
		int[][] frame = new int[ny][nx];
		for(int y = 0; y < ny; y++)
			for(int x = 0; x < nx; x++)
				frame[y][x] = x == position || cripple[(y + 1) * (nx + 2) + x + 1] >= ON || patterns[patternIndex][y][x] == ON ? ON : OFF;
		
		setLedFrame(frame);
	}
	
	
	public void setPosition(int _position)
	{
		position = _position;
		updateFrame();
	}
	
	public void cuePosition(int _position)
	{
		position = _position == 0 ? nx - 1 : _position - 1;
		metronome.reset();
	}
}
