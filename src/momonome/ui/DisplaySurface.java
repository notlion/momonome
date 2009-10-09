package momonome.ui;

import momonome.OscMonome;
import processing.core.PApplet;


/**
 * @author g.dunne
 * @authorurl http://quilime.com 
 * @year 2009
 */
public class DisplaySurface
{
    private PApplet p;
    
    private OscMonome monome;
    
    public DisplaySurfaceCell[][] cells;
    
    public int x = 0;
    public int y = 0;
    
    public int cellw = 10;
    public int cellh = 10;
    public int cellm = 2;
    
    public DisplaySurface(PApplet _p, OscMonome _monome, int _cellw, int _cellh, int _cellm)
    {
        p = _p;
        monome = _monome;
        cellw = _cellw;
        cellh = _cellh;
        cellm = _cellm;
        
        cells = new DisplaySurfaceCell[monome.ny][monome.nx];

        for (int y = 0; y < monome.ny; y++) {
            for (int x = 0; x < monome.nx; x++) {
                cells[y][x] = new DisplaySurfaceCell(y * (cellh+cellm), x * (cellw+cellm), OscMonome.OFF); 
            }
        }
    }

    
    public void draw()
    {        
        p.pushMatrix();
        p.translate(x, y);
        
        for (int y = 0; y < monome.ny; y++) {
            for (int x = 0; x < monome.nx; x++) {
                cells[y][x].setState(monome.ledState[y][x]);
                cells[y][x].draw();
            }
        }
        
        p.popMatrix();
    }
    
    
    
    public void mousePressed(int mousex, int mousey)
    {
        PApplet.println(mousex + " " + mousey);
        
        for (int y = 0; y < monome.ny; y++) {
            for (int x = 0; x < monome.nx; x++) {
                
            }
        }
    }
    
    
    
    public class DisplaySurfaceCell
    {
        public int x, y;
        public int state = OscMonome.OFF;
        
        public DisplaySurfaceCell(int y, int x, int s)
        {
            this.y = y;
            this.x = x;
            state = s;
        }
        
        public void setState(int s)
        {
            state = s;
        }
        
        public void draw()
        {
            p.noStroke();
            
            if (state == OscMonome.ON)
                p.fill(229, 113, 73);
            else
                p.fill(45);
            
            p.pushMatrix();
            p.translate(x, y);
            p.rect(0, 0, cellw, cellh);
            p.popMatrix();
        }
    }
}
