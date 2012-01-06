/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buddha;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.BoxLayout;

/**
 *
 * @author claus
 */
public class AWTRenderer implements Renderer {
    
    int width;
    int height;
    int aa=4;
    double sqaa;
    float[] bgcolor;
    float[] fgcolor;
    boolean render=false;
    Thread renderer;
    
    Frame f;
    
    final Object dataSync=new Object();
    
    long numPoints;
    
    double[][] data;
    double maxValue;
    double minValue;
    
    
    @Override
    public void setPoint(int x, int y, double value) {
        synchronized(dataSync) {
            data[x][y]=value;
            numPoints++;
        }
    }
    
    
    @Override
    public void addToPoint(int x, int y, double value) {
        synchronized(dataSync) {
            data[x][y]+=value;
            numPoints++;
        }
    }

    @Override
    public long getNumDataRecvd() {
        return numPoints;
    }

    @Override
    public void render() {
        render=true;
    }
    
    private void render1() {
        findMaxValue();
        Graphics g = f.getBufferStrategy().getDrawGraphics();
        g.setColor(new Color(bgcolor[0], bgcolor[1], bgcolor[2], bgcolor[3]));
        g.fillRect(0, 0, width / aa, height / aa);

        double ramp = 0;
        synchronized (dataSync) {
            for (int ix = 0; ix < width; ix += sqaa) {
                for (int iy = 0; iy < height; iy += sqaa) {
                    ramp = 0;
                    for (int x = 0; x < sqaa; x++) {
                        for (int y = 0; x < sqaa; x++) {
                            ramp = ramp + data[ix + x][iy + y];
                        }
                    }
                    ramp /= aa;
                    ramp = 2 * (ramp - minValue) / (maxValue - minValue);
                    if (ramp > 1f) {
                        ramp = 1f;
                    }
                    ramp = Math.pow(ramp, 0.5);

                    g.setColor(new Color((float) (fgcolor[0] * ramp), (float) (fgcolor[1] * ramp), (float) (fgcolor[2] * ramp), fgcolor[3]));
                    //glVertex3f(ix,iy,-ramp); //-ramp for later awesome mountains
                    g.drawRect((int) (ix / sqaa), (int) (iy / sqaa), 1, 1);

                }
            }
        }
        f.getBufferStrategy().show();

    }
    

    @Override
    public void init(float[] bgcolor, float[] fgcolor, int sizex, int sizey) {
        width=sizex;
        height=sizey;
        this.bgcolor=bgcolor;
        this.fgcolor=fgcolor;
        sqaa=Math.sqrt(aa);
        data= new double[width][height];
        
        //start thread that checks for keys etc.
        renderer = new RenderThread();
        renderer.start();
    }
    
    public void awtInit() {
        f=new Frame("Butterbrot");
        f.setLayout(new BoxLayout(f, BoxLayout.Y_AXIS));
        Button b= new Button("Start Over (delete data)");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        f.add(b);
        
        
        f.setPreferredSize(new Dimension((int)(width/sqaa),(int)(height/sqaa)));
        f.setBounds(0, 0, (int)(width/sqaa), (int)(height/sqaa));
        f.setResizable(false);
        f.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {
                
            }
            @Override public void windowClosing(WindowEvent e) {
                System.exit(0);
            } @Override public void windowClosed(WindowEvent e) {
                
            } @Override
            public void windowIconified(WindowEvent e) {
                
            } @Override
            public void windowDeiconified(WindowEvent e) {
                
            } @Override
            public void windowActivated(WindowEvent e) {

            }  @Override
            public void windowDeactivated(WindowEvent e) {
                
            }
        });
        f.setVisible(true);
        f.createBufferStrategy(2);
    }
    
    private void findMaxValue() {
        maxValue=0;
        for(double[] dx: data) {
            for(double dxy:dx) {
                if(dxy>maxValue) {
                    maxValue=dxy;
                }
            }
        }
        minValue=maxValue;
        for(double[] dx: data) {
            for(double dxy:dx) {
                if(dxy<minValue) {
                    minValue=dxy;
                }
            }
        }
    }

    @Override
    public void updateInfo(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private class RenderThread extends Thread {
        boolean run=true;
        @Override
        public void run() {
            awtInit();
            while (run) {
                
                //TODO check for keys pressed
                if(render) {
                    render=false;
                    render1();
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {}
            }
        }
    }
    
}
