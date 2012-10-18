/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buddha;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author claus
 */
public interface Renderer {
    
    /**
     * sets a picture point to that value.
     * @param x
     * @param y
     * @param value 
     */
    void setPoint(int x,int y, double value);
    
    /**
     * adds a value to a specific point
     * @param x
     * @param y
     * @param value
     */
    void addToPoint(int x,int y, double value);
    
    long getExposes();
    
    /**
     * render the current picture
     * disposes the graphics, too.
     * @param image the Graphics element to draw the image to.
     */
    void render(Graphics2D g);
    
    /**
     * initializes the renderer
     * @param bgcolor a given background color (rgba, 0-1), defaults to black
     * @param fgcolor a given background color (rgba, 0-1), defaults to white
     * @param sizex the canvas width in pixels
     * @param sizey the canvas height in pixels
     */
    void init(int sizex, int sizey);
    
    void reInit();

    //public void updateInfo(String string);

    public void expose(int iy, int ix);
    
    public void setColor(Color foreground, Color background);
}
