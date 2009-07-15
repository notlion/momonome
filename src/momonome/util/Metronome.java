package momonome.util;

import java.util.ArrayList;
import java.util.LinkedList;

public class Metronome implements Runnable
{
	public Thread beatThread;
	
	public float bpm = 120;
	public float resolution = 4;
	
	private int _bufferSize = 4;
	private LinkedList<Long> _tickBuffer;
	
	private long _maxTimeout = 2000;
	
	private ArrayList<MetronomeListener> _listeners;
	
	
	public Metronome()
	{
		_tickBuffer = new LinkedList<Long>();
		_listeners = new ArrayList<MetronomeListener>();
	}
	
	
	public void tap()
	{
		long millis = System.currentTimeMillis();
		
		if(_tickBuffer.size() > 0 && millis - _tickBuffer.get(0) > _maxTimeout)
			_tickBuffer.clear();
		else if(_tickBuffer.size() == _bufferSize)
			_tickBuffer.removeLast();
		
		_tickBuffer.addFirst(millis);
		
		calcBpm();
	}
	
	private void calcBpm()
	{
		if(_tickBuffer.size() >= 2)
		{
			float tickoff = 0;
			for(int i = _tickBuffer.size(); --i >= 1;)
				tickoff += _tickBuffer.get(i-1) - _tickBuffer.get(i);
			tickoff /= _tickBuffer.size() - 1;
			
			System.out.println(tickoff);
			
			bpm = 60000.0f / tickoff;
		}
	}
	
	
	public void addListener(MetronomeListener listener)
	{
		_listeners.add(listener);
	}
	public void removeListener(MetronomeListener listener)
	{
		if(_listeners.contains(listener))
			_listeners.remove(listener);
	}


	public void start()
	{
		beatThread = new Thread(this);
		beatThread.start();
	}
	public void stop()
	{
		beatThread = null;
	}
	
	public void run()
	{
		while(beatThread != null)
		{
			try
			{
				for(int i = 0, n = _listeners.size(); i < n; i++)
					_listeners.get(i).onBeat(this);
				
				long delay = (long)(60000.0 / bpm / resolution);
				Thread.sleep(delay);
			}
			catch(InterruptedException e)
			{
			}
		}
	}
	
	
	public interface MetronomeListener
	{
		public void onBeat(Metronome m);
	}
}