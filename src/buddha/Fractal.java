/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buddha;

/**
 *
 * @author claus
 */
public interface Fractal {
    
    void init(int sizex,int sizey, Renderer renderer);
    
    
    void generateData(int minIterations,int num, int bailout);
    
    long getAccuracy();
}
