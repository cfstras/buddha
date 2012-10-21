/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buddha;

import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.TextField;
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
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;


/**
 *
 * @author claus, morth
 */
public class PNGRenderer implements Renderer {

    int width;
    int height;
    Color bgColor = Color.BLACK;
    Color fgColor = Color.WHITE;
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
    //JSlider color_r,color_g,color_b,alpha;

    final Object dataSync=new Object();

    long numPoints;

    double[] data;
    double maxValue;
    double minValue;


    @Override
    public void setPoint(int x, int y, double value) {
        synchronized(dataSync) {
            data[x+y*width]=value;
            numPoints++;
        }
    }


    @Override
    public void addToPoint(int x, int y, double value) {
        synchronized(dataSync) {
            data[x+y*width]+=value;
            numPoints++;
        }
    }
    @Override
    public void expose(int x, int y) {
        synchronized(dataSync) {
            data[x+y*width]++;
            numPoints++;
        }
    }
    
    @Override
    public void expose(int[] x, int[] y, int num) {
        if(x==null && y == null) {
            System.out.println("error: got empty exposure array");
            return;
        }
        if(x.length!=y.length) {
            System.out.println("error: exposure array size mismatch");
            return;
        }
        int l = Math.min(x.length,num);
        synchronized(dataSync) {
            for(int i = 0;i<l;i++) {
                data[x[i]+y[i]*width]++;
            }
            numPoints += l;
        }
    }
    @Override
    public void expose(int[] x, int[] y) {
        if(x==null && y == null) {
            System.out.println("error: got empty exposure array");
            return;
        }
        if(x.length!=y.length) {
            System.out.println("error: exposure array size mismatch");
            return;
        }
        expose(x,y,x.length);
    }
    
    

    @Override
    public long getExposes() {
        return numPoints;
    }

    @Override
    public void render(Graphics2D g) {
        render=true;
        //renderButton.setEnabled(false);
        render1(g);
    }

    private void render1(Graphics2D g) {
        
	//System.out.println("writing image");
	//infoLabel.setText("buddha-" + Buddha.maxIterations + "-" + Buddha.minIterations + "-" + numPoints / 1000000 + "M  writing          ");
	//statusLabel.setText("   generating image (Threads paused)...   ");
	findMaxValue();
	//BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	//Graphics2D g = img.createGraphics();
	g.setColor(new Color(0.0f, 0.0f, 0.0f, 1.0f)); // set color to black
	g.fillRect(0, 0, width, height); // draw background
        //TODO set bg color herre
        double ramp = 0;
        synchronized (dataSync) {
            for (int ix = 0; ix < width; ix ++) {
                for (int iy = 0; iy < height; iy ++) {
                    ramp = data[iy+ix*width];
                    //ramp /= aa;
                    ramp = 2 * (ramp - minValue) / (maxValue - minValue);
                    if (ramp > 1f) {
                        ramp = 1f;
                    } else if(ramp<=0)
                        continue;
                    ramp = Math.pow(ramp, 0.5);
                    
                    float cr = fgColor.getRed() * (float)ramp + bgColor.getRed() * (1-(float)ramp);
                    float cg = fgColor.getGreen() * (float)ramp + bgColor.getGreen() * (1-(float)ramp);
                    float cb = fgColor.getBlue() * (float)ramp + bgColor.getBlue() * (1-(float)ramp);
                    float ca = ramp==0?0:1;                   
                    g.setColor(new Color(cr/255,cg/255,cb/255,ca));
                    //color support atm without transparency - lower alpha value only make the image brighter
                    g.drawRect(ix ,iy, 0, 0);// ix,iy,1,1 draws a 2x2 rect - baaad.
                }
            }
        }
        //img.flush();
        g.dispose();
        //statusLabel.setText("   writing image to file...   ");
        //File output=new File("buddha-"+Buddha.maxIterations+"-"+Buddha.minIterations+"-"+numPoints/1000000+"M.png");
        //try {
        //    ImageIO.write(img, "png", output);
        //} catch (IOException ex) {
        //    ex.printStackTrace();
        //}
        //System.out.println("written image.");
        //infoLabel.setText("buddha-"+Buddha.maxIterations+"-"+Buddha.minIterations+"-"+numPoints/1000000+"M    written     ");
        //statusLabel.setText("  finished writing image");
        //renderButton.setEnabled(true);
    }
    
    
    
