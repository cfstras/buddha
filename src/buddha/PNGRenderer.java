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
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;

/**
 *
 * @author claus
 */
public class PNGRenderer implements Renderer {
    
    int width;
    int height;
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
        System.out.println("writing image");
        l.setText("buddha-"+Buddha.bailout+"-"+Buddha.minIterations+"-"+numPoints/1000000+"M  writing          ");
        findMaxValue();
        BufferedImage img = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(bgcolor[0], bgcolor[1], bgcolor[2], bgcolor[3]));
        g.fillRect(0, 0, width, height);

        double ramp = 0;
        synchronized (dataSync) {
            for (int ix = 0; ix < width; ix ++) {
                for (int iy = 0; iy < height; iy ++) {
                    ramp =data[ix ][iy ];
                    //ramp /= aa;
                    ramp = 2 * (ramp - minValue) / (maxValue - minValue);
                    if (ramp > 1f) {
                        ramp = 1f;
                    }
                    ramp = Math.pow(ramp, 0.5);

                    g.setColor(new Color((float) (fgcolor[0] * ramp), (float) (fgcolor[1] * ramp), (float) (fgcolor[2] * ramp), fgcolor[3]));
                    //glVertex3f(ix,iy,-ramp); //-ramp for later awesome mountains
                    g.drawRect(ix ,iy, 1, 1);
                }
            }
        }
        img.flush();
        g.dispose();
        File output=new File("buddha-"+Buddha.bailout+"-"+Buddha.minIterations+"-"+numPoints/1000000+"M.png");
        try {
            ImageIO.write(img, "png", output);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("written image.");
        l.setText("buddha-"+Buddha.bailout+"-"+Buddha.minIterations+"-"+numPoints/1000000+"M    written     ");
    }
    

    @Override
    public void init(float[] bgcolor, float[] fgcolor, int sizex, int sizey) {
        width=sizex;
        height=sizey;
        this.bgcolor=bgcolor;
        this.fgcolor=fgcolor;
        reInit();
        
        //start thread that checks for keys etc.
        renderer = new RenderThread();
        renderer.start();
    }
    
    private void reInit() {
        data= null;
        System.gc();
        data=new double[width][height];
        numPoints=0;
    }
    
    Label l;
    public void awtInit() {
        f=new Frame("Butterbrot");
        l=new Label("buddha-"+Buddha.bailout+"-"+Buddha.minIterations+"-"+numPoints/1000000+"M       not started        ");
        f.add(l);
        
        f.setLayout(new BoxLayout(f, BoxLayout.Y_AXIS));
        Button b= new Button("Start Over (delete data)");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reInit();
                Buddha.restartThreads();
            }
        });
        f.add(b);
        
        b= new Button("Stop Threads");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Buddha.stopThreads();
            }
        });
        f.add(b);
        
        b= new Button("X2 bailout (stop)");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Buddha.stopThreads();
                Buddha.bailout*=2;
                Buddha.stopThreads(); // again just to update info
            }
        });
        f.add(b);
        
        b= new Button("/2 bailout (stop)");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Buddha.stopThreads();
                Buddha.bailout/=2;
                Buddha.stopThreads();
            }
        });
        f.add(b);
        
        b= new Button("X2 minIterations (stop)");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Buddha.stopThreads();
                Buddha.minIterations*=2;
                Buddha.stopThreads();
            }
        });
        f.add(b);
        b= new Button("/2 minIterations (stop)");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Buddha.stopThreads();
                Buddha.minIterations/=2;
                Buddha.stopThreads();
            }
        });
        f.add(b);
        
        f.setResizable(false);
        f.pack();
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
        l.setText(string);
        f.pack();
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
