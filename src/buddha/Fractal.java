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
    
    void init(int sizex,int sizey, int minIterations,int maxIterations,Renderer renderer, long seed);
    
    void set(int minIterations, int maxIterations);
    
    void generateData(int num);
    
    long getAccuracy();
}
