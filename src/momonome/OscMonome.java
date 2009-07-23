package momonome;

import java.util.ArrayList;

import netP5.NetAddress;
import oscP5.OscEventListener;
import oscP5.OscMessage;
import oscP5.OscP5;
import oscP5.OscStatus;
import processing.core.PApplet;


public class OscMonome implements OscEventListener
{
	public OscP5 osc;
	public NetAddress oscOut;
	
	public String baseName;
	public String press_addr, led_addr, led_col_addr, led_row_addr, led_frame_addr, led_clear_addr, led_intensity_addr;
	
	public ArrayList<MonomeEventListener> listeners;
	public ArrayList<MonomeCombo> combos;
	
	public int[][] ledState;
	public int[][] buttonState;
	public int[][] buttonComboMask;
	public int nx, ny;
	
	public static final int ON = 1;
	public static final int OFF = 0;
	
	
	public OscMonome(OscP5 osc, String oscName, int listenPort, int nx, int ny)
	{
		setBaseName(oscName);
		
		this.osc = osc;
		this.nx = nx;
		this.ny = ny;
		
		ledState = new int[ny][nx];
		buttonState = new int[ny][nx];
		buttonComboMask = new int[ny][nx];
		
		osc.addListener(this);
		
		oscOut = new NetAddress("localhost", listenPort);
		
		listeners = new ArrayList<MonomeEventListener>();
		combos = new ArrayList<MonomeCombo>();
		
		clearLeds(0);
	}
	
	public OscMonome(PApplet app, String oscName, int hostPort, int listenPort, int nx, int ny)
	{
		this(new OscP5(app, hostPort, OscP5.UDP), oscName, listenPort, nx, ny);
	}
	
	public OscMonome(PApplet app, String oscName, int hostPort, int listenPort)
	{
		this(new OscP5(app, hostPort, OscP5.UDP), oscName, listenPort, 8, 8);
	}
	
	
	public void setBaseName(String _baseName)
	{
		baseName = _baseName.startsWith("/") ? _baseName : "/" + _baseName;
		
		press_addr = baseName + "/press";
		
		led_addr = baseName + "/led";
		led_col_addr = baseName + "/led_col";
		led_row_addr = baseName + "/led_row";
		led_frame_addr = baseName + "/frame";
		led_clear_addr = baseName + "/clear";
		led_intensity_addr = baseName + "/intensity";
	}
	
	
	public MonomeCombo getNewCombo()
	{
		return new MonomeCombo();
	}
	
	public void addCombo(MonomeCombo combo)
	{
		combos.add(combo);
	}
	public void addCombo(int[] x, int[] y)
	{
		combos.add(new MonomeCombo(x, y));
	}
	
	
	public void setLed(int x, int y, int state)
	{
		OscMessage msg = new OscMessage(led_addr);
		msg.add(x);
		msg.add(y);
		msg.add(state);
		osc.send(msg, oscOut);
		ledState[y][x] = state;
	}
	public void toggleLed(int x, int y)
	{
		setLed(x, y, ledState[y][x] == OFF ? ON : OFF);
	}
	
	
	public void setLedCol(int x, int[] states)
	{
		setLedColRow(led_col_addr, x, states);
		
		for(int i = ny; --i >= 0;)
			ledState[i][x] = states[i];
	}
	public void toggleLedCol(int x)
	{
		int[] col = new int[nx];
		for(int i = ny; --i >= 0;)
			col[i] = ledState[i][x] == OFF ? ON : OFF;
		setLedCol(x, col);
	}
	
	public void setLedRow(int y, int[] states)
	{
		setLedColRow(led_row_addr, y, states);
		
		for(int i = nx; --i >= 0;)
			ledState[y][i] = states[i];
	}
	public void toggleLedRow(int y)
	{
		int[] row = new int[nx];
		for(int i = nx; --i >= 0;)
			row[i] = ledState[y][i] == OFF ? ON : OFF;
		setLedRow(y, row);
	}
	
