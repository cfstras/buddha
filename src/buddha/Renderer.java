/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buddha;

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
    
    long getNumDataRecvd();
    
    /**
     * animates the Renderer to render the current picture
     * in its own thread
     * 
     */
    void render();
    
    /**
     * initializes the renderer
     * @param bgcolor a given background color (rgba, 0-1), defaults to black
     * @param fgcolor a given background color (rgba, 0-1), defaults to white
     * @param sizex the canvas width in pixels
     * @param sizey the canvas height in pixels
     */
    void init(float[] bgcolor, float[] fgcolor, int sizex, int sizey);

    public void updateInfo(String string);

    public void addOneToPoint(int iy, int ix);
}
