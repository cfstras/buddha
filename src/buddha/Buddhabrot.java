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
        
    Random r= new Random();
    
    @Override
    public void init(int sizex, int sizey, Renderer renderer) {
        width=sizey;
        height=sizex; //the mixup is intentionally, to rotate it.
        this.renderer=renderer;
    }
    
    @Override
    public void generateData(int numPoints, int bailout) {
        
        float x, y;
        // iterate through some plots
        for (int n = 0; n < numPoints; n++) {
            // Choose a random point in same range
            x = r.nextFloat()*3f -2f;  // range -2.0, 1.0
            y = r.nextFloat()*3f -1.5f; //range -1.5, 1.5

            iterate(x, y,bailout);
        }
        
    }
    
    
    void iterate(float x0, float y0, int bailout) {
        float x = 0;
        float y = 0;
        float xnew, ynew;
        int ix, iy;
        boolean drawIt = false;

        for (int i = 0; i < bailout; i++) {
            xnew = x * x - y * y + x0;
            ynew = 2 * x * y + y0;

            if ((xnew * xnew + ynew * ynew) > 4) {
                // escapes
                drawIt = true;
            }

            x = xnew;
            y = ynew;
        }
        
        x=0;
        y=0;
        if(drawIt) {
            for (int i = 0; i < bailout; i++) {
                xnew = x * x - y * y + x0;
                ynew = 2 * x * y + y0;

                if ((xnew * xnew + ynew * ynew) > 4) {
                    // escapes
                    drawIt = true;
                }
                if (drawIt && (i > 3)) {
                    ix = (int) (width * (xnew + 2.0f) / 3.0f);
                    iy = (int) (height * (ynew + 1.5f) / 3.0f);
                    if (ix > 0 && iy > 0 && ix < width && iy < height) {
                        renderer.addToPoint(iy, ix, 1f);
                    }

                }

                x = xnew;
                y = ynew;
            }
            
        }
        

        // does not escape, don't care.
    }

    @Override
    public long getAccuracy() {
        return 0;
    }
    
}