    private void findMaxValue() {
        maxValue=0;
        for(double d: data) {
            if(d>maxValue) {
                maxValue=d;
            }
        }
        minValue=maxValue;
        for(double d: data) {
            if(d<minValue) {
                minValue=d;
            }
        }
        if(maxValue >= Double.MAX_VALUE*0.5) {
            System.out.println("Careful, max value is "+maxValue);
        }
    }

    @Override
    public void init(int sizex, int sizey) {
        width=sizex;
        height=sizey;
        reInit();
    }

    @Override
    public void reInit() {
        synchronized (dataSync) {
            data= null;
            //Thread.currentThread().setPriority((Thread.MIN_PRIORITY+Thread.NORM_PRIORITY)/2);
            data=new double[width*height];
        }
        //System.gc(); //TODO maybe add some gc
        numPoints=0;
    }

    /*Button color;
    public void awtInit() {
        f=new Frame("Butterbrot");
	try {
	    f.setIconImage(ImageIO.read(this.getClass().getResourceAsStream("/res/icon.png")));
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
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
        c.add(new Label("Red: "));
	color_r=new JSlider(0, 1000, (int)Buddha.color_r*1000);
        c.add(color_r);
        f.add(c);

        c= new Container();
        c.setLayout(new BoxLayout(c,BoxLayout.X_AXIS));
        c.add(new Label("Green: "));
	color_g=new JSlider(0, 1000, (int)Buddha.color_g*1000);
	c.add(color_g);
        f.add(c);

        c= new Container();
        c.setLayout(new BoxLayout(c,BoxLayout.X_AXIS));
        c.add(new Label("Blue: "));
        color_b=new JSlider(0, 1000, (int)Buddha.color_b*1000);
        c.add(color_b);
        f.add(c);

        c= new Container();
        c.setLayout(new BoxLayout(c,BoxLayout.X_AXIS));
        c.add(new Label("Alpha: "));
        alpha=new JSlider(0, 1000, (int)Buddha.alpha*1000);
        c.add(alpha);
        f.add(c);
	ChangeListener l=new ChangeListener() {
	    @Override
	    public void stateChanged(ChangeEvent e) {
		parseColor();
	    }
	};
	color=new Button("       ");
	color.setEnabled(false);
	f.add(color);
	parseColor();
	color_r.addChangeListener(l);
	color_g.addChangeListener(l);
	color_b.addChangeListener(l);
	alpha.addChangeListener(l);


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
        parseColor();
        try {
            int val=Integer.parseInt(sizeField.getText());
            if(val!=Buddha.sizex) {
                Buddha.sizey=val;
                Buddha.sizex=val;
                width=val;
                height=val;
                reInit();
            }
        } catch(NumberFormatException ex) {}
    }

    private void parseColor() {
            Buddha.color_r=color_r.getValue()/(float)1000;
            Buddha.color_g=color_g.getValue()/(float)1000;
            Buddha.color_b=color_b.getValue()/(float)1000;
            Buddha.alpha=alpha.getValue()/(float)1000;

	color.setBackground(new Color(Buddha.color_r,Buddha.color_g,Buddha.color_b,Buddha.alpha));
    }

    @Override
    public void updateInfo(String string) {
        infoLabel.setText(string);
        iterateLabel.setText("min Iterations:"+Buddha.minIterations+" max Iterations:"+Buddha.maxIterations);

        f.pack();
    }
*/