	private void setLedColRow(String addr, int i, int[] states)
	{
		OscMessage msg = new OscMessage(addr);
		msg.add(i);
		
		byte[] bytes = getPackedBytes(states);
		for(int j = 0; j < bytes.length; j++)
			msg.add(bytes[j] & 0xff);
		
		osc.send(msg, oscOut);
	}
	
	
	public void setLedFrame(int[][] frame)
	{
		// We can only update 8x8 sections so we need to split up the full frame
		for(int y = 0; y < ny; y += 8)
		{
			for(int x = 0; x < nx; x += 8)
			{
				OscMessage msg = new OscMessage(led_frame_addr);
				
				msg.add(x);
				msg.add(y);
				
				for(int fy = y; fy < y + 8; fy++)
				{
					int[] row = new int[8];
					for(int i = 0; i < 8; i++)
						row[i] = frame[fy][x + i];
					msg.add(getPackedBytes(row)[0] & 0xff);
//					System.out.print(getPackedBytes(row)[0] & 0xff);
//					System.out.print(" ");
				}
//				System.out.println("-- " + x + " " + y);
				
				osc.send(msg, oscOut);
			}
		}
		
//		System.out.println();
		
		for(int y = 0; y < ny; y++)
			System.arraycopy(frame[y], 0, ledState[y], 0, nx);
	}
	public void toggleLedFrame()
	{
		int[][] frame = new int[nx][ny];
		for(int y = ledState.length; --y >= 0;)
			for(int x = ledState[y].length; --x >= 0;)
				frame[y][x] = ledState[y][x] == OFF ? ON : OFF;
		setLedFrame(frame);
	}
	
	
	public void clearLeds(int state)
	{
		OscMessage msg = new OscMessage(led_clear_addr);
		msg.add(state);
		osc.send(msg, oscOut);
		
		for(int y = ledState.length; --y >= 0;)
			for(int x = ledState[y].length; --x >= 0;)
				ledState[y][x] = state;
	}
	
	
	private byte[] getPackedBytes(int[] states)
	{
		byte[] bytes = new byte[states.length / 8];
		for(int b = 0, i = 0; b < bytes.length; b++)
		{
			for(int j = 8; --j >= 0;)
				bytes[b] |= states[i + j] << j;
			i += 8;
		}
		return bytes;
	}
	
	
	public void addListener(MonomeEventListener listener)
	{
		listeners.add(listener);
	}


	public void oscEvent(OscMessage msg)
	{
		if(msg.checkAddrPattern(press_addr) && msg.checkTypetag("iii"))
		{
			MonomeButtonEvent event = new MonomeButtonEvent(this,
				msg.get(0).intValue(),
				msg.get(1).intValue(),
				msg.get(2).intValue()
			);
			
			buttonState[event.y][event.x] = event.state;
			
			if(event.state == ON || buttonComboMask[event.y][event.x] == OFF)
			{
				for(int i = 0, n = listeners.size(); i < n; i++)
					listeners.get(i).onMonomeButton(event);
			}
			else
				buttonComboMask[event.y][event.x] = OFF;
			
			if(event.state == ON)
			{
				MonomeCombo combo;
				for(int i = 0, n = combos.size(); i < n; i++)
				{
					combo = combos.get(i);
					if(combo.isPressed())
					{
						MonomeComboEvent mevent = new MonomeComboEvent(this, combo);
						
						for(int j = 0, jn = listeners.size(); j < jn; j++)
							listeners.get(j).onMonomeCombo(mevent);
						
						XY xy;
						for(int j = combo.xy.size(); --j >= 0;)
						{
							xy = combo.xy.get(j);
							buttonComboMask[xy.y][xy.x] = ON;
						}
					}
				}
			}
		}
	}


	public void oscStatus(OscStatus status)
	{
	}
	
	
	
	
	public class MonomeButtonEvent
	{
		public OscMonome monome;
		public int x, y, state;
		
		public MonomeButtonEvent(OscMonome _monome, int _x, int _y, int _state)
		{
			monome = _monome;
			x = _x;
			y = _y;
			state = _state;
		}
		
		public String toString()
		{
			return "{x:" + x + ",y:" + y + "state:" + state + "}";
		}
	}
	
	public class MonomeComboEvent
	{
		public OscMonome monome;
		public MonomeCombo combo;
		
		public MonomeComboEvent(OscMonome _monome, MonomeCombo _combo)
		{
			monome = _monome;
			combo = _combo;
		}
	}
	
	public class MonomeCombo
	{
		public ArrayList<XY> xy;
		
		public MonomeCombo()
		{
			xy = new ArrayList<XY>();
		}
		
		public MonomeCombo(int[] x, int[] y)
		{
			this();
			add(x, y);
		}
		
		public void add(int x, int y)
		{
			xy.add(new XY(x, y));
		}
		
		public void add(int[] x, int[] y)
		{
			for(int i = 0, n = Math.min(x.length, y.length); i < n; i++)
				add(x[i], y[i]);
		}
		
		public boolean isPressed()
		{
			for(int i = xy.size(); --i >= 0;)
				if(!xy.get(i).isPressed())
					return false;
			return true;
		}
	}
	
	
	public interface MonomeEventListener
	{
		public void onMonomeButton(MonomeButtonEvent event);
		public void onMonomeCombo(MonomeComboEvent event);
	}
	
	
	private final class XY
	{
		public int x, y;
		
		public XY(int _x, int _y)
		{
			x = _x;
			y = _y;
		}
		
		public boolean isPressed()
		{
			return buttonState[y][x] == ON;
		}
	}
}
