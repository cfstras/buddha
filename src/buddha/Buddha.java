/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buddha;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 *
 * @author claus
 */
public class Buddha {

    static int updateInterval = 300;
    
    static int sizex=1024;
    static int sizey=1024;
    
    static int maxIterations=200;
    static int minIterations=5;
    static int numToRun=1024*1024*512;
    
    static int maxThreads=32;
    static int numThreads;
    
    static Color fgColor;
    static Color bgColor;
    
    static boolean threadsRunning;
    static Renderer renderer;
    static GenerateThread[] threads;
    static PreviewThread prevThread;
    //static Thread guiThread;
    static GUI gui;
    static boolean previewing;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        setLwjglPath();
        renderer = new PNGRenderer();
        bgColor= new Color(0,0,0,0);
        fgColor= new Color(255,255,255,255);
        
        renderer.init(sizex, sizey);
        renderer.setColor(fgColor, bgColor);
        
        numThreads = Math.min(Runtime.getRuntime().availableProcessors(),maxThreads);
        threads = new GenerateThread[numThreads];
        //renderer.updateInfo("buddha-"+Buddha.maxIterations+"-"+Buddha.minIterations+"-"+renderer.getExposes()/1000000+"M    not started     ");
        gui = new GUI();
    }
    
    static void stopThreads() {
        if(threadsRunning) {
            threadsRunning=false;
            //renderer.updateInfo("buddha-"+Buddha.maxIterations+"-"+Buddha.minIterations+"-"+renderer.getExposes()/1000000+"M    stopping       ");
            for(int i=0;i<threads.length;i++) {
                threads[i].run=false;
                threads[i].interrupt();
            }
            for(int i=0;i<threads.length;i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException ex) {}
            }
            gui.setGenerateStatus("stopped.");
            System.gc();
        }
        //renderer.updateInfo("buddha-"+Buddha.maxIterations+"-"+Buddha.minIterations+"-"+renderer.getExposes()/1000000+"M    stopped        ");
    }
    
    static void restartThreads() {
        stopThreads();
        long seed = System.currentTimeMillis();
        for(int i=0;i<numThreads;i++) {
            threads[i]=new GenerateThread(seed+i);
            threads[i].setPriority(Thread.MIN_PRIORITY);
            threads[i].setName("GeneratorThread-"+i);
            threads[i].start();
        }
        threadsRunning=true;
        gui.setGenerateStatus("running...");
    }

    private static void setLwjglPath() {
        String fs=System.getProperty("file.separator","/");
        String os = System.getProperty("os.name");
        if(os.contains("Windows")) os = "windows";
        else if(os.contains("Linux")) os = "linux";
        else if(os.contains("Solaris")) os = "solaris";
        else if(os.contains("Mac")) os = "macosx";
        System.setProperty("org.lwjgl.librarypath","lib"+fs+"native"+fs+os+fs);
    }

    static void reStartPreview() {
        previewing = true;
        if(prevThread!=null)
            prevThread.run=false;
        prevThread = new PreviewThread();
        prevThread.start();
    }
    static void stopPreview() {
        previewing = false;
        prevThread.run=false;
        prevThread = null;
    }
    
    static synchronized void exportImage() {
        gui.startExporting();
        
        System.out.println("exporting image");
	//infoLabel.setText("buddha-" + Buddha.maxIterations + "-" + Buddha.minIterations + "-" + numPoints / 1000000 + "M  writing          ");
	gui.setExportStatus("generating...");
	BufferedImage img = new BufferedImage(sizex, sizey, BufferedImage.TYPE_INT_ARGB);
	Graphics2D g = img.createGraphics();
        renderer.render(g);
        img.flush();
        g.dispose();
        
        gui.setExportStatus("saving...");
        String filename = "buddha-"+Buddha.maxIterations+"-"+Buddha.minIterations+"-"+renderer.getExposes()/1000000+"M";
        File output=new File(filename+".png");
        int i = 1;
        while(output.exists()) {
            output = new File(filename+"_"+i+".png"); 
            i++;
        }
        
        try {
            ImageIO.write(img, "png", output);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(gui,"Error exporting: "+ex, "Error exporting image", JOptionPane.ERROR_MESSAGE);
        }
        System.out.println("written image.");
        gui.setExportStatus("finished");
        gui.stopExporting();
        
        String[] options = {"Open Image", "OK"};
        int option = JOptionPane.showOptionDialog(gui, "Image successfully exported: \n"
                + output.getAbsolutePath(),"Hooray!", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,null, options, options[1]);
        
        if(option == 0) {
            try {
                //open image
                Desktop.getDesktop().open(output);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(gui,"Error opening image: "+ex, "Error opening image", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {}
        gui.setExportStatus("          ");
    }
    
    static class GenerateThread extends Thread {
        public GenerateThread(long seed) {
            this.seed = seed;
        }
        long seed;
        boolean run = true;
        Fractal f;
        @Override
        public void run() {
            setName("Generator-");
            f = new Buddhabrot();
            f.init(sizex, sizey,minIterations,maxIterations, renderer, seed);
            while (run) {
                f.generateData(numToRun/maxIterations);
                gui.updateExposures(renderer.getExposes());
            }
            f.deInit();
        }
    }
    static class PreviewThread extends Thread {
        int i = 0;
        static String[] tick = {"-","\\","|","/"};
        boolean run = true;
        public boolean redraw = false;
        @Override
        public void run() {
            setName("Preview");
            gui.setRenderStatus("on");
            while (run) {
                try {
                    Thread.sleep(updateInterval);
                } catch (InterruptedException ex) {
                }
                if (threadsRunning || redraw) {
                    redraw=false;
                    gui.render();
                    gui.setRenderStatus("on: "+tick[i%tick.length]);
                    i++;
                }
            }
            gui.setRenderStatus("off");
        }
    }
}