    public void save() {
        //startButton.setEnabled(false);
        //restartButton.setEnabled(false);
        // progressbar
        JDialog pgf=new JDialog(f,"saving to file...");
        JProgressBar jpg=new JProgressBar();
        jpg.setBounds(0,0,300,70);
        pgf.add(jpg);
        pgf.pack();
        pgf.setLocationRelativeTo(Buddha.gui);
        OutputStream os = null;
        try {
            File db = new File("buddha-" + Buddha.maxIterations + "-" + Buddha.minIterations + "-" + numPoints / 1000000 + "M.bbf");
            if (!db.createNewFile()) {
                JOptionPane.showMessageDialog(f, "File "+db.getName()+" already exists!", "File exists!", JOptionPane.ERROR_MESSAGE);
            }

            pgf.setVisible(true);
            jpg.setIndeterminate(true);
            os = new FileOutputStream(db);
            ZipOutputStream zos = new ZipOutputStream(os);
            zos.setLevel(5);
            zos.putNextEntry(new ZipEntry("BuddhaBrot cfs database entry"));

            //header
            jpg.setMinimum(0);
            int jpgOffset=(int)(0.1f*width);
            jpg.setMaximum(width+jpgOffset);

            jpg.setValue(0);
            jpg.setIndeterminate(false);
            DataOutputStream dos=new DataOutputStream(zos);
            dos.writeUTF("BuddhaBrot Save File.");
            dos.writeInt(width);
            dos.writeInt(height);
            dos.writeInt(Buddha.minIterations);
            dos.writeInt(Buddha.maxIterations);
            dos.writeLong(numPoints);
            jpg.setValue(jpgOffset);
            for(int ix=0;ix<width;ix++) {
                for(int iy=0;iy<height;iy++) {
                    dos.writeDouble(data[ix+iy*width]);
                }
                jpg.setValue(jpgOffset+ix);
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
        pgf.dispose();
        //startButton.setEnabled(true);
        //restartButton.setEnabled(true);
    }


    @Override
    public void load() {
        //startButton.setEnabled(false);
        //restartButton.setEnabled(false);
        // progressbar
        JDialog pgf=new JDialog(f,"loading...");
        JProgressBar jpg=new JProgressBar();
        jpg.setBounds(0,0,300,70);
        pgf.add(jpg);
        pgf.pack();
        pgf.setLocationRelativeTo(f);

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
            pgf.setVisible(true);
            jpg.setIndeterminate(true);
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
            
            jpg.setMinimum(0);
            int jpgOffset=(int)(0.2f*width);
            jpg.setMaximum(width+jpgOffset);
            
            synchronized(dataSync) {
            
                jpg.setValue(0);
                jpg.setIndeterminate(false);
                reInit();
                jpg.setValue(jpgOffset);
                numPoints=dos.readLong();
                for(int ix=0;ix<width;ix++) {
                    for(int iy=0;iy<height;iy++) {
                        data[ix+iy*width]=dos.readDouble();
                    }
                    jpg.setValue(jpgOffset+ix);
                }
            }
            if(!dos.readUTF().equals("End of Save file.")) {
                error="Not a Buddhabrot Savefile / corrupted file.";
                return;
            }
            zos.closeEntry();
            //updateInfo("buddha-" + Buddha.maxIterations + "-" + Buddha.minIterations + "-" + numPoints / 1000000 + "M");
            JOptionPane.showMessageDialog(f, "File "+db.getName()+" loaded!", "Yay!", JOptionPane.INFORMATION_MESSAGE);
            Buddha.gui.fetchProperties();
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
            pgf.dispose();
            //startButton.setEnabled(true);
            //restartButton.setEnabled(true);
        }
    }


    /*private class RenderThread extends Thread {
        boolean run=true;
        @Override
        public void run() {
            //Buddha.guiThread=this;
            //awtInit();
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
    }*/

    @Override
    public void setColor(Color fg, Color bg) {
         bgColor = bg;
         fgColor = fg;
    }
}
