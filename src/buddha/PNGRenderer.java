/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buddha;

import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

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
    Label infoLabel;
    Label statusLabel;
    Label iterateLabel;
    Button startButton;
    Button restartButton;
    Button renderButton;
    
    TextField minItField;
    TextField maxItField;
    TextField sizeField;
    
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
        renderButton.setEnabled(false);
    }
    
    private void render1() {
        System.out.println("writing image");
        infoLabel.setText("buddha-"+Buddha.maxIterations+"-"+Buddha.minIterations+"-"+numPoints/1000000+"M  writing          ");
        statusLabel.setText("   generating image (Threads paused)...   ");
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
                    } else if(ramp<=0)
                        continue;
                    ramp = Math.pow(ramp, 0.5);
                    
                    g.setColor(new Color((float) (fgcolor[0] * ramp), (float) (fgcolor[1] * ramp), (float) (fgcolor[2] * ramp), fgcolor[3]));
                    g.drawRect(ix ,iy, 1, 1);
                }
            }
        }
        img.flush();
        g.dispose();
        statusLabel.setText("   writing image to file...   ");
        File output=new File("buddha-"+Buddha.maxIterations+"-"+Buddha.minIterations+"-"+numPoints/1000000+"M.png");
        try {
            ImageIO.write(img, "png", output);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("written image.");
        infoLabel.setText("buddha-"+Buddha.maxIterations+"-"+Buddha.minIterations+"-"+numPoints/1000000+"M    written     ");
        statusLabel.setText("  finished writing image");
        renderButton.setEnabled(true);
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
        Thread.currentThread().setPriority((Thread.MIN_PRIORITY+Thread.NORM_PRIORITY)/2);
        data=new double[width][height];
        numPoints=0;
    }
    
    
    public void awtInit() {
        f=new Frame("Butterbrot");
        f.setLayout(new BoxLayout(f, BoxLayout.Y_AXIS));
        f.add(new Label("maxIterations must be larger than minIterations!"));
        iterateLabel= new Label("min Iterations:  max Iterations:");
        f.add(iterateLabel);
        infoLabel=new Label("buddha-"+Buddha.maxIterations+"-"+Buddha.minIterations+"-"+numPoints/1000000+"M       not started        ");
        f.add(infoLabel);
        
        statusLabel= new Label("         ");
        f.add(statusLabel);
        
        restartButton= new Button("Start Threads (delete data first)");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reInit();
                Buddha.restartThreads();
            }
        });
        f.add(restartButton);
        
        startButton= new Button("Start Threads (don't delete data)");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Buddha.restartThreads();
            }
        });
        f.add(startButton);
        
        Button b= new Button("Stop Threads");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Buddha.stopThreads();
            }
        });
        f.add(b);
        
        renderButton= new Button("Render Image");
        renderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                render();
            }
        });
        f.add(renderButton);
        
        b= new Button("Save Data (Huge file.)");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Buddha.stopThreads();
                new Thread() {
                    @Override public void run() {
                        save();
                    }
                }.start();
            }
        });
        f.add(b);
        b= new Button("Load Data");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Buddha.stopThreads();
                new Thread() {
                    @Override public void run() {
                        load();
                    }
                }.start();
            }
        });
        f.add(b);
        
        Container c= new Container();
        c.setLayout(new BoxLayout(c,BoxLayout.X_AXIS));
        c.add(new Label("Min Iterations: "));
        minItField=new TextField(Integer.toString(Buddha.minIterations));
        c.add(minItField);
        f.add(c);
        
        c= new Container();
        c.setLayout(new BoxLayout(c,BoxLayout.X_AXIS));
        c.add(new Label("Max Iterations: "));
        maxItField=new TextField(Integer.toString(Buddha.maxIterations));
        c.add(maxItField);
        f.add(c);
        
        c= new Container();
        c.setLayout(new BoxLayout(c,BoxLayout.X_AXIS));
        c.add(new Label("Image side length (if you change it, data will be deleted):"));
        sizeField=new TextField(Integer.toString(Buddha.sizex));
        c.add(sizeField);
        f.add(c);
        
        b= new Button("Use These Values! (stops Threads)");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Buddha.stopThreads();
                parseValues();
                Buddha.stopThreads(); // again just to update info
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
    
    void parseValues() {
        try {
            Buddha.minIterations=Integer.parseInt(minItField.getText());
        } catch(NumberFormatException ex) {}
        try {
            Buddha.maxIterations=Integer.parseInt(maxItField.getText());
        } catch(NumberFormatException ex) {}
        try {
            int val=Integer.parseInt(maxItField.getText());
            if(val!=Buddha.sizex) {
                Buddha.sizey=val;
                Buddha.sizex=val;
                width=val;
                height=val;
                reInit();
            }
        } catch(NumberFormatException ex) {}
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
        infoLabel.setText(string);
        iterateLabel.setText("min Iterations:"+Buddha.minIterations+" max Iterations:"+Buddha.maxIterations);
        
        f.pack();
    }
    
    
    
    private void save() {
        startButton.setEnabled(false);
        restartButton.setEnabled(false);
        OutputStream os = null;
        try {
            File db = new File("buddha-" + Buddha.maxIterations + "-" + Buddha.minIterations + "-" + numPoints / 1000000 + "M.bbf");
            if (!db.createNewFile()) {
                JOptionPane.showMessageDialog(f, "File "+db.getName()+" already exists!", "File exists!", JOptionPane.ERROR_MESSAGE);
            }
            os = new FileOutputStream(db);
            ZipOutputStream zos = new ZipOutputStream(os);
            zos.setLevel(5);
            zos.putNextEntry(new ZipEntry("BuddhaBrot cfs database entry"));
            
            //header
            
            DataOutputStream dos=new DataOutputStream(zos);
            dos.writeUTF("BuddhaBrot Save File.");
            dos.writeInt(width);
            dos.writeInt(height);
            dos.writeInt(Buddha.minIterations);
            dos.writeInt(Buddha.maxIterations);
            dos.writeLong(numPoints);
            for(int ix=0;ix<width;ix++) {
                for(int iy=0;iy<height;iy++) {
                    dos.writeDouble(data[ix][iy]);
                }
            }
            dos.writeUTF("End of Save file.");
            dos.flush();
            zos.closeEntry();
            zos.finish();
            
            JOptionPane.showMessageDialog(f, "File "+db.getName()+" written!", "Yay!", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(f, ex.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try {
                if(os!=null)
                    os.close();
            } catch (IOException ex) {}
        }
        startButton.setEnabled(true);
        restartButton.setEnabled(true);
    }
    
    
    private void load() {
        startButton.setEnabled(false);
        restartButton.setEnabled(false);
        //TODO progressbar
        String error="";
        InputStream os = null;
        try {
            JFileChooser jfc=new JFileChooser(".") {
                @Override public boolean accept(File f) {
                    return f.getName().endsWith(".bbf");
                }
            };
            jfc.setDialogTitle("Please choose bbf file to load!");
            jfc.setVisible(true);
            if( jfc.showDialog(f, "Load File")!=JFileChooser.APPROVE_OPTION) {
                return;
            }
            File db=jfc.getSelectedFile();
            if (!db.canRead()) {
                JOptionPane.showMessageDialog(f, "File "+db.getName()+" cannot be read!", "Read error!", JOptionPane.ERROR_MESSAGE);
            }
            os = new FileInputStream(db);
            ZipInputStream zos = new ZipInputStream(os);
            zos.getNextEntry();
            
            //header
            
            DataInputStream dos=new DataInputStream(zos);
            if(!dos.readUTF().equals("BuddhaBrot Save File.")) {
                error="Not a Buddhabrot Savefile / corrupted file.";
                return;
            }
            width=dos.readInt();
            height=dos.readInt();
            if(width!=height) {
                error="Corrupted file.";
                return;
            }
            Buddha.sizex=width;
            Buddha.sizey=height;
            
            Buddha.minIterations=dos.readInt();
            Buddha.maxIterations=dos.readInt();
            reInit();
            numPoints=dos.readLong();
            for(int ix=0;ix<width;ix++) {
                for(int iy=0;iy<height;iy++) {
                    data[ix][iy]=dos.readDouble();
                }
            }
            if(!dos.readUTF().equals("End of Save file.")) {
                error="Not a Buddhabrot Savefile / corrupted file.";
                return;
            }
            zos.closeEntry();
            updateInfo("buddha-" + Buddha.maxIterations + "-" + Buddha.minIterations + "-" + numPoints / 1000000 + "M");
            JOptionPane.showMessageDialog(f, "File "+db.getName()+" loaded!", "Yay!", JOptionPane.INFORMATION_MESSAGE);
        } catch(EOFException ex) {
            error="Not a Buddhabrot Savefile / corrupted file.";
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(f, ex.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            if(!error.equals("")) {
                JOptionPane.showMessageDialog(f, error, "Error", JOptionPane.ERROR_MESSAGE);
            }
            try {
                if(os!=null)
                    os.close();
            } catch (IOException ex) {}
            startButton.setEnabled(true);
            restartButton.setEnabled(true);
        }
    }
    
    
    private class RenderThread extends Thread {
        boolean run=true;
        @Override
        public void run() {
            Buddha.guiThread=this;
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
