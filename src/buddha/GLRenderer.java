/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buddha;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
/**
 *
 * @author claus
 */
public class GLRenderer implements Renderer {
    
    int width;
    int height;
    float[] bgcolor;
    float[] fgcolor;
    boolean render=false;
    Thread renderer;
    
    final Object dataSync=new Object();
    
    long numPoints;
    
    float[][] data;
    float maxValue;
    
    @Override
    public void setPoint(int x, int y, float value) {
        synchronized(dataSync) {
            data[x][y]=value;
            numPoints++;
        }
    }
    
    @Override
    public void addToPoint(int x, int y, float value) {
        synchronized(dataSync) {
            data[x][y]+=value;
            numPoints++;
        }
    }

    @Override
    public void render() {
        render=true;
        //synchronized(renderer) {
        //    renderer.notify();
        //}
    }
    
    /**
     * actually render.
     */
    private void render1() {
        findMaxValue();
        
        if(bgcolor!=null) {
            glClearColor(bgcolor[0], bgcolor[1],bgcolor[2] , bgcolor[3]);
        }
        glClear(GL_COLOR_BUFFER_BIT );//| GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();
        //glTranslatef(0f,0f,0f); //TODO adjust this and scale
        
        if(fgcolor!=null) {
            glColor4f(fgcolor[0], fgcolor[1],fgcolor[2], fgcolor[3]);
        } else {
            glColor4f(1, 1, 1, 1);
        }
        
        glTranslatef(0.375f,0.375f,0f); //keep the pixels in the middle
        
        float ramp;
        glBegin(GL_POINTS);
        synchronized(dataSync) {
            for(int ix=0;ix<width;ix++) {
                for(int iy=0;iy<height;iy++) {
                    ramp=data[ix][iy] / maxValue / 1f;
                    if(ramp>1f) ramp=1f;
                    glColor4f(fgcolor[0]*ramp, fgcolor[1]*ramp,fgcolor[2]*ramp, fgcolor[3]);
                    //glVertex3f(ix,iy,-ramp); //-ramp for later awesome mountains
                    glVertex2f(ix,iy);
                }
            }
        }
        glEnd();
        
        try {
            Display.swapBuffers();
        } catch (LWJGLException ex) {
            ex.printStackTrace();
        }
        System.out.println("rendered frame.");
    }

    @Override
    public void init(float[] bgcolor, float[] fgcolor, int sizex, int sizey) {
        width=sizex;
        height=sizey;
        this.bgcolor=bgcolor;
        this.fgcolor=fgcolor;
        
        data= new float[width][height];
        
        
        //start thread that checks for keys etc.
        renderer = new Renderer();
        renderer.start();
    }
    
    private void glInit() {
        try {
	    Display.setDisplayMode(new DisplayMode(width,height));
	    Display.create();
            Display.setTitle("Butterbrot");
	} catch (LWJGLException e) {
	    e.printStackTrace();
	    System.exit(0);
	}
        glDisable(GL_DEPTH_TEST);
        
        glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	glOrtho(0, width, height, 0, 0, -1f);
	glMatrixMode(GL_MODELVIEW);
        
    }
    
    private void findMaxValue() {
        maxValue=0;
        for(float[] dx: data) {
            for(float dxy:dx) {
                if(dxy>maxValue) {
                    maxValue=dxy;
                }
            }
        }
    }

    @Override
    public long getNumDataRecvd() {
        return numPoints;
    }

    
    
    private class Renderer extends Thread {
        boolean run=true;
        @Override
        public void run() {
            glInit();
            while (run) {
                if(Display.isCloseRequested()) {
                    Display.destroy();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {}
                    System.exit(0);
                    return;
                }
                //TODO check for keys pressed
                if(render) {
                    render=false;
                    render1();
                }
                Display.sync(60);
            }
        }
    }
    
}
