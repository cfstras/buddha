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
        render1(g);
    }

    private void render1(Graphics2D g) {
        findMaxValue();

        g.setBackground(Color.BLACK); // set color to black
        g.clearRect(0, 0, width, height); // draw background
        //TODO set bg color here
        double ramp = 0;
        synchronized (dataSync) {
            for (int ix = 0; ix < width; ix++) {
                for (int iy = 0; iy < height; iy++) {
                    ramp = data[iy + ix * width];
                    //ramp /= aa;
                    ramp = 2 * (ramp - minValue) / (maxValue - minValue);
                    if (ramp > 1f) {
                        ramp = 1f;
                    } else if (ramp <= 0) {
                        continue;
                    }
                    ramp = Math.pow(ramp, 0.5);

                    float cr = fgColor.getRed() * (float) ramp + bgColor.getRed() * (1 - (float) ramp);
                    float cg = fgColor.getGreen() * (float) ramp + bgColor.getGreen() * (1 - (float) ramp);
                    float cb = fgColor.getBlue() * (float) ramp + bgColor.getBlue() * (1 - (float) ramp);
                    float ca = ramp == 0 ? 0 : 1;
                    g.setColor(new Color(cr / 255, cg / 255, cb / 255, ca));
                    //color support atm without transparency - lower alpha value only make the image brighter
                    g.drawRect(ix, iy, 0, 0);// ix,iy,1,1 draws a 2x2 rect - baaad.
                }
            }
        }
        g.dispose();
        render = false;
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

    @Override
    public void save(JProgressBar jpg) {
        //startButton.setEnabled(false);
        //restartButton.setEnabled(false);
        OutputStream os = null;
        try {
            File db = new File("buddha-" + Buddha.maxIterations + "-" + Buddha.minIterations + "-" + numPoints / 1000000 + "M.bbf");
            if (!db.createNewFile()) {
                JOptionPane.showMessageDialog(f, "File "+db.getName()+" already exists!", "File exists!", JOptionPane.ERROR_MESSAGE);
            }

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
