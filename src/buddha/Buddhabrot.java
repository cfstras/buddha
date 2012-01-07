/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buddha;

import java.util.Random;

/**
 *
 * @author claus
 */
public class Buddhabrot implements Fractal{
    
    int width;
    int height;
    Renderer renderer;
    double[] xyseq;
    int minIterations=0;
    int maxIterations=0;
    
    Random r= new Random((long) (Math.random()*300000));
    
    @Override
    public void init(int sizex, int sizey,int minIterations,int maxIterations, Renderer renderer) {
        width=sizey;
        height=sizex; //the mixup is intentionally, to rotate it.
        this.renderer=renderer;
        this.minIterations=minIterations;
        this.maxIterations=maxIterations;
        xyseq =new double[maxIterations*2];
    }
    
    @Override
    public void generateData(int numPoints) {
        double x, y;
        // iterate through some plots
        for (int n = 0; n < numPoints; n++) {
            // Choose a random point in same range
            x = r.nextDouble()*6f -3f;  // range -2.0, 1.0 //old, now both -3,3
            y = r.nextDouble()*6f -3f; //range -1.5, 1.5 

            iterate(x, y);
        }
        
    }
    
    
    void iterate(double x0, double y0) {
        double x = 0;
        double y = 0;
        double xnew, ynew;
        int ix, iy;
        boolean escapes = false;
        int n=0;
        
        for (int i = 0; i < maxIterations; i++) {
            xnew = x * x - y * y + x0;
            ynew = 2 * x * y + y0;
            xyseq[i*2]=xnew;
            xyseq[i*2+1]=ynew;
            if ((xnew * xnew + ynew * ynew) > 10) {
                // escapes
                n=i;
                escapes = true;
                break;
            }
            if(Thread.interrupted())
                return;
            x = xnew;
            y = ynew;
        }
        if(escapes) {
            for (int i = 0; i < n; i++) {
                if (i > minIterations) {
                    //ix = (int) (width * (xnew + 2.0f) / 3.0f);
                    //iy = (int) (height * (ynew + 1.5f) / 3.0f);
                    ix=(int)( 0.3 * width * (xyseq[i*2]+0.5) + width/2);
                    iy=(int)( 0.3 * height * xyseq[i*2+1] + height/2);
                    if (ix > 0 && iy > 0 && ix < width && iy < height) {
                        renderer.addOneToPoint(iy, ix);
                    }

                }
                if(Thread.interrupted())
                    return;
            }
            
        }
        

        // does not escape, don't care.
    }

    @Override
    public long getAccuracy() {
        return 0;
    }
    
}
