package momonome.examples;

import momonome.MonomeSequencer;
import momonome.OscMonome;
import momonome.ui.DisplaySurface;
import processing.core.PApplet;

/**
 * @author g.dunne
 * @authorurl http://quilime.com 
 * @year 2009
 */
public class DisplaySurfaceTest extends PApplet
{    
    private MonomeSequencer monome;
    private DisplaySurface monomeD;
    
    
    public void setup()
    {
        size(390, 390);
        background(35);
        
        monome = new MonomeSequencer(this, "256", 8000, 8080, 16, 16);
        monomeD = new DisplaySurface(this, monome, 20, 20, 2);
        monomeD.x = 20;
        monomeD.y = 20;
    }
    
    
    public void draw()
    {
        // setting some initial states for example
        monome.setState(0,0, OscMonome.ON);
        monome.setState(5,5, OscMonome.ON);
        monome.setState(7,2, OscMonome.ON);
        
        monomeD.draw();
    }
    
    
    public void mousePressed()
    {
        monomeD.mousePressed(mouseX, mouseY);
    }


    
    public static void main(String[] args)
    {
        PApplet.main(new String[]{ "DisplaySurfaceTest" });
    }    
}
