/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buddha;

import java.nio.DoubleBuffer;
import java.util.List;
import java.util.Random;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opencl.*;
import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.CL11.*;

/**
 *
 * @author claus
 */
public class CLBuddha implements Fractal {

    int minIterations;
    int maxIterations;
    int sizex, sizey;
    long seed;
    Random random;
    Renderer renderer;
    
    List<CLPlatform> platforms;
    CLPlatform plat;
    List<CLDevice> devices;
    CLContext con;
    CLCommandQueue queue;
    
    @Override
    public void init(int sizex, int sizey, int minIterations, int maxIterations, Renderer renderer, long seed) {
        random = new Random(seed);
        this.seed = seed;
        this.renderer = renderer;
        this.sizex = sizex;
        this.sizey = sizey;
        try {
            CL.create();
            platforms = CLPlatform.getPlatforms();
            for(int i=0;i<platforms.size();i++) {
                System.out.println("platform "+i+": "+platforms.get(i).getInfoString(CL_DEVICE_NAME));
            }
            plat = platforms.get(0);
            devices = plat.getDevices(CL_DEVICE_TYPE_GPU);
            con = CLContext.create(plat, devices,null);
            for(int i=0;i<devices.size();i++) {
                System.out.println("device "+i+": "+devices.get(i).getInfoString(CL_DEVICE_NAME));
            }
            queue = clCreateCommandQueue(con, devices.get(0), CL_QUEUE_PROFILING_ENABLE, null);
            
        } catch (LWJGLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void set(int minIterations, int maxIterations) {
        this.minIterations = minIterations;
        this.maxIterations = maxIterations;
    }

    @Override
    public void generateData(int num) {
        DoubleBuffer inX = BufferUtils.createDoubleBuffer(num);
        DoubleBuffer inY = BufferUtils.createDoubleBuffer(num);
        
    }

    @Override
    public long getAccuracy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deInit() {
        //TODO teardown
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
